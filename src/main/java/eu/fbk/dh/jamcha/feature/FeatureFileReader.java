package eu.fbk.dh.jamcha.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class FeatureFileReader
{

  /**
   * Minimum number of words that every line must have
   */
  public static final int COLUMNS_COUNT_MIN = 3;

  /**
   * Number of words of file line, tag column included
   */
  @Nonnegative
  protected final int WORDS_LINE_COUNT_BASE;

  /**
   * Separator for two different sentences
   */
  private final String SENTENCE_BOUNDARY_MARKER;

  @Nonnull
  private final Path filePath;
  protected ArrayList<Line> features;

  /**
   * Constructor
   *
   * @param filePath            path of file to read
   * @param columnsCountWithTag minimun number of words for every line
   * @param sentenceSeparator   separator that divides two sentences
   */
  protected FeatureFileReader(@Nonnull Path filePath, int columnsCountWithTag, final String sentenceSeparator)
  {
    // File Path
    this.filePath = filePath;

    // Columns count
    if (columnsCountWithTag < COLUMNS_COUNT_MIN)
    {
      throw new IllegalArgumentException("Columns count is less than " + COLUMNS_COUNT_MIN);
    }
    this.WORDS_LINE_COUNT_BASE = columnsCountWithTag;

    // Sentence separator
    this.SENTENCE_BOUNDARY_MARKER = sentenceSeparator != null ? sentenceSeparator : "";
  }

  /**
   * Read features file. File can have tag
   *
   * @return all lines features
   *
   * @throws IOException default
   */
  @Nonnull
  public final List<Line> read() throws IOException
  {
    try (BufferedReader reader = Files.newBufferedReader(filePath))
    {
      features = new ArrayList<>(500);
      String line;
      final int columnsTagIndex = WORDS_LINE_COUNT_BASE - 1;
      final int columnsNoTagMaxIndex = columnsTagIndex - 1;

      int rowCounter = 0;
      int sequenceIndex = 0;

      // Read file lines and get features of each line
      while ((line = reader.readLine()) != null)
      {
        if (!line.equals(SENTENCE_BOUNDARY_MARKER))
        {
          // Split read line and check possible incorrect number of words(columns)
          String[] lineWords = line.split(" ");

          if (!checkLineWordsCount(lineWords))
          {
            throw new IOException("Line " + rowCounter + " has an invalid words count");
          }

          //Put row features to global structure
          ArrayList<String> lineFeatures = new ArrayList<>(columnsNoTagMaxIndex);
          for (int column = 0; column <= columnsNoTagMaxIndex; column++)
          {
            if (!lineWords[column].equals("__nil__"))
            {
              lineFeatures.add(lineWords[column]);
            }
          }
          String tag = lineWords.length >= WORDS_LINE_COUNT_BASE ? lineWords[columnsTagIndex] : null;
          Line row = new Line(rowCounter, sequenceIndex, tag, lineFeatures);
          features.add(row);
          rowCounter++;
        }
        else
        {
          sequenceIndex++;
        }
      }
      return this.features;
    }
  }

  @Nullable
  public List<Line> getLines()
  {
    return this.features;
  }

  @Nonnegative
  public final int getLineWordsCount()
  {
    return this.WORDS_LINE_COUNT_BASE;
  }

  /**
   *
   * @param line line words
   *
   * @return true if line length is correct
   */
  protected abstract boolean checkLineWordsCount(@Nonnull String[] line);
}
