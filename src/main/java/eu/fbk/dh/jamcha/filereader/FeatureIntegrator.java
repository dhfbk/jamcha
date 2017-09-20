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

  private final List<Integer> sentencesStartLines;

  /**
   * Constructor
   *
   * @param featuresParameters  list of all features parameters values (T:-8..-2)
   * @param defaultFeatures     list of all features for each token read from test file
   * @param sentencesStartLines list of lines index that are first sentence line. (i.e.
   */
  public FeatureIntegrator(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters,
                           @Nonnull final List<Row> defaultFeatures, @Nonnull final List<Integer> sentencesStartLines)
  {
    this.defaultFeatures = defaultFeatures;
    this.integratedFeatures = new ArrayList<>(defaultFeatures.size());
    this.sentencesStartLines = sentencesStartLines;

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
    int sentenceIndex = 0;

    // For each line
    for (int actualRow = 0; actualRow < defaultFeatures.size(); actualRow++)
    {
      // Se la riga attuale è più grande della prossima linea iniziale
      int nextUpperRowLimit=sentencesStartLines.get(sentenceIndex+1);
      if (sentenceIndex < sentencesStartLines.size() - 1 && actualRow >= sentencesStartLines.get(sentenceIndex+1))
      {
        sentenceIndex++;
      }

      // Set row number and tag
      integratedFeatures.add(new Row(actualRow, defaultFeatures.get(actualRow).getTag()));

      // Add to this line previous or later line features according to features parameters (static and dynamic)
      for (int desideredRowOffset : rowColumns.keySet())
      {
        List<String> requestedLineFeaturesToAdd = this.extractLineFeaturesValue(actualRow + desideredRowOffset, sentenceIndex);

        if (requestedLineFeaturesToAdd != null)
        {
          integratedFeatures.get(actualRow).getFeatures().addAll(requestedLineFeaturesToAdd);
        }
      }
    }
  }

  /**
   * Take all features values of line passed as parameter. (all features that must be considered according to features
   * parameters)
   *
   * @param requestedline line of which we want the features
   * @param sentenceIndex index of sentencesStartLine representing the first occurrence of the sentence to which the line
   *                      belongs to which we want to add the features. In other word, every sentence is represented by its
   *                      starting line number.
   * @return features values of considered line that we must consider
   */
  private List<String> extractLineFeaturesValue(int requestedline, int sentenceIndex)
  {
    // La riga richiesta deve appartenere alla stessa frase della linea da integrare
    if (requestedline >= defaultFeatures.size() || requestedline < sentencesStartLines.get(sentenceIndex) || requestedline >= sentencesStartLines.get(
        sentenceIndex + 1))
    {
      return null;
    }

    // List of columns numbers to consider of the requested line
    Set<Integer> lineFeaturesNumbers = rowColumns.get(requestedline);

    // Contains all columns values for this line
    ArrayList<String> retval = new ArrayList<>();

    // Take all line features that have a valid column number (a number passed by lineFeaturesNumbers)
    for (int column : lineFeaturesNumbers)
    {
      String columnFeature;
      if (column != DynamicFeatureParser.COLUMN_VALUE)
      {
        columnFeature = defaultFeatures.get(requestedline).getFeatures().get(column);
      }
      else
      {
        columnFeature = defaultFeatures.get(requestedline).getTag();
      }
      retval.add(columnFeature);
    }

    return retval;
  }

  /**
   * Switch from column->rows to row->cols. In other words, input is a list of lines to consider for each column. Output is a
   * list of all columns to consider for each line
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
