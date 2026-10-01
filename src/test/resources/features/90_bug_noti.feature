# language: it
@bug
Funzionalità: Bug noti, documentati da test che falliscono finché non vengono corretti

  # Di default il TestRunner li esclude (tags = "not @bug").
  # Per eseguirli: -Dcucumber.filter.tags="not @nessuno" (tutta la suite, bug compresi).
  # Il bug delle note perse è in 05_verifica_api.feature.

  Scenario: La risposta dell'ordine non espone il costo di produzione al kiosk
    Dato che il kiosk ha confermato un ordine
    Quando leggo l'ordine dal backend
    Allora la risposta non contiene il campo "costoProduzione"

  @ripristina_filtro
  Scenario: Dopo un ordine il cliente successivo trova il menu completo, senza filtri
    Dato che il menu è visibile
    E il cliente seleziona una categoria diversa da quella di "Cappuccino"
    E il cliente aggiunge al carrello il primo prodotto di quella categoria
    E apre il carrello
    E il cliente va al riepilogo
    E inserisce il suo nome
    E conferma l'ordine
    E vede la conferma dell'ordine
    E torna al menu
    Allora vede "Cappuccino" nel menu

  @ripristina_prodotto
  Scenario: Un prodotto segnato esaurito dal gestionale non si può più ordinare dal kiosk
    Dato che il menu è visibile
    Quando il gestore segna "Cappuccino" come esaurito dal gestionale
    E il cliente prova ad aprire il prodotto "Cappuccino"
    Allora il dettaglio del prodotto non si apre
