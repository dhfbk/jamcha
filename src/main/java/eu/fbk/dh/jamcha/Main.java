package eu.fbk.dh.jamcha;

import eu.fbk.dh.jamcha.filereader.FeatureFileReader;
import eu.fbk.dh.jamcha.filereader.FeatureIntegrator;
import eu.fbk.dh.jamcha.parameterparser.ParametersParser;
import eu.fbk.dh.jamcha.parameterparser.ParametersParser.Parameters;
import eu.fbk.dh.jamcha.parameterparser.feature.FeatureParserSelector;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main
{

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException
    {
        Parameters parameters = ParametersParser.readParameters(args);
        FeatureFileReader fileReader = new FeatureFileReader(parameters.CORPUS_PATH, true);

        fileReader.parseFile();
        FeatureParserSelector selector = FeatureParserSelector.getInstance(fileReader.getColumnsCount());
        selector.parseFeature(parameters.FEATURES_PARAMETERS);
        FeatureIntegrator integrator = new FeatureIntegrator(selector.getGlobalValuesSchema(), fileReader.getRowsFeatures());
        integrator.integrateFeatures();
        ModelCreator modelCreator = new ModelCreator(integrator.getIntegratedFeatures());
        modelCreator.writeModelTo(parameters.MODEL_PATH);
    }
}
