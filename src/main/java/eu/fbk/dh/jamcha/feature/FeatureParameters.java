package eu.fbk.dh.jamcha.feature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Class that represent feature parameters that influence feature integration (eg. static and dynamic features)
 */
public final class FeatureParameters
{
   /**
    * File name where will be saved features parameters and columns count
    */
   public static final String FILE_NAME = "featuresParameters.txt";
   private String features;
   private TreeMultimap<Integer, Integer> parametersMap;
   @Nonnegative
   private final int columnsCount;
   private final int valuesCount;

   /**
    * All features values that will be saved in a file.
    */
   private static class SaveOptions
   {
      private final static String WORDS_COUNT = "LINE_WORDS_COUNT";
      private final static String FEATURES = "FEATURES_PARAMETERS";
      private final static String SEPARATOR = " ";
   }

   private FeatureParameters(@Nonnull String feature, @Nonnull Multimap<Integer, Integer> featuresMap, int columnsCount)
   {
      this.features = feature;
      if (columnsCount < FeatureFileReader.COLUMNS_COUNT_MIN)
      {
         throw new IllegalArgumentException("Number of columns is less tha " + FeatureFileReader.COLUMNS_COUNT_MIN);
      }
      this.columnsCount = columnsCount;
      featuresMap = fromColRowsToRowCols(featuresMap);
      parametersMap = TreeMultimap.create(featuresMap);
      valuesCount = parametersMap.values().size();
   }

   /**
    * Builder of FeatureParameters
    *
    * @param allFeatures    single feature or a list of features separated by one whitespace char. e.gT:-3.. F:-2..3:-6..-1 etc
    * @param lineWordsCount number of words of a line of train file (every line of train file has same number of words). Check {@code  FeatureFileReader.COLUMNS_COUNT_MIN}
    *
    * @return instance of FeatureParameters built from a string cointaining all tuning features
    */
   @Nonnull
   public static FeatureParameters build(@Nonnull String allFeatures, final int lineWordsCount)
   {
      String[] features = allFeatures.split(" ");

      Multimap<Integer, Integer> featuresMap = TreeMultimap.create();
      // Getting values map of each feature
      for (String feature : features)
      {
         Multimap<Integer, Integer> singleFeatureMap = FeatureParser.parseFeature(feature, lineWordsCount);
         featuresMap.putAll(singleFeatureMap);
      }
      FeatureParameters retval = new FeatureParameters(allFeatures, featuresMap, lineWordsCount);
      return retval;
   }

   /**
    * Switch from a view column-rows to a view row-cols. In other words, input is a list of line to consider for each column. Output is a list of all columns to consider for each line
    *
    * @param featuresParameters features multimap with this structure: a column-multiple rows
    *
    * @return list of all columns to consider for each line (e.g. for line 4 consider feature-column number 0,3,4)
    */
   @Nonnull
   protected static Multimap<Integer, Integer> fromColRowsToRowCols(@Nonnull final Multimap<Integer, Integer> featuresParameters)
   {
      Set<Integer> keySet = featuresParameters.keySet();
      TreeMultimap<Integer, Integer> rowCols = TreeMultimap.create();
      keySet.forEach((col) ->
      {
         Collection<Integer> rows = featuresParameters.get(col);
         for (int row : rows)
         {
            rowCols.put(row, col);
         }
      });
      return rowCols;
   }

   /**
    * Save feature parameters to a file. Do not indicate file name.
    *
    * @param folderPath a folder path i.e. ......./folder/
    *
    * @throws IOException invalid folderPath
    */
   public void saveTo(@Nonnull Path folderPath) throws IOException
   {
      if ( ! Files.isDirectory(folderPath))
      {
         folderPath = folderPath.getParent();
      }
      Path filePath = Paths.get(folderPath.toString(), FILE_NAME);
      try (BufferedWriter writer = Files.newBufferedWriter(filePath))
      {
         String line = SaveOptions.WORDS_COUNT + SaveOptions.SEPARATOR + columnsCount;
         writer.append(line);
         writer.newLine();
         line = SaveOptions.FEATURES + SaveOptions.SEPARATOR + features;
         writer.append(line);
      }
   }

   /**
    * Load feature parameters from a file. Do not indicate file name, only folder that contains that file
    *
    * @param folderPath a folder path i.e. ......./folder/ where feature parameters file is located
    *
    * @return build a valid FeatureParameters instance
    *
    * @throws IOException invalid modelFolderPath or file does not contain valid data
    */
   @Nonnull
   public static FeatureParameters loadFrom(@Nonnull final Path folderPath) throws IOException
   {
      Path filePath = Files.isDirectory(folderPath) ? Paths.get(folderPath.toString(), FILE_NAME) : Paths.get(folderPath.getParent().toString(), FILE_NAME);
      if ( ! Files.isReadable(filePath))
      {
         throw new IOException("Invalid path, file does not exist. Feature parameters cannot be loaded");
      }
      try (BufferedReader reader = Files.newBufferedReader(filePath))
      {
         String line;
         int wordsCount = 0;
         String features = null;
         while ((line = reader.readLine()) != null)
         {
            String[] splittedLine = line.split(SaveOptions.SEPARATOR, 2);
            if (splittedLine != null && splittedLine.length == 2)
            {
               switch (splittedLine[0])
               {
                  case SaveOptions.WORDS_COUNT:
                  {
                     wordsCount = Integer.parseInt(splittedLine[1]);
                     break;
                  }
                  case SaveOptions.FEATURES:
                  {
                     features = splittedLine[1];
                     break;
                  }
               }
            }
         }

         if (wordsCount < 2 || features == null)
         {
            throw new IOException("Invalid file data: impossible to find valid words count or features parameters");
         }
         return FeatureParameters.build(features, wordsCount);
      }
   }

   @Nonnull
   public String getFeature()
   {
      if (this.features == null)
      {
         features = "";
      }
      return this.features;
   }

   @Nonnegative
   public int getWordsLineCount()
   {
      return this.columnsCount;
   }

   public int getValuesCount()
   {
      return this.valuesCount;
   }

   @Nonnull
   protected Multimap<Integer, Integer> getParameters()
   {
      return this.parametersMap;
   }

   /**
    * Class that parse a feature "command". In other words read a string written in an appropriate way(i.e. F:x,y,z:t.. ) and convert it to a data structure
    */
   protected static class FeatureParser
   {
      /**
       * Number of previous lines(belonging to same sentence), starting from the current one, of which it can get the features
       */
      private static final int ROWS_MIN_OFFSET = - 30;

      /**
       * Number of lines(belonging to same sentence), starting from the current one, of which it can get the features
       */
      private static final int ROWS_MAX_OFFSET = 30;

      /**
       * Index that represents tag column in a
       */
      public final static int TAG_COLUMN_INDEX = -1;

      /**
       * All features syntax options(e.g. which char divides two sections)
       */
      private static class SyntaxOptions
      {
         /**
          * Feature section separator (example ":")
          */
         private static final char SECTION_SEPARATOR = ':';

         /**
          * Feature values separator isWithin same section (value may not be in order and may not be consecutive)
          */
         private static final char VALUE_SEPARATOR = ',';

         /**
          * Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1) Delete \\ if not used
          * with String.split() function
          */
         private static final String RANGE_VALUE_SEPARATOR = "\\.\\.";
      }

      /**
       * List of all feature names. Use only chars.
       */
      private final static class Types
      {
         /**
          * Static feature name constant
          */
         private static final String FEATURE_STATIC = "F";
         /**
          * Dynamic feature name constant
          */
         private static final String FEATURE_DYNAMIC = "T";
      }

      /**
       * Parse and obtains feature values
       *
       * @param featureToParse list of values, written using feature pattern. E.g. F:-5..3:-3..-1
       *
       * @return All line and columns values for this feature/** Parse and obtains feature values
       */
      private static Multimap<Integer, Integer> parseFeature(@Nonnull String featureToParse, int columnsCount)
      {
         if (featureToParse.isEmpty())
         {
            throw new IllegalArgumentException("String must be not empty");
         }
         // Split string on character separator (e.g. : ) creating a list of sections
         String[] sectionsList = featureToParse.split(String.valueOf(SyntaxOptions.SECTION_SEPARATOR), 2);

         Multimap<Integer, Integer> featureValues;
         switch (sectionsList[0])
         {
            case Types.FEATURE_STATIC:
            {
               featureValues = parseStaticFeature(sectionsList[1], columnsCount);
               break;
            }
            case Types.FEATURE_DYNAMIC:
            {
               featureValues = parseDynamicFeature(sectionsList[1]);
               break;
            }
            default:
            {
               String general = "Only Static and Dynamic features are supported. Please insert ";
               String staticMex = Types.FEATURE_STATIC + " for static feature";
               String dynamicMex = Types.FEATURE_DYNAMIC + " for dynamic feature";
               throw new IllegalArgumentException(general + staticMex + " or " + dynamicMex);
            }
         }
         return featureValues;
      }

      /**
       * Extract static feature values
       *
       * @param staticFeature string that represents static feature without first letter (e.g. F:x..y:k.. is passed as x..y:k..)
       *
       * @return list of all line considered by each column
       */
      @Nonnull
      private static Multimap<Integer, Integer> parseStaticFeature(@Nonnull String staticFeature, int columnsCount)
      {
         String[] sections = staticFeature.split(SyntaxOptions.SECTION_SEPARATOR + "");
         if (sections.length != 2)
         {
            throw new IllegalArgumentException("Wrong number of sections: only 2 permitted");
         }
         List<Integer> lines = parseSection(sections[0], ROWS_MAX_OFFSET, false);
         List<Integer> columns = parseSection(sections[1], columnsCount - 2, true);

         Multimap<Integer, Integer> retval = HashMultimap.create();
         for (int col : columns)
         {
            retval.putAll(col, lines);
         }
         return retval;
      }

      /**
       * Extract dynamic feature values
       *
       * @param dynamicFeature string that represents dynamic feature without first letter (e.g. T:x..y is passed as x..y)
       *
       * @return list of all line that must consider tag column
       */
      @Nonnull
      private static Multimap<Integer, Integer> parseDynamicFeature(@Nonnull String dynamicFeature)
      {
         String[] sections = dynamicFeature.split(SyntaxOptions.SECTION_SEPARATOR + "");
         if (sections.length != 1)
         {
            throw new IllegalArgumentException("Wrong number of sections: only 1 permitted");
         }
         List<Integer> line = parseSection(sections[0], -1, true);

         Multimap<Integer, Integer> retval = HashMultimap.create();
         retval.putAll(TAG_COLUMN_INDEX, line);
         return retval;
      }

      /**
       * Extract section values (numbers)
       *
       * @param section feature section to parse. ATTENTION: there are no checks if section is a true section
       *
       * @return section values list
       *
       * @throws NumberFormatException    this section does not respects right pattern
       * @throws IllegalArgumentException this section is empty or uses invalid value
       */
      @Nonnull
      private static List<Integer> parseSection(@Nonnull String sectionToParse, int upperLineMaxValue, boolean possibleIndefinite)
      {
         if (sectionToParse.isEmpty())
         {
            throw new IllegalArgumentException("Section to parse cannot be empty");
         }

         ArrayList<Integer> list = new ArrayList<>();

         String[] valuesList = sectionToParse.split(String.valueOf(SyntaxOptions.VALUE_SEPARATOR));
         // Section contains multiple values? If length > 1 yes
         if (valuesList.length > 1)
         {
            // Condition: a section may contain multiple single values or a range values, but not both (e.g. not x,x,x and ..). Therefore must have one of these three
            // patterns ( 1) x,y,z or 2) x..y or 3) x.. ).
            // A malformed string of valuesList satisfies one of these cases: (remember that at this point there are no ','
            // 1) there are '..' -> ERROR (parseInt fails) Explanation: valuesList length is > 1 therefore there is at least one ','. This violates initial condition.
            // 2) is empty -> ERROR (parseInt fails) Explanation: this happens when count of ',' is >= count of values (not digits)

            // Add all values to list
            for (String str : valuesList)
            {
               int number = Integer.parseInt(str);
               if (number >= ROWS_MIN_OFFSET && number <= upperLineMaxValue)
               {
                  list.add(number);
               }
               else
               {
                  throw new IllegalArgumentException("FeatureParameters: value is too high or too lower");
               }
            }
         }
         else
         {
            // Section contains no ',' therefore it must contains a RANGE_VALUE_SEPARATOR.
            valuesList = sectionToParse.split(SyntaxOptions.RANGE_VALUE_SEPARATOR, -1);

            // If length is not 2 range pattern (x..y or x..) is not satisfied therefore feature is malformed
            if (valuesList.length == 2)
            {
               // There may be two cases:
               // 1) the pattern is x..y -> both valuesList strings have length > 0
               // 2) the pattern is x.. -> first valuesList string has length > 0 and second string have lenth 0.

               // First valuesList string must have valid value otherwise this feature section has invalid pattern and there is an error
               int firstValue = Integer.parseInt(valuesList[0]);

               int secondValue;
               // Getting range max value
               // If empty will be used default max value otherwise will be used parsed value
               // If value is present will be used parsed value
               /**
                * If value is absent there are two scenarios: 1) format x.. is permitted and upperLineMaxValue becomes second value 2) format x.. is not permitted, and there is an exception
                * Example: take static feature F:-2..5:-6.. : in the first section after -2.. a value must be present (otherwise there will be raised an exception). In the second section
                * after -6.. you can or not put a value. If there is no value, upperLineMaxValue is taken
                */
               if (valuesList[1].isEmpty())
               {
                  if (possibleIndefinite)
                  {
                     secondValue = upperLineMaxValue;
                  }
                  else
                  {
                     throw new IllegalArgumentException("Malformed feature section: " + sectionToParse);
                  }
               }
               else
               {
                  secondValue = Integer.parseInt(valuesList[1]);
               }

               firstValue = uniformValue(ROWS_MIN_OFFSET, upperLineMaxValue, firstValue);
               secondValue = uniformValue(ROWS_MIN_OFFSET, upperLineMaxValue, secondValue);
//               // If firstValue and upperLineMaxValue are valid, will be created and added to list all numbers from firstValue to upperLineMaxValue (a sequence from firstValue to secondValue)
//               if (isWithin(ROWS_MIN_OFFSET, upperLineMaxValue, firstValue) && isWithin(ROWS_MIN_OFFSET, upperLineMaxValue, secondValue))
//               {
//
//               }
//               else
//               {
//
//                  throw new IllegalArgumentException(sectionToParse + " contains an invalid value");
//               }
               for (int i = firstValue; i <= secondValue; i ++)
               {
                  list.add(i);
               }
            }
            else
            {
               throw new IllegalArgumentException("Section range pattern must be: x..x or x..");
            }
         }
         return list;
      }

      /**
       * Check if value is in a range
       *
       * @param min   min value range, inclusive
       * @param max   max value range, inclusive
       * @param value value to test
       *
       * @return true if value is in range
       */
      private static boolean isWithin(int min, int max, int value)
      {
         return min <= value && value <= max;
      }

      private static int uniformValue(int min, int max, int value)
      {
         int retval;
         if (value > max)
         {
            retval = max;
         }
         else
         {
            if (value < min)
            {
               retval = min;
            }
            else
            {
               retval = value;
            }
         }
         return retval;
      }
   }
}
