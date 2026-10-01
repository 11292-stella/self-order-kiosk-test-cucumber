# language: it
Funzionalità: Filtro del menu per categoria

  Scenario: Selezionando una categoria il menu mostra solo i suoi prodotti
    Dato che il menu è visibile
    Quando il cliente seleziona una categoria diversa da quella di "Cappuccino"
    Allora vede il primo prodotto di quella categoria
    E non vede "Cappuccino" nel menu

  Scenario: Selezionando "Tutte" il menu torna completo
    Quando il cliente seleziona tutte le categorie
    Allora vede "Cappuccino" nel menu