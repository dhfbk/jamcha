package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
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
   private TreeMultimap tokenFeatures=null; 

   public FeatureFileReader(@Nonnull Path filePath)
   {
      this.filePath = filePath;
   }

   /**
    * Parse train file and all tokens features. Call getTokensFeatures() to retrieve the result of this method.
    * @throws java.io.IOException cannot open/read file or file does not have same words number for every line
    */
   protected void parseFile() throws IOException
   {
         // Open file to read
         BufferedReader reader = Files.newBufferedReader(filePath);
         getColumnsNumber();
         TreeMultimap<Integer, String> lineFeaturesMap=TreeMultimap.create();
         
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

            ArrayList<String> lineFeatures = new ArrayList<>(colsNumber);

            // For each line word create a string with this pattern: 0_wordNumber_featureValue
            // WordNumber is order number of word in line, starting from zero
            for (int wordNumber = 0; wordNumber < lineWords.length; wordNumber++)
            {
               lineFeatures.add("0_" + wordNumber + "_" + lineWords[wordNumber]);
            }

            // Put all line features in all features schema
            lineFeaturesMap.putAll(rowCounter, lineFeatures);
            rowCounter++;
         }
         this.tokenFeatures=lineFeaturesMap;
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
    * @return multimap of tokens values
    */
   @Nullable
   public SortedSetMultimap getTokensFeatures()
   {
      return tokenFeatures;
   }
   
}
