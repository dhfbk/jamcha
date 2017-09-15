package eu.fbk.dh.jamcha.filereader;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import eu.fbk.dh.jamcha.Row;
import eu.fbk.dh.jamcha.parameterparser.feature.DynamicFeatureParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;

/**
 * This class adds features of other lines to the current line using parsed features parameters.
 *
 * @see FeatureFileReader
 * @see FeatureParser
 */
public final class FeatureIntegrator
{

   /**
    * List of all default features for each row. (from parsed train file)
    */
   private final List<Row> defaultFeatures;

   /**
    * For each row lists alla columns that must be considered
    */
   private SortedSetMultimap<Integer, Integer> rowColumns;

   /**
    * All integrated features for each each row
    */
   private List<Row> integratedFeatures;

   /**
    * Constructor
    *
    * @param featuresParameters list of all features parameters values (T:-8..-2)
    * @param defaultFeatures    list of all features for each token read from test file
    */
   public FeatureIntegrator(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters,
                            @Nonnull final List<Row> defaultFeatures)
   {
      this.defaultFeatures = defaultFeatures;
      this.integratedFeatures = new ArrayList<>(defaultFeatures.size());

      // Trasforma le features da colonne->righe a righe->colonne
      rowColumns = fromColRowsToRowCols(featuresParameters);
   }

   /**
    * For each line, add the features of the previous or later lines according to features parameters values
    * (featuresParameters in constructor).
    *
    * @see FeatureIntegrator
    * @see FeatureParser
    */
   public void integrateFeatures()
   {  
      // For each line
      for (int actualRow=0; actualRow<defaultFeatures.size(); actualRow++)
      {
         // Set row number and tag
         integratedFeatures.set(actualRow, new Row(actualRow, defaultFeatures.get(actualRow).getTag()));
         
         // Add to this line previous or later line features according to features parameters (static and dynamic)
         for (int desideredRowOffset : rowColumns.keySet())
         {
            List<String> requestedLineFeaturesToAdd = this.extractLineFeaturesValue(actualRow + desideredRowOffset);
            
            if (requestedLineFeaturesToAdd != null)
            {
               integratedFeatures.get(actualRow).getFeatures().addAll(requestedLineFeaturesToAdd);
            }
         }
      }
   }

   /**
    * Take all features values of line passed as parameter. (all features that must be considered according to features parameters)
    * @param requestedline line of which we want the features 
    * @return features values of considered line that we must consider
    */
   private List<String> extractLineFeaturesValue(int requestedline, int sentenceStartLine)
   {
      // Se la riga richiesta è inferiore a zero oppure superiore al numero massimo di righe
      if (requestedline < 0 || requestedline >= defaultFeatures.size())
      {
         return null;
      }

      // List of columns numbers to consider of the requested line
      Set<Integer> lineFeaturesNumbers = rowColumns.get(requestedline);

      // Contains all columns values for this line
      ArrayList<String> retval = new ArrayList<>();
      
      // Take all line features that have a valid column number (a number passed by lineFeaturesNumbers)
      for(int column : lineFeaturesNumbers)
      {
         String columnFeature;
         if(column!=DynamicFeatureParser.COLUMN_VALUE)
         {
            columnFeature= defaultFeatures.get(requestedline).getFeatures().get(column);
         }
         else
         {
            columnFeature=defaultFeatures.get(requestedline).getTag();
         }
         retval.add(columnFeature);
      }

      return retval;
   }

   /**
    * Switch from column->rows to row->cols. In other words, input is a list of lines to consider for each column.
    * Output is a list of all columns to consider for each line
    *
    * @param featuresParameters features multimap with this structure: column->rows
    * @return list of all columns to consider for each line (e.g. for line 4 consider feature-column number 0,3,4)
    */
   @Nonnull
   private SortedSetMultimap<Integer, Integer> fromColRowsToRowCols(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters)
   {
      Set<Integer> keySet = featuresParameters.keySet();
      SortedSetMultimap<Integer, Integer> rowCols = TreeMultimap.create();
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

   protected List<Row> getIntegratedFeatures()
   {
      return this.integratedFeatures;
   }
}
