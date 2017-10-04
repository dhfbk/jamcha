package eu.fbk.dh.jamcha;

import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.LabelledVector;
import eu.fbk.utils.svm.Vector;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;

/**
 * This class creates the training model. Internally it extracts tags from each row trainingSet list and creates tags hashmap.
 *
 */
public final class ModelCreator
{
   private HashMap<Integer, String> tagsMap;
   private List<Row> features;
   private List<LabelledVector> trainingSet;

   public ModelCreator(@Nonnull List<Row> features)
   {
      this.features = features;
      tagsMap = new HashMap<>(features.size());
      trainingSet = new ArrayList<>(features.size());
   }

   /**
    * Create model and write it to a specified path
    *
    * @param path path to write model file
    *
    * @throws IOException default description
    */
   public void writeModelTo(Path path) throws IOException
   {
      this.prepareParametersForTrainModel(features);
      Classifier.Parameters parameters = Classifier.Parameters.forSVMPolyKernel(tagsMap.size(), null, null, null, null, null);
      Classifier classifier = Classifier.train(parameters, trainingSet);
      classifier.writeTo(path);
   }

   @Nonnull
   public Map getTagsMap()
   {
      if (tagsMap == null)
      {
         tagsMap = new HashMap<>();
      }

      return tagsMap;
   }

   /**
    * Prepare tags map and the list of labelled vectors for Classifier
    *
    * @param featuresPerRow complete model of all trainingSet and their related tag
    */
   private void prepareParametersForTrainModel(@Nonnull List<Row> featuresPerRow)
   {
      featuresPerRow.forEach((row) ->
      {
         int hashcode = row.getTag().hashCode();

         // Insert line tag into tagsMap
         tagsMap.put(row.getTag().hashCode(), row.getTag());

         // Create labelledVector representing this line
         Vector.Builder builder = Vector.builder();
         builder.set(row.getFeatures());
         LabelledVector vector = builder.build().label(hashcode);
         trainingSet.add(vector);
      });
   }
}
