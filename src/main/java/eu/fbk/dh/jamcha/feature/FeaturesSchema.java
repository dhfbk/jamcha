package eu.fbk.dh.jamcha.feature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeaturesSchema
{
   /**
    * All train file read features, without any change
    */
   private final ArrayList<Line> defaultFeatures;

   /**
    * All the features of the training file with additions resulting from the integration process, performed with tuning parameters passed to the integrated method
    */
   private ArrayList<Line> integratedFeatures;
   
   @Nonnull
   private FeatureParameters featureParameters;

   /**
    * Constructor
    *
    * @param defaultFeatures list of all lines and their features.
    * @param parametes       feature parameters that will be used to integrate features read by reader
    */
   public FeaturesSchema(@Nonnull Collection<Line> defaultFeatures, @Nonnull FeatureParameters parametes)
   {
      this.defaultFeatures = new ArrayList<>(defaultFeatures);
      this.featureParameters = parametes;
   }

   /**
    * Constructor
    *
    * @param reader     reader that will parse file containing the list of features
    * @param parameters feature parameters that will be used to integrate features read by reader
    *
    * @throws IOException
    */
   public FeaturesSchema(@Nonnull FeatureFileReader reader, @Nonnull FeatureParameters parameters) throws IOException
   {
      this(reader.getFeatures() != null ? reader.getFeatures() : reader.read(), parameters);
   }

   /**
    * For each line, add the features of the previous or later lines according to features tuning parameters values.
    *
    * @param parameters tuning features parameters that will be used to influence integration. If null, constructor parameters will be used
    */
   public void integrate(FeatureParameters parameters)
   {
      // if the default features have already been integrated with the same parameters do nothing
      if (this.featureParameters.equals(parameters) && integratedFeatures != null)
      {
         return;
      }
      
      if (parameters != null)
      {
         this.featureParameters = parameters;
      }
      this.integratedFeatures = null;
      System.gc();
      this.integratedFeatures = new ArrayList<>(this.defaultFeatures.size());
      FeatureIntegrator.integrateFeatures(this);
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
    * This class adds features of other lines to the current co using parsed features parameters.
    *
    * @see FeatureFileReader
    * @see FeatureParser
    */
   private final static class FeatureIntegrator
   {
      /**
       * For each co, add the features of the previous or later lines according to features tuning parameters values.
       *
       * @param schema schema cointaining default features and features parameters that will be used to integrate default features
       *
       * @see FeatureIntegrator
       * @see FeatureParser
       */
      private static void integrateFeatures(@Nonnull FeaturesSchema schema)
      {
         FeatureParameters parameters = schema.featureParameters;

         // For each line to integrate
         for (int actualLine = 0; actualLine < schema.defaultFeatures.size(); actualLine ++)
         {
            ArrayList<String> integratedLine = new ArrayList<>();
            // Add to this line previous or later lines features according to features parameters (static and dynamic)
            for (int desideredRowOffset : parameters.getParameters().keySet())
            {
               int requestedLine = actualLine + desideredRowOffset;

               // requestedLine and actualLine must belong to same sentence
               if (requestedLine >= 0 && requestedLine < schema.defaultFeatures.size() && schema.defaultFeatures.get(actualLine).getSequence() == schema.defaultFeatures.get(
                     requestedLine).getSequence())
               {
                  List<String> requestedLineFeaturesToAdd = extractLineFeaturesValue(schema, parameters, actualLine + desideredRowOffset, desideredRowOffset);
                  integratedLine.addAll(requestedLineFeaturesToAdd);
               }
            }
            String tag = schema.defaultFeatures.get(actualLine).getTag();
            Line rowToAdd = new Line(actualLine, schema.defaultFeatures.get(actualLine).getSequence(), tag, integratedLine);
            rowToAdd.getWords().sort(null);
            schema.integratedFeatures.add(rowToAdd);
         }
      }

      /**
       * Take all features values of co passed as parameter. (all features that must be considered according to features parameters)
       *
       * @param requestedline co of which we want the features. Must be between 0(inclusive) and lines count (exclusive)
       *
       * @return features values of considered co that we must consider
       */
      @Nonnull
      private static List<String> extractLineFeaturesValue(@Nonnull FeaturesSchema schema, @Nonnull FeatureParameters parameters, int requestedline, int offset)
      {
         if (requestedline < 0 || requestedline >= schema.defaultFeatures.size())
         {
            String error = "Requested line must be between zero and " + (schema.defaultFeatures.size() - 1) + "(inclusive)";
            throw new IllegalArgumentException(error);
         }
         // List of columns numbers to consider of the requested line
         Collection<Integer> columnsToAdd = parameters.getParameters().get(offset);

         // Contains all columns values for this line
         ArrayList<String> retval = new ArrayList<>(schema.defaultFeatures.get(0).features.size());

         // Take all line columns that have a valid column number (a number passed by columnsToAdd)
         for (int column : columnsToAdd)
         {
            String columnFeature;
            if (column != FeatureParameters.FeatureParser.TAG_COLUMN_INDEX)
            {
               columnFeature = schema.defaultFeatures.get(requestedline).features.get(column);
            }
            else
            {
               columnFeature = schema.defaultFeatures.get(requestedline).getTag();
            }
            
            if (columnFeature != null)
            {
               retval.add(columnFeature);
            }
         }
         return retval;
      }
   }

   /**
    * Class that represent a features line
    */
   public static class Line
   {
      private int line;
      @Nullable
      private String tag;
      private ArrayList<String> features;
      private int sequenceIndex;

      /**
       * Constructor
       *
       * @param line          line number, zero or greater
       * @param sequenceIndex the number of the sentence to which this line belongs. Value must be zero or greater
       */
      public Line(@Nonnegative int line, @Nonnegative int sequenceIndex)
      {
         this(line, sequenceIndex, null, null);
      }

      /**
       * Constructor
       *
       * @param line          line number, zero or greater
       * @param sequenceIndex the number of the sentence to which this co belongs. Value must be zero or greater
       * @param tag           tag of line Can be null
       * @param lineFeatures  list of words to add to co features
       */
      public Line(@Nonnegative int line, @Nonnegative int sequenceIndex, @Nullable String tag, @Nonnull Collection<String> lineFeatures)
      {
         if (line < 0 || sequenceIndex < 0)
         {
            throw new IllegalArgumentException("Line or sequenceIndex < 0");
         }
         this.line = line;
         this.sequenceIndex = sequenceIndex;
         this.tag = tag;
         this.features = lineFeatures != null ? new ArrayList<>(lineFeatures) : new ArrayList<>(0);
      }
      
      public int getLine()
      {
         return this.line;
      }
      
      public String getTag()
      {
         return this.tag;
      }
      
      public int getSequence()
      {
         return this.sequenceIndex;
      }
      
      public List<String> getWords()
      {
         return this.features;
      }
      
      public void setTag(@Nullable String tag)
      {
         this.tag = tag;
      }
      
      @Override
      public boolean equals(Object o)
      {
         boolean retval = false;
         if (o != null && o instanceof Line)
         {
            Line co = (Line) o;
            boolean tagsFlag = co.tag == null ? this.tag == null : co.tag.equals(this.tag);
            retval = tagsFlag && co.features.equals(this.features) && co.line == this.line && co.sequenceIndex == this.sequenceIndex;
         }
         return retval;
      }
      
      @Override
      public int hashCode()
      {
         int hash = 7;
         hash = 97 * hash + this.line;
         hash = 97 * hash + Objects.hashCode(this.tag);
         hash = 97 * hash + Objects.hashCode(this.features);
         hash = 97 * hash + this.sequenceIndex;
         return hash;
      }
   }
}
