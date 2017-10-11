package eu.fbk.dh.jamcha.feature;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import eu.fbk.dh.jamcha.feature.IO.FeatureFileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;

/**
 * Class that represent feature parameters that influence feature integration (eg. static and dynamic features)
 */
public class FeatureParameters
{
   /**
    * File name where will be saved features parameters and columns count
    */
   public static final String FILE_NAME = "featuresParameters.txt";
   private String features;
   private TreeMultimap<Integer, Integer> featuresParametersMap;
   private final int columnsCount;

   private FeatureParameters(@Nonnull String feature, @Nonnull Multimap<Integer, Integer> featuresMap, int columnsCount)
   {
      this.features = feature;
      if (columnsCount < FeatureFileReader.COLUMNS_COUNT_MIN)
      {
         throw new IllegalArgumentException("Number of columns is less tha " + FeatureFileReader.COLUMNS_COUNT_MIN);
      }
      this.columnsCount = columnsCount;
      featuresMap = fromColRowsToRowCols(featuresMap);
      featuresParametersMap = TreeMultimap.create(featuresMap);
   }

   /**
    * Builder of FeatureParameters
    *
    * @param allFeatures             single feature or a list of features separated by one whitespace char
    * @param trainFileLineWordsCount number of words of a line of train file (every line of train file has same number of words). Check
    *                                {@value FeatureFileReader.COLUMNS_COUNT_MIN}
    *
    * @return
    */
   public static FeatureParameters create(@Nonnull String allFeatures, int trainFileLineWordsCount)
   {
      FeatureParser parser = new FeatureParser(trainFileLineWordsCount);
      String[] features = allFeatures.split(" ");

      Multimap<Integer, Integer> featuresMap = TreeMultimap.create();
      // Getting values map of each feature
      for (String feature : features)
      {
         Multimap<Integer, Integer> singleFeatureMap = parser.parseFeature(feature);
         featuresMap.putAll(singleFeatureMap);
      }
      FeatureParameters retval = new FeatureParameters(allFeatures, featuresMap, trainFileLineWordsCount);
      return retval;
   }

   /**
    * Switch from column->rows to row->cols. In other words, input is a list of lines to consider for each column. Output is a list of all columns to consider for each
    * line
    *
    * @param featuresParameters features multimap with this structure: column->rows
    *
    * @return list of all columns to consider for each line (e.g. for line 4 consider feature-column number 0,3,4)
    */
   @Nonnull
   private static Multimap<Integer, Integer> fromColRowsToRowCols(@Nonnull final Multimap<Integer, Integer> featuresParameters)
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

   public void saveTo(Path filePath) throws IOException
   {
      //TODO: FeatureParameters.saveTo implementare
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public static FeatureParameters loadFrom(Path filePath) throws IOException
   {
      //TODO: FeatureParameters.saveTo implementare
      throw new UnsupportedOperationException("Not supported yet.");
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

   private static class FeatureParser
   {
      private static final int ROWS_MIN_OFFSET = - 30;

      private static final int ROWS_MAX_OFFSET = 30;

      private int columnsCount;

      public final static int COLUMN_VALUE = -1;

      /**
       * All features syntax options(e.g. which char divides two sections)
       */
      private class SyntaxOptions
      {
         /**
          * Feature section separator (example ":")
          */
         private static final char SECTION_SEPARATOR = ':';

         /**
          * Feature values separator within same section (value may not be in order and may not be consecutive)
          */
         private static final char VALUE_SEPARATOR = ',';

         /**
          * Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1)
          * Delete \\ if not used with String.split() function
          */
         private static final String RANGE_VALUE_SEPARATOR = "\\.\\.";
      }

      /**
       * List of all feature names. Use only chars.
       */
      private final class Types
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

      private FeatureParser(int columnsCount)
      {
         this.columnsCount = columnsCount;
      }

      /**
       * Parse and obtains feature values
       *
       * @param featureToParse list of values, written using feature pattern. E.g. F:-5..3:-3..-1
       *
       * @return All lines and columns values for this feature/** Parse and obtains feature values
       */
      private Multimap<Integer, Integer> parseFeature(@Nonnull String featureToParse)
      {
         if (featureToParse.isEmpty())
         {
            throw new IllegalArgumentException("String must be not empty");
         }
         // Split string on character separator (e.g. : ) creating a list of sections
         String[] typeList = featureToParse.split(String.valueOf(SyntaxOptions.SECTION_SEPARATOR), 2);

         Multimap<Integer, Integer> featureValues;
         switch (typeList[0])
         {
            case Types.FEATURE_STATIC:
            {
               featureValues = parseStaticFeature(typeList[1]);
               break;
            }
            case Types.FEATURE_DYNAMIC:
            {
               featureValues = parseDynamicFeature(typeList[1]);
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
       * @return list of all lines considered by each column
       */
      @Nonnull
      private Multimap<Integer, Integer> parseStaticFeature(@Nonnull String staticFeature)
      {
         String[] sections = staticFeature.split(SyntaxOptions.SECTION_SEPARATOR + "");
         if (sections.length != 2)
         {
            throw new IllegalArgumentException("Wrong number of sections: only 2 permitted");
         }
         List<Integer> lines = parseSection(sections[0], ROWS_MAX_OFFSET);
         List<Integer> columns = parseSection(sections[1], columnsCount - 2);

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
       * @return list of all lines that must consider tag column
       */
      @Nonnull
      private Multimap<Integer, Integer> parseDynamicFeature(@Nonnull String dynamicFeature)
      {
         String[] sections = dynamicFeature.split(SyntaxOptions.SECTION_SEPARATOR + "");
         if (sections.length != 1)
         {
            throw new IllegalArgumentException("Wrong number of sections: only 1 permitted");
         }
         List<Integer> lines = parseSection(sections[0], ROWS_MAX_OFFSET);

         Multimap<Integer, Integer> retval = HashMultimap.create();
         retval.putAll(COLUMN_VALUE, lines);
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
      private List<Integer> parseSection(@Nonnull String sectionToParse, int maxValue)
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
               if (number >= ROWS_MIN_OFFSET && number <= maxValue)
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
               // If empty wil be used default max value otherwise will be used parsed value
               if (valuesList[1].isEmpty())
               {
                  secondValue = maxValue;
               }
               else
               {
                  secondValue = Integer.parseInt(valuesList[1]);
               }

               // If firstValue and maxValue are valid, will be created and added to list all numbers from firstValue to maxValue (a sequence from firstValue to secondValue)
               if (within(ROWS_MIN_OFFSET, maxValue, firstValue) && within(ROWS_MIN_OFFSET, maxValue, secondValue))
               {
                  for (int i = firstValue; i <= maxValue; i ++)
                  {
                     list.add(i);
                  }
               }
               else
               {
                  throw new IllegalArgumentException(sectionToParse + " contains an invalid value");
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
      private boolean within(int min, int max, int value)
      {
         return min <= value && value <= max;
      }
   }
}
