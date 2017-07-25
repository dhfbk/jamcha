package eu.fbk.dh.jamcha.feature.parameterparser;

import eu.fbk.dh.jamcha.feature.FeatureValues;
import eu.fbk.dh.jamcha.feature.FeatureSectionValuesConstraints;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Singleton Class that represents and manage static feature (F)
 *
 * @author dan92
 */
public final class StaticFeatureParser extends FeatureParser
{

   private static StaticFeatureParser parser = null;
   private final static int ROWS_MIN_VALUE = -500;
   private final static int ROWS_MAX_VALUE = 500;

   private StaticFeatureParser(int totalColumns)
   {
      super("F", 1, new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, ROWS_MAX_VALUE), new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, totalColumns-2));
   }

   /**
    * 
    * @param numberOfAllColumns number(count) of columns, tag column included
    * @return 
    */
   public static StaticFeatureParser getInstance(int numberOfAllColumns)
   {
//      if(numberOfAllColumns<3)
//      {
//         throw new IllegalArgumentException("Parameter must be >= 3");
//      }
      if (parser == null)
      {
         parser = new StaticFeatureParser(numberOfAllColumns);
      }
      return parser;
   }

   @Override
   protected FeatureValues createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureValues schema = new FeatureValues();

      // Parse each section: first is rows section, second columns section
      List<Integer> listToAdd = parseSection(listOfSections[0], sectionValueConstraintsList.get(0));
      schema.setRows(listToAdd);

      // COLUMN
      listToAdd = parseSection(listOfSections[1], sectionValueConstraintsList.get(1));;
      schema.setColumns(listToAdd);
//      for(int i=0; i<SECTION_SEPARATORS_COUNT+1;i++)
//      {
//         List<Integer> listToAdd= parseSection(listOfSections[i], sectionValueConstraintsList.get(i));
//         schema.addList(listToAdd, i);
//      }
      return schema;
   }
}
