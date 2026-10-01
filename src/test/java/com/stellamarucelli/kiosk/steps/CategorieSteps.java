package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.MenuPage;
import com.stellamarucelli.kiosk.support.ApiClient;
import com.stellamarucelli.kiosk.support.DriverManager;
import io.cucumber.java.it.Allora;
import io.cucumber.java.it.E;
import io.cucumber.java.it.Quando;

import static org.testng.Assert.assertTrue;

public class CategorieSteps {

    int categoriaScelta;   // la categoria selezionata in questo scenario

    // Quando il cliente seleziona una categoria diversa da quella di "Cappuccino"
    @Quando("il cliente seleziona una categoria diversa da quella di {string}")
    public void selezionaAltraCategoria(String nomeProdotto) {
        int categoriaDelProdotto = ApiClient.categoriaDi(nomeProdotto);          // es. 1
        categoriaScelta = ApiClient.altraCategoriaConProdotti(categoriaDelProdotto); // es. 2
        System.out.println("Categoria selezionata: " + categoriaScelta);

        MenuPage menu = new MenuPage(DriverManager.getDriver());
        menu.selezionaCategoria(categoriaScelta);
    }

    // Allora vede il primo prodotto di quella categoria
    @Allora("vede il primo prodotto di quella categoria")
    public void vedePrimoProdotto() {
        String primo = ApiClient.primoProdottoDellaCategoria(categoriaScelta);
        System.out.println("Primo prodotto atteso: " + primo);

        MenuPage menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.vedeProdotto(primo),
                "Il primo prodotto della categoria (" + primo + ") non è nel menu filtrato");
    }

    // E non vede "Cappuccino" nel menu
    @E("non vede {string} nel menu")
    public void nonVede(String nomeProdotto) {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.nonVedeProdotto(nomeProdotto),
                nomeProdotto + " è ancora nel menu: il filtro per categoria non funziona");
    }

    // Quando il cliente seleziona tutte le categorie
    @Quando("il cliente seleziona tutte le categorie")
    public void selezionaTutte() {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        menu.selezionaTutte();
    }

    // Allora vede "Cappuccino" nel menu
    @Allora("vede {string} nel menu")
    public void vede(String nomeProdotto) {
        MenuPage menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.vedeProdotto(nomeProdotto),
                nomeProdotto + " non è nel menu");
    }
}