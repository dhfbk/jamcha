package eu.fbk.dh.jamcha.feature;

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
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Create model, save and load model and all data that will be used in future sessions
 */
public class ModelDataManager
{
    private static final String FOLDER_ROOT = "Data";

    @Nonnull
    private final List<Line> features;
    private final FeatureParameters parameters;
    private HashMap<Integer, String> tagsMap;
    private Classifier classifier;
    private Path basePath;

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

    public boolean createModel()
    {
        if (features == null)
        {
            return false;
        }
        boolean retval = false;
        int counter = 0;
        if (features != null)
        {
            List<LabelledVector> labVectors = new ArrayList<>(features.size());
            tagsMap = new HashMap<>(features.size());
            HashMap<String, Integer> tagIndex = new HashMap<>(tagsMap.size());

            for (Line line : features)
            {
                String tag = line.getTag();
                if (tag != null)
                {
                    int index;
                    if ( ! tagIndex.containsKey(tag))
                    {
                        // Add tag to tags maps
                        counter ++;
                        index = counter;
                        tagIndex.put(tag, counter);
                        tagsMap.put(counter, tag);
                    }
                    else
                    {
                        index = tagIndex.get(tag);
                    }
                    // Create labelledVector representing this line and add it to global list
                    Vector.Builder builder = Vector.builder();
                    builder.set(line.getWords());
                    LabelledVector vector = builder.build().label(index);
                    labVectors.add(vector);
                }
            }
            Classifier.Parameters classParams = Classifier.Parameters.forSVMPolyKernel(tagsMap.size(), null, null, null, null, null);
            try
            {
                classifier = Classifier.train(classParams, labVectors);
                retval = true;
            }
            catch (IOException e)
            {
                System.out.println(e.getLocalizedMessage());
            }
        }
        return retval;
    }

    @Nullable
    public Classifier getClassifier()
    {
        return this.classifier;
    }

    @Nullable
    public HashMap<Integer, String> getTagsIndexesMap()
    {
        return this.tagsMap;
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
     * Save the model created using train file and all values that will be used in future sessions
     *
     * @throws IOException default
     * @see IOException
     */
    public void saveModel() throws IOException
    {
        if (classifier != null && basePath != null)
        {
            // Save model
            classifier.writeTo(basePath);
            // Save features parameters
            parameters.saveTo(basePath);
        }
    }

    /**
     * Save all features to specified path. To set path call "setPath"
     *
     * @throws IOException default
     * @see IOException
     */
    public void saveFeatures() throws IOException
    {
        Path featuresPath = Paths.get(basePath.toString(), "Features.txt");
        try (BufferedWriter writer = Files.newBufferedWriter(featuresPath))
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

    private static Path validateBasePath(@Nonnull final Path pathToCheck)
    {
        Path retval = Paths.get(pathToCheck.toString());
        // Build correct path
        if ( ! Files.isDirectory(retval))
        {
            retval = retval.getParent();
        }
        if ( ! retval.endsWith(FOLDER_ROOT))
        {
            retval = Paths.get(retval.toString(), FOLDER_ROOT);
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

        public Classifier loadModel() throws IOException
        {
            return Classifier.readFrom(baseLoadPath);
        }

        public FeatureParameters loadFeatureParameters() throws IOException
        {
            return FeatureParameters.loadFrom(baseLoadPath);
        }
    }
}
