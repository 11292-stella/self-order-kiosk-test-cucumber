# language: it
@regressione @api
Funzionalità: Nomi cliente particolari

  # Apostrofi, accenti, trattini, nomi cortissimi o lunghi: il backend
  # deve salvarli ESATTAMENTE come li ha scritti il cliente.
  Schema dello scenario: Il nome "<nome>" viene salvato esattamente com'è
    Dato che il menu è visibile
    E il cliente aggiunge "Cappuccino" con quantità 1 e nota "Nessuna"
    E apre il carrello
    E il cliente va al riepilogo
    Quando il cliente inserisce il nome "<nome>"
    E conferma l'ordine
    Allora vede la conferma dell'ordine
    E torna al menu
    E nel backend il cliente dell'ordine è "<nome>"

    Esempi:
      | nome                                   |
      | Anna-Maria D'Angelo                    |
      | José Núñez                             |
      | Lu                                     |
      | Bartolomeo Massimiliano Giovanni Rossi |
