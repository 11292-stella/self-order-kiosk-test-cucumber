package com.stellamarucelli.kiosk.support;

import net.datafaker.Faker;

import java.util.Locale;

public class DatiTest {

    // Faker in italiano
    private static final Faker faker = new Faker(Locale.ITALIAN);

    // Restituisce una nota casuale, realistica per un bar
    public static String notaCasuale() {
        return faker.options().option(
                "Senza zucchero",
                "Con latte di soia",
                "Poca schiuma",
                "Ben caldo",
                "Con cacao sopra",
                "Senza lattosio",
                "Da portare via"
        );
    }
}