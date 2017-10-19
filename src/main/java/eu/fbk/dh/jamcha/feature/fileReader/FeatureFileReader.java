package eu.fbk.dh.jamcha.feature.fileReader;

import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.dh.jamcha.feature.FeaturesSchema;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
    protected final int COLUMNS_COUNT_BASE;

    /**
     * Separator for two different sentences
     */
    private final String SENTENCE_BOUNDARY_MARKER;

    @Nonnull
    protected final Path filePath;
    protected ArrayList<Line> features;

    /**
     * Constructor
     *
     * @param filePath            path of file to read
     * @param columnsCountWithTag minimun number of words for every line
     */
    protected FeatureFileReader(@Nonnull Path filePath, int columnsCountWithTag)
    {
        this(filePath, columnsCountWithTag, null);
    }

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
        this.COLUMNS_COUNT_BASE = columnsCountWithTag;

        // Sentence separator
        if (sentenceSeparator != null)
        {
            SENTENCE_BOUNDARY_MARKER = sentenceSeparator;
        }
        else
        {
            SENTENCE_BOUNDARY_MARKER = "";
        }
    }

    /**
     * Read features file. File can have tag
     *
     * @return all lines features
     *
     * @throws IOException default
     */
    @Nonnull
    public final FeaturesSchema read() throws IOException
    {
        try (BufferedReader reader = Files.newBufferedReader(filePath))
        {
            features = new ArrayList<>(500);
            String line;
            final int columnsTagIndex = COLUMNS_COUNT_BASE - 1;
            final int columnsNoTagMaxIndex = columnsTagIndex - 1;

            int rowCounter = 0;
            int sequenceIndex = 0;

            // Read file lines and get features of each line
            while ((line = reader.readLine()) != null)
            {
                if ( ! line.equals(SENTENCE_BOUNDARY_MARKER))
                {
                    // Split read line and check possible incorrect number of words(columns)
                    String[] lineWords = line.split(" ");

                    checkLineWordsCount(lineWords);

                    String tag = null;
                    if (lineWords.length >= COLUMNS_COUNT_BASE)
                    {
                        tag = lineWords[columnsTagIndex];
                    }

                    Line row = new Line(rowCounter, sequenceIndex);
                    row.setTag(tag);

                    //Put row features to global structure
                    ArrayList<String> lineFeatures = new ArrayList<>(columnsNoTagMaxIndex);
                    for (int column = 0; column <= columnsNoTagMaxIndex; column ++)
                    {
                        lineFeatures.add(lineWords[column]);
                    }

                    row.addFeatures(lineFeatures);
                    features.add(row);
                    rowCounter ++;
                }
                else
                {
                    sequenceIndex ++;
                }
            }
            FeaturesSchema schema = FeaturesSchema.build(this);
            return schema;
        }
    }

    @Nullable
    public List<Line> getFeatures()
    {
        return this.features;
    }

    /**
     *
     * @param line line words
     *
     * @throws IOException line words count is not a permitted value
     */
    protected abstract void checkLineWordsCount(@Nonnull String[] line) throws IOException;
}
