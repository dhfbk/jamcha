package eu.fbk.dh.jamcha.filereader;

import eu.fbk.dh.jamcha.Row;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureFileReaderTest
{

   private FeatureFileReader fileReader;
   private final ArrayList<Row> expectedResult = new ArrayList<>();
   private int colsNum;

   // Test file path
   public static final Path fileTestPath = Paths.get("DefaultLineFeatures.txt");

   public FeatureFileReaderTest()
   {
      Path filepath=loadFileFromResources(fileTestPath.toString()).toPath();
      fileReader = new FeatureFileReader(filepath, true);
      colsNum = 4;

      // ******************************************************************
      //                      TOKENS FEATURES
      // ******************************************************************
      // token0 a0 b0 c0
      String[] tmp =
      {
         "token0", "a0", "b0"
      };
      expectedResult.add(new Row(0, "c0", Arrays.asList(tmp)));

      // token1 a1 b1 c1
      tmp = new String[]
      {
         "token1", "a1", "b1"
      };
      expectedResult.add(new Row(1, "c1", Arrays.asList(tmp)));

      //token2 a2 b2 c2
      tmp = new String[]
      {
         "token2", "a2", "b2"
      };
      expectedResult.add(new Row(2, "c2", Arrays.asList(tmp)));

      //token3 a3 b3 c3
      tmp = new String[]
      {
         "token3", "a3", "b3"
      };
      expectedResult.add(new Row(3, "c3", Arrays.asList(tmp)));

      //token4 a4 b4 c4
      tmp = new String[]
      {
         "token4", "a4", "b4"
      };
      expectedResult.add(new Row(4, "c4", Arrays.asList(tmp)));

      //token5 a5 b5 c5
      tmp = new String[]
      {
         "token5", "a5", "b5"
      };
      expectedResult.add(new Row(5, "c5", Arrays.asList(tmp)));

      //token6 a6 b6 c6
      tmp = new String[]
      {
         "token6", "a6", "b6"
      };
      expectedResult.add(new Row(6, "c6", Arrays.asList(tmp)));
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
      
      assertEquals(expectedResult, fileReader.getRowsFeatures());

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

//   /**
//    * Legge un file di testo con la lista di features per ogni riga
//    *
//    * @param testFile file contenente per ogni riga la lista delle features ad essa associate
//    * @return map riga->lista features
//    * @throws IOException
//    */
//   protected static List<Row> parseTestFile(@Nonnull File testFile) throws IOException
//   {
//      ArrayList<Row> expectedResult = new ArrayList<>();
//      
//      BufferedReader reader = Files.newBufferedReader(testFile.toPath());
//
//      String line;
//      int rowCounter = 0;
//
//      // Read file lines and get features of each line
//      while ((line = reader.readLine()) != null)
//      {
//         // Split read line and check possible incorrect number of words(columns)
//         String[] lineWords = line.split(" ");
//
//         ArrayList<String> lineFeatures = new ArrayList<>();
//
//         // For each line word create an object containing row, column and feature value
//         // WordNumber is order number of word in line, starting from zero
//         for (short wordNumber = 0; wordNumber < lineWords.length; wordNumber++)
//         {
//            lineFeatures.add();
//         }
//
//         // Put all line features in all features schema
//         expectedResult.putAll(rowCounter, lineFeatures);
//         rowCounter++;
//      }
//      return expectedResult;
//   }

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
