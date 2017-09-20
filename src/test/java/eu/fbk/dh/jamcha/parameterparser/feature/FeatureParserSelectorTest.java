package eu.fbk.dh.jamcha.parameterparser.feature;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Test;

public class FeatureParserSelectorTest
{

    private final ArrayList<String> parametersListValues = new ArrayList<>();
    private final TreeMultimap<Integer, Integer> expected = TreeMultimap.create();

    public FeatureParserSelectorTest()
    {
        // ***********************************************************
        //                         PARAMETRI FEATURES
        // ***********************************************************
        String parameter = "F:-2..1:0..1";
        parametersListValues.add(parameter);

        parameter = "F:-3..-2:1..2";
        parametersListValues.add(parameter);

        parameter = "T:-4..-1";
        parametersListValues.add(parameter);

        parameter = "T:-5..-3";
        parametersListValues.add(parameter);

        // ***********************************************************
        //                         VALORI CORRETTI PARAMETRI FEATURES
        // ***********************************************************
        // column 0
        expected.put(0, -2);
        expected.put(0, -1);
        expected.put(0, 0);
        expected.put(0, 1);

        // column 1
        expected.put(1, -3);
        expected.put(1, -2);
        expected.put(1, -1);
        expected.put(1, 0);
        expected.put(1, 1);

        // column 2
        expected.put(2, -3);
        expected.put(2, -2);

        // column 3 (tag)
        expected.put(-1, -5);
        expected.put(-1, -4);
        expected.put(-1, -3);
        expected.put(-1, -2);
        expected.put(-1, -1);

    }

    /**
     * Test of parseFeature method, of class FeatureParserSelector.
     */
    @Test
    public void testParseFeature()
    {
        System.out.println("Selector: parseFeature");
        FeatureParserSelector selector = new FeatureParserSelector(4);
        for (String featParameter : parametersListValues)
        {
            selector.parseFeature(featParameter);
        }
        TreeMultimap<Integer, Integer> actualResult = selector.getGlobalValuesSchema();
        assertEquals(this.expected, actualResult);
    }

    public SortedSetMultimap<Integer, Integer> getFeaturesParameters()
    {
        return this.expected;
    }

}
