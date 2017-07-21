package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.parser.FeatureParser;
import eu.fbk.dh.jamcha.feature.parser.DynamicFeatureParser;
import eu.fbk.dh.jamcha.feature.parser.StaticFeatureParser;
import javax.annotation.Nonnull;
import com.google.common.collect.TreeMultimap;

/**
 * Singleton class that manage feature parsing and trasformation
 *
 * @author dan92
 */
public final class FeatureSelector
{

   /**
    * List of all feature names. Use only chars.
    */
   private final class FeatureNames
   {

      /**
       * Static feature name constant
       */
      static final char FEATURE_STATIC = 'F';
      /**
       * Dynamic feature name constant
       */
      static final char FEATURE_DYNAMIC = 'T';
   }

   /**
    * Character at the end of feature name section (e.g. F:5..8:-3,4 in this case is the char between F and 5)
    */
   private final char FEATURE_COMMAND_END_LETTER = ':';

   /**
    * number of columns in the training file, tag column included
    */
   private final int columsNumber;

   /**
    * Contains all features values
    */
   private TreeMultimap<Integer, Integer> treeMultimap = null;

   private static FeatureSelector parser = null;

   /**
    *
    * @param numberOfColumns number of columns in the training file, tag column included
    * @return
    */
   public static FeatureSelector getInstance(int numberOfColumns)
   {
      if (parser == null)
      {
         parser = new FeatureSelector(numberOfColumns);
      }
      return parser;
   }

   /**
    * Reads a string and selects the right class that can parse this specific feature. If there is a unsupported or invalid
    * feature the method ends without any error.
    *
    * @param featureToParse feature string written with this pattern: featureName:featurePattern. (e.g. T:-5..-2). This
    * method reads only featureName, that must consist of only one char. If last condition (one char) is not respected no
    * exception is thrown.
    */
   public FeatureValues parseFeature(@Nonnull String featureToParse)
   {
      // -------------------------------------------------------------------------------
      //                       VALIDATE INPUT
      // ********************************************************************************
      if (featureToParse.isEmpty())
      {
         return null;
      }
      // Splits string, reads first char(feature name) and calls right feature parser
      String[] splittedString = featureToParse.split(String.valueOf(FEATURE_COMMAND_END_LETTER), 2);
      // First array string contains feature name, that is a char. If length is not 1 therefore all string does not represents a valid or supported feature
      if (splittedString[0].length() != 1)
      {
         return null;
      }
      // ***********************************************************************************

      
      // ------------------------------------------------------------------------------------
      //                         CHOOSE AND CALL FEATURE PARSER
      // ************************************************************************************
      FeatureParser featureParser = null;
      // Choose right feature parser
      switch (splittedString[0].charAt(0))
      {
         case FeatureNames.FEATURE_STATIC:
            featureParser = StaticFeatureParser.getInstance(columsNumber);
            break;
         case FeatureNames.FEATURE_DYNAMIC:
            featureParser = DynamicFeatureParser.getInstance();
            break;
      }
      FeatureValues schema = null;
      if (featureParser != null)
      {
         try
         {
            schema = featureParser.parseFeature(splittedString[1]);
            insertFeatureValuesToGlobalSchema(schema);
         }
         catch (Exception e)
         {
            System.out.println(e);
         }

      }
      return schema;

   }

   /**
    * Return all features values.
    *
    * @return
    */
   public TreeMultimap<Integer, Integer> getGlobalValuesSchema()
   {
      return treeMultimap;
   }

   /**
    * Inserts all feature schema values together with the others (to get all these values call getGlobalValuesSchema()
    */
   private void insertFeatureValuesToGlobalSchema(FeatureValues schema)
   {
      for (int col : schema.getColumns())
      {
         treeMultimap.putAll(col, schema.getRows());
      }
   }

   //TODO: implementare aggiunta di tutte le feature schema ottenute nella treemultimap
   /**
    * Constructor
    *
    * @param numberOfColumns number of columns in the training file, tag column included
    */
   private FeatureSelector(int numberOfColumns)
   {
      if (numberOfColumns < 3)
      {
         throw new IllegalArgumentException("NumberOfColumns must be > 2");
      }
      this.columsNumber = numberOfColumns;
      treeMultimap = TreeMultimap.create();
   }

}
