package eu.fbk.dh.jamcha.filereader;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import eu.fbk.dh.jamcha.Row;
import eu.fbk.dh.jamcha.parameterparser.feature.DynamicFeatureParser;
import eu.fbk.dh.jamcha.parameterparser.feature.FeatureParser;
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
  private final SortedSetMultimap<Integer, Integer> rowColumns;

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
  public FeatureIntegrator(@Nonnull final SortedSetMultimap<Integer, Integer> featuresParameters, @Nonnull final List<Row> defaultFeatures)

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
  public final void integrateFeatures()
  {
    // For each line to integrate
    for (int actualLine = 0; actualLine < defaultFeatures.size(); actualLine++)
    {
      integratedFeatures.add(new Row(actualLine, 0, defaultFeatures.get(actualLine).getTag()));

      // Add to this line previous or later lines features according to features parameters (static and dynamic)
      for (int desideredRowOffset : rowColumns.keySet())
      {
        int requestedLine = actualLine + desideredRowOffset;

        // requestedLine and actualLine must belong to same sentence
        if (requestedLine >= 0 && requestedLine < defaultFeatures.size() && defaultFeatures.get(actualLine).getSequenceIndex() == defaultFeatures.get(
            requestedLine).getSequenceIndex())
        {
          List<String> requestedLineFeaturesToAdd = this.extractLineFeaturesValue(actualLine + desideredRowOffset,
                                                                                  desideredRowOffset);
          integratedFeatures.get(actualLine).getFeatures().addAll(requestedLineFeaturesToAdd);
        }
      }
    }
  }

  /**
   * Take all features values of line passed as parameter. (all features that must be considered according to features
   * parameters)
   *
   * @param requestedline line of which we want the features. Must be between 0(inclusive) and lines count (exclusive)
   *
   * @return features values of considered line that we must consider
   */
  @Nonnull
  private List<String> extractLineFeaturesValue(int requestedline, int offset)
  {
    if (requestedline < 0 || requestedline >= defaultFeatures.size())
    {
      throw new IllegalArgumentException(
          "Requested line must be between zero and " + (defaultFeatures.size() - 1) + "(inclusive)");
    }

    // List of columns numbers to consider of the requested line
    Set<Integer> columnsToAdd = rowColumns.get(offset);

    // Contains all columns values for this line
    ArrayList<String> retval = new ArrayList<>(defaultFeatures.get(0).getFeatures().size());

    // Take all line features that have a valid column number (a number passed by columnsToAdd)
    for (int column : columnsToAdd)
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
   *
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

  public List<Row> getIntegratedFeatures()
  {
    return this.integratedFeatures;
  }
}
