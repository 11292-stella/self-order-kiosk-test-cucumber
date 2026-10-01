package com.stellamarucelli.kiosk.steps;
import com.stellamarucelli.kiosk.support.DatiTest;
import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class CarrelloSteps {

    DettaglioProdottoPage dettaglio;
    MenuPage menu;
    CarrelloPage carrello;
    String nota;

    // 1) Dato che il dettaglio del prodotto è aperto
    @Dato("che il dettaglio del prodotto è aperto")
    public void dettaglioAperto() {
        dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
        assertTrue(dettaglio.isVisibile(), "Il dettaglio del prodotto non è aperto");
    }

    // 2) Quando il cliente imposta la quantità a 2
    @Quando("il cliente imposta la quantità a {int}")
    public void impostaQuantita(int quantita) {
        // 1. leggo da dove parte (es. "1") e lo trasformo in numero
        int attuale = Integer.parseInt(dettaglio.leggiQuantita());

        // 2. premo "+" finché non arrivo al numero richiesto
        while (attuale < quantita) {
            dettaglio.aumentaQuantita();
            attuale++;
        }

        // 3. controllo che sullo schermo ci sia proprio quel numero
        assertEquals(dettaglio.leggiQuantita(), String.valueOf(quantita),
                "La quantità mostrata non è corretta");
    }

    // E scrive una nota per la cucina
    @E("scrive una nota per la cucina")
    public void scriveNota() {
        nota = DatiTest.notaCasuale();                 // Faker sceglie una nota a caso
        System.out.println("Nota usata: " + nota);     // la stampo, così so quale è uscita
        dettaglio.scriviNota(nota);
        assertEquals(dettaglio.leggiNota(), nota, "La nota non è stata scritta correttamente");
    }

    // 3) E aggiunge il prodotto al carrello
    @E("aggiunge il prodotto al carrello")
    public void aggiungeAlCarrello() {
        dettaglio.aggiungiAlCarrello();
    }

    // 4) E apre il carrello
    @E("apre il carrello")
    public void apreCarrello() {
        menu = new MenuPage(DriverManager.getDriver());
        menu.apriCarrello();
    }

    // 5) Allora il carrello contiene "Cappuccino"
    @Allora("il carrello contiene {string}")
    public void carrelloContiene(String nome) {
        carrello = new CarrelloPage(DriverManager.getDriver());
        assertTrue(carrello.isVisibile(), "Il carrello non si è aperto");
        assertTrue(carrello.contieneProdotto(nome), "Il carrello non contiene " + nome);
    }
}
