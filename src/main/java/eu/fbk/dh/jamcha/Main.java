package eu.fbk.dh.jamcha;

import eu.fbk.dh.jamcha.data.DataIO;
import eu.fbk.dh.jamcha.data.Model;
import eu.fbk.dh.jamcha.feature.FeatureFileReader;
import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.Integrator;
import eu.fbk.dh.jamcha.feature.IntegratorPredictor;
import eu.fbk.dh.jamcha.feature.Line;
import eu.fbk.dh.jamcha.feature.PredictFileReader;
import eu.fbk.dh.jamcha.feature.TrainFileReader;
import eu.fbk.dh.jamcha.parametersReader.ParametersReader;
import eu.fbk.dh.jamcha.parametersReader.TrainParametersReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Main
{
   //"FEATURES=F:-2..1:0..1 F:-3..-2:1..2 T:-4..-1 T:-5..-3"
   private static final int MODE = 0;
   private static final String[] trainArguments =
   {
      "train", "CORPUS=/home/mazzetti//Documents/train.data", "MODEL=/home/mazzetti/Documents", "FEATURES=F:0..0:0.. T:-7..-1"
   };
   private static final String[] predictArguments =
   {
      "predict", "CORPUS=/home/mazzetti/Documents/train.data", "MODEL=/home/mazzetti/Documents"
   };

   public static void main(String[] args)
   {
      //args = MODE == 0 ? trainArguments : predictArguments;
      Logger.getRootLogger().setLevel(Level.OFF);
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

               // Build feature integrator and integrate
               Integrator integrator = new Integrator(fileReader.getLines(), featureParameters);
               integrator.integrate();

               // Train features and save data
               Model model = Model.train(integrator.getIntegratedLines());
               DataIO data = new DataIO(model, featureParameters, trainParams.getModelPath());
               data.save();
               DataIO.saveFeatures(integrator.getIntegratedLines(), paramsReader.getModelPath(), "trainExpansesLines.txt");
               System.out.println("MODEL CREATED!");
               break;
            }
            case COMMAND_PREDICT:
            {
               // Load saved data
               DataIO data = DataIO.load(paramsReader.getModelPath());

               // Read features file to predict
               fileReader = new PredictFileReader(paramsReader.getCorpusPath(), data.getFeatureParameters().getWordsLineCount());
               fileReader.read();

               // Build feature integrator
               IntegratorPredictor integrator = new IntegratorPredictor(fileReader.getLines(), data.getFeatureParameters(), data.getModel());

               // Predict tags and integrateand save data
               List<Line> predicted = data.getModel().predict(integrator);
               DataIO.saveFeatures(predicted, paramsReader.getModelPath());
               int guessed = integrator.getGuessedTags();
               int linesCount = integrator.getLinesCount();
               System.out.println("Guessed tags: " + guessed + "/" + linesCount);
               System.out.println("Prediction precision: " + new DecimalFormat("###.#").format(guessed * 100.0 / linesCount) + "%");
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
