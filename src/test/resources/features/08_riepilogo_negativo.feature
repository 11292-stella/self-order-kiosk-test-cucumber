# language: it
@regressione @negativo
Funzionalità: Validazione del nome nel riepilogo

  Scenario: Senza nome l'ordine non parte
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" al carrello dal menu
    E apre il carrello
    E il cliente va al riepilogo
    Quando il cliente conferma senza scrivere il nome
    Allora vede l'avviso "Inserisci il nome del cliente"
    E resta sul riepilogo

  Scenario: Un nome fatto solo di spazi non è valido
    Dato che il riepilogo è aperto
    Quando il cliente scrive solo spazi come nome e conferma
    Allora vede l'avviso "Inserisci il nome del cliente"
    E resta sul riepilogo

  Scenario: Dopo l'errore il cliente corregge il nome e completa l'ordine
    Dato che il riepilogo è aperto
    Quando inserisce il suo nome
    E conferma l'ordine
    Allora vede la conferma dell'ordine
    E torna al menu