# language: it
@regressione @api
Funzionalità: Verifica dell'ordine nel backend

  Scenario: L'ordine inviato dal kiosk è salvato correttamente
    Dato che il kiosk ha confermato un ordine
    Quando leggo l'ordine dal backend
    Allora il cliente è quello inserito nel kiosk
    E l'ordine contiene il prodotto con la quantità scelta
    E il totale è uguale a prezzo per quantità

  @bug
  Scenario: La nota scritta dal cliente arriva al backend
    Dato che il kiosk ha confermato un ordine
    Quando leggo l'ordine dal backend
    Allora la nota del cliente è salvata nell'ordine