package eu.fbk.dh.jamcha.feature;

import java.util.Arrays;
import javax.annotation.Nonnull;

/**
 * Class representing a feature position in a table schema and its value
 *
 * @author dan92d
 */
public final class FeatureInfo implements Comparable<FeatureInfo>
{

   private int row;
   private short column;

   @Nonnull
   private final char[] value;

   public FeatureInfo(int row, short column, @Nonnull final char[] featureValue)
   {
      this.row = row;
      this.column = column;
      this.value = featureValue;
   }

   public int getRow()
   {
      return row;
   }

   public short getColumn()
   {
      return column;
   }

   public void setRow(int row)
   {
      if (row >= 0)
      {
         this.row = row;
      }
   }

   public void setColumn(short col)
   {
      this.column = col;
   }

   public String getFeatureValue()
   {
      return Arrays.toString(this.value);
   }

   @Override
   public String toString()
   {
      StringBuilder build = new StringBuilder(value.length);
      build.append(value);
      return row + "_" + column + "_" + build.toString();
   }

   @Override
   public int compareTo(FeatureInfo o)
   {
      int retval;
      if (this.getRow() < o.getRow())
      {
         retval = -1;
      }
      else
      {
         if (this.getRow() > o.getRow())
         {
            retval = 1;
         }
         else
         {
            if (this.getColumn() < o.getColumn())
            {
               retval = -1;
            }
            else
            {
               if (this.getColumn() > o.getColumn())
               {
                  retval = 1;
               }
               else
               {
                  retval = Arrays.toString(this.value).compareTo(Arrays.toString(o.value));
                  System.out.println("Equals");
               }
            }
         }
      }
      return retval;
   }

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      if (!(obj instanceof FeatureInfo))
      {
         return false;
      }
      boolean rowsFlag = ((FeatureInfo) obj).getRow() == this.getRow();
      boolean colsFlag = ((FeatureInfo) obj).getColumn() == this.getColumn();
      boolean valuesFlag = Arrays.equals(((FeatureInfo) obj).value, this.value);
      return rowsFlag && colsFlag && valuesFlag;
   }

   @Override
   public int hashCode()
   {
      int hash = 5;
      hash = 31 * hash + this.row;
      hash = 31 * hash + this.column;
      hash = 31 * hash + Arrays.hashCode(this.value);
      return hash;
   }
}
