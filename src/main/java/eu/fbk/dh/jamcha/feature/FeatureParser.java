package eu.fbk.dh.jamcha.feature;

import java.util.*;
import javax.annotation.Nonnull;

/**
 * Abstract class that represents a feature: NAME, pattern, values constraints. It reads feature string and extracts rows and columns
 *
 * @author dan92
 */
public abstract class FeatureParser
{
    /**
     * Feature NAME(command)
     */
    protected final String NAME;

    /**
     * Feature section separator (example ":")
     */
    protected static final char SECTION_SEPARATOR = ':';

    /**
     * Feature values separator within same section (value may not be in order and may not be consecutive)
     */
    protected static char VALUE_SEPARATOR = ',';

    /**
     * Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1) Delete \\
     * if not used with String.split() function
     */
    protected static final String RANGE_VALUE_SEPARATOR = "\\..";

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
     * @param sectionSeparatorCount  Number of sections of this feature. A section is a part of string divided by "section separator"
     * @param listSectionConstraints Costraints for every section of this feature. First element of this list represents the first sections constraints and so on. Can be
     *                               NULL if none of the sections have any restriction
     *
     * @exception IllegalArgumentException If number of constraints is not sectionSeparatorCount + 1
     */
    public FeatureParser(@Nonnull final String name,
                         final int sectionSeparatorCount,
                         final FeatureSectionValuesConstraints... listSectionConstraints)
    {
        if (sectionSeparatorCount < 0)
        {
            throw new IllegalArgumentException("SectionSeparatorCount must be >= zero");
        }
        this.NAME = name;
        this.SECTION_SEPARATORS_COUNT = sectionSeparatorCount;
        sectionValueConstraintsList = new ArrayList<FeatureSectionValuesConstraints>();

        // 
        // If there are no constraints, will be generated default constraints
        if (listSectionConstraints == null)
        {
            for (int i = 0; i < SECTION_SEPARATORS_COUNT; i ++)
            {
                sectionValueConstraintsList.add(new FeatureSectionValuesConstraints());
            }

        }
        else
        {
            // If number of constraints is not equal of feature section count, throws an exception
            if (listSectionConstraints.length != SECTION_SEPARATORS_COUNT + 1)
            {
                throw new IllegalArgumentException("Constraints must be equal to sectionSeparatorCount + 1");
            }
            else
            {
                // Constraints are good
                sectionValueConstraintsList.addAll(Arrays.asList(listSectionConstraints));
            }
        }
    }

    /**
     * Parse and isValid feature values
     *
     * @param stringToParse list of values, written using feature pattern
     *
     * @return
     *
     * @exception IllegalArgumentException stringToParse is empty
     * @exception Exception                invalid feature pattern
     */
    @Nonnull
    public FeatureSchema parseFeature(@Nonnull String stringToParse) throws Exception, IllegalArgumentException
    {
        if (stringToParse.isEmpty())
        {
            throw new IllegalArgumentException("String must be not empty");
        }

        // Split string on character separator (e.g. : )
        String[] typeList = stringToParse.split(String.valueOf(SECTION_SEPARATOR));

        // Creates feature pattern to show in error message
        if (typeList.length != SECTION_SEPARATORS_COUNT)
        {
            throw new Exception("Feature " + NAME + " must have the form: " + createStringSchema());
        }

        //TODO: implementare lettura delle sottostringhe e generare lista di righe e colonne
        return null;
    }

    /**
     *
     * @param section     feature section to parse
     * @param constraints section constraints
     *
     * @return section values list
     *
     * @throws NumberFormatException    this section does not respects right pattern
     * @throws IllegalArgumentException this section is empty or uses invalid value
     */
    @Nonnull
    public List<Integer> parseSection(@Nonnull String section, @Nonnull FeatureSectionValuesConstraints constraints) throws NumberFormatException, IllegalArgumentException
    {
        if (section.isEmpty())
        {
            throw new IllegalArgumentException("Section to parse cannot be empty");
        }

        ArrayList<Integer> list = new ArrayList<Integer>();

        String[] valuesList = section.split(String.valueOf(VALUE_SEPARATOR));
        // Section contains multiple values? If length > 1 yes
        if (valuesList.length > 1)
        {
            // Condition: a section may contain multiple single values or a range values, but not both (e.g. not x,x,x and ..). Therefore must have one of these three
            // patterns ( 1) x,x,x or 2) x..x or 3) x.. ).
            // A malformed string of valuesList satisfies one of these cases: (remember that at this point there are no ','
            // 1) there are '..' -> ERROR (parseInt fails) Explanation: valuesList length is > 1 therefore there is at least one ','. This violates initial condition.
            // 2) is empty -> ERROR (parseInt fails) Explanation: this happens when count of ',' is >= count of values (not digits)

            // Add all values to list
            for (String str : valuesList)
            {
                int number = Integer.parseInt(str);
                if (constraints.isValid(number))
                {
                    list.add(number);
                }
                else
                {
                    throw new IllegalArgumentException(constraints.errorMessage(number));
                }
            }
        }
        else
        {
            // Section contains no ',' therefore it must contains a RANGE_VALUE_SEPARATOR.
            valuesList = section.split(RANGE_VALUE_SEPARATOR, -1);

            // If length is not 2 range pattern (x..x or x..) is not satisfied therefore feature is malformed and there is an exception
            if (valuesList.length == 2)
            {
                // There may be two cases:
                // 1) the pattern is x..x -> both valuesList strings have length > 0
                // 2) the pattern is x.. -> first valuesList string has length > 0 and second string have lenth 0.

                // First valuesList string must have valid value otherwise this feature section has invalid pattern and there is an error
                int minValue = Integer.parseInt(valuesList[0]);
                int maxValue;

                // Getting range max value
                // If empty wil be used default max value otherwise will be used parsed value
                if (valuesList[1].isEmpty())
                {
                    maxValue = constraints.VALUE_MAX;
                }
                else
                {
                    maxValue = Integer.parseInt(valuesList[1]);
                }

                // If minValue and maxValue are valid, will be created and added to list all numbers from minValue to maxValue (a sequence from minValue to maxValue)
                if (constraints.isValid(minValue) && constraints.isValid(maxValue))
                {
                    for (int i = minValue; i <= maxValue; i ++)
                    {
                        list.add(i);
                    }
                }
                else
                {
                    int invalid = constraints.isValid(minValue) ? maxValue : minValue;
                    throw new IllegalArgumentException(constraints.errorMessage(invalid));
                }

            }
            else
            {
                throw new IllegalArgumentException("Section range pattern must be: x..x or x..");
            }
        }
        return list;

//        boolean mustBeNumber = true;
//        char[] charArray = section.toCharArray();
//        StringBuilder builder = new StringBuilder();
//
//        for (int i = 0; i < charArray.length; i ++)
//        {
//            char c = charArray[i];
//            if (Character.isDigit(c) || c == '-')
//            {
//                builder.append(c);
//                mustBeNumber = false;
//            }
//            else
//            {
//                if (mustBeNumber)
//                {
//                    throw new ParseException("Malformed section, expected a minValue or '-'", i);
//                }
//
//                if (c == VALUE_SEPARATOR)
//                {
//                    try
//                    {
//                        // Parse int, add it to values list and reset builder
//                        int minValue = Integer.parseInt(builder.toString());
//                        list.add(minValue);
//                        builder.setLength(0);
//                        mustBeNumber = true;
//                    }
//                    catch (NumberFormatException e)
//                    {
//                        // Invalid value
//                        int startIndex = charArray.length - builder.length() - 1;
//                        String error = "Substring of this section, from " + charArray[startIndex] + " to " + charArray[i] + " is not a valid value";
//                        throw new ParseException(error, i);
//                    }
//                }
//                else
//                {
//                    // Is a values range?
//                    if (c == RANGE_VALUE_SEPARATOR.charAt(1) && c == lastChar)
//                    {
//                        
//
//                    }
//                }
//
//            }
//
//        }
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
            builder.append("values").append(SECTION_SEPARATOR);
        }

        // Deletes last char appendend (the section separator)
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }
}
