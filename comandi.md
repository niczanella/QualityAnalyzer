# Comandi

## initDB 
popolamento database da http://dati.trentino.it da zero (vengono eliminati tutti i dati già presenti) con verifica della correttezza sintattica delle e-mail fornite nei metadati relativi ai dataset e verifica del corretto aggiornamento dei dataset in base a quanto dichiarato nei metadati. 
Tabelle: 
dataset, resource, res_in_dataset, organization, org_in_dataset, dataset_is_updated

## continue-initDB 
completamento della procedura di inizializzazione del database

## validate-all-resources
validazione di tutte le risorse contenute nel database. 
Popolamento tabella resource_controls da zero (vengono eliminati i dati già presenti)

## continue-validate-all-resources 
completamento procedura validate-all-resources

## validate-package <package_name>
validazione delle risorse all'interno del package indicato. 
Popolamento tabella resource_controls

## info 
questo messaggio

## exit 
uscita dal programma
