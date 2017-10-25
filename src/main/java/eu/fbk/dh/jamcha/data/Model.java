package eu.fbk.dh.jamcha.data;

import eu.fbk.dh.jamcha.feature.FeaturesSchema;
import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.LabelledVector;
import eu.fbk.utils.svm.Vector;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 */
public class Model
{
   private static final String TAGS_INDEX_MAP_FILENAME = "tagsIndexes.txt";

   @Nonnull
   private HashMap<Integer, String> tagsMap;
   private Classifier classifier;

   private Model()
   {
   }

   @Nonnull
   public static Model train(@Nonnull List<Line> features) throws IOException
   {
      int counter = -1;
      List<LabelledVector> labVectors = new ArrayList<>(features.size());
      HashMap<Integer, String> tagsMapTmp = new HashMap<>(features.size());
      HashMap<String, Integer> tagIndex = new HashMap<>(tagsMapTmp.size());

      for (FeaturesSchema.Line line : features)
      {
         String tag = line.getTag();
         if (tag != null)
         {
            int index;
            if ( ! tagIndex.containsKey(tag))
            {
               // Add tag to tags maps
               counter ++;
               index = counter;
               tagIndex.put(tag, counter);
               tagsMapTmp.put(counter, tag);
            }
            else
            {
               index = tagIndex.get(tag);
            }
            // Create labelledVector representing this line and add it to global list
            Vector.Builder builder = Vector.builder();
            builder.set(line.getWords());
            LabelledVector vector = builder.build().label(index);
            labVectors.add(vector);
         }
      }
      Classifier.Parameters classParams = Classifier.Parameters.forSVMRBFKernel(tagsMapTmp.size(), null, null, null);
      Classifier classifierTmp = Classifier.train(classParams, labVectors);

      Model model = new Model();
      model.classifier = classifierTmp;
      model.tagsMap = tagsMapTmp;
      return model;
   }

   protected static Model load(@Nonnull Path folderPath) throws IOException
   {
      Model model = new Model();
      model.classifier = Classifier.readFrom(folderPath);
      model.tagsMap = loadTagsIndexesMap(folderPath);
      return model;
   }

   /**
    * Save model in a persistent way.
    *
    * @param folderPath folder path where to save the data
    *
    * @return true: all data were successfully written. False: no data to write.
    *
    * @throws IOException default
    * @see IOException
    */
   public boolean save(@Nonnull final Path folderPath) throws IOException
   {
      if (classifier == null || tagsMap == null)
      {
         return false;
      }

      // Save model
      classifier.writeTo(folderPath);

      // Save tags indexes map
      Path tagsPath = Paths.get(folderPath.toString(), TAGS_INDEX_MAP_FILENAME);
      try (BufferedWriter writer = Files.newBufferedWriter(tagsPath))
      {
         for (Map.Entry<Integer, String> entry : tagsMap.entrySet())
         {
            writer.append(entry.getKey() + " ");
            writer.append(entry.getValue());
            writer.newLine();
         }
      }
      return true;
   }

   /**
    * Guess the most likely tag for each features line
    *
    * @param features list of the features lines we want to get the most likely tag
    *
    * @return list of feature lines each with its calculated tag
    */
   public List<Line> predict(@Nonnull final List<Line> features)
   {
      ArrayList<Vector> vectors = new ArrayList<>(features.size());
      //TODO: trasformare features in labelledvector
      for (Line line : features)
      {
         Vector vector = Vector.builder().set(line.getWords()).build();
         vectors.add(vector);
      }

      // chiamare classifier.predict->ottengo lista di labelled vector
      List<LabelledVector> predictResult = classifier.predict(true, vectors);

      // convertire labelledvector in line usando la tagmap
      ArrayList<Line> retval = new ArrayList<>(features.size());
      for (LabelledVector vector : predictResult)
      {
         String tag = tagsMap.get(vector.getLabel());
         Line line = new Line(0, 0, tag, vector.getFeatures());
         retval.add(line);
      }

      return retval;
   }

   @Nonnull
   public Classifier getClassifier()
   {
      return this.classifier;
   }

   @Nonnull
   public Map<Integer, String> getTagsIndexes()
   {
      return this.tagsMap;
   }

   @Nonnull
   private static HashMap<Integer, String> loadTagsIndexesMap(@Nonnull Path folderPath) throws NumberFormatException, IOException
   {
      Path tagsPath = Paths.get(folderPath.toString(), TAGS_INDEX_MAP_FILENAME);
      HashMap<Integer, String> retval = new HashMap<>(100);
      try (BufferedReader reader = Files.newBufferedReader(tagsPath))
      {
         String line;
         while ((line = reader.readLine()) != null)
         {
            String[] split = line.split(" ");
            if (split != null && split.length == 2)
            {
               int key = Integer.parseInt(split[0]);
               retval.putIfAbsent(key, split[1]);
            }
         }
      }
      return retval;
   }
}
