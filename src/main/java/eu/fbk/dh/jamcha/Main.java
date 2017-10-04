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
      // Get command line aprameters
        Parameters parameters = ParametersParser.readParameters(args);
        if(parameters.getMode()==ParametersParser.)
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
        
        
        /**      TRAIN
        * Lettura parametri: MODEL,CORPUS,FEATURES
        * Lettura file train con TAG
        * Integrazione file train seguendo FEATURES
        * Creazione MODEL con Classifier
        * Salvataggio modello
        * Salvataggio FEATURES e conteggio colonne
        **/
        
        /**
         *        PREDICT
         * Lettura parametri: MODEL, CORPUS
         * Lettura FEATURE e conteggio colonne da file salvato insieme al modello (ATTENZIONE: conteggio include colonna dei TAG)
         * Lettura CORPUS:
         *                 - senza TAG: conteggio colonne=TAG-1
         *                 - con TAG: conteggio colonne>=TAG (indice colonna TAG sarà TAG-1)
         * Integrazione riga per riga CORPUS: se CORPUS con TAG allora confronto TAG con risposte calcolate e salvo confronto
         * Salvataggio file calcolato in path predefinito
         * 
         * DOMANDE
         * Se CORPUS aveva dei TAGS devo mettere il confronto nel file calcolato? Se si, con che formato?
         */
    }
}
