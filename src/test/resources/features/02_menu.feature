# language: it
@smoke @regressione
Funzionalità: Schermata prodotti

  Scenario: Il cliente apre un prodotto
    Dato che il menu è visibile
    Quando il cliente apre il prodotto "Cappuccino"
    Allora vede il dettaglio del prodotto