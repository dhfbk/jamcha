package eu.fbk.dh.jamcha.feature.parameterparser;

import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import eu.fbk.dh.jamcha.feature.*;
import java.util.ArrayList;

/**
 *
 * @author dan92
 */
public class FeatureParserTest
{

   private final String featureParameter;
   private final int numCols;
   private final FeatureValues featureValues = new FeatureValues();
   private final FeatureParser parser;
   private final List<FeatureSectionValuesConstraints> constraints=new ArrayList<>(2);

   public FeatureParserTest()
   {
      numCols = 4;
      parser=StaticFeatureParser.getInstance(numCols);
      
      featureParameter = "-4..-2:0..2";
      Integer[] section1Values =
      {
         -4, -3, -2
      };
      Integer[] section2Values =
      {
         0, 1, 2
      };
      featureValues.setRows(Arrays.asList(section1Values));
      featureValues.setColumns(Arrays.asList(section2Values));
   }

   /**
    * Test of parseSection method, of class FeatureParser.
    */
   @Test
   public void testParseSection()
   {
      System.out.println("parseSection");
      
      String[] sections= featureParameter.split(String.valueOf(FeatureParser.SECTION_SEPARATOR));
     List<Integer> section= parser.parseSection(sections[0], parser.getConstraintsList().get(0));
      assertEquals(featureValues.getRows(), section);
   }

   /**
    * Test of parseFeature method, of class FeatureParser.
    */
   @Test
   public void testParseFeature() throws Exception
   {
      System.out.println("parseFeature");
      assertEquals(featureValues, parser.parseFeature(featureParameter));
   }

}
