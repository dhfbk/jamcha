package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureFileReaderTest
{

   private FeatureFileReader fileReader;
   private final ArrayListMultimap<Integer, FeatureInfo> result = ArrayListMultimap.create();
   private int colsNum;

   // Test file path
   public static final Path fileTestPath = Paths.get("DefaultLineFeatures.txt");

   public FeatureFileReaderTest()
   {
      fileReader = new FeatureFileReader(fileTestPath);
      colsNum = 4;

      // ******************************************************************
      //                      TOKENS FEATURES
      // ******************************************************************
      // token0 a0 b0 c0
      result.put(0, new FeatureInfo(0, (short) 0, "token0".toCharArray()));
      result.put(0, new FeatureInfo(0, (short) 1, "a0".toCharArray()));
      result.put(0, new FeatureInfo(0, (short) 2, "b0".toCharArray()));
      result.put(0, new FeatureInfo(0, (short) 3, "c0".toCharArray()));

      // token1 a1 b1 c1
      result.put(1, new FeatureInfo(0, (short) 0, "token1".toCharArray()));
      result.put(1, new FeatureInfo(0, (short) 1, "a1".toCharArray()));
      result.put(1, new FeatureInfo(0, (short) 2, "b1".toCharArray()));
      result.put(1, new FeatureInfo(0, (short) 3, "c1".toCharArray()));

      //token2 a2 b2 c2
      result.put(2, new FeatureInfo(0, (short) 0, "token2".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 1, "a2".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 2, "b2".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 3, "c2".toCharArray()));

      //token3 a3 b3 c3
      result.put(2, new FeatureInfo(0, (short) 0, "token3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 1, "a3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 2, "b3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 3, "c3".toCharArray()));

      //token4 a4 b4 c4
      result.put(2, new FeatureInfo(0, (short) 0, "token4".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 1, "a4".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 2, "b4".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 3, "c4".toCharArray()));

      //token5 a5 b5 c5
      result.put(2, new FeatureInfo(0, (short) 0, "token5".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 1, "a5".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 2, "b5".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 3, "c5".toCharArray()));

      //token6 a6 b6 c6
      result.put(2, new FeatureInfo(0, (short) 0, "token6".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 1, "a6".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 2, "b6".toCharArray()));
      result.put(2, new FeatureInfo(0, (short) 3, "c6".toCharArray()));
   }

   /**
    * Test of parseFile method, of class FeatureFileReader.
    */
   @Test
   public void testParseFile()
   {
      System.out.println("parseFile");
      try
      {
         fileReader.parseFile();
      }
      catch (IOException e)
      {
         System.out.println(e.getLocalizedMessage());
      }
      assertEquals(result, fileReader.getTokensFeatures());

   }

   /**
    * Test of getColumnsNumber method, of class FeatureFileReader.
    */
   @Test
   public void testGetColumnsNumber()
   {
      System.out.println("getColumnsNumber");
      int retval = fileReader.getColumnsNumber();
      assertEquals(colsNum, retval);
   }

   public int getColumnsNumber()
   {
      return colsNum;
   }

   public ListMultimap<Integer, FeatureInfo> getTokenFeatures()
   {
      return this.result;
   }

   /**
    * Legge un file di testo con la lista di features per ogni riga
    *
    * @param testFile file contenente per ogni riga la lista delle features ad essa associate
    * @return map riga->lista features
    * @throws IOException
    */
   protected static ListMultimap<Integer, FeatureInfo> parseTestFile(@Nonnull File testFile) throws IOException
   {
      ListMultimap<Integer, FeatureInfo> expectedResult = ArrayListMultimap.create();
      // Open file to read
      BufferedReader reader = Files.newBufferedReader(testFile.toPath());

      String line;
      int rowCounter = 0;

      // Read file lines and get features of each line
      while ((line = reader.readLine()) != null)
      {
         // Split read line and check possible incorrect number of words(columns)
         String[] lineWords = line.split(" ");

         ArrayList<FeatureInfo> lineFeatures = new ArrayList<>();

         // For each line word create an object containing row, column and feature value
         // WordNumber is order number of word in line, starting from zero
         for (short wordNumber = 0; wordNumber < lineWords.length; wordNumber++)
         {
            lineFeatures.add(new FeatureInfo(0, wordNumber, lineWords[wordNumber].toCharArray()));
         }

         // Put all line features in all features schema
         expectedResult.putAll(rowCounter, lineFeatures);
         rowCounter++;
      }
      return expectedResult;
   }

   /**
    * Load resource file from resources.
    *
    * @param filePath path del file che si trova nelle risorse. Inserire il path relativo a resources
    * @return File associato al path passato come parametro
    */
   protected static File loadFileFromResources(String filePath)
   {
      
      Path path = Paths.get("src/test/java/resources/", filePath);
      File retvalFile = path.toFile();
      return retvalFile;
   }

}
