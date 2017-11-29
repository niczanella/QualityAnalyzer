#### About

Software java da command line per l'analisi dei dati presenti su un catalogo open data basato su ckan.
Viene usato dati.trentino.it come test.


**Pre-requisiti:**

  * libreria spatialite >= 4.3.0
  * spatialite-tool >= 4.3.0
  * unzip
  * java >= 1.7
  * ant >= 1.7

**INSTALLAZIONE**

Da una bash shell eseguire le seguenti istruzioni:

 * sh setup.sh

Lo script genera un file sqlite, con estensioni spaziali, arricchito dagli shapefile ISTAT dei limiti amministrativi (regioni, province e comuni) al 2016 e il setup delle tabelle usate dal programma.
Nello specifico:
 * viene creato il database sqlite e inizializzate le relative tabelle, su cui verranno salvati i risultati delle analisi
 * viene scaricato e aggiunto al database lo shape file che descrive i confini delle regioni, province e comuni del territorio italiano

Una volta che il file sqlite è stato generato si può procedere alla compilazione della parte java attraverso questo comando:
 * ant

Il programma va eseguito usando questo comando

 * java -jar dist/QualityAnalyzer.jar

All'interno del programma i comandi disponibili sono i seguenti:

 * **initDB**: popolamento database da "http://dati.trentino.it" da zero (vengono eliminati tutti i dati già presenti). Tabelle: dataset, resource, res_in_dataset, organization, org_in_dataset, dataset_is_updated
 * **continue-initDB**: completamento della procedura di inizializzazione del database
 * **validate-email**: verifica della correttezza sintattica delle e-mail fornite nei metadati relativi ai dataset. Popolamento tabella email_verification (vengono eliminati i dati già presenti)
 * **continue-validate-email**: completamento procedura validate-email.
 * **validate-all-resources**: validazione di tutte le risorse contenute nel database. Popolamento tabella resource_controls da zero (vengono eliminati i dati già presenti)
 * **continue-validate-all-resources**: completamento procedura validate-all-resources
 * **validate-package <package_name>**: validazione delle risorse all'interno del package indicato. Popolamento tabella resource_controls
 * **info**: questo messaggio
 * **exit**: uscita dal programma
