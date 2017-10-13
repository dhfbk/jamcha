package eu.fbk.dh.jamcha.feature;

import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import java.util.Arrays;
import org.junit.Assert;
import org.junit.Test;

public class FeatureParametersTest
{
   private final String featuresList;
   private final int columnsCount;
   private TreeMultimap<Integer, Integer> featureMapCorrect = TreeMultimap.create();

   public FeatureParametersTest()
   {
      featuresList = "F:-2..1:0..1 F:-3..-2:1..2 T:-4..-1 T:-5..-3";
      columnsCount = 4;
      featureMapCorrect.putAll(-5, Arrays.asList(-1));
      featureMapCorrect.putAll(-4, Arrays.asList(-1));
      featureMapCorrect.putAll(-3, Arrays.asList(-1, 1, 2));
      featureMapCorrect.putAll(-2, Arrays.asList(-1, 0, 1, 2));
      featureMapCorrect.putAll(-1, Arrays.asList(-1, 0, 1));
      featureMapCorrect.putAll(0, Arrays.asList(0, 1));
      featureMapCorrect.putAll(1, Arrays.asList(0, 1));
   }

   @Test
   public void testBuild()
   {
      FeatureParameters featureParameters = FeatureParameters.build(featuresList, this.columnsCount);
      Assert.assertEquals(featureMapCorrect, featureParameters.getParameters());
   }

   @Test
   public void testFromColRowsToRowCols()
   {
      TreeMultimap<Integer, Integer> testMap = TreeMultimap.create();
      // column 0
      testMap.putAll(0, Arrays.asList(-2, -1, 0, 1));
      // column 1
      testMap.putAll(1, Arrays.asList(-3, -2, -1, 0, 1));
      // column 2
      testMap.putAll(2, Arrays.asList(-3, -2));
      // column 3 (tag)
      testMap.putAll(-1, Arrays.asList(-5, -4, -3, -2, -1));

      Multimap<Integer, Integer> resultMap = FeatureParameters.fromColRowsToRowCols(testMap);
      Assert.assertEquals(featureMapCorrect, resultMap);
   }

//   @Test
//   public void testSaveTo() throws Exception
//   {
//      //TODO:implementare testSaveTo
//   }
//
//   @Test
//   public void testLoadFrom() throws Exception
//   {
//      //TODO:implementare testLoadFrom
//   }
}
