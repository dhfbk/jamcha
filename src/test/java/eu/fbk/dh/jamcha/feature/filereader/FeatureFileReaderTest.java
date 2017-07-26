package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureFileReaderTest
{

   FeatureFileReader fileReader;
   public FeatureFileReaderTest()
   {
      fileReader= new FeatureFileReader(Paths.get("/home/mazzetti/Documents/test"));
   }

   /**
    * Test of parseFile method, of class FeatureFileReader.
    */
   @Test
   public void testParseFile()
   {
      System.out.println("parseFile");
      SortedSetMultimap result = TreeMultimap.create();

      // token1 a1 b1 c1
      result.put(0, new FeatureInfo(0, (short)0, "token1".toCharArray()));
      result.put(0, new FeatureInfo(0, (short)1, "a1".toCharArray()));
      result.put(0, new FeatureInfo(0, (short)2, "b1".toCharArray()));
      result.put(0, new FeatureInfo(0, (short)3, "c1".toCharArray()));

      // token2 a2 b2 c2
      result.put(1, new FeatureInfo(0, (short)0, "token2".toCharArray()));
      result.put(1, new FeatureInfo(0, (short)1, "a2".toCharArray()));
      result.put(1, new FeatureInfo(0, (short)2, "b2".toCharArray()));
      result.put(1, new FeatureInfo(0, (short)3, "c2".toCharArray()));

      //token3 a3 b3 c3
      result.put(2, new FeatureInfo(0, (short)0, "token3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short)1, "a3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short)2, "b3".toCharArray()));
      result.put(2, new FeatureInfo(0, (short)3, "c3".toCharArray()));
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
      int expResult = 4;
      int result = fileReader.getColumnsNumber();
      assertEquals(expResult, result);
   }

}
