package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

// Scenario 12: carrello con più prodotti diversi
public class CarrelloPiuProdottiSteps {

    // Allora il totale del carrello è la somma delle righe
    @Allora("il totale del carrello è la somma delle righe")
    public void totaleSommaRighe() {
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        assertTrue(carrello.isVisibile(), "Il carrello non è aperto o è vuoto");

        // per ogni prodotto attivo: se ha una riga nel carrello, sommo prezzo × quantità
        double atteso = 0;
        int righe = 0;
        for (Map.Entry<Integer, Double> p : ApiClient.prezziProdottiAttivi().entrySet()) {
            int id = p.getKey();
            if (carrello.haRiga(id)) {
                int quantita = Integer.parseInt(carrello.leggiQuantita(id));
                atteso += p.getValue() * quantita;
                righe++;
                System.out.println("Riga prodotto " + id + ": " + quantita + " × " + p.getValue());
            }
        }

        assertTrue(righe > 0, "Nessuna riga trovata nel carrello");
        assertEquals(carrello.leggiTotale(), atteso, 0.001,
                "Il totale del carrello non è la somma di prezzo × quantità delle righe");
    }

    // E non contiene più "Maritozzo con panna"
    @E("non contiene più {string}")
    public void nonContienePiu(String nomeProdotto) {
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        int id = ApiClient.idProdotto(nomeProdotto);
        assertTrue(carrello.rigaSparita(id), nomeProdotto + " è ancora nel carrello dopo la rimozione");
    }

    // Quando il cliente cambia dal carrello la nota di "Cappuccino" in "Con cacao sopra"
    @Quando("il cliente cambia dal carrello la nota di {string} in {string}")
    public void cambiaNota(String nomeProdotto, String nuovaNota) {
        CarrelloPage carrello = new CarrelloPage(DriverManager.getDriver());
        int id = ApiClient.idProdotto(nomeProdotto);
        carrello.scriviNota(id, nuovaNota);
    }
}
