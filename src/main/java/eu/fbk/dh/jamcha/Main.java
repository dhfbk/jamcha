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
        FeatureParserSelector selector = new FeatureParserSelector(fileReader.getColumnsCount());

        for (String feature : parameters.FEATURES_PARAMETERS)
        {
            selector.parseFeature(feature);
        }
        FeatureIntegrator integrator = new FeatureIntegrator(selector.getGlobalValuesSchema(), fileReader.getRowsFeatures());
        integrator.integrateFeatures();
        ModelCreator modelCreator = new ModelCreator(integrator.getIntegratedFeatures());
        modelCreator.writeModelTo(parameters.MODEL_PATH);
        
        
        //TODO: implementare prediction con file senza tag
        //per ogni riga del file da predirre devo chiamare la Classifier.predict con il vector della linea stessa integrata. Ottengo lo stesso vector ma con il tag (predetto dal classifier).
        
        //TODO:implementare prediction con file con tag giusti
        //fare poi la prediction facendo finta di non avere i tag ma poi confrontare i valoridie tag dati dalla prediction con i tag dati nel file di input (cioè quelli giusti)
    }
}
