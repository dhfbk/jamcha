package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.data.Model;
import eu.fbk.dh.jamcha.feature.FeatureParameters.FeatureParser;
import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.Vector;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class that integrates integratedLine features using feature tuning parameters
 */
public final class FeaturesIntegrator
{
   /**
    * All train file read features, without any change
    */
   private final ArrayList<Line> defaultFeatures;

   /**
    * All the features of the training file with additions resulting from the integration process, performed with tuning parameters passed to the integrated method
    */
   private ArrayList<Line> integratedFeatures;
   private ArrayList<String> tagsList;
   private int tagsCorrectGuessed = -1;

   @Nonnull
   private FeatureParameters featureParameters;

   /**
    * Constructor
    *
    * @param defaultFeatures list of all lines and their features.
    * @param parameters      feature parameters that will be used to integrate features read by reader
    */
   public FeaturesIntegrator(@Nonnull Collection<Line> defaultFeatures, @Nonnull FeatureParameters parameters)
   {
      this.defaultFeatures = new ArrayList<>(defaultFeatures);
      integratedFeatures = new ArrayList<>(defaultFeatures.size());
      this.featureParameters = parameters;
   }

   /**
    * Constructor
    *
    * @param reader     reader that will parse file containing the list of features
    * @param parameters feature parameters that will be used to integrate features read by reader
    *
    * @throws IOException
    */
   public FeaturesIntegrator(@Nonnull FeatureFileReader reader, @Nonnull FeatureParameters parameters) throws IOException
   {
      this(reader.getFeatures() != null ? reader.getFeatures() : reader.read(), parameters);
   }

   /**
    * For each integratedLine, add the features of the previous or later lines according to features tuning parameters values.
    */
   public void integrate()
   {
      this.integratedFeatures = new ArrayList<>(this.defaultFeatures.size());
      for (int i = 0; i < defaultFeatures.size(); i ++)
      {
         doIntegrateLine(i);
      }
   }

   /**
    * Add the features of the previous or later lines according to features tuning parameters values.
    *
    * @param model
    * @param lineToIntegrate
    *
    * @return
    */
   @Nonnull
   public void integrateWithPrediction(@Nonnull Model model)
   {
      createTagsList();
      this.integratedFeatures = new ArrayList<>(this.defaultFeatures.size());
      Classifier classifier = model.getClassifier();
      Vector.Builder builder = Vector.builder();
      Vector vector;
      // For each integratedLine
      for (int actualLine = 0; actualLine < defaultFeatures.size(); actualLine ++)
      {
         // Integrate line
         Line integratedLine = doIntegrateLine(actualLine);

         // Predict label and convert it to string tag
         vector = builder.set(integratedLine.getWords()).build();
         int label = classifier.predict(true, vector).getLabel();
         String tag = model.getTagsIndexes().get(label);
         integratedLine.setTag(tag);
         defaultFeatures.get(actualLine).setTag(tag);
      }

      if (tagsList != null)
      {
         int tagsCounter = 0;
         for (int i = 0; i < defaultFeatures.size(); i ++)
         {
            String tagGuessed = defaultFeatures.get(i).getTag();
            String correctTag = tagsList.get(i);
            if (correctTag.equals(tagGuessed))
            {
               tagsCounter ++;
            }
         }
         this.tagsCorrectGuessed = tagsCounter;
      }
   }

   public void setFeatureParameters(@Nonnull FeatureParameters parameters)
   {
      if ( ! this.featureParameters.equals(parameters))
      {
         this.featureParameters = parameters;
         this.integratedFeatures = null;
      }
   }

   public int getLinesCount()
   {
      return this.defaultFeatures.size();
   }

   /**
    * Feature parameters that is used to integrate train features file
    *
    * @return feature parameters
    */
   @Nonnull
   public FeatureParameters getFeatureParameters()
   {
      return this.featureParameters;
   }

   @Nonnull
   public List<Line> getDefaultFeatures()
   {
      return this.defaultFeatures;
   }

   /**
    * Return list of all lines integrated features.
    *
    * @return list of all integrated features or null if "integrate" method has never been called
    */
   @Nullable
   public List<Line> getIntegratedFeatures()
   {
      return this.integratedFeatures;
   }

   /**
    * How many tags have been correctly guessed
    *
    * @return count of correctly guessed tags. -1 if {@code integrateWithPrediction} have been never called.
    */
   @Nullable
   public int getGuessedTags()
   {
      return this.tagsCorrectGuessed;
   }

   @Nonnull
   private Line doIntegrateLine(int lineToIntegrate)
   {
      ArrayList<String> integratedLine = new ArrayList<>();
      // Add to this integratedLine previous or later lines features according to features parameters (static and dynamic)
      for (int desideredRowOffset : featureParameters.getParameters().keySet())
      {
         int requestedLine = lineToIntegrate + desideredRowOffset;
         int defSeq = defaultFeatures.get(lineToIntegrate).getSequence();

         // requestedLine and actualLine must belong to same sentence
         if (requestedLine >= 0 && requestedLine < defaultFeatures.size() && defSeq == defaultFeatures.get(requestedLine).getSequence())
         {
            List<String> requestedLineFeaturesToAdd = extractLineFeatures(requestedLine, desideredRowOffset);
            integratedLine.addAll(requestedLineFeaturesToAdd);
         }
      }
      String tag = defaultFeatures.get(lineToIntegrate).getTag();
      Line rowToAdd = new Line(lineToIntegrate, defaultFeatures.get(lineToIntegrate).getSequence(), tag, integratedLine);
      this.integratedFeatures.add(lineToIntegrate, rowToAdd);
      return rowToAdd;
   }

   /**
    * Take all features values of line passed as parameter. (all features that must be considered according to features parameters)
    *
    * @param requestedline line from which we want to extract features. Must be between 0(inclusive) and lines count (exclusive). ATTENTION: there is no value check
    * @param offset        count of previous o later lines (if 0 is actual line) from actual line.
    *
    * @return feature values of considered line that we must consider
    */
   @Nonnull
   private List<String> extractLineFeatures(int requestedline, int offset)
   {
      // List of columns numbers to consider of the requested integratedLine
      Collection<Integer> wordsToAdd = featureParameters.getParameters().get(offset);

      // Contains all columns values for this integratedLine
      ArrayList<String> retval = new ArrayList<>(getFeatureParameters().getValuesCount());

      // Take all integratedLine columns that have a valid index number (a number passed by wordsToAdd)
      Line line = defaultFeatures.get(requestedline);
      for (int index : wordsToAdd)
      {
         String columnFeature = index != FeatureParser.TAG_COLUMN_INDEX ? line.getWords().get(index) : line.getTag();
         retval.add(columnFeature);
      }
      return retval;
   }

   /**
    * Copy all default features tag
    */
   private void createTagsList()
   {
      // If no integratedLine has tag we don't need to copy tags
      if (defaultFeatures.get(0).getTag() != null)
      {
         tagsList = new ArrayList<>(defaultFeatures.size());
         for (int i = 0; i < defaultFeatures.size(); i ++)
         {
            tagsList.add(defaultFeatures.get(i).getTag());
         }
      }
   }

}
