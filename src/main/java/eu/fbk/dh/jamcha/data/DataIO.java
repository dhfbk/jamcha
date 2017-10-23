package eu.fbk.dh.jamcha.data;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
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

    private static final class FILE_NAME
    {
        private static final String FOLDER_ROOT = "Data";
        private static final String LINES = "IntegratedFeatures.txt";
    }

    private DataIO(@Nonnull Model model, @Nonnull FeatureParameters parameters)
    {
        this.parameters = parameters;
        this.model = model;
    }

    /**
     * Save data to hd disk
     *
     * @param parameters feature parameters that have been used to integrate lines features
     * @param model      model instance containing association map tag -> index used to create train model
     * @param features   all integrated features to save. If null no features data will be saved
     * @param folderPath where data will be saved
     *
     * @return true all data saved. False invalid path
     *
     * @throws java.io.IOException default
     */
    public static boolean Save(@Nonnull Model model, @Nonnull FeatureParameters parameters, @Nullable List<Line> features, @Nonnull Path folderPath) throws IOException
    {
        boolean retval = false;
        if (isBasePath(folderPath))
        {
            model.save(folderPath);
            parameters.saveTo(folderPath);
            if (features != null)
            {
                saveFeatures(features, folderPath);
            }
            retval = true;
        }
        return retval;
    }

    /**
     * Load feature parameters and train model(with association map tag->int)
     *
     * @param folderPath base folder path where all files are saved. Path must ends with: {@link FILE_NAME.FOLDER_ROOT }
     *
     * @return an instance containing features parameters and training model
     *
     * @throws IllegalArgumentException
     * @throws IOException
     */
    public static DataIO load(@Nonnull Path folderPath) throws IllegalArgumentException, IOException
    {
        if ( ! isBasePath(folderPath))
        {
            throw new IllegalArgumentException("Invalid path");
        }

        FeatureParameters parameters = FeatureParameters.loadFrom(folderPath);
        Model model = Model.load(folderPath);
        DataIO data = new DataIO(model, parameters);
        return data;
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
//        Path retval = Paths.get(pathToCheck.toString());
//        // Check path validity
//        if ( ! Files.isDirectory(retval))
//        {
//            retval = retval.getParent();
//        }
//        if ( ! retval.endsWith(FILE_NAME.FOLDER_ROOT))
//        {
//            retval = Paths.get(retval.toString(), FILE_NAME.FOLDER_ROOT);
//        }
//        
//        return retval;
    }

    /**
     * Save all features to specified path. To set path call "setPath"
     *
     * @param features       features to save
     * @param baseFolderPath folder base path to which will be added another path, file name included
     *
     * @throws IOException default
     * @see IOException
     */
    private static void saveFeatures(@Nonnull List<Line> features, @Nonnull Path baseFolderPath) throws IOException
    {
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
            }
        }
    }
}
