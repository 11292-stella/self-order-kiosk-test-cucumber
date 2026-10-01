# language: it
@regressione @robustezza
Funzionalità: Robustezza dell'invio ordine e della schermata di conferma

  Scenario: Un doppio tocco su "Conferma ordine" crea un solo ordine
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" con quantità 1 e nota "Doppio tocco"
    E apre il carrello
    E il cliente va al riepilogo
    E inserisce il suo nome
    Quando il cliente tocca due volte di fila il pulsante di conferma
    Allora nel backend esiste un solo ordine per quel cliente
    E dopo qualche secondo il kiosk torna da solo al menu

  Scenario: Senza toccare nulla, la conferma torna da sola al menu dopo qualche secondo
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" con quantità 1 e nota "Timer"
    E apre il carrello
    E il cliente va al riepilogo
    E inserisce il suo nome
    E conferma l'ordine
    Allora vede la conferma dell'ordine
    E dopo qualche secondo il kiosk torna da solo al menu
