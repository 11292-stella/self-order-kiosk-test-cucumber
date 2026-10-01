# language: it
@regressione @negativo
Funzionalità: Limiti della quantità e prezzo nel dettaglio prodotto

  Scenario: La quantità non scende sotto 1
    Dato che il menu è visibile
    Quando il cliente apre il prodotto "Cappuccino"
    E prova a diminuire la quantità sotto 1
    Allora la quantità nel dettaglio resta 1

  Scenario: Il prezzo nel pulsante "Aggiungi" segue la quantità
    Quando il cliente porta la quantità nel dettaglio a 3
    Allora il pulsante mostra il prezzo di 3 "Cappuccino"
    E il cliente torna al menu senza aggiungere
    E il cliente apre il carrello dal menu e lo trova vuoto
