package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.CarrelloPage;
import com.stellamarucelli.kiosk.pages.ConfermaPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.pages.RiepilogoPage;
import com.stellamarucelli.kiosk.support.DatiTest;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.Dato;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;
import com.stellamarucelli.kiosk.support.ContestoTest;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class OrdineSteps {

    CarrelloPage carrello;
    RiepilogoPage riepilogo;
    ConfermaPage conferma;
    MenuPage menu;
    String nomeCliente;

    // 1) Dato che il carrello è aperto con almeno un prodotto
    @Dato("che il carrello è aperto con almeno un prodotto")
    public void carrelloAperto() {
        carrello = new CarrelloPage(DriverManager.getDriver());
        assertTrue(carrello.isVisibile(), "Il carrello non è aperto o è vuoto");
    }

    // 2) Quando il cliente va al riepilogo
    @Quando("il cliente va al riepilogo")
    public void vaAlRiepilogo() {
        carrello.vaiAlRiepilogo();
        riepilogo = new RiepilogoPage(DriverManager.getDriver());
        assertTrue(riepilogo.isVisibile(), "Il riepilogo non si è aperto");
    }

    // 3) E inserisce il suo nome
    @E("inserisce il suo nome")
    public void inserisceNome() {
        nomeCliente = DatiTest.nomeCliente();               // Faker genera un nome
        System.out.println("Cliente: " + nomeCliente);      // lo stampo per riprodurre eventuali errori
        riepilogo.inserisciNome(nomeCliente);
        assertEquals(riepilogo.leggiNome(), nomeCliente, "Il nome non è stato scritto correttamente");
        ContestoTest.nomeCliente = nomeCliente;
    }

    // 4) E conferma l'ordine
    @E("conferma l'ordine")
    public void confermaOrdine() {
        riepilogo.confermaOrdine();
    }

    // 5) Allora vede la conferma dell'ordine
    @Allora("vede la conferma dell'ordine")
    public void vedeConferma() {
        conferma = new ConfermaPage(DriverManager.getDriver());
        assertTrue(conferma.isVisibile(), "La schermata di conferma non è comparsa");
        System.out.println(conferma.leggiMessaggio());       // es. "Ordine #12 confermato"
        assertTrue(conferma.numeroOrdine() > 0, "Il numero d'ordine non è valido");
        ContestoTest.numeroOrdine = conferma.numeroOrdine();
    }

    // 6) E torna al menu
    @E("torna al menu")
    public void tornaAlMenu() {
        conferma.tornaAlMenu();
        menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Dopo la conferma non si è tornati al menu");
    }
}