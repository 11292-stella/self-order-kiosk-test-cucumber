# language: it
@regressione
Funzionalità: Carrello con più prodotti diversi

  Scenario: Il totale è la somma delle righe e si aggiorna rimuovendo un prodotto
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" con quantità 2 e nota "Poca schiuma"
    E il cliente aggiunge "Maritozzo con panna" con quantità 1 e nota "Scaldato"
    E apre il carrello
    Allora il totale del carrello è la somma delle righe
    Quando il cliente rimuove "Maritozzo con panna" dal carrello
    Allora non contiene più "Maritozzo con panna"
    E il carrello contiene "Cappuccino"
    E il totale del carrello è la somma delle righe

  Scenario: La nota si può modificare direttamente dal carrello
    Quando il cliente cambia dal carrello la nota di "Cappuccino" in "Con cacao sopra"
    Allora nel carrello la nota di "Cappuccino" è "Con cacao sopra"
    E il cliente rimuove "Cappuccino" dal carrello
    E il carrello è vuoto
    E il cliente torna al menu dal carrello
