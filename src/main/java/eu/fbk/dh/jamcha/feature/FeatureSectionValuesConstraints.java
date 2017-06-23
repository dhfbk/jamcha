package eu.fbk.dh.jamcha.feature;

/**
 * Define which values are permitted for a feature section (i.e. in static feature, tag columns cannot be included)
 *
 * @author dan92
 */
public class FeatureSectionValuesConstraints
{
    public final int VALUE_MIN;
    public final int VALUE_MAX;

    /**
     * Constructor
     *
     * @param valueMin
     * @param valueMax
     */
    public FeatureSectionValuesConstraints(int valueMin, int valueMax)
    {
        VALUE_MIN = valueMin;
        VALUE_MAX = valueMax;
    }

    /**
     * Constructor with default values: min=Integer.minValue, max=Integer.maxValue
     */
    public FeatureSectionValuesConstraints()
    {
        this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }
}
