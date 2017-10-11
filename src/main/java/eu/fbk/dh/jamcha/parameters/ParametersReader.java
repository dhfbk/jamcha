package eu.fbk.dh.jamcha.parameters;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.Arrays;

public abstract class ParametersReader
{

   protected Path CORPUS_PATH;
   protected Path MODEL_PATH;
   protected String features;

   private static final String TRAIN_COMMAND = "train";
   private static final String PREDICT_COMMAND = "predict";

   protected static final class ParameterOption
   {
      protected static final String CORPUS_OPTION = "CORPUS";
      protected static final String MODEL_OPTION = "MODEL";
      protected static final String FEATURES_OPTION = "FEATURES";
   }

   public Path getCorpusPath()
   {
      return CORPUS_PATH;
   }

   public Path getModelPath()
   {
      return MODEL_PATH;
   }

   public String getFeatures()
   {
      return this.features;
   }

   public final void readParameters(String[] parameters)
   {
      if (parameters.length < 1)
      {
         throw new InvalidParameterException("Insert at least one command");
      }
      String[] parametersWithoutModeCommand = Arrays.copyOfRange(parameters, 1, parameters.length);
      doReadParameters(parametersWithoutModeCommand);
   }

   protected abstract void doReadParameters(String[] parameters);
}
