package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.fileReader.FeatureFileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeaturesSchema
{
   /**
    * All train file read features, without any change
    */
   private final ArrayList<Row> defaultFeatures;

   /**
    * All the features of the training file with additions resulting from the integration process, performed with tuning parameters passed to the integrated method
    */
   private ArrayList<Row> integratedFeatures;

   private ArrayList<String> tagsIndexes;

   private FeatureParameters parameters;

   private FeaturesSchema(@Nonnull Collection<Row> defaultFeatures)
   {
      this.defaultFeatures = new ArrayList<>(defaultFeatures);
   }

   public static FeaturesSchema build(@Nonnull FeatureFileReader reader) throws IOException
   {
      FeaturesSchema schema;
      if (reader.getFeatures() == null)
      {
         schema = reader.read();
      }
      else
      {
         schema = new FeaturesSchema(reader.getFeatures());
      }
      return schema;
   }

   private void createTagsIndexes()
   {
   }

   /**
    * For each line, add the features of the previous or later lines according to features tuning parameters values.
    *
    * @param parameters tuning features parameters that will be used to influence integration
    */
   public void integrate(@Nonnull FeatureParameters parameters)
   {
      if (this.integratedFeatures != null)
      {
         this.integratedFeatures = null;
         System.gc();
      }
      this.integratedFeatures = new ArrayList<>(this.defaultFeatures.size());
      FeatureIntegrator.integrateFeatures(this, parameters);
   }

   public int getTagByIndex(int tagIndex)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public int getTagIndex(String tagToSearch)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   private Iterable<String> getLineFeaturesByParameters(int lineNumber)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public static class Row
   {
      private int line;

      @Nullable
      private String tag;

      private ArrayList<String> features = new ArrayList<>();

      private int sequenceIndex;

      /**
       * COnstructor
       *
       * @param line          line number, zero or greater
       * @param sequenceIndex the number of the sentence to which this line belongs. Value must be zero or greater
       */
      public Row(int line, int sequenceIndex)
      {
         if (line < 0 || sequenceIndex < 0)
         {
            throw new IllegalArgumentException("Line or sequenceIndex < 0");
         }
         this.line = line;
         this.sequenceIndex = sequenceIndex;
      }

      public int getLine()
      {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public String getTag()
      {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void addFeature(String featureToAdd)
      {
      }

      public void addFeatures(Iterable<String> featuresToAdd)
      {
         if (featuresToAdd instanceof Collection)
         {
            this.features.addAll((Collection<String>) featuresToAdd);
         }
         else
         {
            for (String str : featuresToAdd)
            {
               this.features.add(str);
            }
         }
      }

      public int getSequence()
      {
         throw new UnsupportedOperationException("Not supported yet.");
      }

      public void setTag(@Nullable String tag)
      {
         this.tag = tag;
      }
   }

   /**
    * This class adds features of other lines to the current line using parsed features parameters.
    *
    * @see FeatureFileReader
    * @see FeatureParser
    */
   private final static class FeatureIntegrator
   {
      /**
       * For each line, add the features of the previous or later lines according to features tuning parameters values.
       *
       * @param schema
       *
       * @see FeatureIntegrator
       * @see FeatureParser
       */
      private static void integrateFeatures(@Nonnull FeaturesSchema schema, @Nonnull FeatureParameters parameters)
      {
         // For each line to integrate
         for (int actualLine = 0; actualLine < schema.defaultFeatures.size(); actualLine ++)
         {
            Row rowToAdd = new Row(actualLine, 0);
            rowToAdd.setTag(schema.defaultFeatures.get(actualLine).getTag());
            schema.integratedFeatures.add(rowToAdd);

            // Add to this line previous or later lines features according to features parameters (static and dynamic)
            for (int desideredRowOffset : parameters.getParameters().keySet())
            {
               int requestedLine = actualLine + desideredRowOffset;

               // requestedLine and actualLine must belong to same sentence
               if (requestedLine >= 0 && requestedLine < schema.defaultFeatures.size() && schema.defaultFeatures.get(actualLine).getSequence() == schema.defaultFeatures.get(
                     requestedLine).getSequence())
               {
                  List<String> requestedLineFeaturesToAdd = extractLineFeaturesValue(schema, parameters, actualLine + desideredRowOffset, desideredRowOffset);
                  schema.integratedFeatures.get(actualLine).features.addAll(requestedLineFeaturesToAdd);
               }
            }
         }
      }

      /**
       * Take all features values of line passed as parameter. (all features that must be considered according to features parameters)
       *
       * @param requestedline line of which we want the features. Must be between 0(inclusive) and lines count (exclusive)
       *
       * @return features values of considered line that we must consider
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

         // Take all line features that have a valid column number (a number passed by columnsToAdd)
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
            retval.add(columnFeature);
         }
         return retval;
      }
   }
}
