package eu.fbk.dh.jamcha.commandLineParameters;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import java.io.IOException;
import java.nio.file.Paths;

public class PredictParameters extends ParametersReader
{
   FeatureParameters featureParameters;

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

      // All command line parameters must be present
      if (this.CORPUS_PATH == null || this.MODEL_PATH == null)
      {
         throw new IllegalArgumentException("Please specify all parameters: CORPUS, MODEL");
      }

      try
      {
         this.loadSavedParameters();
      }
      catch (IOException e)
      {
         throw new IllegalArgumentException(e.getMessage());
      }
   }

   /**
    * Load all saved parameters that are not passed via command line and are needed
    *
    * @return
    *
    * @throws IOException default
    */
   private void loadSavedParameters() throws IOException
   {
      featureParameters = FeatureParameters.loadFrom(MODEL_PATH);
   }

   /**
    * Get tuning features parameters map(static and dynamic features parameters)
    *
    * @return map of static and dynamic features parameters
    */
   public FeatureParameters getFeatureParameters()
   {
      return this.featureParameters;
   }
}
