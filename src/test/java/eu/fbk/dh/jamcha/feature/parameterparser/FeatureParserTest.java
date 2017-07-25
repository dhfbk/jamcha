package eu.fbk.dh.jamcha.feature.parameterparser;

import eu.fbk.dh.jamcha.feature.parameterparser.FeatureParser;
import eu.fbk.dh.jamcha.feature.parameterparser.StaticFeatureParser;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import eu.fbk.dh.jamcha.feature.*;

/**
 *
 * @author dan92
 */
public class FeatureParserTest
{

    public FeatureParserTest()
    {
    }

    @BeforeClass
    public static void setUpClass()
    {
    }

    @AfterClass
    public static void tearDownClass()
    {
    }

    @Before
    public void setUp()
    {
    }

    @After
    public void tearDown()
    {
    }

    /**
     * Test of parseSection method, of class FeatureParser.
     */
    @Test
    public void testParseSection()
    {
       int columnsNumber=5;
        System.out.println("parseSection");
        String section = "-4..-2";
        Integer[] arr =
        {
            -4, -3, -2
        };
        FeatureSectionValuesConstraints constraints = new FeatureSectionValuesConstraints(-8, -1);
        FeatureParser instance = StaticFeatureParser.getInstance(columnsNumber);
        List<Integer> expResult = Arrays.asList(arr);
        List<Integer> result = instance.parseSection(section, constraints);
        assertEquals(expResult, result);
    }

}
