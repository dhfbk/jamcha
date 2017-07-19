package eu.fbk.dh.jamcha.feature;

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
}
