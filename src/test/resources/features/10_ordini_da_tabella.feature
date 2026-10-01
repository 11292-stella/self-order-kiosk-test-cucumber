# language: it
@regressione @api
Funzionalità: Ordini di prodotti diversi, con i dati presi da una tabella

  # "Schema dello scenario" (Scenario Outline) = lo stesso scenario ripetuto
  # una volta per ogni riga della tabella "Esempi". <prodotto>, <quantita> e <nota>
  # vengono sostituiti con i valori della riga.
  Schema dello scenario: Il cliente ordina <quantita> "<prodotto>" e il backend registra il totale giusto
    Dato che il menu è visibile
    Quando il cliente aggiunge "<prodotto>" con quantità <quantita> e nota "<nota>"
    E apre il carrello
    Allora nel carrello "<prodotto>" ha quantità <quantita>
    E il totale del carrello è corretto
    E il cliente va al riepilogo
    E inserisce il suo nome
    E conferma l'ordine
    E vede la conferma dell'ordine
    E torna al menu
    E il backend ha registrato <quantita> "<prodotto>" al prezzo di listino

    Esempi:
      | prodotto            | quantita | nota           |
      | Cappuccino          | 1        | Senza zucchero |
      | Cappuccino          | 3        | Ben caldo      |
      | Maritozzo con panna | 2        | Da portare via |
