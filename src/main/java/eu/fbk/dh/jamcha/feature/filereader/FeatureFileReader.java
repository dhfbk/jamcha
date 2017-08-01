package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * This class parses train file and inserts all rows feature in a specific data structure
 *
 * @author dan92d
 */
public class FeatureFileReader
{

   private int colsNumber = 0;
   private final Path filePath;
   
   /**
    * List of all features of each token
    */
   private ArrayListMultimap<Integer, FeatureInfo> tokenFeatures;

   public FeatureFileReader(@Nonnull Path filePath)
   {
      this.filePath = filePath;
      tokenFeatures= ArrayListMultimap.create();
   }

   /**
    * Parse train file and all tokens features. Call getTokensFeatures() to retrieve the result of this method.
    * Each file line must have same number of words.
    * @throws java.io.IOException cannot open/read file or file does not have same words number for every line
    */
   protected final void parseFile() throws IOException
   {
         // ADD: gestire le righe bianche alla fine del file
      
         // Open file to read
         BufferedReader reader = Files.newBufferedReader(filePath);
         getColumnsNumber();
         
         String line;
         int rowCounter = 0;
         
         // Read file lines and get features of each line
         while ((line = reader.readLine()) != null)
         {
            // Split read line and check possible incorrect number of words(columns)
            String[] lineWords = line.split(" ");
            if (lineWords.length != colsNumber)
            {
               throw new IOException("All file lines must have same number of words");
            }

            ArrayList<FeatureInfo> lineFeatures = new ArrayList<>(colsNumber);

            // For each line word create an object containing row, column and feature value
            // WordNumber is order number of word in line, starting from zero
            for (short wordNumber = 0; wordNumber < lineWords.length; wordNumber++)
            {
               lineFeatures.add(new FeatureInfo(0, wordNumber, lineWords[wordNumber].toCharArray()));
            }

            // Put all line features in all features schema
            tokenFeatures.putAll(rowCounter, lineFeatures);
            rowCounter++;
         }
   }

   /**
    * Obtains number of columns in train file. Columns are separated by " " (space). Assumption: all lines of file have same
    * number of columns(words).
    *
    * @return number of columns in this file
    */
   protected int getColumnsNumber()
   {
      if (colsNumber <= 0)
      {
         try (BufferedReader reader = Files.newBufferedReader(filePath))
         {
            String line = reader.readLine();
            String[] lineCols = line.split(" ");
            colsNumber = lineCols.length;
         }
         catch (IOException e)
         {
         }

      }
      return colsNumber;
   }
   
   /**
    * GET: all features values for each token, tokens included
    * @return listmultimap of tokens values
    */
   @Nullable
   public ListMultimap<Integer, FeatureInfo> getTokensFeatures()
   {
      return tokenFeatures;
   }
   
}
