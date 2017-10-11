package eu.fbk.dh.jamcha.featureFile;

import eu.fbk.dh.jamcha.feature.Row;
import java.nio.file.Path;
import java.util.List;

public class FeatureFileReader
{

   private final int columnsNumber;

   private Path filePath;

   private List<Row> features;

   private boolean hasTags;

   private boolean fixedColumnsNumber;

   private int sequenceCounter;

   public FeatureFileReader(Path filePath, boolean hasTags, boolean fixedColumnsNumber)
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public void read()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public List<Row> getFeatures()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   public int getColumnsCount()
   {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
