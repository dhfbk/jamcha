package eu.fbk.dh.jamcha.parametersReader;

import java.nio.file.Paths;
import javax.annotation.Nonnull;

/**
 * Reader for train command parameters
 */
public class TrainParametersReader extends ParametersReader
{
   private String features;

   protected TrainParametersReader(ParametersReader.COMMAND_TYPE command_type)
   {
      super(command_type);
   }

   @Override
   protected void doReadParameters(@Nonnull String[] parameters)
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

            case ParametersReader.PARAMETER_OPTION.FEATURES_OPTION:
            {
               if (this.features != null)
               {
                  throw new IllegalArgumentException(ParametersReader.PARAMETER_OPTION.FEATURES_OPTION + "is duplicated");
               }
               this.features = splittedParameter[1];
               break;
            }
         }
      }

      // All parameters must be present
      if (this.CORPUS_PATH == null || this.MODEL_PATH == null)
      {
         throw new IllegalArgumentException("Please spcify all parameters: CORPUS, MODEL, FEATURE");
      }
   }

   public String getRawFeaturesParameters()
   {
      return this.features;
   }
}
