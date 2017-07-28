/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.fbk.dh.jamcha.feature.filereader;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import eu.fbk.dh.jamcha.feature.FeatureInfo;
import eu.fbk.dh.jamcha.feature.FeatureParserSelectorTest;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dan92d
 */
public class FeatureIntegratorTest
{

   private FeatureIntegrator integrator;
   private FeatureParserSelectorTest selectorTest;
   private FeatureFileReaderTest readerTest;

   public FeatureIntegratorTest()
   {
      readerTest = new FeatureFileReaderTest();
      selectorTest = new FeatureParserSelectorTest();
      integrator = new FeatureIntegrator(selectorTest.getFeaturesParameters(), readerTest.getTokenFeatures());
      // F:-2..2:1..1
      // F:-3..-2:0..1
      // T:-4..-1
      // T:-5..-3

      // -5:-1
      // -4:-1
      // -3:0,1,-1
      // -2:0,1,-1
      // -1:1,-1
      //  1:1
      //  2:1
      // CAMBIARE IN BASE A FEATURES PARAMETRS
   }

   /**
    * Test of integrateTokensFeatures method, of class FeatureIntegrator.
    */
   @Test
   public void testIntegrateTokensFeatures() throws IOException
   {
      System.out.println("integrateTokensFeatures");
      ListMultimap<Integer, FeatureInfo> expectedFileIntegrated = readIntegratedTestFile(Paths.get("/home/mazzetti/Documents/integratorTest"));
      integrator.integrateTokensFeatures();
      assertEquals(readerIntegratedFile.getTokensFeatures(), integrator.getTokensFeaturesMap());
   }

   /**
    * Legge un file di testo con la lista di features già integrata per ogni riga (token)
    *
    * @param filePath
    * @return riga->features integrate
    * @throws IOException
    */
   private ListMultimap<Integer, FeatureInfo> readIntegratedTestFile(@Nonnull Path filePath) throws IOException
   {
      ListMultimap<Integer, FeatureInfo> expectedResult = ArrayListMultimap.create();
      // Open file to read
      BufferedReader reader = Files.newBufferedReader(filePath);

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

}
