package eu.fbk.dh.jamcha;

import eu.fbk.dh.jamcha.feature.FeaturesSchema;
import eu.fbk.utils.svm.Classifier;
import java.nio.file.Path;

public abstract class ModelManager
{

   public static void createModel(FeaturesSchema featuresSchema)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public static void saveModel(FeaturesSchema featuresSchema, Path filePath)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public static Classifier loadModel(Path filePath)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
