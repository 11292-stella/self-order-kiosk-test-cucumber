package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CarrelloAvanzatoSteps {

    MenuPage menu;
    DettaglioProdottoPage dettaglio;
    CarrelloPage carrello;
    int idProdotto;   // id del prodotto su cui stiamo lavorando (es. 5)

    // E il cliente aggiunge "Cappuccino" al carrello dal menu
    @E("il cliente aggiunge {string} al carrello dal menu")
    public void aggiungeDalMenu(String nome) {
        menu = new MenuPage(DriverManager.getDriver());
        menu.apriProdotto(nome);

        dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.isVisibile(), "Il dettaglio di " + nome + " non si è aperto");
        dettaglio.aggiungiAlCarrello();                       // quantità di default: 1

        assertTrue(menu.isVisibile(), "Dopo l'aggiunta non si è tornati al menu");
    }

    // Quando aumenta di 1 la quantità di "Cappuccino" nel carrello
    @Quando("aumenta di {int} la quantità di {string} nel carrello")
    public void aumenta(int volte, String nome) {
        carrello = new CarrelloPage(DriverManager.getDriver());
        idProdotto = ApiClient.idProdotto(nome);              // "Cappuccino" → 5
        for (int i = 0; i < volte; i++) {
            carrello.aumenta(idProdotto);
        }
    }

    // Allora nel carrello "Cappuccino" ha quantità 2
    @Allora("nel carrello {string} ha quantità {int}")
    public void haQuantita(String nome, int quantita) {
        carrello = new CarrelloPage(DriverManager.getDriver());
        idProdotto = ApiClient.idProdotto(nome);
        assertEquals(carrello.leggiQuantita(idProdotto), String.valueOf(quantita),
                "Quantità di " + nome + " nel carrello non corretta");
    }

    // E il totale del carrello è corretto
    @E("il totale del carrello è corretto")
    public void totaleCorretto() {
        double prezzo = ApiClient.getProdotto(idProdotto).jsonPath().getDouble("prezzo");
        int quantita = Integer.parseInt(carrello.leggiQuantita(idProdotto));

        assertEquals(carrello.leggiTotale(), prezzo * quantita, 0.001,
                "Il totale del carrello non è prezzo × quantità");
    }

    // Dato che nel carrello c'è "Cappuccino"
    @Dato("che nel carrello c'è {string}")
    public void nelCarrelloCe(String nome) {
        carrello = new CarrelloPage(DriverManager.getDriver());
        idProdotto = ApiClient.idProdotto(nome);
        assertTrue(carrello.contieneProdotto(nome), "Nel carrello non c'è " + nome);
    }

    // Quando diminuisce di 2 la quantità di "Cappuccino" nel carrello
    @Quando("diminuisce di {int} la quantità di {string} nel carrello")
    public void diminuisce(int volte, String nome) {
        for (int i = 0; i < volte; i++) {
            carrello.diminuisci(idProdotto);
        }
    }

    // Allora il carrello è vuoto
    @Allora("il carrello è vuoto")
    public void carrelloVuoto() {
        carrello = new CarrelloPage(DriverManager.getDriver());
        assertTrue(carrello.isVuoto(), "Il carrello dovrebbe essere vuoto");
    }

    // E il cliente torna al menu dal carrello
    @E("il cliente torna al menu dal carrello")
    public void tornaAlMenu() {
        carrello = new CarrelloPage(DriverManager.getDriver());
        carrello.tornaIndietro();
        menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Dal carrello non si è tornati al menu");
    }
}