# language: it
@smoke @regressione
Funzionalità: Schermata iniziale

  Scenario: Il cliente tocca la splash
    Dato che la splash è visibile
    Quando il cliente tocca per ordinare
    Allora vede il menu dei prodotti
