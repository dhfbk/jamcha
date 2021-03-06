package eu.fbk.dh.jamcha.parameterparser.feature;

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
   protected static final char VALUE_SEPARATOR = ',';

   /**
    * Is similar to "Value separator", but this is used when user indicates a range and not every single value (e.g. every single value -2,-1,0,1 range: -2..1) Delete \\ if not used with
    * String.split() function
    */
   protected static final String RANGE_VALUE_SEPARATOR = "\\.\\.";

   /**
    * Number of sections of this feature. A section is a part of string divided by "section separator"
    */
   protected int SECTION_SEPARATORS_COUNT;

   protected final static int ROWS_MIN_VALUE = -500;

   /**
    * List of constraints for every section of feature. First element of this list represents the first sections constraints and so on
    */
   protected ArrayList<FeatureSectionValuesConstraints> sectionValueConstraintsList;

   /**
    * Constructor
    *
    * @param name                   feature name(command)
    * @param sectionSeparatorCount  Number of sections of this feature. A section is a part of string divided by "section separator"
    * @param listSectionConstraints Costraints for every section of this feature. First element of this list represents the first sections constraints and so on. Can be NULL if none of the
    *                               sections have any restriction
    *
    * @exception IllegalArgumentException If number of constraints is not sectionSeparatorCount + 1
    */
   protected FeatureParser(@Nonnull final String name,
                           final int sectionSeparatorCount,
                           final FeatureSectionValuesConstraints... listSectionConstraints)
   {
      if (sectionSeparatorCount < 0)
      {
         throw new IllegalArgumentException("SectionSeparatorCount must be >= zero");
      }
      this.NAME = name;
      this.SECTION_SEPARATORS_COUNT = sectionSeparatorCount;
      sectionValueConstraintsList = new ArrayList<>();

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
    * Parse and obtains feature values
    *
    * @param stringToParse list of values, written using feature pattern without feature name. E.g. Considering F:-5..3:-3..-1 , substring passed as parameter is -5..3:-3..-1
    *
    * @return All lines and columns values for this feature
    *
    * @exception IllegalArgumentException stringToParse is empty or invalid feature pattern
    * @exception Exception                invalid feature pattern
    */
   @Nonnull
   public FeatureParameters parseFeature(@Nonnull String stringToParse) throws Exception, IllegalArgumentException
   {
      if (stringToParse.isEmpty())
      {
         throw new IllegalArgumentException("String must be not empty");
      }

      // Split string on character separator (e.g. : ) creating a list of sections
      String[] typeList = stringToParse.split(String.valueOf(SECTION_SEPARATOR));

      // Creates feature pattern to show in error message
      if (typeList.length != SECTION_SEPARATORS_COUNT + 1)
      {
         throw new IllegalArgumentException("Feature " + NAME + " must have the form: " + createStringSchema());
      }

      // Creates values matrix for this feature and return it
      return createValuesSchema(typeList);
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
   protected List<Integer> parseSection(@Nonnull String section, @Nonnull FeatureSectionValuesConstraints constraints) throws NumberFormatException, IllegalArgumentException
   {
      if (section.isEmpty())
      {
         throw new IllegalArgumentException("Section to parse cannot be empty");
      }

      ArrayList<Integer> list = new ArrayList<>();

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

            // If minValue and maxValue are valid, will be created and added to list all numbers from minValue to maxValue (a sequence from                    minValue to maxValue)
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
   }

   /**
    * Creates feature pattern as string (featureName:values:values:values)
    *
    * @return how to write correct feature string
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

   /**
    * Creates list of rows and columns for a specific feature. Therefore, every feature must implement this method and create a valid list of rows and columns values according to own
    * constraints (Dynamic feature T cannot have multiple columns). Please call this method from parseFeature().
    *
    * @param listOfSections list of sections, each written using feature pattern. ATTENTION: Size is not controlled.(there is no control if number of passed sections respects feature
    *                       pattern)
    *
    * @return all values for rows and columns (in other words, a matrix)
    */
   @Nonnull
   protected abstract FeatureParameters createValuesSchema(String[] listOfSections);

   protected List<FeatureSectionValuesConstraints> getConstraintsList()
   {
      return sectionValueConstraintsList;
   }
}
