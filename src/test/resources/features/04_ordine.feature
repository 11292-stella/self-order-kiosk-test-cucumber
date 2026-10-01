# language: it
@smoke @regressione
Funzionalità: Invio ordine

  Scenario: Il cliente conferma l'ordine e torna al menu
    Dato che il carrello è aperto con almeno un prodotto
    Quando il cliente va al riepilogo
    E inserisce il suo nome
    E conferma l'ordine
    Allora vede la conferma dell'ordine
    E torna al menu