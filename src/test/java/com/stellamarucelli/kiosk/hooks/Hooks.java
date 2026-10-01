package com.stellamarucelli.kiosk.hooks;

import com.stellamarucelli.kiosk.pages.DettaglioProdottoPage;
import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.ContestoTest;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;

public class Hooks {

    // Una volta sola, prima di TUTTI gli scenari
    @BeforeAll
    public static void apriApp() {
        DriverManager.avvia();
    }

    // Dopo OGNI scenario: screenshot se è fallito (l'app resta aperta)
    // order alto = gira PRIMA delle pulizie qui sotto, così lo screenshot mostra il momento dell'errore
    @After(order = 1000)
    public void screenshotSeFallito(Scenario scenario) {
        if (scenario.isFailed() && DriverManager.getDriver() != null) {
            byte[] foto = DriverManager.getDriver().getScreenshotAs(OutputType.BYTES);
            scenario.attach(foto, "image/png", "screenshot errore");
        }
    }

    // Solo per gli scenari @ripristina_prodotto: rimette il prodotto disponibile nel backend
    // e, se il dettaglio si è aperto (bug), torna al menu
    @After(value = "@ripristina_prodotto", order = 10)
    public void ripristinaProdotto() {
        if (ContestoTest.prodottoDaRipristinare != null) {
            int status = ApiClient.impostaEsaurito(ContestoTest.prodottoDaRipristinare, false);
            System.out.println("Ripristino prodotto " + ContestoTest.prodottoDaRipristinare + ": status " + status);
            ContestoTest.prodottoDaRipristinare = null;
        }
        try {
            DettaglioProdottoPage dettaglio = new DettaglioProdottoPage(DriverManager.getDriver());
            if (!dettaglio.restaChiuso()) {
                dettaglio.tornaIndietro();
            }
        } catch (RuntimeException e) {
            System.out.println("Pulizia dettaglio non necessaria: " + e.getMessage());
        }
    }

    // Solo per gli scenari @ripristina_filtro: rimette il filtro su "Tutte" per chi viene dopo
    @After(value = "@ripristina_filtro", order = 10)
    public void ripristinaFiltro() {
        try {
            MenuPage menu = new MenuPage(DriverManager.getDriver());
            if (menu.isVisibile()) {
                menu.selezionaTutte();
            }
        } catch (RuntimeException e) {
            System.out.println("Ripristino filtro non riuscito: " + e.getMessage());
        }
    }

    // Una volta sola, dopo TUTTI gli scenari
    @AfterAll
    public static void chiudiApp() {
        DriverManager.chiudi();
    }
}
