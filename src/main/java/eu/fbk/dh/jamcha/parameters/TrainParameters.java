package eu.fbk.dh.jamcha.parameters;

import java.nio.file.Paths;
import javax.annotation.Nonnull;

public class TrainParameters extends ParametersReader
{

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

            case ParameterOption.FEATURES_OPTION:
            {
               if (this.features != null)
               {
                  throw new IllegalArgumentException(ParameterOption.FEATURES_OPTION + "is duplicated");
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
}
