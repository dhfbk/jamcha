package eu.fbk.dh.jamcha.data;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.Line;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
    *
    * @param model          model with classifier and tag map
    * @param parameters     feature tuning parameters that have been used to integrate feature line and used to create model
    * @param baseFolderPath folder retval where will be saved all data. Do not insert file retval
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
    * @param folderPath base folder retval where all files are saved. Path must ends with: {@code FILE_NAME.FOLDER_ROOT }
    *
    * @return an instance containing features parameters and training model
    *
    * @throws IllegalArgumentException invalid retval
    * @throws IOException              default
    */
   public static DataIO load(@Nonnull final Path folderPath) throws IllegalArgumentException, IOException
   {
      Path checkedPath = isBasePath(folderPath);
      if (checkedPath == null)
      {
         throw new IllegalArgumentException("Invalid path: cannot load persistent data(model, etc)");
      }

      FeatureParameters parameters = FeatureParameters.loadFrom(checkedPath);
      Model model = Model.load(checkedPath);
      DataIO data = new DataIO(model, parameters, checkedPath);
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
    * save all features to specified retval. To set retval call "setPath"
    *
    * @param features       features to save
    * @param baseFolderPath folder base retval to which will be added another retval, file name included
    *
    * @throws IOException default
    * @see IOException
    */
   public static void saveFeatures(@Nonnull Iterable<Line> features, @Nonnull Path baseFolderPath) throws IOException
   {
      Path checkedPath = isBasePath(baseFolderPath);
      if (checkedPath == null)
      {
         throw new IllegalArgumentException("Invalid path: cannot save persistent data(model, etc)");
      }
      Path featuresPath = Paths.get(checkedPath.toString(), FILE_NAME.LINES);
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
    * Check if the pathToCheck represents root folder where all data are stored. If path does not end with root folder it search root folder and add it
    *
    * @param pathToCheck this path represents root data folder?
    *
    * @return valid path including root folder. Otherwise null.
    */
   @Nullable
   private static Path isBasePath(@Nonnull final Path pathToCheck)
   {
      Path retval = null;
      if (pathToCheck.endsWith(FILE_NAME.FOLDER_ROOT))
      {
         if (Files.isDirectory(pathToCheck))
         {
            retval = Paths.get(pathToCheck.toString());
         }
      }
      else
      {
         File[] directories = new File(pathToCheck.toString()).listFiles(File::isDirectory);
         for (File file : directories)
         {
            if (file.getName().equals(FILE_NAME.FOLDER_ROOT))
            {
               retval = Paths.get(pathToCheck.toString(), file.getName());
               break;
            }
         }
      }
      return retval;
   }
}
