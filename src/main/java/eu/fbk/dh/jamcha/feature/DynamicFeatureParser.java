package eu.fbk.dh.jamcha.feature;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 *
 * @author echerost
 */
public class DynamicFeatureParser extends FeatureParser
{

   private static DynamicFeatureParser parser = null;
   private final static int COLUMN_VALUE = -1;

   private DynamicFeatureParser()
   {
      super("T", 0, new FeatureSectionValuesConstraints(Integer.MIN_VALUE, -1));
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
    * Creates list of rows and columns for dynamic feature. The behaviour for lines values is the default. Columns have only
    * one value (COLUMN_VALUE)
    * 
    * @param listOfSections list of sections, each written using feature pattern
    * @return all values for rows and columns (in other words, a matrix)
    */
   @Override
   @Nonnull
   protected FeatureSchema createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureSchema schema = new FeatureSchema();
      
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
