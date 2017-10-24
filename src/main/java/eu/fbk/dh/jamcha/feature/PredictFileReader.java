package eu.fbk.dh.jamcha.feature;

import java.io.IOException;
import java.nio.file.Path;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * Reader for a file that must be classified
 */
public final class PredictFileReader extends FeatureFileReader
{
    /**
     * Constructor
     *
     * @param filePath              path of file to read
     * @param trainFileColumnsCount number of columns of train file (train file must have a fixed number of columns). Number must be 3 or greater
     */
    public PredictFileReader(@Nonnull Path filePath, @Nonnegative int trainFileColumnsCount)
    {
        super(filePath, trainFileColumnsCount);
    }

    /**
     * Constructor
     *
     * @param filePath              path of file to read
     * @param trainFileColumnsCount number of columns of train file (train file must have a fixed number of columns). Number must be 3 or greater
     * @param sentenceSeparator     separator of two sentences
     *
     * @throws java.io.IOException file path not valid
     */
    public PredictFileReader(@Nonnull Path filePath, @Nonnegative int trainFileColumnsCount, final String sentenceSeparator)
    {
        super(filePath, trainFileColumnsCount, sentenceSeparator);
    }

    @Override
    protected void checkLineWordsCount(@Nonnull String[] line) throws IOException
    {
        if (line.length < WORDS_LINE_COUNT_BASE - 1)
        {
            throw new IOException("Line words count is less than " + (WORDS_LINE_COUNT_BASE - 1));
        }
    }
}
