package eu.fbk.dh.jamcha.feature;

import java.util.List;
import javax.annotation.Nonnull;

/**
 * Singleton Class that represents and manage static feature (F).
 *
 * @author dan92
 */
public final class StaticFeatureParser extends FeatureParser
{
    private static StaticFeatureParser feature = null;

    private StaticFeatureParser()
    {
        super("F", 1, new FeatureSectionValuesConstraints(Integer.MIN_VALUE, Integer.MAX_VALUE), new FeatureSectionValuesConstraints(Integer.MIN_VALUE, -1));
    }

    public static StaticFeatureParser getInstance()
    {
        if (feature == null)
        {
            feature = new StaticFeatureParser();
        }
        return feature;
    }

   @Override
   protected FeatureSchema createValuesSchema(@Nonnull String[] listOfSections)
   {
      FeatureSchema schema=new FeatureSchema();
      for(int i=0; i<SECTION_SEPARATORS_COUNT+1;i++)
      {
         List<Integer> listToAdd= parseSection(listOfSections[i], sectionValueConstraintsList.get(i));
         schema.addList(listToAdd, i);
      }
      return schema;
   }
}
