package eu.fbk.dh.jamcha.data;

import com.google.common.collect.HashBiMap;
import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.FeaturesSchema;
import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.LabelledVector;
import eu.fbk.utils.svm.Vector;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Create model, save and load model and all data that will be used in future sessions.Manage persistent data.
 */
public class ModelDataManager
{
    @Nonnull
    private final List<Line> features;
    private final FeatureParameters parameters;
    private HashMap<Integer, String> tagsMap;
    private Classifier classifier;
    private Path basePath;

    private static final class FILE_NAME
    {
        private static final String FOLDER_ROOT = "Data";
        private static final String TAGS_INDEX_MAP = "Tags.txt";
        private static final String LINES = "IntegratedFeatures.txt";
    }

    private static enum DATA_TYPE
    {
        FEATURES, PARAMETERS, TAGS_MAP, CLASSIFIER
    }

    /**
     * Constructor
     *
     * @param featuresSchema schema containing feature parameters and all integrated features
     */
    public ModelDataManager(@Nonnull final FeaturesSchema featuresSchema)
    {
        if (featuresSchema.getIntegratedFeatures() == null)
        {
            featuresSchema.integrate(null);
        }
        this.features = featuresSchema.getIntegratedFeatures();
        this.parameters = featuresSchema.getFeatureParameters();
    }

    /**
     * Constructor
     *
     * @param features   all integrated features
     * @param parameters
     */
    public ModelDataManager(@Nonnull List<Line> features, @Nonnull FeatureParameters parameters)
    {
        this.features = features;
        this.parameters = parameters;
    }

    /**
     *
     * @param folderPath path of folder that will contain all files.
     *
     * @return
     */
    public boolean setPath(@Nonnull final Path folderPath)
    {
        this.basePath = validateBasePath(folderPath);
        return this.basePath != null;
    }

    /**
     * Save all features to specified path. To set path call "setPath"
     *
     * @throws IOException default
     * @see IOException
     */
    public void saveFeatures() throws IOException
    {
        try (BufferedWriter writer = Files.newBufferedWriter(getFullPath(DATA_TYPE.FEATURES)))
        {
            for (Line line : this.features)
            {
                for (String feature : line.getWords())
                {
                    writer.append(feature).append(" ");
                }
                writer.append(line.getTag() != null ? line.getTag() : "null");
            }
        }
    }

    private Path getFullPath(DATA_TYPE type)
    {
        Path retval;
        switch (type)
        {
            case TAGS_MAP:
            {
                retval = Paths.get(this.basePath.toString(), FILE_NAME.TAGS_INDEX_MAP);
                break;
            }
            case FEATURES:
            {
                retval = Paths.get(this.basePath.toString(), FILE_NAME.LINES);
                break;
            }
            default:
            {
                retval = this.basePath;
            }
        }
        return retval;
    }

    /**
     * Check if the path represents root path where are stored all data
     *
     * @param pathToCheck   this path represents root data folder?
     * @param fileNameToAdd this is added to pathToCheck if path is valid. Can be null.
     *
     * @return valid path including fileNameToAdd. Otherwise null.
     */
    public static Path validateAndBuildPath(@Nonnull final Path pathToCheck, @Nullable String fileNameToAdd)
    {
        Path retval = Paths.get(pathToCheck.toString());
        // Check path validity
        if ( ! Files.isDirectory(retval))
        {
            retval = retval.getParent();
        }
        if ( ! retval.endsWith(FILE_NAME.FOLDER_ROOT))
        {
            retval = Paths.get(retval.toString(), FILE_NAME.FOLDER_ROOT);
        }
        // Build path
        if (fileNameToAdd != null)
        {
            retval = Paths.get(retval.toString(), fileNameToAdd);
        }
        if (retval.equals(pathToCheck))
        {
            retval = null;
        }
        return retval;
    }

    /**
     * Load all saved values(model, etc)
     */
    public final static class Loader
    {
        private final Path baseLoadPath;

        /**
         * Constructor
         *
         * @param baseFolderPath path of folder that will contain all files.
         */
        public Loader(@Nonnull Path baseFolderPath)
        {
            this.baseLoadPath = validateBasePath(baseFolderPath);
        }

        public FeatureParameters loadFeatureParameters() throws IOException
        {
            return FeatureParameters.loadFrom(baseLoadPath);
        }
    }
}
