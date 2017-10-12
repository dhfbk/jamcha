package eu.fbk.dh.jamcha.parametersReader;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.util.Arrays;
import javax.annotation.Nonnull;

public abstract class ParametersReader
{
   protected Path CORPUS_PATH;
   protected Path MODEL_PATH;

   private static final String TRAIN_COMMAND = "train";
   private static final String PREDICT_COMMAND = "predict";

   private final COMMAND_TYPE COMMAND;

   protected static final class ParameterOption
   {
      protected static final String CORPUS_OPTION = "CORPUS";
      protected static final String MODEL_OPTION = "MODEL";
      protected static final String FEATURES_OPTION = "FEATURES";
   }

   /**
    * List of all supported commands. What does application will do? Depends on the command entered in the command line
    */
   public static enum COMMAND_TYPE
   {
      COMMAND_TRAIN, COMMAND_PREDICT;
   }

   private ParametersReader(COMMAND_TYPE commandType)
   {
      COMMAND = commandType;
   }

   /**
    * Build a parameter reader from command line arguments
    *
    * @param commandLineParameters command line arguments passed on application launch
    *
    * @return A parameter reader
    *
    * @throws IllegalArgumentException if first parameter is not a supported command
    */
   @Nonnull
   public static final ParametersReader build(@Nonnull String[] commandLineParameters) throws IllegalArgumentException
   {
      if (commandLineParameters.length < 1)
      {
         throw new InvalidParameterException("Insert at least one command");
      }
      String command = commandLineParameters[0];
      ParametersReader reader;
      switch (command)
      {
         case TRAIN_COMMAND:
         {
            reader = new TrainParameters(COMMAND_TYPE.COMMAND_TRAIN);
            break;
         }
         case PREDICT_COMMAND:
         {
            reader = new PredictParameters(COMMAND_TYPE.COMMAND_PREDICT);
            break;
         }

         default:
         {
            throw new IllegalArgumentException(command + " is not a valid command. Please insert " + TRAIN_COMMAND + " or " + PREDICT_COMMAND + " as first parameter");
         }
      }
      return reader;
   }

   public final Path getCorpusPath()
   {
      return CORPUS_PATH;
   }

   public final Path getModelPath()
   {
      return MODEL_PATH;
   }

   /**
    * Read command line parameters
    *
    * @param parameters command line parameters
    *
    * @return type of reader that read parameters
    *
    * @see COMMAND_TYPE
    */
   public final COMMAND_TYPE readParameters(String[] parameters)
   {
      String[] parametersWithoutModeCommand = Arrays.copyOfRange(parameters, 1, parameters.length);
      doReadParameters(parametersWithoutModeCommand);
      return this.COMMAND;
   }

   protected abstract void doReadParameters(String[] parameters);

   private static class TrainParameters extends ParametersReader
   {
      private String features;

      private TrainParameters(COMMAND_TYPE command_type)
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

      public String getRawFeaturesParameters()
      {
         return this.features;
      }
   }

   private static class PredictParameters extends ParametersReader
   {
      FeatureParameters featureParameters;

      private PredictParameters(COMMAND_TYPE command_type)
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
}
