package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.IO.FeatureFileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FeaturesSchema
{

   private ArrayList<Row> defaultFeatures;

   private ArrayList<Row> integratedFeatures;

   private List<String> tagsIndexes;

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

   public void integrate(FeatureParameters parameters)
   {
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
}
