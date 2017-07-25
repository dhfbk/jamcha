package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.Multimaps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;
import java.io.IOException;
import java.nio.file.Paths;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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

   @BeforeClass
   public static void setUpClass()
   {
   }

   @AfterClass
   public static void tearDownClass()
   {
   }

   @Before
   public void setUp()
   {
   }

   @After
   public void tearDown()
   {
   }

   /**
    * Test of parseFile method, of class FeatureFileReader.
    */
   @Test
   public void testParseFile()
   {
      System.out.println("parseFile");
      SortedSetMultimap result = TreeMultimap.create();
      result.put(0, "0_0_token1");
      result.put(0, "0_1_a1");
      result.put(0, "0_2_a2");
      result.put(0, "0_3_r1");

      result.put(1, "0_0_token2");
      result.put(1, "0_1_b1");
      result.put(1, "0_2_b2");
      result.put(1, "0_3_r2");

      result.put(2, "0_0_token3");
      result.put(2, "0_1_c1");
      result.put(2, "0_2_c2");
      result.put(2, "0_3_r3");
      try
      {
         fileReader.parseFile();
      }
      catch (IOException e)
      {
         System.out.println("" + e.getLocalizedMessage());
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
