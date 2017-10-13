package eu.fbk.dh.jamcha.parametersReader;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Reader for predict command parameters. Automatically obtains all needed value that are not passed in command line
 */
public class PredictParametersReader extends ParametersReader
{
   FeatureParameters featureParameters;

   protected PredictParametersReader(ParametersReader.COMMAND_TYPE command_type)
   {
      super(command_type);
   }

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
            case ParametersReader.PARAMETER_OPTION.CORPUS_OPTION:
            {
               if (this.CORPUS_PATH != null)
               {
                  throw new IllegalArgumentException(ParametersReader.PARAMETER_OPTION.CORPUS_OPTION + " is duplicated");
               }
               this.CORPUS_PATH = Paths.get(splittedParameter[1]);
               break;
            }

            case ParametersReader.PARAMETER_OPTION.MODEL_OPTION:
            {
               if (this.MODEL_PATH != null)
               {
                  throw new IllegalArgumentException(ParametersReader.PARAMETER_OPTION.MODEL_OPTION + " is duplicated");
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