/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import eu.fbk.dh.jamcha.feature.FeatureSelector;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureIntegratorTest
{
   
   FeatureIntegrator integrator;
   FeatureSelector selector;
   
   public FeatureIntegratorTest()
   {
      FeatureFileReader reader= new File
      selector= 
      integrator=new FeatureIntegrator(featuresParameters, tokensFeatures);
   }   

   /**
    * Test of integrateTokensFeatures method, of class FeatureIntegrator.
    */
   @Test
   public void testIntegrateTokensFeatures()
   {
      System.out.println("integrateTokensFeatures");
      FeatureIntegrator instance = null;
      instance.integrateTokensFeatures();
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }

   /**
    * Test of extractDefaultFeatures method, of class FeatureIntegrator.
    */
   @Test
   public void testExtractDefaultFeatures()
   {
      System.out.println("extractDefaultFeatures");
      int row = 0;
      FeatureIntegrator instance = null;
      List<FeatureInfo> expResult = null;
      List<FeatureInfo> result = instance.extractDefaultFeatures(row);
      assertEquals(expResult, result);
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }

   /**
    * Test of fromColRowsToRowCols method, of class FeatureIntegrator.
    */
   @Test
   public void testFromColRowsToRowCols()
   {
      System.out.println("fromColRowsToRowCols");
      FeatureIntegrator instance = null;
      ListMultimap<Integer, Integer> expResult = null;
      ListMultimap<Integer, Integer> result = instance.fromColRowsToRowCols();
      assertEquals(expResult, result);
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }

   /**
    * Test of getLineFeatures method, of class FeatureIntegrator.
    */
   @Test
   public void testGetLineFeatures()
   {
      System.out.println("getLineFeatures");
      int requestedline = 0;
      int baseLine = 0;
      List<Integer> featsNumbers = null;
      FeatureIntegrator instance = null;
      List<FeatureInfo> expResult = null;
      List<FeatureInfo> result = instance.getLineFeatures(requestedline, baseLine, featsNumbers);
      assertEquals(expResult, result);
      // TODO review the generated test code and remove the default call to fail.
      fail("The test case is a prototype.");
   }
   
}
