package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

// Scenario 11 (limiti della quantità e prezzo nel dettaglio) + bug del prodotto esaurito
public class DettaglioSteps {

    // E prova a diminuire la quantità sotto 1
    @E("prova a diminuire la quantità sotto 1")
    public void diminuisceSottoUno() {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.isVisibile(), "Il dettaglio non è aperto");
        assertEquals(dettaglio.leggiQuantita(), "1", "La quantità iniziale dovrebbe essere 1");
        // a quantità 1 il "−" è disabilitato: due tocchi non devono cambiare nulla
        dettaglio.diminuisciQuantita();
        dettaglio.diminuisciQuantita();
    }

    // Allora la quantità nel dettaglio resta 1
    @Allora("la quantità nel dettaglio resta {int}")
    public void quantitaResta(int attesa) {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertEquals(dettaglio.leggiQuantita(), String.valueOf(attesa),
                "La quantità è scesa sotto il minimo consentito");
    }

    // Quando il cliente porta la quantità nel dettaglio a 3
    @Quando("il cliente porta la quantità nel dettaglio a {int}")
    public void portaQuantita(int quantita) {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        dettaglio.impostaQuantita(quantita);
        assertEquals(dettaglio.leggiQuantita(), String.valueOf(quantita), "Quantità nel dettaglio non corretta");
    }

    // Allora il pulsante mostra il prezzo di 3 "Cappuccino"
    @Allora("il pulsante mostra il prezzo di {int} {string}")
    public void prezzoNelPulsante(int quantita, String nomeProdotto) {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        double listino = ApiClient.prezzo(ApiClient.idProdotto(nomeProdotto));
        assertEquals(dettaglio.prezzoNelPulsante(), listino * quantita, 0.001,
                "Il prezzo nel pulsante non è prezzo di listino × quantità");
    }

    // E il cliente torna al menu senza aggiungere
    @E("il cliente torna al menu senza aggiungere")
    public void tornaSenzaAggiungere() {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        dettaglio.tornaIndietro();
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Dal dettaglio non si è tornati al menu");
    }

    // E il cliente apre il carrello dal menu e lo trova vuoto
    @E("il cliente apre il carrello dal menu e lo trova vuoto")
    public void carrelloAncoraVuoto() {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        menu.apriCarrello();
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        assertTrue(carrello.isVuoto(), "Tornando indietro dal dettaglio il prodotto è finito lo stesso nel carrello");
        carrello.tornaIndietro();
        assertTrue(menu.isVisibile(), "Dal carrello non si è tornati al menu");
    }

    // E il cliente prova ad aprire il prodotto "Cappuccino"   (bug esaurito)
    @E("il cliente prova ad aprire il prodotto {string}")
    public void provaAdAprire(String nomeProdotto) {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        menu.apriProdotto(nomeProdotto);
    }

    // Allora il dettaglio del prodotto non si apre
    @Allora("il dettaglio del prodotto non si apre")
    public void dettaglioNonSiApre() {
        DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.restaChiuso(),
                "BUG: il prodotto è esaurito nel backend ma il kiosk lo lascia ordinare (menu mai ricaricato)");
    }
}
