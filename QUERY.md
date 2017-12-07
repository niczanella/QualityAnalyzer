# Esempi di interrogazione alla base dati creata

## estrazione dei dataset che non vengono aggiornati come quanto dichiarato dal campo frequency. Quando il dato non è verificabile result = 'NON VERIFICABILE'
```sql
SELECT dataset.id, dataset.name, dataset_is_updated.result FROM dataset, dataset_is_updated WHERE dataset_is_updated.dataset_id = dataset.id AND result = 'NO'
```

## estrazione dei dataset in cui le email dichiarate nei campi maintainer_email, author_email e contact non sono sintatticamente corrette
```sql
SELECT dataset.id, dataset.name, dataset.maintainer_email, email_verification.maintainer_email, dataset.author_email, email_verification.author_email, dataset.contact, email_verification.contact
FROM dataset, email_verification
WHERE email_verification.dataset_id = dataset.id AND (email_verification.maintainer_email = 'NOT VALID' OR email_verification.author_email = 'NOT VALID' OR email_verification.contact = 'NOT VALID')
```

