package eu.fbk.dh.jamcha;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A row with all of its features and its tag
 */
public class Row
{

   private final int rowNumber;
   private final int sequenceIndex;
   private final String tag;
   private List<String> features;

   public Row(int rowNumber, int sequenceIndex, String tag, List<String> rowFeatures)
   {
      this.rowNumber = rowNumber;
      this.sequenceIndex = sequenceIndex;
      this.tag = tag;
      this.features = rowFeatures;
   }

   public Row(int rowNumber, int sequenceIndex, @Nonnull String tag)
   {
      this(rowNumber, sequenceIndex, tag, new ArrayList<String>());
   }

   public int getRowNumber()
   {
      return rowNumber;
   }

   /**
    *
    * @return tag (true value, the response) of this line
    */
   @Nullable
   public String getTag()
   {
      return tag;
   }

   public List<String> getFeatures()
   {
      return features;
   }

   @Override
   public boolean equals(Object o)
   {
      boolean retval = false;
      if (o instanceof Row)
      {
         Row row = (Row) o;
         if (this.rowNumber == row.getRowNumber() && this.sequenceIndex == row.getSequenceIndex() && this.tag.equals(row.getTag()) && this.features.equals(
            row.getFeatures()))
         {
            retval = true;
         }
      }
      return retval;
   }

   public int getSequenceIndex()
   {
      return sequenceIndex;
   }

   @Override
   public int hashCode()
   {
      int hash = 7;
      hash = 19 * hash + this.rowNumber;
      hash = 19 * hash + this.sequenceIndex;
      hash = 19 * hash + Objects.hashCode(this.tag);
      hash = 19 * hash + Objects.hashCode(this.features);
      return hash;
   }

}
