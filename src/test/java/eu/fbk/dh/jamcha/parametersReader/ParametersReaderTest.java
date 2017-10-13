package eu.fbk.dh.jamcha.parametersReader;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

public class ParametersReaderTest
{
   private final Path MODEL;
   private final Path CORPUS;
   private final String FEATURES = "F:-2..1:0..1 F:-3..-2:1..2 T:-4..-1 T:-5..-3";
   private String[] paramsTrain;
   private String[] paramsPredict;

   public ParametersReaderTest()
   {
      this.MODEL = Paths.get("MODELPath");
      this.CORPUS = Paths.get("CORPUSPath");
      createTrainParameters();
   }

   @Test
   public void testReadParameters()
   {
      // TRAIN PARAMETERS TEST: CORRECT PARAMETERS
      ParametersReader reader = ParametersReader.build(paramsTrain);
      reader.readParameters(paramsTrain);
      TrainParametersReader trainReader = (TrainParametersReader) reader;
//      boolean flag = trainReader.MODEL_PATH.equals(this.MODEL);
//      flag = flag && trainReader.CORPUS_PATH.equals(this.CORPUS);
//      flag = flag && trainReader.getRawFeaturesParameters().equals(this.FEATURES);
      Assert.assertEquals(this.MODEL, trainReader.MODEL_PATH);
      Assert.assertEquals(this.CORPUS, trainReader.CORPUS_PATH);
      Assert.assertEquals(this.FEATURES, trainReader.getRawFeaturesParameters());

      // TRAIN PARAMETERS TEST: NOT CORRECT PARAMETERS
      boolean errorOccurred = false;
      try
      {
         testReadParametersWithException();
      }
      catch (IllegalArgumentException e)
      {
         errorOccurred = true;
      }
      Assert.assertEquals(true, errorOccurred);

      //TODO: aggiungere test predict parameters
   }

   private void testReadParametersWithException() throws IllegalArgumentException
   {
      // TRAIN PARAMETERS TEST: NOT CORRECT PARAMETERS
      String[] wrongParams =
      {
         ParametersReader.TRAIN_COMMAND, ParametersReader.PARAMETER_OPTION.MODEL_OPTION
      };
      ParametersReader reader = ParametersReader.build(wrongParams);
      reader.readParameters(wrongParams);
   }

   private void createTrainParameters()
   {
      ArrayList<String> params = new ArrayList<>();

      // -------------------------------------------------------------
      //                       TRAIN parameters
      // -------------------------------------------------------------
      params.add(ParametersReader.TRAIN_COMMAND);

      // MODEL="ModelPath"
      String param = ParametersReader.PARAMETER_OPTION.MODEL_OPTION + "=" + this.MODEL;
      params.add(param);

      // CORPUS="CorpusPath"
      param = ParametersReader.PARAMETER_OPTION.CORPUS_OPTION + "=" + this.CORPUS;
      params.add(param);
      // FEATURES=".........."
      param = ParametersReader.PARAMETER_OPTION.FEATURES_OPTION + "=" + this.FEATURES;
      params.add(param);

      paramsTrain = new String[params.size()];
      paramsTrain = params.toArray(paramsTrain);
   }
}
