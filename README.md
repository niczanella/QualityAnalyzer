# About

Software java da command line per l'analisi dei dati presenti su un catalogo open data basato su ckan.
Viene usato dati.trentino.it come test.


## Pre-requisiti

1. libreria spatialite >= 4.3.0
1. spatialite-tool >= 4.3.0
1. java >= 1.8
1. ant >= 1.9.9
1. unzip

## QUICK&DIRTY ##
- compilazione
- distribuzione
- setup del database
- avvio del programma
... semplicemente digita:

```
ant
```
inviare i comandi
- initDB
- validate-all-resources

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
