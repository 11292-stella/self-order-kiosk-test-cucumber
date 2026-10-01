# language: it
@smoke @regressione
Funzionalità: Carrello

  Scenario: Il cliente aggiunge un prodotto al carrello
    Dato che il dettaglio del prodotto è aperto
    Quando il cliente imposta la quantità a 2
    E scrive una nota per la cucina
    E aggiunge il prodotto al carrello
    E apre il carrello
    Allora il carrello contiene "Cappuccino"