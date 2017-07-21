package eu.fbk.dh.jamcha.feature.parser;

import eu.fbk.dh.jamcha.feature.FeatureValues;
import eu.fbk.dh.jamcha.feature.FeatureSectionValuesConstraints;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Singleton Class that represents and manage dynamic feature (T)
 *
 * @author dan92
 */
public class DynamicFeatureParser extends FeatureParser
{

   private static DynamicFeatureParser parser = null;
   private final static int ROWS_MIN_VALUE = -500;
   private final static int ROWS_MAX_VALUE = 500;

   /**
    * In dynamic feature we consider only one column, tag column. This is an arbitrary value. Do not choose a value >-1
    * because those values are reserved to features columns.
    */
   private final static int COLUMN_VALUE = -1;

   private DynamicFeatureParser()
   {
      super("T", 0, new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, ROWS_MAX_VALUE));
   }

   public static DynamicFeatureParser getInstance()
   {
      if (parser == null)
      {
         parser = new DynamicFeatureParser();
      }
      return parser;
   }

   /**
    * Creates list of rows and columns for dynamic feature. The behavior for lines values is the default. Columns have only
    * one value (COLUMN_VALUE)
    *
    * @param listOfSections list of sections, each written using feature pattern
    * @return all values for rows and columns (in other words, a matrix)
    */
   @Override
   @Nonnull
   protected FeatureValues createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureValues schema = new FeatureValues();

      // ROWS
      List<Integer> listToAdd = parseSection(listOfSections[0], sectionValueConstraintsList.get(0));
      schema.setRows(listToAdd);
      listToAdd = new ArrayList(1);

      // COLUMN with default value
      listToAdd.add(COLUMN_VALUE);
      schema.setColumns(listToAdd);

      return schema;
   }
}
