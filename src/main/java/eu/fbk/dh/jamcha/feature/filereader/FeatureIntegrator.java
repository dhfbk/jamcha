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
public class FeatureIntegrator
{

   private final SortedSetMultimap<Integer, Integer> featuresParameters;
   private ListMultimap<Integer, FeatureInfo> tokensFeatures;

   /**
    * Constructor
    *
    * @param featuresParameters list of all features parameters values (T:-8..-2)
    * @param tokensFeatures list of all features for each token read from test file
    */
   public FeatureIntegrator(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters, @Nonnull ListMultimap<Integer, FeatureInfo> tokensFeatures)
   {
      this.featuresParameters = featuresParameters;
      this.tokensFeatures = tokensFeatures;
   }

   /**
    * For each token, add the features of the previous or later token following features parameters values
    * (featuresParameters in constructor)
    *
    * @see FeatureIntegrator
    */
   public void integrateTokensFeatures()
   {
      // STRING: row_col_value -> -5_2_avverbio
      // PARAMETER: col->values     featuresParameters
      // FILE: row->0_2_avverbio    tokensFeatures

      ListMultimap<Integer, Integer> rowColumns = fromColRowsToRowCols();
      // for each file row
      for (int key : tokensFeatures.keySet())
      {
         // for each row parameter add row features
         for (int rowFeatures : rowColumns.keySet())
         {
            List<FeatureInfo> lineRenamedFeatures = getLineFeatures(key + rowFeatures, key, rowColumns.get(rowFeatures));
            
            //Aggiungi 
            if(lineRenamedFeatures!=null)
            {
               tokensFeatures.get(key).addAll(lineRenamedFeatures);
            }
         }
      }

   }

   /**
    * Extract features of passed row token, read from train file. N.B. Training file must be read before calling this method
    *
    * @param row line number, starting from zero, which we want to get the default features
    * @return
    */
   public List<FeatureInfo> extractDefaultFeatures(int row)
   {
      List<FeatureInfo> rowFeatures = tokensFeatures.get(row);
      List<FeatureInfo> retval = new ArrayList<>(rowFeatures.size());
      for (FeatureInfo feature : rowFeatures)
      {
         if (feature.getRow() == 0)
         {
            retval.add(feature);
         }
      }
      return retval;
   }

   /**
    * Switch from col->rows to row->cols
    *
    * @return
    */
   public ListMultimap<Integer, Integer> fromColRowsToRowCols()
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

   public List<FeatureInfo> getLineFeatures(int requestedline, final int baseLine, @Nonnull final List<Integer> featsNumbers)
   {
      if (requestedline < 0)
      {
         return null;
      }
      // all default features
      List<FeatureInfo> lineFeatures = extractDefaultFeatures(requestedline);
      ArrayList<FeatureInfo> retval = new ArrayList<>(lineFeatures.size());
      requestedline = requestedline - baseLine;

      /**
       * Take all line features that have a valid column number (a number passed in featsNumbers) Then rename all features
       * row as a
       */
      for (FeatureInfo info : lineFeatures)
      {
         for (int col : featsNumbers)
         {
            if (info.getColumn() == col)
            {
               info.setRow(requestedline);
               retval.add(info);
            }
         }
      }

      return retval;
   }
}
