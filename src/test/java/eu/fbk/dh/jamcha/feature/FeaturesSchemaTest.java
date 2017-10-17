package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.dh.jamcha.feature.fileReader.FeatureFileReaderTest;
import eu.fbk.dh.jamcha.feature.fileReader.TrainFileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

public class FeaturesSchemaTest
{
    private FeaturesSchema schema;

    public FeaturesSchemaTest() throws IOException
    {
        Path filePath = FeatureFileReaderTest.getResourceFilePath(FeatureFileReaderTest.DEFAULT_TRAIN_FILE_PATH.toString());
        TrainFileReader reader = new TrainFileReader(filePath);
        schema = reader.read();
    }

    @Test
    public void testIntegrate() throws IOException
    {
        // Load correct integrated features
        List<Line> correctIntegrated = loadTestIntegratedFeatures();
        {
            FeatureParametersTest testParams = new FeatureParametersTest();
            FeatureParameters params = FeatureParameters.build(testParams.featuresList, testParams.columnsCount);
            schema.integrate(params);
            for (int i = 0; i < correctIntegrated.size(); i ++)
            {

                schema.getIntegratedFeatures().get(i).getWords().sort(null);
                correctIntegrated.get(i).getWords().sort(null);
            }
        }
        Assert.assertEquals(correctIntegrated, schema.getIntegratedFeatures());

    }

    private List<Line> loadTestIntegratedFeatures() throws IOException
    {
        ArrayList<Line> retval = new ArrayList<>();
        Path tmp = FeatureFileReaderTest.getResourceFilePath("IntegratedFeatures.txt");
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
                String tag = lineWords[lineWords.length - 2];

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

//    @Test
//    public void testGetTagByIndex()
//    {
//        //TODO: implementare
//    }
//
//    @Test
//    public void testGetTagIndex()
//    {
//        //TODO: implementare
//    }
}
