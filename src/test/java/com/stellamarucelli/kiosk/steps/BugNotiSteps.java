package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.ContestoTest;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertTrue;

// Step usati solo dagli scenari @bug (90_bug_noti.feature)
public class BugNotiSteps {

    // Quando il gestore segna "Cappuccino" come esaurito dal gestionale
    @Quando("il gestore segna {string} come esaurito dal gestionale")
    public void segnaEsaurito(String nomeProdotto) {
        int id = ApiClient.idProdotto(nomeProdotto);
        ContestoTest.prodottoDaRipristinare = id;     // l'hook @After lo rimetterà disponibile

        int status = ApiClient.impostaEsaurito(id, true);
        assertTrue(status >= 200 && status < 300,
                "Il backend ha rifiutato la modifica del prodotto (status " + status + "): controlla DTO o ruolo dell'utente di test");
    }
}
