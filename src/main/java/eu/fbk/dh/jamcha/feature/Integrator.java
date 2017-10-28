package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.FeatureParameters.FeatureParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Class that integrates lines features using feature tuning parameters
 */
public class Integrator
{
   /**
    * All train file read features, without any change
    */
   protected final ArrayList<Line> defaultLines;

   /**
    * All the features of the training file with additions resulting from the integration process, performed with tuning parameters passed to the integrated method
    */
   protected ArrayList<Line> integratedLines;

   @Nonnull
   private FeatureParameters featureParameters;

   /**
    * Constructor
    *
    * @param defaultLines list of all lines and their features.
    * @param parameters   feature parameters that will be used to integrate features read by reader
    */
   public Integrator(@Nonnull Collection<Line> defaultLines, @Nonnull FeatureParameters parameters)
   {
      this.defaultLines = new ArrayList<>(defaultLines);
      integratedLines = new ArrayList<>(defaultLines.size());
      this.featureParameters = parameters;
   }

   /**
    * For each integratedLine, add the features of the previous or later lines according to features tuning parameters values.
    */
   public List<Line> integrate()
   {
      this.integratedLines = new ArrayList<>(this.defaultLines.size());
      for (int i = 0; i < defaultLines.size(); i ++)
      {
         doIntegrateLine(i);
      }
      return this.getIntegratedLines();
   }

   public final void setFeatureParameters(@Nonnull FeatureParameters parameters)
   {
      if ( ! this.featureParameters.equals(parameters))
      {
         this.featureParameters = parameters;
         this.integratedLines = null;
      }
   }

   public final int getLinesCount()
   {
      return this.defaultLines.size();
   }

   /**
    * Feature parameters that is used to integrate train features file
    *
    * @return feature parameters
    */
   @Nonnull
   public final FeatureParameters getFeatureParameters()
   {
      return this.featureParameters;
   }

   @Nonnull
   public final List<Line> getDefaultLines()
   {
      return this.defaultLines;
   }

   /**
    * Return list of all lines integrated features.
    *
    * @return list of all integrated features or null if "integrate" method has never been called
    */
   @Nullable
   public final List<Line> getIntegratedLines()
   {
      return this.integratedLines;
   }

   @Nonnull
   protected final Line doIntegrateLine(int lineToIntegrate)
   {
      ArrayList<String> integratedLine = new ArrayList<>();
      // Add to this integratedLine previous or later lines features according to features parameters (static and dynamic)
      for (int desideredRowOffset : featureParameters.getParameters().keySet())
      {
         int requestedLine = lineToIntegrate + desideredRowOffset;
         int defSeq = defaultLines.get(lineToIntegrate).getSequence();

         // requestedLine and actualLine must belong to same sentence
         if (requestedLine >= 0 && requestedLine < defaultLines.size() && defSeq == defaultLines.get(requestedLine).getSequence())
         {
            List<String> requestedLineFeaturesToAdd = extractLineFeatures(requestedLine, desideredRowOffset);
            integratedLine.addAll(requestedLineFeaturesToAdd);
         }
      }
      String tag = defaultLines.get(lineToIntegrate).getTag();
      Line rowToAdd = new Line(lineToIntegrate, defaultLines.get(lineToIntegrate).getSequence(), tag, integratedLine);
      this.integratedLines.add(lineToIntegrate, rowToAdd);
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
      Line line = defaultLines.get(requestedline);
      for (int index : wordsToAdd)
      {
         String columnFeature = index != FeatureParser.TAG_COLUMN_INDEX ? line.getWords().get(index) : line.getTag();
         retval.add(columnFeature);
      }
      return retval;
   }

}
