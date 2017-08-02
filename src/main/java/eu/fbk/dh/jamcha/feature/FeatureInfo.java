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
   private char[] value;

   public FeatureInfo(int row, short column, @Nonnull final char[] featureValue)
   {
      this.row = row;
      this.column = column;
      this.value = featureValue;
   }

   public FeatureInfo(int row, short column, @Nonnull String featureValue)
   {
      this(row, column, featureValue.toCharArray());
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
      this.row = row;
   }

   public void setColumn(short col)
   {
      if (col > -1)
      {
         this.column = col;
      }
   }
   
   public void setValue(String value)
   {
      if(value!=null)
      {
         this.value=value.toCharArray();
      }
   }

   public String getFeatureValue()
   {
      return new String(this.value);
   }
   
   public char[] getFeatureValueLight()
   {
      return this.value;
   }

   @Override
   public String toString()
   {
//      StringBuilder build = new StringBuilder(value.length);
//      build.append(value);
//      return build.toString();
      return row + "_" + column + "_" + String.valueOf(value);
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
