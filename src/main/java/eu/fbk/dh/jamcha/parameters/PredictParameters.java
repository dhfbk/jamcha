package eu.fbk.dh.jamcha.parameters;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PredictParameters extends ParametersReader
{
   private int columnsCount;

   @Override
   public void doReadParameters(String[] parameters)
   {
      // Parse all supported parameters
      for (String option : parameters)
      {
         String[] splittedParameter = option.split("=");
         if (splittedParameter.length != 2)
         {
            throw new IllegalArgumentException(option + " is not valid");
         }

         option = splittedParameter[0];

         switch (option)
         {
            case ParameterOption.CORPUS_OPTION:
            {
               if (this.CORPUS_PATH != null)
               {
                  throw new IllegalArgumentException(ParameterOption.CORPUS_OPTION + " is duplicated");
               }
               this.CORPUS_PATH = Paths.get(splittedParameter[1]);
               break;
            }

            case ParameterOption.MODEL_OPTION:
            {
               if (this.MODEL_PATH != null)
               {
                  throw new IllegalArgumentException(ParameterOption.MODEL_OPTION + " is duplicated");
               }
               this.MODEL_PATH = Paths.get(splittedParameter[1]);
               break;
            }

         }
      }
      FeatureParameters loadedParams = this.loadSavedParameters(MODEL_PATH);
      String errorMessage = null;

      // All parameters must be present
      if (this.CORPUS_PATH == null || this.MODEL_PATH == null)
      {
         errorMessage = "Please specify all parameters: CORPUS, MODEL";
      }
      else
      {
         if (loadedParams == null)
         {
            errorMessage = "Impossible to load saved parameters. MODEL path does not contain features parameters file.";
         }
      }
      if (errorMessage != null)
      {
         throw new IllegalArgumentException(errorMessage);
      }

      this.features = loadedParams.getFeature();
      this.columnsCount = loadedParams.getColumnsCount();
   }

   private FeatureParameters loadSavedParameters(Path modelPath)
   {
      Path parametersPath = Paths.get(modelPath.toString(), FeatureParameters.FILE_NAME);

      return FeatureParameters.loadFrom(parametersPath);
   }

   public int getColumnsCount()
   {
      return this.columnsCount;
   }
}
