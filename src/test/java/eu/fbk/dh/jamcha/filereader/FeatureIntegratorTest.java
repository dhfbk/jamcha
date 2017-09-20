package eu.fbk.dh.jamcha.filereader;

import eu.fbk.dh.jamcha.Row;
import eu.fbk.dh.jamcha.parameterparser.feature.FeatureParserSelectorTest;
import java.io.IOException;
import java.nio.file.Path;
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
   private FeatureFileReaderTest readerTest;

   public FeatureIntegratorTest() throws IOException
   {
      // Caricare file feature deafult
      // Caricare file feature integrate
      // Calcolare feature integrate
      selectorTest = new FeatureParserSelectorTest();
      readerTest = new FeatureFileReaderTest();
      integrator = new FeatureIntegrator(selectorTest.getFeaturesParameters(), readerTest.getDefaultFeatures());
   }

   /**
    * Test of integrateFeatures method, of class FeatureIntegrator.
    *
    * @throws java.io.IOException
    */
   @Test
   public void testIntegrateFeatures() throws IOException
   {
      System.out.println("integrateTokensFeatures");

      integrator.integrateFeatures();

      // Lettura file con le features già  integrate
      Path filePath = FeatureFileReaderTest.getResourceFilePath("IntegratedFeatures.txt");
      List<Row> correctIntegratedFeatures = FeatureFileReaderTest.parseTestFile(filePath);
      List<Row> attemptIntegratedFeatures = integrator.getIntegratedFeatures();

      if (correctIntegratedFeatures.size() == attemptIntegratedFeatures.size())
      {
         for (int i = 0; i < correctIntegratedFeatures.size(); i ++)
         {
            Row attemptRow = attemptIntegratedFeatures.get(i);
            Row correctRow = correctIntegratedFeatures.get(i);

            attemptRow.getFeatures().sort(null);
            correctRow.getFeatures().sort(null);

            if (attemptRow.equals(correctRow) == false)
            {
               System.out.println("Errore riga " + i);
               break;
            }
         }
      }

      assertEquals(correctIntegratedFeatures, attemptIntegratedFeatures);
   }
}
