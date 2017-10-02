package eu.fbk.dh.jamcha;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import javax.annotation.Nonnull;

/**
 * Class that represents feature tuning parameters (e.g. values of static and dynamic features)
 */
public final class LineFeaturesMap
{

  private final TreeMultimap<Integer, Integer> features;
  final int columnsCount;

  /**
   * Build Map of all feature values from a schema where
   *
   * @param featureParameters map witch a features schema: column -> list of lines that consider this columns
   * @param columnsCount  number of columns of a line, tag column included
   * @param convert True: featureParameter has the schema column -> list of lines that consider this columns. False: featureParameter has the schema line -> all columns to be considered for this line
   */
  public LineFeaturesMap(@Nonnull SortedSetMultimap<Integer, Integer> featureParameters, int columnsCount, boolean convert)
  {
    this.columnsCount = columnsCount;

    if (convert)
    {
      Set<Integer> keySet = featureParameters.keySet();
      features = TreeMultimap.create();
      keySet.forEach((col) ->
      {
        SortedSet<Integer> rows = featureParameters.get(col);
        for (int row : rows)
        {
          features.put(row, col);
        }
      });
    }
    else
    {
      this.features = TreeMultimap.create(featureParameters);
    }
  }

  /**
   * Save features parameters to a file
   *
   * @param filePath
   * @throws java.io.IOException {@link  IOException }
   */
  public void saveTo(@Nonnull Path filePath) throws IOException
  {
    try (BufferedWriter writer = Files.newBufferedWriter(filePath))
    {
      // FILE STRUCTURE: columnsNumber
      //                 line -> list of columns to consider
      writer.append("" + this.columnsCount);
      for (int row : features.keySet())
      {
        writer.newLine();
        NavigableSet<Integer> rowValues = features.get(row);
        writer.append(row + "");
        for (int col : rowValues)
        {
          writer.append(" " + col);
        }
      }
    }
  }

  /**
   * Load a file containing features parameters and count of columns that have train file 
   * @param filePath filePath of the file (if you don't know file location, try in the same location of model generated file)
   * @return an instance of LineFeaturesMap containing feature parameters read from file
   * @throws IOException {@link IOException}
   * @throws NumberFormatException file contains invalid line or column numbers
   */
  public static LineFeaturesMap loadFrom(@Nonnull Path filePath) throws IOException, NumberFormatException
  {
    try (BufferedReader reader = Files.newBufferedReader(filePath))
    {
      // Get columns count
      int colsCount = Integer.parseInt(reader.readLine());
      String line;
      TreeMultimap<Integer, Integer> featuresMap = TreeMultimap.create();
      
      // Parse all file lines with this schema: lineNumber columnIndex1 columnsIndex2 and so on
      while ((line = reader.readLine()) != null)
      {
        String[] lineSplitted = line.split(" ");
        if (lineSplitted != null && lineSplitted.length > 1)
        {
          int lineNumber = Integer.parseInt(lineSplitted[0]);

          Integer[] colsArray = new Integer[lineSplitted.length - 1];
          for (int i = 1; i < lineSplitted.length; i++)
          {
            int parsedCol = Integer.parseInt(lineSplitted[i]);
            colsArray[i - 1] = parsedCol;
          }
          featuresMap.putAll(lineNumber, Arrays.asList(colsArray));
        }
      }
      return new LineFeaturesMap(featuresMap, colsCount, false);
    }
  }
}
