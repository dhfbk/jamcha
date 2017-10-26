package eu.fbk.dh.jamcha;

import eu.fbk.dh.jamcha.data.DataIO;
import eu.fbk.dh.jamcha.data.Model;
import eu.fbk.dh.jamcha.feature.FeatureFileReader;
import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.FeaturesSchema;
import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.dh.jamcha.feature.PredictFileReader;
import eu.fbk.dh.jamcha.feature.TrainFileReader;
import eu.fbk.dh.jamcha.parametersReader.ParametersReader;
import eu.fbk.dh.jamcha.parametersReader.TrainParametersReader;
import java.io.IOException;
import java.util.List;

public class Main
{
   private static final int MODE = 1;
   private static final String[] trainArguments =
   {
      "train", "CORPUS=/home/mazzetti/Documents/DefaultTrain.txt", "MODEL=/home/mazzetti/Documents", "FEATURES=F:-2..1:0..1 F:-3..-2:1..2 T:-4..-1 T:-5..-3"
   };
   private static final String[] predictArguments =
   {
      "predict", "CORPUS=/home/mazzetti/Documents/DefaultPredict.txt", "MODEL=/home/mazzetti/Documents/Data"
   };

   public static void main(String[] args)
   {
      //args = MODE == 0 ? trainArguments : predictArguments;

      ParametersReader paramsReader = ParametersReader.build(args);
      ParametersReader.COMMAND_TYPE paramsType = paramsReader.readParameters(args);
      FeatureFileReader fileReader;
      try
      {
         switch (paramsType)
         {
            case COMMAND_TRAIN:
            {
               // Read train file
               fileReader = new TrainFileReader(paramsReader.getCorpusPath());
               fileReader.read();

               // Build feature parameters
               TrainParametersReader trainParams = (TrainParametersReader) paramsReader;
               FeatureParameters featureParameters = FeatureParameters.build(trainParams.getRawFeaturesParameters(), fileReader.getLineWordsCount());

               // Build feature schema and integrate
               FeaturesSchema schema = new FeaturesSchema(fileReader.getFeatures(), featureParameters);
               schema.integrate(null);

               // Train features and save data
               Model model = Model.train(schema.getIntegratedFeatures());
               DataIO data = new DataIO(model, featureParameters, trainParams.getModelPath());
               data.save();
               break;
            }
            case COMMAND_PREDICT:
            {
               // Load saved data
               DataIO data = DataIO.load(paramsReader.getModelPath());

               // Read features file to predict
               fileReader = new PredictFileReader(paramsReader.getCorpusPath(), data.getFeatureParameters().getWordsLineCount());
               fileReader.read();

               // Build feature schema and integrate
               FeaturesSchema schema = new FeaturesSchema(fileReader.getFeatures(), data.getFeatureParameters());
               schema.integrate(null);

               // Predict tags and save data
               List<Line> features = schema.getIntegratedFeatures();
               if (features != null)
               {
                  List<Line> predictedLines = data.getModel().predict(features);
                  DataIO.saveFeatures(predictedLines, paramsReader.getModelPath());
               }
               break;
            }
         }
      }
      catch (IOException e)
      {
         System.out.println(e.getLocalizedMessage());
         System.exit(1);
      }
   }
}
