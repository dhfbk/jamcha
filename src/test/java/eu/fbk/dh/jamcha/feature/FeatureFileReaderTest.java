package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.Line;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class FeatureFileReaderTest
{
   private final ArrayList<Line> expectedResultTrain;
   private final ArrayList<Line> expectedResultPredict;
   // Test file path
   public static final Path DEFAULT_TRAIN_FILE_PATH = Paths.get("Default.txt");
   public static final Path DEFAULT_PREDICT_FILE_PATH = Paths.get("Default.txt");

   public FeatureFileReaderTest()
   {
      this.expectedResultTrain = createTrainFile();
      this.expectedResultPredict = createPredictFile();
   }

   @Test
   public void testRead() throws Exception
   {
      // TRAIN READER TEST
      Path filePath = getResourceFilePath(DEFAULT_TRAIN_FILE_PATH.toString());
      TrainFileReader trainReader = new TrainFileReader(filePath);
      trainReader.read();
      Assert.assertEquals(expectedResultTrain, trainReader.getLines());

      // PREDICT READER TEST
      filePath = getResourceFilePath(DEFAULT_PREDICT_FILE_PATH.toString());
      PredictFileReader predictReader = new PredictFileReader(filePath, trainReader.WORDS_LINE_COUNT_BASE);
      predictReader.read();
      Assert.assertEquals(expectedResultPredict, predictReader.getLines());
   }

   /**
    * Load resource file from resources.
    *
    * @param filePath path del file che si trova nelle risorse. Inserire il path relativo a resources
    *
    * @return File associato al path passato come parametro
    */
   public static Path getResourceFilePath(String filePath)
   {
      Path path = Paths.get("src/test/java/resources/", filePath);
      return path;
   }

   private ArrayList<Line> createTrainFile()
   {
      ArrayList<Line> retval = new ArrayList<>();
      // -------------------- FIRST SENTENCE --------------------
      // token0 a0 b0 c0
      Line row = new Line(0, 0, "c0", Arrays.asList("token0", "a0", "b0"));
      retval.add(row);

      // token1 a1 b1 c1
      row = new Line(1, 0, "c1", Arrays.asList("token1", "a1", "b1"));
      retval.add(row);

      //token2 a2 b2 c2
      row = new Line(2, 0, "c2", Arrays.asList("token2", "a2", "b2"));
      retval.add(row);

      //token3 a3 b3 c3
      row = new Line(3, 0, "c3", Arrays.asList("token3", "a3", "b3"));
      retval.add(row);

      //token4 a4 b4 c4
      row = new Line(4, 0, "c4", Arrays.asList("token4", "a4", "b4"));
      retval.add(row);

      //token5 a5 b5 c5
      row = new Line(5, 0, "c5", Arrays.asList("token5", "a5", "b5"));
      retval.add(row);

      //token6 a6 b6 c6
      row = new Line(6, 0, "c6", Arrays.asList("token6", "a6", "b6"));
      retval.add(row);

      // ------------------------------ SECOND SENTENCE -------------------------
      //token7 a7 b7 c7
      row = new Line(7, 1, "c7", Arrays.asList("token7", "a7", "b7"));
      retval.add(row);

      //token8 a8 b8 c8
      row = new Line(8, 1, "c8", Arrays.asList("token8", "a8", "b8"));
      retval.add(row);

      //token9 a9 b9 c9
      row = new Line(9, 1, "c9", Arrays.asList("token9", "a9", "b9"));
      retval.add(row);

      // ------------------------------ THIRD SENTENCE -------------------------
      //token10 a10 b10 c10
      row = new Line(10, 2, "c10", Arrays.asList("token10", "a10", "b10"));
      retval.add(row);

      //token11 a11 b11 c11
      row = new Line(11, 2, "c11", Arrays.asList("token11", "a11", "b11"));
      retval.add(row);
      return retval;
   }

   private ArrayList<Line> createPredictFile()
   {
      ArrayList<Line> retval = new ArrayList<>();
      // -------------------- FIRST SENTENCE --------------------
      // token0 a0 b0 c0
      Line row = new Line(0, 0, "c0", Arrays.asList("token0", "a0", "b0"));
      retval.add(row);

      // token1 a1 b1 c1
      row = new Line(1, 0, null, Arrays.asList("token1", "a1", "b1"));
      retval.add(row);

      //token2 a2 b2 c2
      row = new Line(2, 0, "c2", Arrays.asList("token2", "a2", "b2"));
      retval.add(row);

      //token3 a3 b3 c3
      row = new Line(3, 0, null, Arrays.asList("token3", "a3", "b3"));
      retval.add(row);

      //token4 a4 b4 c4
      row = new Line(4, 0, "c4", Arrays.asList("token4", "a4", "b4"));
      retval.add(row);

      //token5 a5 b5 c5
      row = new Line(5, 0, "c5", Arrays.asList("token5", "a5", "b5"));
      retval.add(row);

      //token6 a6 b6 c6
      row = new Line(6, 0, "c6", Arrays.asList("token6", "a6", "b6"));
      retval.add(row);

      // ------------------------------ SECOND SENTENCE -------------------------
      //token7 a7 b7 c7
      row = new Line(7, 1, null, Arrays.asList("token7", "a7", "b7"));
      retval.add(row);

      //token8 a8 b8 c8
      row = new Line(8, 1, "c8", Arrays.asList("token8", "a8", "b8"));
      retval.add(row);

      //token9 a9 b9 c9
      row = new Line(9, 1, "c9", Arrays.asList("token9", "a9", "b9"));
      retval.add(row);

      // ------------------------------ THIRD SENTENCE -------------------------
      //token10 a10 b10 c10
      row = new Line(10, 2, null, Arrays.asList("token10", "a10", "b10"));
      retval.add(row);

      //token11 a11 b11 c11
      row = new Line(11, 2, null, Arrays.asList("token11", "a11", "b11"));
      retval.add(row);
      return retval;
   }

}
