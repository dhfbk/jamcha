package eu.fbk.dh.jamcha.feature;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Reader for a train file. Train file is a file with these features: all lines have same number of words; last word of a line is line tag (the response)
 */
public final class TrainFileReader extends FeatureFileReader
{
   /**
    * Constructor
    *
    * @param filePath path of file to read
    *
    * @throws java.io.IOException file path not valid
    */
   public TrainFileReader(@Nonnull Path filePath) throws IOException
   {
      this(filePath, null);
   }

   /**
    * Constructor
    *
    * @param filePath          path of file to read
    * @param sentenceSeparator separator of two sentences. If null, default will be used
    *
    * @throws java.io.IOException file path not valid
    */
   public TrainFileReader(@Nonnull Path filePath, @Nullable final String sentenceSeparator) throws IOException
   {

      super(filePath, readColumnsCountFromFile(filePath), sentenceSeparator);
   }

   /**
    * Return number of words of a train file line. It reads first file line and assumes that all file lines have same columns count
    *
    * @return number of file columns
    *
    * @throws IOException default
    */
   private static int readColumnsCountFromFile(Path filePath) throws IOException
   {
      try (BufferedReader reader = Files.newBufferedReader(filePath))
      {
         String line = reader.readLine();
         String[] splittedLine = line.split(" ");
         return splittedLine.length;
      }
   }

   @Override
   protected boolean checkLineWordsCount(@Nonnull String[] line)
   {
      return line.length != this.WORDS_LINE_COUNT_BASE;
   }
}
