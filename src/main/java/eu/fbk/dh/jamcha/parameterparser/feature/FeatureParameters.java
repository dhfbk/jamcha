package eu.fbk.dh.jamcha.parameterparser.feature;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Contains all rows and all columns used by a feature entry
 *
 * @author dan92
 */
public final class FeatureParameters
{

   private ArrayList<Integer> rows;
   private ArrayList<Integer> columns;

   /**
    * @return the rows
    */
   @Nonnull
   public List<Integer> getRows()
   {
      if(rows==null)
      {
         rows=new ArrayList<>();
      }
      return rows;
   }

   /**
    * @param rows the rows to set
    */
   @Nonnull
   public void setRows(@Nonnull List<Integer> rows)
   {
      this.rows = new ArrayList<>(rows);
   }

   /**
    * @return the columns
    */
   @Nonnull
   public List<Integer> getColumns()
   {
      if(columns==null)
      {
         columns=new ArrayList<>();
      }
      return columns;
   }

   /**
    * @param columns the columns to set
    */
   @Nonnull
   public void setColumns(@Nonnull List<Integer> columns)
   {
      this.columns = new ArrayList<>(columns);
   }

   /**
    * Adds a list in the specified position. For position permitted values see below.
    *
    * @param listToAdd list to add, non null
    * @param position list position: in others words, this list contains rows or columns numbers?. Value permitted: 0(rows),
    * 1(columns)
    * 
    * @exception IllegalArgumentException if position has an invalid value
    */
   public void addList(@Nonnull List<Integer> listToAdd, int position)
   {
      switch (position)
      {
         case 0:
            setRows(listToAdd);
         case 1:
            setColumns(listToAdd);
         default:
            throw
            new IllegalArgumentException("Position value must be 0 or 1");
      }
   }
   
   @Override
   public boolean equals(Object obj)
   {
      if(!(obj instanceof FeatureParameters))
      {
         return false;
      }
      
      return ((FeatureParameters)obj).rows.equals(this.rows) && ((FeatureParameters)obj).columns.equals(this.columns);
      
   }
}
