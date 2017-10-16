package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.fileReader.FeatureFileReaderTest;
import eu.fbk.dh.jamcha.feature.fileReader.TrainFileReader;
import java.io.IOException;
import java.nio.file.Path;
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
    public void testIntegrate()
    {

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
