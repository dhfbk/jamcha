/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature;

import java.util.ArrayList;
import java.util.Arrays;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureSelectorTest
{
   private static FeatureSelector selector;
   public FeatureSelectorTest()
   {
   }
   
   @BeforeClass
   public static void setUpClass()
   {
      selector=FeatureSelector.getInstance(5);
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
    * Test of parseFeature method, of class FeatureSelector.
    */
   @Test
   public void testParseFeature()
   {
      System.out.println("parseFeature");
      String featureToParse = "F:-2,-1,3,5:-5..-2";
      
      Integer[] expRows={-2,-1,3,5};
      Integer[] expCols = {-5,-4,-3,-2};
      
      FeatureValues expVal= new FeatureValues();
      expVal.setRows(Arrays.asList(expRows));
      expVal.setColumns(Arrays.asList(expCols));
      FeatureValues resultValues=selector.parseFeature(featureToParse);
      boolean rowsFlag= resultValues.getRows().equals(expVal.getRows());
      boolean colsFlag= resultValues.getColumns().equals(expVal.getColumns());
      assertSame(true, rowsFlag&&colsFlag);
   }
   
}
