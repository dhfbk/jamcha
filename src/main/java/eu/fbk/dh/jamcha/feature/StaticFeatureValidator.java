package eu.fbk.dh.jamcha.feature;

/**
 * Singleton Class that represents and manage static feature (F).
 *
 * @author dan92
 */
public final class StaticFeatureValidator extends FeatureValidator
{
    private static StaticFeatureValidator feature = null;

    private StaticFeatureValidator()
    {
        super("F", ':', ',', "..", 1);
    }

    public static StaticFeatureValidator getInstance()
    {
        if (feature == null)
        {
            feature = new StaticFeatureValidator();
        }
        return feature;
    }
}
