/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature;

import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
        System.out.println("parseSection");
        String section = "-4..";
        Integer[] arr =
        {
            -4, -3, -2, -1
        };
        FeatureSectionValuesConstraints constraints = new FeatureSectionValuesConstraints(-8, -1);
        FeatureParser instance = StaticFeatureParser.getInstance();
        List<Integer> expResult = Arrays.asList(arr);
        List<Integer> result = instance.parseSection(section, constraints);
        assertEquals(expResult, result);
    }

}
