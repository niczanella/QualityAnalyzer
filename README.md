## QualityAnalyzer
è un software java da eseguire da linea di comando per l'analisi dei dati presenti su un catalogo open data basato su ckan.
Viene usato dati.trentino.it come test.

## Pre-requisiti

1. libreria spatialite >= 4.3.0
1. spatialite-tool >= 4.3.0
1. java >= 1.8
1. ant >= 1.9.9
1. unzip

## Avvio quick&dirty 
(in pratica non serve molto ...)
- compilazione
- distribuzione
- setup del database
- avvio del programma
... semplicemente digita:

```
ant
```
inviare il comando
- init-and-validate

# Esecuzione di QualityAnalyzer senza passare da ANT
- creare pacchetto distribuzione 
```
ant dist
```
- eseguire da bash 
```
java -jar dist/QualityAnalyzer.jar
```
- inviare il comando: 
*init-and-validate*


## task di ant 
### compilazione
ant compila tutti i sorgenti java
```
ant compile
```
### distribuzione
ant genera nella directory dist il pacchetto jar necessario e copia le librerie dipendenti
```
ant dist
```
### setup
ant invoca lo script *setup.sh* che si occupa di inizializzare il file sqlite db.sqlite creando tabelle e popolandole con dati ISTAT dei limiti amministrativi
```
ant setup
```
... in alternativa eseguire il comando bash in questo modo
```
chmod 755 setup.sh
./setup.sh
```

### esecuzione
```
ant run
```
### pulizia
```
ant clean
```

# Comandi per QualityAnalyzer

## init-and-validate
procedura di inizializzazione del database da http://dati.trentino.it con verifica della correttezza sintattica delle e-mail fornite nei metadati relativi ai dataset e verifica del corretto aggiornamento dei dataset in base a quanto dichiarato nei metadati e validazione delle risorse.
Tabelle:
dataset, resource, res_in_dataset, organization, org_in_dataset, dataset_is_updated, email_verification, resource_controls

## continue-init-and-validate
continuazione della procedura init-and-validate

## validate-package <package_name>
validazione delle risorse all'interno del package indicato. 
Inserimento nella tabella resource_controls

## info 
questo messaggio

## exit 
uscita dal programma

# ESEMPI
TODO
