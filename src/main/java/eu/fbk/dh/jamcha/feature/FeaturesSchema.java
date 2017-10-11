package eu.fbk.dh.jamcha.feature;

import java.util.List;

public class FeaturesSchema
{

   private List<Row> defaultFeatures;

   private List<Row> integratedFeatures;

   private List<String> tagsIndexes;

   private FeatureParameters parameters;

   public FeaturesSchema(FeatureFileReader reader)
   {
   }

   private void createTagsIndexes()
   {
   }

   private void readDefaultFile()
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
}
