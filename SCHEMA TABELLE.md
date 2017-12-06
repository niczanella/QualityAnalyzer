# TABELLE
## DATASET
contiene l'elenco dei package ottenuti interrogando il catalogo
- *id* => id del package, chiave primaria della tabella. Viene usato nelle tabelle res_in_dataset, org_in_dataset, dataset_is_updated e email_verification come "dataset_id"
- *license_title* => titolo della licenza con cui viene rilasciato il package
- *maintainer* => nome di chi si occupa del mantenimento del dataset
- *encoding* => encoding dei caratteri con cui vengono rilasciati i dati
- *issued* => data di rilascio del package
- *temporal_start* => inizio copertura temporale
- *private* => campo booleano che indica se il package è privato o meno
- *creation_date* => data di creazione del package
- *num_tags* => numero di tag correlati
- *frequency* => frequenza di aggiornamento dichiarata
- *publisher_name* => nome editore del package
- *metadata_created* => data di creazione dei metadati che descrivono il package
- *temporal_end* => fine copertura temporale
- *metadata_modified* => data di modifica dei metadati relativi al package
- *author* => nome autore del package
- *author_email* => email dell'autore del package
- *theme* => sigla che si riferisce al tema del package
- *site_url* => url al sito da cui viene caricato il package
- *state* => stato in cui si trova il package, *active* se è attivo
- *version* => versione del package
- *license_id* => id della licenza con cui viene rilasciato il package
- *type* => tipo del package (dataset)
- *holder_name* => nome del titolare del package
- *holder_identifier* => identificativo del titolare del package
- *fields_description* => descrizione dei campi del package
- *creator_user_id* => identificativo del creatore del package
- *maintainer_email* => email di chi si occupa del mantenimento del dataset
- *num_resources* => numero delle risorse contenute nel package
- *name* => nome del package
- *isopen* => campo booleano che indica se il package è aperto o meno
- *url* => url al package salvato su *site_url* da cui viene scaricato
- *notes* => descrizione del package
- *owner_org* => id dell'organizzazione proprietaria del package
- *modified* => data di modifica del package
- *publisher_identifier* => identificativo dell'editore del package
- *geographical_name* => nome del riferimento geografico del package
- *license_url* => url della licenza con cui viene rilasciato il package
- *title* => titolo del package
- *revision_id* => id della revisione
- *identifier* => altro identificativo del package
- *creator_name* => nome del creatore del package
- *creator_identifier* => identificativo del creatore del package
- *conforms_to* => specifica a cosa è conforme il package
- *language* => sigla che si riferisce alla lingua del package
- *alternate_identifier* => altro identificativo del package
- *is_version_of* => il package fa riferimento ad un altro package
- *contact* => informazioni di contatto
- *geographical_geonames_url* => url di geonames che descrive i limiti geografici a cui fa riferimento il package
- *portalUrl* => url del catalogo


## RESOURCE
contiene l'elenco delle risorse contenute nei package
- *id* => id della risorsa, chiave primaria della tabella. Viene usato nelle tabelle res_in_dataset e resource_controls come resource_id
- *cache_last_updated* => 
- *package_id* => id del package in cui è contenuta la risorsa
- *webstore_last_updated* => 
- *datastore_active* => 
- *size* => dimensione della risorsa
- *state* => stato in cui si trova la risorsa, *active* se è attiva
- *hash* => 
- *description* => descrizione della risorsa
- *format* => formato della risorsa
- *last_modified* => data di ultima modifica della risorsa
- *url_type* => tipo di url della risorsa
- *mimetype* => mimetype della risorsa
- *cache_url* => 
- *name* => nome della risorsa
- *created* => data di creazione della risorsa
- *url* => url della risorsa
- *webstore_url* => 
- *mimetype_inner* => 
- *position* => indice in cui è salvata la risorsa all'interno del package
- *revision_id* => id della revisione della risorsa
- *resource_type* => tipo della risorsa (file, api, ...)
- *distribution_format* => 


## RES_IN_DATASET
indica in quale package è contenuta una risorsa
- *dataset_id* => id del package
- *resource_id* => id della risorsa


## ORGANIZATION
contiene l'elenco delle organizzazioni
- *id* => id dell'organizzazione, chiave primaria
- *description* => descrizione dell'organizzazione
- *created* => data di creazione dell'organizzazione
- *title* => titolo dell'organizzazione
- *name* => nome dell'organizzazione
- *is_organization* => booleano che indica se si tratta di un'organizzazione o meno
- *state* => stato in cui si trova l'organizzazione *active* se è attiva
- *image_url* => url dell'immagine del logo dell'organizzazione
- *revision_id* => id della revisione dell'organizzazione
- *type* => tipo di oggetto (organization)
- *approval_status* => stato di approvazione dell'organizzazione


## ORG_IN_DATASET
indica l'organizzazine che fa riferimento ad ogni dataset
- *dataset_id* => id del package
- *organization_id* => id dell'organizzazione


## DATASET_IS_UPDATED
indica se il package è aggiornato rispettando quanto dichiarato nel campo frequency
- *dataset_id* => id del package
- *result* => risultato della verifica della frequenza di aggiornamento del package: YES => il package è aggiornato correttamente, NO => il package non è aggiornato correttamente, NON VERIFICABILE => non è possibile verificare se il package è aggiornato correttamente o meno


## EMAIL_VERIFICATION
contiene i risultati di verifica della correttezza sintattica delle e-mail (maintainer_email, author_email e contact) dichiarate nei package. (OK => corretto, NOT VALID => non corretto, NULL => metadato non inserito)
- *dataset_id* => id del package
- *maintainer_email* => risultato analisi relativo all'email maintainer_email
- *author_email* => risultato analisi relativo all'email author_email
- *contact* => risultato analisi relativo all'email contact


## RESOURCE_CONTROLS
contiene i risultati di verifica delle analisi sulle risorse
- *resource_id* => id della risorsa, chiave primaria della tabella
- *response_code* => response code relativo alla richiesta di download della risorsa
- *is_downloadable* => booleano che indica se la risorsa è scaricabile
- *format_correspondence* => booleano che indica la corrispondenza del formato dichiarato nei metadati con il formato del file che viene scaricato
- *is_empty* => booleano che indica se il file scaricato è vuoto
- *is_correct* => booleano che indica la correttezza del file
- *log* => campo di testo in cui vengono descritte le operazioni effettuate
- *correct_encoding* => 
- *declared_format* => formato dichiarato nei metadati della risorsa
- *found_format* => formato del file che viene scaricato
- *processed* => booleano che indica se il file viene processato
- *diretto* => booleano che indica se il file viene processato direttamente o a seguito di un passaggio intermedio (unzip supplementare, file contenuto in una sottocartella, ...)
- *geo_processed* => booleano che indica se vengono effettuate verifiche geografiche sul file. Per essere effettuate verifiche geografiche il file deve contenere informazioni geografiche (esempio: lat,lon) e il package in cui è contenuta la risorsa deve avere il campo geographical_geonames_url
- *geo_valid* => booleano che indica se le informazioni geografiche contenute nel file sono contenute nei confini a cui fa riferimento geographical_geonames_url
- *md5sum* => somma md5 del file che viene scaricato
