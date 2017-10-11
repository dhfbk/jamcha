package eu.fbk.dh.jamcha.feature.IO;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

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
      super(filePath, readColumnsCountFromFile(filePath));
   }

   /**
    * Constructor
    *
    * @param filePath          path of file to read
    * @param sentenceSeparator separator of two sentences
    *
    * @throws java.io.IOException file path not valid
    */
   public TrainFileReader(@Nonnull Path filePath, final String sentenceSeparator) throws IOException
   {
      super(filePath, readColumnsCountFromFile(filePath), sentenceSeparator);
   }

   /**
    * Return number of columns of a train file. It reads first file line and assumes that all file lines have same columns count
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
   protected void checkLineWordsCount(@Nonnull String[] line) throws IOException
   {
      if (line.length != this.COLUMNS_COUNT_BASE)
      {
         throw new IOException("Line words count is not equal to " + COLUMNS_COUNT_BASE);
      }
   }
}
