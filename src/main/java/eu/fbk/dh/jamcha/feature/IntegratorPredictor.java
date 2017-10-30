package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.data.Model;
import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.Vector;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class that integrates lines features using feature tuning parameters with prediction of previous lines tags
 */
public class IntegratorPredictor extends Integrator
{
   @Nonnull
   private final Model model;
   private ArrayList<String> tagsList;
   int tagsCorrectGuessed = -1;

   /**
   * Constructor
   *
   * @param model model that will be used to predict TAG
   * @param defaultLines list of all lines and their features.
   * @param parameters   feature parameters that will be used to integrate features read by reader
   */
   public IntegratorPredictor(@Nonnull Collection<Line> defaultLines, @Nonnull FeatureParameters parameters, @Nonnull Model model)
   {
      super(defaultLines, parameters);
      this.model = model;
   }

   /**
    * Add the features of the previous or later lines according to features tuning parameters values. Integrate line
    * @return all integrated lines. Equivalent to {@code integrate(); getIntegratedLines()};
    */
   @Override
   public List<Line> integrate()
   {
      createTagsList();
      this.integratedLines = new ArrayList<>(this.defaultLines.size());
      Classifier classifier = model.getClassifier();
      Vector vector;
      // For each integratedLine
      for (int actualLine = 0; actualLine < defaultLines.size(); actualLine ++)
      {
         // Integrate line
         Line integratedLine = doIntegrateLine(actualLine);
         System.out.println(actualLine +" "+ integratedLine.getWords());

         // Predict label and convert it to string tag
         vector = Vector.builder().set(integratedLine.getWords()).build();
         int label = classifier.predict(true, vector).getLabel();
         String tag = model.getTagsIndexes().get(label);
         integratedLine.setTag(tag);
         this.defaultLines.get(actualLine).setTag(tag);
         System.out.println("Tag: " + tag);
         System.out.println();
      }
      
      // Compare how many tag are correctly guessed if file to predict has "correct tags"
      if (tagsList != null)
      {
         int tagsCounter = 0;
         for (int i = 0; i < this.defaultLines.size(); i ++)
         {
            String tagGuessed = this.defaultLines.get(i).getTag();
            String correctTag = tagsList.get(i);
            if (correctTag.equals(tagGuessed))
            {
               tagsCounter ++;
            }
         }
         this.tagsCorrectGuessed = tagsCounter;
      }
      return this.defaultLines;
   }

   /**
    * How many tags have been correctly guessed?
    *
    * @return count of correctly guessed tags. -1 if {@code integrate()} have been never called or predict features file does not have TAGs
    */
   @Nullable
   public int getGuessedTags()
   {
      return this.tagsCorrectGuessed;
   }

   /**
    * Copy all default feature tags of file to predict
    */
   private void createTagsList()
   {
      // If defaultLines does not have tags we don't need to copy tags
      if (this.defaultLines.get(0).getTag() != null)
      {
         tagsList = new ArrayList<>(this.defaultLines.size());
         for (int i = 0; i < this.defaultLines.size(); i ++)
         {
            tagsList.add(this.defaultLines.get(i).getTag());
         }
      }
   }
}
