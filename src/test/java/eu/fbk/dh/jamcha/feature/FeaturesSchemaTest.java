package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.Line;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Assert;
import org.junit.Test;

public class FeaturesSchemaTest
{
   private FeaturesIntegrator schema;
   private final FeatureParameters params;
   private FeatureParametersTest paramsTest;

   public FeaturesSchemaTest() throws IOException
   {
      Path filePath = FeatureFileReaderTest.getResourceFilePath(FeatureFileReaderTest.DEFAULT_TRAIN_FILE_PATH.toString());
      TrainFileReader reader = new TrainFileReader(filePath);
      paramsTest = new FeatureParametersTest();
      params = FeatureParameters.build(paramsTest.featuresList, paramsTest.columnsCount);
      schema = new FeaturesIntegrator(reader, params);
   }

   @Test
   public void testIntegrate() throws IOException
   {
      // TEST INTEGRATE TRAIN FILE
      // Load correct integrated features
      List<Line> correctIntegrated = loadTestIntegratedFeatures("IntegratedFeatures.txt");

      schema.integrate(null);
      for (int i = 0; i < correctIntegrated.size(); i ++)
      {
         correctIntegrated.get(i).getWords().sort(null);
      }
      Assert.assertEquals(correctIntegrated, schema.getIntegratedFeatures());

      // TEST INTEGRATE PREDICT FILE
      correctIntegrated = loadTestIntegratedFeatures("IntegratedFeaturesPredict.txt");

      Path filePath = FeatureFileReaderTest.getResourceFilePath(FeatureFileReaderTest.DEFAULT_PREDICT_FILE_PATH.toString());
      PredictFileReader predictReader = new PredictFileReader(filePath, paramsTest.columnsCount);
      schema = new FeaturesIntegrator(predictReader, params);
      schema.integrate(null);
      for (int i = 0; i < correctIntegrated.size(); i ++)
      {
         correctIntegrated.get(i).getWords().sort(null);
      }
//      for (int i = 0; i < correctIntegrated.size(); i ++)
//      {
//         Line correctLine = correctIntegrated.get(i);
//         Line testLine = schema.getIntegratedFeatures().get(i);
//         if ( ! correctLine.equals(testLine))
//         {
//            break;
//         }
//      }
      Assert.assertEquals(correctIntegrated, schema.getIntegratedFeatures());
   }

   private List<Line> loadTestIntegratedFeatures(@Nonnull String fileName) throws IOException
   {
      ArrayList<Line> retval = new ArrayList<>();
      Path tmp = FeatureFileReaderTest.getResourceFilePath(fileName);
      try (BufferedReader reader = Files.newBufferedReader(tmp))
      {
         String line;
         int lineCounter = 0;

         // Read all lines
         while ((line = reader.readLine()) != null)
         {
            String[] lineWords = line.split(" ");

            // Sequence is last word of line
            int sequence = Integer.parseInt(lineWords[lineWords.length - 1]);

            // Tag is second-last word of line
            String tag = null;
            if ( ! (lineWords[lineWords.length - 2]).equals("null"))
            {
               tag = lineWords[lineWords.length - 2];
            }

            // Add all feature to line except last word (that is sequence index)
            ArrayList<String> features = new ArrayList<>(lineWords.length);
            for (int i = 0; i < lineWords.length - 2; i ++)
            {
               features.add(lineWords[i]);
            }

            Line newLine = new Line(lineCounter, sequence, tag, features);
            retval.add(newLine);
            lineCounter ++;
         }
      }
      return retval;
   }
}