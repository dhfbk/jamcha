package eu.fbk.dh.jamcha.parameterparser;

import eu.fbk.dh.jamcha.parameterparser.feature.FeatureParserSelector;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

/**
 * Class that parse command line parameters and return a their values
 */
public final class ParametersParser
{

   private static final class ParametersOptions
   {

      protected final static String CORPUS = "CORPUS";
      protected final static String MODEL = "MODEL";
      protected final static String FEATURE = "FEATURE";
   }

   /**
    * CORPUS, MODEL and FEATURE parameters values
    */
   public static class Parameters
   {
      public final Path CORPUS_PATH;
      public final Path MODEL_PATH;
      public final String FEATURES_PARAMETERS;

      private Parameters(@Nonnull Path corpus, @Nonnull Path model, @Nonnull String features)
      {
         CORPUS_PATH = corpus;
         MODEL_PATH = model;
         FEATURES_PARAMETERS = features;
      }
   }

   /**
    * Read all supported parameters and store values. Feature value parameters must be elaborate using {@link FeatureParserSelector }
    *
    * @param parameters command line parameters
    *
    * @return
    */
   public static Parameters readParameters(@Nonnull String[] parameters)
   {
      Path corpus = null;
      Path model = null;
      String features = null;

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
               features = splittedParameter[1];
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

   private ParametersParser()
   {
   }
;
}
