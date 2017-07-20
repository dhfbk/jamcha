package eu.fbk.dh.jamcha.feature;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Singleton Class that represents and manage static feature (F).
 *
 * @author dan92
 */
public final class StaticFeatureParser extends FeatureParser
{
    private static StaticFeatureParser parser = null;

    private StaticFeatureParser()
    {
        super("F", 1, new FeatureSectionValuesConstraints(Integer.MIN_VALUE, Integer.MAX_VALUE), new FeatureSectionValuesConstraints(Integer.MIN_VALUE, -1));
    }

    public static StaticFeatureParser getInstance()
    {
        if (parser == null)
        {
            parser = new StaticFeatureParser();
        }
        return parser;
    }

   @Override
   protected FeatureSchema createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureSchema schema=new FeatureSchema();
      
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
