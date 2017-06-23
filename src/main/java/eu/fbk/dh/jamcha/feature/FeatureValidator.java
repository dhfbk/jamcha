package eu.fbk.dh.jamcha.feature;

import java.util.ArrayList;

/**
 * Abstract class that represents a feature: NAME, pattern, values constraints. It reads feature string and extracts rows and columns
 *
 * @author dan92
 */
public abstract class FeatureValidator
{
    /**
     * Feature NAME(command)
     */
    protected final String NAME;

    /**
     * Feature section separator (example ":")
     */
    protected final char SECTION_SEPARATOR;

    /**
     * Feature values separator within same section (value may not be in order and may not be consecutive)
     */
    protected char VALUE_SEPARATOR;

    /**
     * Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1)
     */
    protected String RANGE_VALUE_SEPARATOR;

    /**
     * Number of sections of this feature. A section is a part of string divided by "section separator"
     */
    protected int SECTION_SEPARATORS_COUNT;

    /**
     * List of constraints for every section of feature. First element of this list represents the first sections constraints and so on
     */
    protected ArrayList<FeatureSectionValuesConstraints> sectionValueConstraintsList;

    /**
     * Constructor
     *
     * @param name                   feature name(command)
     * @param sectionSeparator       Feature section separator (example ":")
     * @param valueSeparator         Feature values separator within same section (value may not be in order and may not be consecutive)
     * @param rangeValueSeparator    Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1)
     * @param sectionSeparatorCount  Number of sections of this feature. A section is a part of string divided by "section separator"
     * @param listSectionConstraints Costraints for every section of this feature. First element of this list represents the first sections constraints and so on. Can be NULL if none of the
     *                               sections have any restriction
     *
     * @exception IllegalArgumentException If number of constraints is not sectionSeparatorCount + 1
     */
    public FeatureValidator(final String name, final char sectionSeparator, final char valueSeparator, final String rangeValueSeparator, final int sectionSeparatorCount, final FeatureSectionValuesConstraints... listSectionConstraints)
    {
        //TODO: aggiungere numero massimo colonne da prendere dall'esterno
        if (name == null || sectionSeparatorCount < 0)
        {
            this.NAME = name;
        }
        this.SECTION_SEPARATOR = sectionSeparator;
        this.VALUE_SEPARATOR = valueSeparator;
        this.RANGE_VALUE_SEPARATOR = rangeValueSeparator;
        this.SECTION_SEPARATORS_COUNT = sectionSeparatorCount;
        sectionValueConstraintsList = new ArrayList<FeatureSectionValuesConstraints>();

        // If there are no constraints will be generated default constraints
        if (listSectionConstraints == null)
        {
            for (int i = 0; i < SECTION_SEPARATORS_COUNT; i ++)
            {
                sectionValueConstraintsList.add(new FeatureSectionValuesConstraints());
            }

        }
        else
        {

            if (listSectionConstraints.length != SECTION_SEPARATORS_COUNT + 1)
            {
                throw new IllegalArgumentException("Constraints must be equal to sectionSeparatorCount + 1");
            }
        }
        for (FeatureSectionValuesConstraints constraints : listSectionConstraints)
        {
            sectionValueConstraintsList.add(constraints);
        }
    }

    /**
     * Parse and validate feature values
     *
     * @param stringToParse list of values, written using feature pattern
     *
     * @return
     *
     * @exception IllegalArgumentException stringToParse is null or empty
     * @exception Exception                invalid feature pattern
     */
    public FeatureSchema parseFeature(String stringToParse) throws Exception, IllegalArgumentException
    {
        if (stringToParse == null || stringToParse.isEmpty())
        {
            throw new IllegalArgumentException("String must be not null and not empty");
        }

        // Split string on character separator (e.g. : )
        String[] typeList = stringToParse.split(String.valueOf(SECTION_SEPARATOR));

        // Creates feature pattern to show in error message
        if (typeList.length != SECTION_SEPARATORS_COUNT)
        {
            throw new Exception("Feature " + NAME + " must have the form: " + createStringSchema());
        }

        //TODO: implementare lettura delle sottostringhe e generare lista di righe e colonne
    }

    /**
     * Creates feature pattern as string (featureName:values:values:values)
     *
     * @return
     */
    protected String createStringSchema()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(NAME);
        builder.append(':');
        for (int i = 0; i < SECTION_SEPARATORS_COUNT; i ++)
        {
            builder.append("values" + SECTION_SEPARATOR);
        }

        // Deletes last char appendend (the section separator)
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
