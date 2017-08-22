/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature.filereader;
import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import eu.fbk.dh.jamcha.feature.FeatureParserSelectorTest;
import java.io.File;
import java.io.IOException;
import java.util.List;
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
//      reader = new FeatureFileReader(FeatureFileReaderTest.fileTestPath);
//      reader.parseFile();
      File defaultFeaturesFile=FeatureFileReaderTest.loadFileFromResources(FeatureFileReaderTest.fileTestPath.toString());
      
      selectorTest = new FeatureParserSelectorTest();
      integrator = new FeatureIntegrator(selectorTest.getFeaturesParameters(), FeatureFileReaderTest.parseTestFile(defaultFeaturesFile));
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
      ListMultimap<Integer, FeatureInfo> correctIntegratedFeatures = FeatureFileReaderTest.parseTestFile(fileIntegratedTest);
      transformParsedIntegratedFileIntoValidMap(correctIntegratedFeatures);
      ListMultimap<Integer, FeatureInfo> attemptIntegratedFeatures = integrator.getIntegratedtFeaturesMap();

      boolean retval = true;
      for (int key : correctIntegratedFeatures.keySet())
      {
         if(retval==false)
         {
            break;
         }
         for (FeatureInfo featInfo : correctIntegratedFeatures.get(key))
         {
            List<FeatureInfo> keyCorrectFeatures=correctIntegratedFeatures.get(key);
            List<FeatureInfo> keyAttemptFeatures=attemptIntegratedFeatures.get(key);
            if (keyCorrectFeatures.size()== keyAttemptFeatures.size() && keyAttemptFeatures.contains(featInfo))
            {
               retval = true;
            }
            else
            {
               retval = false;
               break;
            }
         }
         if (retval == false)
         {
            break;
         }
      }
      assertEquals(true, retval);
   }

   /**
    * Trasforma la map del file di prova con le fatures integrate in una map confrontabile con la map generata dal metodo
    * FeatureIntegrator.integrateTokensFeatures()
    *
    * @param parsedFileListFeatures
    */
   private void transformParsedIntegratedFileIntoValidMap(ListMultimap<Integer, FeatureInfo> parsedFileListFeatures)
   {
      for (int row : parsedFileListFeatures.keySet())
      {
         for (FeatureInfo info : parsedFileListFeatures.get(row))
         {
            String[] values = info.getFeatureValue().split("_");
            info.setRow(Integer.valueOf(values[0]));
            info.setColumn(Short.valueOf(values[1]));
            info.setValue(values[2]);
         }
      }
   }

}
