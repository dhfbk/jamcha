package eu.fbk.dh.jamcha.parameterparser.feature;

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
   private final static int ROWS_MAX_VALUE = 500;

   private StaticFeatureParser(int totalColumns)
   {
      super("F", 1, new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, ROWS_MAX_VALUE), new FeatureSectionValuesConstraints(ROWS_MIN_VALUE, totalColumns - 2));
   }

   /**
    *
    * @param numberOfAllColumns number(count) of columns, tag column included
    *
    * @return instance of StaticFeatureparser
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
   protected FeatureParameters createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureParameters schema = new FeatureParameters();

      // Parse each section: first is rows section, second columns section
      // ROWS
      List<Integer> listToAdd = parseSection(listOfSections[0], sectionValueConstraintsList.get(0));
      schema.setRows(listToAdd);

      // COLUMNS
      listToAdd = parseSection(listOfSections[1], sectionValueConstraintsList.get(1));;
      schema.setColumns(listToAdd);

      return schema;
   }
}
