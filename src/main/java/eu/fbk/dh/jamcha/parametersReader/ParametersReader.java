package eu.fbk.dh.jamcha.parametersReader;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Arrays;
import javax.annotation.Nonnull;

public abstract class ParametersReader
{
   protected Path CORPUS_PATH;
   protected Path MODEL_PATH;

   protected static final String TRAIN_COMMAND = "train";
   protected static final String PREDICT_COMMAND = "predict";

   private final COMMAND_TYPE COMMAND;

   /**
    * List of all parameters that can be inserted in command line
    */
   protected static final class PARAMETER_OPTION
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

   protected ParametersReader(COMMAND_TYPE commandType)
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
            reader = new TrainParametersReader(COMMAND_TYPE.COMMAND_TRAIN);
            break;
         }
         case PREDICT_COMMAND:
         {
            reader = new PredictParametersReader(COMMAND_TYPE.COMMAND_PREDICT);
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
}
