package eu.fbk.dh.jamcha.parameterparser.feature;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Singleton Class that represents and manage dynamic feature (T)
 *
 * @author dan92
 */
public final class DynamicFeatureParser extends FeatureParser
{

   private static DynamicFeatureParser parser = null;

   /**
    * In dynamic feature we consider only one column, tag column. This is an arbitrary value. Do not choose a value greather or equal to 0 because those values are reserved to features
    * columns.
    */
   public final static int COLUMN_VALUE = -1;

   private DynamicFeatureParser()
   {
      super("T", 0, new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, -1));
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
    * Creates list of rows and columns for dynamic feature. The behavior for lines values is the default. Columns have only one value (COLUMN_VALUE)
    *
    * @param listOfSections list of sections, each written using feature pattern
    *
    * @return all values for rows and columns (in other words, a matrix)
    */
   @Override
   @Nonnull
   protected FeatureParameters createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureParameters schema = new FeatureParameters();

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
