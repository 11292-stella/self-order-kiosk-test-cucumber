package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.E;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class RegoleCarrelloSteps {

    // E il cliente aggiunge "Cappuccino" con quantità 2 e nota "Ben caldo"
    @E("il cliente aggiunge {string} con quantità {int} e nota {string}")
    public void aggiungeConQuantitaENota(String nome, int quantita, String nota) {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        menu.apriProdotto(nome);

        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.isVisibile(), "Il dettaglio di " + nome + " non si è aperto");

        // porta la quantità al valore richiesto (parte da 1)
        int attuale = Integer.parseInt(dettaglio.leggiQuantita());
        while (attuale < quantita) {
            dettaglio.aumentaQuantita();
            attuale++;
        }
        assertEquals(dettaglio.leggiQuantita(), String.valueOf(quantita), "Quantità nel dettaglio non corretta");

        dettaglio.scriviNota(nota);
        dettaglio.aggiungiAlCarrello();

        assertTrue(menu.isVisibile(), "Dopo l'aggiunta non si è tornati al menu");
    }

    // E nel carrello la nota di "Cappuccino" è "Ben caldo"
    @E("nel carrello la nota di {string} è {string}")
    public void notaNelCarrello(String nome, String notaAttesa) {
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        int id = ApiClient.idProdotto(nome);
        assertEquals(carrello.leggiNota(id), notaAttesa,
                "Nel carrello la nota di " + nome + " non è l'ultima inserita");
    }

    // E il cliente rimuove "Cappuccino" dal carrello
    @E("il cliente rimuove {string} dal carrello")
    public void rimuove(String nome) {
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        int id = ApiClient.idProdotto(nome);
        carrello.rimuovi(id);
    }
}