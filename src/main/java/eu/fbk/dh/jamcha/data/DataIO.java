package eu.fbk.dh.jamcha.data;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.Line;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;

/**
 * Save and load all data that will be used in future sessions. Manage persistent data.
 */
public class DataIO
{
   @Nonnull
   private final FeatureParameters parameters;
   @Nonnull
   private final Model model;
   @Nonnull
   final Path baseFolderPath;

   private static final class FILE_NAME
   {
      private static final String FOLDER_ROOT = "Data";
      private static final String LINES = "predictionResult.txt";
   }

  /**
   * Constructor
   * @param model model with classifier and tag map
   * @param parameters feature tuning parameters that have been used to integrate feature line and used to create model
   * @param baseFolderPath folder path where will be saved all data. Do not insert file path
   */
  public DataIO(@Nonnull Model model, @Nonnull FeatureParameters parameters, Path baseFolderPath)
   {
      this.parameters = parameters;
      this.model = model;
      this.baseFolderPath = Paths.get(baseFolderPath.toString(), FILE_NAME.FOLDER_ROOT);
   }

   /**
    * save data to hd disk
    *
    * @throws java.io.IOException default
    */
   public void save() throws IOException
   {
      model.save(baseFolderPath);
      parameters.saveTo(baseFolderPath);
   }

   /**
    * Load feature parameters and train model(with association map tag-int)
    *
    * @param folderPath base folder path where all files are saved. Path must ends with: {@code FILE_NAME.FOLDER_ROOT }
    *
    * @return an instance containing features parameters and training model
    *
    * @throws IllegalArgumentException invalid path
    * @throws IOException default
    */
   public static DataIO load(@Nonnull Path folderPath) throws IllegalArgumentException, IOException
   {
      if ( ! isBasePath(folderPath))
      {
         throw new IllegalArgumentException("Invalid path: cannot load persistent data(model, etc)");
      }

      FeatureParameters parameters = FeatureParameters.loadFrom(folderPath);
      Model model = Model.load(folderPath);
      DataIO data = new DataIO(model, parameters, folderPath);
      return data;
   }

   public Model getModel()
   {
      return this.model;
   }

   public FeatureParameters getFeatureParameters()
   {
      return this.parameters;
   }

   /**
    * save all features to specified path. To set path call "setPath"
    *
    * @param features       features to save
    * @param baseFolderPath folder base path to which will be added another path, file name included
    *
    * @throws IOException default
    * @see IOException
    */
   public static void saveFeatures(@Nonnull Iterable<Line> features, @Nonnull Path baseFolderPath) throws IOException
   {
      if ( ! isBasePath(baseFolderPath))
      {
         throw new IllegalArgumentException("Invalid path");
      }
      Path featuresPath = Paths.get(baseFolderPath.toString(), FILE_NAME.LINES);
      try (BufferedWriter writer = Files.newBufferedWriter(featuresPath))
      {
         for (Line line : features)
         {
            for (String feature : line.getWords())
            {
               writer.append(feature).append(" ");
            }
            writer.append(line.getTag() != null ? line.getTag() : "null");
            writer.newLine();
         }
      }
   }

   /**
    * Check if the path represents root path where are stored all data
    *
    * @param pathToCheck   this path represents root data folder?
    * @param fileNameToAdd this is added to pathToCheck if path is valid. Can be null.
    *
    * @return valid path including fileNameToAdd. Otherwise null.
    */
   private static boolean isBasePath(@Nonnull final Path pathToCheck)
   {
      return pathToCheck.endsWith(FILE_NAME.FOLDER_ROOT);
   }
}
