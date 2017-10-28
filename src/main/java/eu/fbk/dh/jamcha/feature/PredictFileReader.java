package eu.fbk.dh.jamcha.feature;

import java.nio.file.Path;
import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
      this(filePath, trainFileColumnsCount, null);
   }

   /**
    * Constructor
    *
    * @param filePath              path of file to read
    * @param trainFileColumnsCount number of columns of train file (train file must have a fixed number of columns). Number must be 3 or greater
    * @param sentenceSeparator     separator of two sentences
    */
   public PredictFileReader(@Nonnull Path filePath, @Nonnegative int trainFileColumnsCount, @Nullable final String sentenceSeparator)
   {
      super(filePath, trainFileColumnsCount, sentenceSeparator);
   }

   @Override
   protected boolean checkLineWordsCount(@Nonnull String[] line)
   {
      return  ! (line.length < (WORDS_LINE_COUNT_BASE - 1));
   }
}
