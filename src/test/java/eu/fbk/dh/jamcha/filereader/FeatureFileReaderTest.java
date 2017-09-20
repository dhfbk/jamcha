package eu.fbk.dh.jamcha.filereader;

import eu.fbk.dh.jamcha.Row;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureFileReaderTest
{

   private final FeatureFileReader fileReader;
   private final ArrayList<Row> expectedResult = new ArrayList<>();
   private final int colsNum;

   // Test file path
   public static final Path TESTFILE_PATH = Paths.get("DefaultLineFeatures.txt");

   public FeatureFileReaderTest()
   {
      Path filepath = loadFileFromResources(TESTFILE_PATH.toString()).toPath();
      fileReader = new FeatureFileReader(filepath, true);
      colsNum = 4;

      // ******************************************************************
      //                      TOKENS FEATURES
      // ******************************************************************
      // -------------------- FIRST SENTENCE --------------------
      // token0 a0 b0 c0
      String[] tmp =
      {
         "token0", "a0", "b0"
      };
      expectedResult.add(new Row(0, 0, "c0", Arrays.asList(tmp)));

      // token1 a1 b1 c1
      tmp = new String[]
      {
         "token1", "a1", "b1"
      };
      expectedResult.add(new Row(1, 0, "c1", Arrays.asList(tmp)));

      //token2 a2 b2 c2
      tmp = new String[]
      {
         "token2", "a2", "b2"
      };
      expectedResult.add(new Row(2, 0, "c2", Arrays.asList(tmp)));

      //token3 a3 b3 c3
      tmp = new String[]
      {
         "token3", "a3", "b3"
      };
      expectedResult.add(new Row(3, 0, "c3", Arrays.asList(tmp)));

      //token4 a4 b4 c4
      tmp = new String[]
      {
         "token4", "a4", "b4"
      };
      expectedResult.add(new Row(4, 0, "c4", Arrays.asList(tmp)));

      //token5 a5 b5 c5
      tmp = new String[]
      {
         "token5", "a5", "b5"
      };
      expectedResult.add(new Row(5, 0, "c5", Arrays.asList(tmp)));

      //token6 a6 b6 c6
      tmp = new String[]
      {
         "token6", "a6", "b6"
      };
      expectedResult.add(new Row(6, 0, "c6", Arrays.asList(tmp)));

      // ------------------------------ SECOND SENTENCE -------------------------
      //token7 a7 b7 c7
      tmp = new String[]
      {
         "token7", "a7", "b7"
      };
      expectedResult.add(new Row(7, 1, "c7", Arrays.asList(tmp)));

      //token8 a8 b8 c8
      tmp = new String[]
      {
         "token8", "a8", "b8"
      };
      expectedResult.add(new Row(8, 1, "c8", Arrays.asList(tmp)));

      //token9 a9 b9 c9
      tmp = new String[]
      {
         "token9", "a9", "b9"
      };
      expectedResult.add(new Row(9, 1, "c9", Arrays.asList(tmp)));

      // ------------------------------ THIRD SENTENCE -------------------------
      //token10 a10 b10 c10
      tmp = new String[]
      {
         "token10", "a10", "b10"
      };
      expectedResult.add(new Row(10, 2, "c10", Arrays.asList(tmp)));

      //token11 a11 b11 c11
      tmp = new String[]
      {
         "token11", "a11", "b11"
      };
      expectedResult.add(new Row(11, 2, "c11", Arrays.asList(tmp)));
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
         System.out.println(e.getMessage());
      }

      boolean featuresFlag = expectedResult.equals(fileReader.getRowsFeatures());
      assertEquals(true, featuresFlag);

   }

   /**
    * Test of getColumnsCount method, of class FeatureFileReader.
    */
   @Test
   public void testGetColumnsCount()
   {
      int retval = -1;
      System.out.println("getColumnsCount");
      try
      {
         retval = fileReader.getColumnsCount();
      }
      catch (IOException e)
      {
         System.out.println(e.getMessage());
      }
      assertEquals(colsNum, retval);
   }

   public int getColumnsNumber()
   {
      return colsNum;
   }

   public List<Row> getDefaultFeatures()
   {
      return this.expectedResult;
   }

   /**
    * Legge un file di testo con la lista di features per ogni riga
    *
    * @param filePath file contenente per ogni riga la lista delle features ad essa associate
    *
    * @return riga->lista features
    *
    * @throws IOException
    */
   protected static List<Row> parseTestFile(@Nonnull Path filePath) throws IOException
   {
      ArrayList<Row> expectedResult = new ArrayList<>();

      try (BufferedReader reader = Files.newBufferedReader(filePath);)
      {
         String line;
         int rowCounter = 0;

         // Read file lines and get features of each line
         while ((line = reader.readLine()) != null)
         {
            String[] lineWords = line.split(" ");
            int columnsMaxIndex = lineWords.length - 1;

            // Integrate row features and put them to global structure
            ArrayList<String> lineFeatures = new ArrayList<>(columnsMaxIndex);
            for (int column = 0; column < columnsMaxIndex; column ++)
            {
               lineFeatures.add(lineWords[column]);
            }
            Row row = new Row(rowCounter, 0, lineWords[lineWords.length - 1], lineFeatures);
            expectedResult.add(row);

            rowCounter ++;
         }
      }
      return expectedResult;
   }

   /**
    * Load resource file from resources.
    *
    * @param filePath path del file che si trova nelle risorse. Inserire il path relativo a resources
    *
    * @return File associato al path passato come parametro
    */
   protected static File loadFileFromResources(String filePath)
   {

      Path path = Paths.get("src/test/java/resources/", filePath);
      File retvalFile = path.toFile();
      return retvalFile;
   }

}
