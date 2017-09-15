package eu.fbk.dh.jamcha.parameterparser.feature;

import javax.annotation.Nonnull;

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

    /**
     * Parameter value satisfies restrictions?
     *
     * @param value value that must satisfies restrictions
     *
     * @return
     */
    public boolean isValid(int value)
    {
        return value >= VALUE_MIN && value <= VALUE_MAX;
    }

    /**
     * Creates an error message using value parameter
     *
     * @param value value that may be not valid
     *
     * @return error message if value is not valid, empty string otherwise
     */
    public @Nonnull
    String errorMessage(int value)
    {
        String error;
        if ( ! isValid(value))
        {
            error = "Value must be >= " + VALUE_MIN + " and <= " + VALUE_MAX;
        }
        else
        {
            error = "";
        }
        return error;
    }
}
