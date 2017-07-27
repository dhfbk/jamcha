package eu.fbk.dh.jamcha.feature;

import com.google.common.collect.TreeMultimap;
import java.util.ArrayList;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author dan92d
 */
public class FeatureSelectorTest
{

   private final ArrayList<String> parametersListValues = new ArrayList<>();
   private final TreeMultimap<Integer, Integer> expected = TreeMultimap.create();

   public FeatureSelectorTest()
   {
      // ***********************************************************
      //                         PARAMETRI FEATURES
      // ***********************************************************
      String parameter = "F:-2..2:1..1";
      parametersListValues.add(parameter);

      parameter = "F:-3..-2:0..1";
      parametersListValues.add(parameter);

      parameter = "T:-4..-1";
      parametersListValues.add(parameter);

      parameter = "T:-5..-3";
      parametersListValues.add(parameter);

      // ***********************************************************
      //                         VALORI CORRETTI PARAMETRI FEATURES
      // ***********************************************************
      // column 0
      expected.put(0, -3);
      expected.put(0, -2);

      // column 1
      expected.put(1, -3);
      expected.put(1, -2);
      expected.put(1, -1);
      expected.put(1, 0);
      expected.put(1, 1);
      expected.put(1, 2);

      // column -1
      expected.put(-1, -5);
      expected.put(-1, -4);
      expected.put(-1, -3);
      expected.put(-1, -2);
      expected.put(-1, -1);

   }

   /**
    * Test of parseFeature method, of class FeatureSelector.
    */
   @Test
   public void testParseFeature()
   {
      System.out.println("Selector: parseFeature");
      FeatureSelector selector = FeatureSelector.getInstance(4);
      for (String featParameter : parametersListValues)
      {
         selector.parseFeature(featParameter);
      }
      TreeMultimap<Integer, Integer> result=selector.getGlobalValuesSchema();
      assertEquals(this.expected, result);
   }

}
