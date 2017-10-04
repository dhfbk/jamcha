package eu.fbk.dh.jamcha.parameterparser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;

public class TrainParameters
{

  private Path CORPUS_PATH;
  private Path MODEL_PATH;
  private String FEATURES_PARAMETERS;
  
  private final static String CORPUS_OPTION="CORPUS";
  private final static String MODEL_OPTION="MODEL";
  private final static String FEATURE_OPTION="FEATURE";

  private static TrainParameters trainParameters;

  private TrainParameters()
  {
  }
  
  public TrainParameters getInstance()
  {
    if (trainParameters == null)
    {
      trainParameters = new TrainParameters();
    }
    return trainParameters;
  }

  public void readParameters(@Nonnull String[] commandLineParameters)
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

  public Path getCORPUS_PATH()
  {
    return CORPUS_PATH;
  }

  public Path getMODEL_PATH()
  {
    return MODEL_PATH;
  }

  public String getFEATURES_PARAMETERS()
  {
    return FEATURES_PARAMETERS;
  }
  }

}
