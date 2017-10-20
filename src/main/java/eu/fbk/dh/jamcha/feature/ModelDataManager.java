package eu.fbk.dh.jamcha.feature;

import eu.fbk.dh.jamcha.feature.FeaturesSchema.Line;
import eu.fbk.utils.svm.Classifier;
import eu.fbk.utils.svm.LabelledVector;
import eu.fbk.utils.svm.Vector;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;

/**
 * Create model, save and load model and all data that will be used in future sessions
 */
public class ModelDataManager
{
    private static final String FOLDER_ROOT = "Data";

    private final List<Line> features;
    private final FeatureParameters parameters;
    private HashMap<Integer, String> tagsMap;
    private Classifier classifier;

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
            HashSet<String> tagsInserted = new HashSet<>(tagsMap.size());
            for (Line line : features)
            {
                String tag = line.getTag();
                if (tag != null && tagsInserted.add(tag))
                {
                    //Create tags map
                    counter ++;
                    tagsMap.put(counter, line.getTag());

                    // Create labelledVector representing this line and add it to global list
                    Vector.Builder builder = Vector.builder();
                    builder.set(line.getTag());
                    LabelledVector vector = builder.build().label(counter);
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

    /**
     * Save the model created using train file and all values that will be used in future sessions
     *
     * @param folderPath path of folder that will contain all files.
     *
     * @throws IOException default
     * @see IOException
     */
    public void saveTo(@Nonnull final Path folderPath) throws IOException
    {
        if (classifier != null)
        {
            Path correctPath = validateBasePath(folderPath);
            // Save model
            classifier.writeTo(correctPath);
            // Save features parameters
            parameters.saveTo(correctPath);
        }
    }

    public static Classifier loadModel(Path folderPath) throws IOException
    {
        Path correctPath = validateBasePath(folderPath);
        return Classifier.readFrom(correctPath);
    }

    public static FeatureParameters loadFeatureParameters(Path folderPath) throws IOException
    {
        Path correctPath = validateBasePath(folderPath);
        return FeatureParameters.loadFrom(correctPath);
    }

    private static Path validateBasePath(final Path pathToCheck)
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
}
