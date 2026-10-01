# language: it
@regressione
Funzionalità: Gestione del carrello

  Scenario: Il cliente cambia la quantità dal carrello e il totale si aggiorna
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" al carrello dal menu
    E apre il carrello
    Quando aumenta di 1 la quantità di "Cappuccino" nel carrello
    Allora nel carrello "Cappuccino" ha quantità 2
    E il totale del carrello è corretto

  Scenario: Diminuendo fino a zero il prodotto sparisce dal carrello
    Dato che nel carrello c'è "Cappuccino"
    Quando diminuisce di 2 la quantità di "Cappuccino" nel carrello
    Allora il carrello è vuoto
    E il cliente torna al menu dal carrello