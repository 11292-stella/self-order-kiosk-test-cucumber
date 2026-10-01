package com.stellamarucelli.kiosk.steps;

import com.stellamarucelli.kiosk.pages.SplashPage;

import io.cucumber.java.it.Dato;
import io.cucumber.java.it.Quando;
import com.stellamarucelli.kiosk.pages.MenuPage;
import io.cucumber.java.it.Allora;


import com.stellamarucelli.kiosk.support.DriverManager;

import static org.testng.Assert.assertTrue;

public class SplashSteps {

    SplashPage splash;
    MenuPage menu;

    @Dato("che la splash è visibile")
    public void splashVisibile(){
        splash = new SplashPage(DriverManager.getDriver());
        assertTrue(splash.isVisibile(), "La splash non è visibile");
    }

    @Quando("il cliente tocca per ordinare")
    public void tocca(){
        splash.toccaPerOrdinare();
    }

    @Allora("vede il menu dei prodotti")
    public void vedeIlMenu() {
        menu = new MenuPage(DriverManager.getDriver());
        assertTrue(menu.isVisibile(), "Il menu non è comparso dopo il tocco");
    }
}
