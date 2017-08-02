package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SortedSetMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;

/**
 * This class adds feature of other lines to the current line using features parameters.
 *
 * @author dan92d
 */
public final class FeatureIntegrator
{
   // List of all default features for each row. (from parsed train file)
   private final ListMultimap<Integer, FeatureInfo> lineDefaultFeatures;
   private ListMultimap<Integer, Integer> rowColumns;
   private ListMultimap<Integer, FeatureInfo> lineFeatures;

   /**
    * Constructor
    *
    * @param featuresParameters list of all features parameters values (T:-8..-2)
    * @param tokensFeatures list of all features for each token read from test file
    */

   /**
    * Constructor
    * @param featuresParameters list of all features parameters values (T:-8..-2)
    * @param lineDefaultFeatures list of all features for each token read from test file
    */
   public FeatureIntegrator(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters, @Nonnull final ListMultimap<Integer, FeatureInfo> lineDefaultFeatures)
   {
      this.lineDefaultFeatures = lineDefaultFeatures;
      this.lineFeatures= ArrayListMultimap.create();
      
      // Trasforma le features da colonne->righe a righe->colonne
      rowColumns = fromColRowsToRowCols(featuresParameters);
   }

   /**
    * For each token, add the features of the previous or later token following features parameters values
    * (featuresParameters in constructor)
    *
    * @see FeatureIntegrator
    */
   public void integrateTokensFeatures()
   {
      for (int actualRow : lineDefaultFeatures.keySet())
      {
         // for each actualRow parameter add all columns
         for (int desideredRow : rowColumns.keySet())
         {
            // 
            List<FeatureInfo> lineRenamedFeatures = getLineFeatures(actualRow+desideredRow, actualRow);
            
            //Aggiungi 
            if(lineRenamedFeatures!=null)
            {
               lineFeatures.get(actualRow).addAll(lineRenamedFeatures);
            }
         }
      }

   }

   private List<FeatureInfo> getLineFeatures(int requestedline, final int baseLine)
   {
      // Se la riga richiesta è inferiore a zero oppure superiore al numero massimo di righe
      if (requestedline < 0 || requestedline>=lineDefaultFeatures.keySet().size())
      {
         return null;
      }
      
      List<Integer> featsNumbers= rowColumns.get(requestedline-baseLine);
      
      // all default features
      ArrayList<FeatureInfo> retval = new ArrayList<>(lineFeatures.size());

      /**
       * Take all line features that have a valid column number (a number passed in featsNumbers) Then rename all features
       * row as a
       */
      for (FeatureInfo info : lineDefaultFeatures.get(requestedline))
      {
         for (int col : featsNumbers)
         {
            if (info.getColumn() == col)
            {
               FeatureInfo newInfo = new FeatureInfo(requestedline-baseLine, info.getColumn(), info.getFeatureValueLight());
               retval.add(newInfo);
            }
         }
      }

      return retval;
   }
   
    /**
    * Switch from col->rows to actualRow->cols
    *
    * @param featuresParameters features multimap with this structure: column->rows
    * @return argument parameter inverted, for each actualRow list all columns to consider
    */
   public ListMultimap<Integer, Integer> fromColRowsToRowCols(SortedSetMultimap<Integer, Integer> featuresParameters)
   {
      Set<Integer> keySet = featuresParameters.keySet();
      ArrayListMultimap<Integer, Integer> rowCols = ArrayListMultimap.create(keySet.size(), featuresParameters.size() / keySet.size());
      keySet.forEach((col) ->
      {
         SortedSet<Integer> rows = featuresParameters.get(col);
         for (int row : rows)
         {
            rowCols.put(row, col);
         }
      });
      return rowCols;
   }
   
   protected ListMultimap<Integer, FeatureInfo> getDefaultFeaturesMap()
   {
      return this.lineDefaultFeatures;
   }
   
    protected ListMultimap<Integer, FeatureInfo> getIntegratedtFeaturesMap()
   {
      return this.lineFeatures;
   }
}
