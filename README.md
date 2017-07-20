Struttura delle classi: (spiegazioni e appunti)

* `FeatureSelector`: classe che coordina il parsing delle feature e che si occupa di chiamare la classe appropriata per ogni singola feature. Inoltre aggiungerà i dati ottenuti nella `TreeMultimap`. Questa sarà la classe da chiamare direttamente.
* `FeatureParser`: classe astratta che funge da "base" per le classi specializzate nel trattare una specifica feature (ogni feature avrà la sua classe specifica). Implementa quasi tutti i metodi. 
* `StaticFeatureParser`: classe che esegue effettivamente il parsing e l'estrazione dei dati da una stringa che rappresenta la feature static (F)
* `DynamicFeatureParser`: uguale alla `StaticFeatureParser`, ma si occupa della feature dynamic (T)
* `FeatureSectionValuesConstraints`: classe che contiene i valori massimi e minimi di ogni singola sezione di una feature.  
  Una sezione è una porzione di stringa separata da ':' (due punti).  
  Ogni classe `FeatureParser` conterrà una lista di classi di questo tipo di lunghezza uguale al  
  numero di sezioni della feature stessa  
  Es. T:-5..   il valore massimo dopo i puntini sarà -1, il valore minimo non è definito  
  Es. F:-2,-1:x..y  il valore minimo di x è zero(non esiste una colonna negativa). Il valore massimo sia di x che y è il numero delle colonne meno 1 (perchè l'ultima è quella dei TAG)  
                
