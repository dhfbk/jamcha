package eu.fbk.dh.jamcha.feature;

/**
 * Class that manage feature parsing and trasformation.
 *
 * @author dan92
 */
public class FeatureSelector
{
    /**
     * Static feature constant (all columns except tag)
     */

    static final char STATIC_FEATURE_LETTER = 'F';

    /**
     * Dynamic feature constant (only tag column)
     */
    static final char DYNAMIC_FEATURE_LETTER = 'T';

    private static FeatureSelector parser = null;

    public static FeatureSelector getInstance()
    {
        if (parser == null)
        {
            parser = new FeatureSelector();
        }
        return parser;
    }

    //TODO: implementare parsing iniziale della feature(del comando) e chiamata al parser specifico e ritorno della FeatureSchema
    //TODO: implementare aggiunta di tutte le feature schema ottenute nella treemultimap
    private FeatureSelector()
    {

    }
}
