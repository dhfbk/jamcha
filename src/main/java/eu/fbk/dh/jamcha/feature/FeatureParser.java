package eu.fbk.dh.jamcha.feature;

/**
 * Class that manage feature parsing and trasformation.
 *
 * @author dan92
 */
public class FeatureParser
{
    /**
     * Static feature constant (all columns except tag)
     */

    static final char STATIC_FEATURE_LETTER = 'F';

    /**
     * Dynamic feature constant (only tag column)
     */
    static final char DYNAMIC_FEATURE_LETTER = 'T';

    private static FeatureParser parser = null;

    public static FeatureParser getInstance()
    {
        if (parser == null)
        {
            parser = new FeatureParser();
        }
        return parser;
    }

    //TODO: implementare parsing iniziale della feature(del comando) e chiamata al parser specifico e ritorno della FeatureSchema
    //TODO: implementare aggiunta dei tutte le feature schema ottenute nella treemultimap
    private FeatureParser()
    {

    }
}
