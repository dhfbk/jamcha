/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import eu.fbk.dh.jamcha.feature.FeatureParserSelectorTest;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureIntegratorTest
{

   private FeatureIntegrator integrator;
   private FeatureParserSelectorTest selectorTest;
   private FeatureFileReader reader;

   public FeatureIntegratorTest() throws IOException
   {
      reader = new FeatureFileReader(FeatureFileReaderTest.fileTestPath);
      reader.parseFile();
      selectorTest = new FeatureParserSelectorTest();
      integrator = new FeatureIntegrator(selectorTest.getFeaturesParameters(), reader.getTokensFeatures());
   }

   /**
    * Test of integrateTokensFeatures method, of class FeatureIntegrator.
    */
   @Test
   public void testIntegrateTokensFeatures() throws IOException
   {
      System.out.println("integrateTokensFeatures");
      integrator.integrateTokensFeatures();

      // Lettura file con le features già integrate
      File fileIntegratedTest = new FeatureFileReaderTest().loadFileFromResources("IntegratedFeatures.txt");
      ListMultimap<Integer, FeatureInfo> expectedFileIntegrated = FeatureFileReaderTest.parseTestFile(fileIntegratedTest);

      boolean retval = true;
      for (int key : expectedFileIntegrated.keySet())
      {
         if (!retval)
         {
            break;
         }
         for (FeatureInfo featInfo : expectedFileIntegrated.get(key))
         {
            if (integrator.getIntegratedtFeaturesMap().get(key).contains(featInfo))
            {
               retval = true;
            }
            else
            {
               retval = false;
               break;
            }
         }
      }
      assertEquals(true, retval);
   }

}
