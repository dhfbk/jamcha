package eu.fbk.dh.jamcha.parameterparser;

import eu.fbk.dh.jamcha.parameterparser.feature.FeatureParserSelector;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Class that parse command line parameters and return a their values
 */
public abstract class ParametersParser
{
   private static final String TRAIN_COMMAND = "train";
   private static final String PREDICT_COMMAND = "predict";
   public static final int TRAIN_MODE = 0;
   public static final int PREDICT_MODE = 1;

   /**
    * Read all supported parameters and store values. Feature value parameters must be elaborate using {@link FeatureParserSelector }
    *
    * @param parameters command line parameters
    *
    * @return command line parameters parsed and wrapped in an appropriate object
    */
   public void readParameters(@Nonnull final String[] parameters)
   {
      // CORPUS=C:\Users\dan92\Documents\train.txt MODEL=C:\Users\dan92\Documents\model FEATURE="F:-2..1:0..1 F:-3..-2:1..2 T:-4..-1 T:-5..-3"
      if (parameters.length < 1)
      {
         throw new InvalidParameterException("Insert at least one command");
      }

      String mode = parameters[0];
      final String[] params = Arrays.copyOfRange(parameters, 1, parameters.length);
      switch (mode)
      {
         case TRAIN_COMMAND:
         {
            return parseTrainModeParameters(params);
         }
         case PREDICT_COMMAND:
         {
            return parsePredictModeParameters(params);
         }
         default:
            throw new InvalidParameterException("Only these parameters are accepted: " + TRAIN_COMMAND + ", " + PREDICT_COMMAND);
      }

   }

   private static Parameters parseTrainModeParameters(@Nonnull String[] parameters)
   {
      Path corpus = null;
      Path model = null;
      List<String> features = null;

      // Parse all supported parameters
      for (String parameter : parameters)
      {
         String[] splittedParameter = parameter.split("=");
         if (splittedParameter.length != 2)
         {
            throw new IllegalArgumentException(parameter + " is not valid");
         }

         parameter = splittedParameter[0];

         switch (parameter)
         {
            case ParametersOptions.CORPUS:
            {
               if (corpus != null)
               {
                  throw new IllegalArgumentException(ParametersOptions.CORPUS + " is duplicated");
               }
               corpus = Paths.get(splittedParameter[1]);
               break;
            }

            case ParametersOptions.MODEL:
            {
               if (model != null)
               {
                  throw new IllegalArgumentException(ParametersOptions.MODEL + " is duplicated");
               }
               model = Paths.get(splittedParameter[1]);
               break;
            }

            case ParametersOptions.FEATURE:
            {
               if (features != null)
               {
                  throw new IllegalArgumentException(ParametersOptions.FEATURE + "is duplicated");
               }
               String[] splittedFeatures = splittedParameter[1].split(" ");
               features = Arrays.asList(splittedFeatures);
               break;
            }
         }
      }

      // All parameters must be present
      if (corpus == null || model == null || features == null)
      {
         throw new IllegalArgumentException("Please spcify all parameters: CORPUS, MODEL, FEATURE");
      }
      return new Parameters(corpus, model, features);
   }

   private static Parameters parsePredictModeParameters(@Nonnull String[] parameters)
   {
      // TODO: implementare
      return new Parameters(null, null, null);
   }

   private ParametersParser()
   {
   }
;
}
