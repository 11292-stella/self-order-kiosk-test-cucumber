# language: it
@regressione
Funzionalità: Regole del carrello

  Scenario: Aggiungere due volte lo stesso prodotto somma le quantità e tiene l'ultima nota
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" con quantità 1 e nota "Senza zucchero"
    E il cliente aggiunge "Cappuccino" con quantità 2 e nota "Ben caldo"
    E apre il carrello
    Allora nel carrello "Cappuccino" ha quantità 3
    E nel carrello la nota di "Cappuccino" è "Ben caldo"
    E il cliente rimuove "Cappuccino" dal carrello
    E il carrello è vuoto
    E il cliente torna al menu dal carrello