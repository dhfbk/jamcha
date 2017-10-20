package eu.fbk.dh.jamcha;

import eu.fbk.dh.jamcha.feature.FeatureParameters;
import eu.fbk.dh.jamcha.feature.FeaturesSchema;
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

public class ModelDataManager
{
    private final FeaturesSchema schema;
    private HashMap<Integer, String> tagsMap;
    private Classifier classifier;

    public ModelDataManager(@Nonnull final FeaturesSchema featuresSchema)
    {
        this.schema = featuresSchema;
    }

    public boolean createModel()
    {
        boolean retval = false;
        // Create tags indexes map
        if (schema.getIntegratedFeatures() == null)
        {
            schema.integrate(null);
        }
        int counter = 0;
        List<Line> features = schema.getIntegratedFeatures();
        if (features != null)
        {
            List<LabelledVector> labVectors = new ArrayList<>(schema.getLinesCount());
            tagsMap = new HashMap<>(schema.getLinesCount());
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
    public void saveModelAndData(@Nonnull Path folderPath) throws IOException
    {
        if ( ! Files.isDirectory(folderPath))
        {
            folderPath = folderPath.getParent();
        }
        if (classifier != null)
        {
            classifier.writeTo(folderPath);
            FeatureParameters params = schema.getFeatureParameters();
            params.saveTo(folderPath);
        }
    }

    public static Classifier loadModel(Path folderPath) throws IOException
    {
        return Classifier.readFrom(folderPath);
    }
}
