package eu.fbk.dh.jamcha.filereader;

import eu.fbk.dh.jamcha.Row;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * This class parses a features file
 *
 */
public class FeatureFileReader
{

   private int colsNumber = 0;
   private Path filePath;
   private boolean isTrainFile;
   
   /**
    * Separator for two different sentences
    */
   private final String SENTENCE_BOUNDARY_MARKER="";

   /**
    * List of all features of each row
    */
   private List<Row> rowFeatures = new ArrayList<>();
   
   /**
    * List of all lines indexes that are first occurrence of a sentence (inclusive). File can contains multiple sentences. These are separated by a line represented by SENTENCE_BOUNDARY_MARKER.
    * 
    */
   private List<Integer> sentencesStartLines=new ArrayList<>();

   /**
    * Constructor
    *
    * @param filePath    path of file to parse
    * @param isTrainFile this file is a train file? In other words, contains solutions(tag)? (line solution tag must be in
    *                    the last column)
    */
   public FeatureFileReader(@Nonnull Path filePath, boolean isTrainFile)
   {
      this.filePath = filePath;
      this.isTrainFile = isTrainFile;
   }

   /**
    * Parse a file and all row features. Each file line must have same number of words.
    *
    * @return list of read features for every file line
    * @throws IOException cannot open/read file or file does not have same words number for every line
    */
   protected final List<Row> parseFile() throws IOException
   {
      // ADD: gestire le righe bianche alla fine del file
      // ADD: gestire più paragrafi (EOS)

      // Open file to read
      BufferedReader reader = Files.newBufferedReader(filePath);
      colsNumber = getColumnsCount();
      sentencesStartLines.add(0);

      String line;
      int columnsMaxIndex = colsNumber - (isTrainFile ? 1 : 0);
      int rowCounter = 0;

      // Read file lines and get features of each line
      while ((line = reader.readLine()) != null)
      {
         if(!line.equals(SENTENCE_BOUNDARY_MARKER))
         {
            // Split read line and check possible incorrect number of words(columns)
         String[] lineWords = line.split(" ");
         if (lineWords.length != colsNumber)
         {
            throw new IOException("All file lines must have same number of words");
         }

         // Integrate row features and put them to global structure
         ArrayList<String> lineFeatures = new ArrayList<>(columnsMaxIndex);
         for (int column = 0; column < columnsMaxIndex; column++)
         {
            lineFeatures.add(lineWords[column]);
         }
         Row row = new Row(rowCounter, lineWords[lineWords.length - 1], lineFeatures);
         rowFeatures.add(row);

         rowCounter++;
         }
         else
         {
            sentencesStartLines.add(rowCounter);
         }
         
      }
      return rowFeatures;
   }

   /**
    * Obtains number of columns in train file. Columns are separated by " " (space). Assumption: all lines of file have same
    * number of columns(words). This method checks first line of train file.
    *
    * @return number of columns in this file
    * @throws java.io.IOException
    */
   public int getColumnsCount() throws IOException
   {
      if (colsNumber <= 0)
      {
         try (BufferedReader reader = Files.newBufferedReader(filePath))
         {
            colsNumber = reader.readLine().split(" ").length;
         }
      }
      return colsNumber;
   }

   /**
    * GET: all features values for each line
    *
    * @return list of all features of each row (tag included)
    */
   public List<Row> getRowsFeatures()
   {
      return rowFeatures;
   }

   public void setFilePath(@Nonnull Path newFilePath, boolean isTrainFile)
   {
      this.filePath = newFilePath;
      this.isTrainFile = isTrainFile;
   }

   @Nonnull
   public Path getFilePath()
   {
      return this.filePath;
   }

   public boolean isTrainFile()
   {
      return this.isTrainFile;
   }
   
   public List<Integer> getSentencesIndexesList()
   {
     return this.sentencesStartLines;
   }

}
