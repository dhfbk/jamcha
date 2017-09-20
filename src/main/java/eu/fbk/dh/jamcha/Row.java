package eu.fbk.dh.jamcha;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;

/**
 * A row with all of its features and its tag
 */
public class Row
{

   private final int rowNumber;
   private final String tag;
   private List<String> features;

   public Row(int rowNumber, String tag, List<String> rowFeatures)
   {
      this.rowNumber = rowNumber;
      this.tag = tag;
      this.features = rowFeatures;
   }

   public Row(int rowNumber, @Nonnull String tag)
   {
      this(rowNumber, tag, new ArrayList<String>());
   }

   public int getRowNumber()
   {
      return rowNumber;
   }

   /**
    *
    * @return tag (true value, the response) of this line
    */
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
         if (this.rowNumber == row.getRowNumber() && this.tag.equals(row.getTag()) && this.features.equals(row.getFeatures()))
         {
            retval = true;
         }
      }
      return retval;
   }

}
