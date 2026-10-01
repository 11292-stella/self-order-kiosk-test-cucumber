package com.stellamarucelli.kiosk.support;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class ApiClient {

    // ---- CONFIGURAZIONE: locale di default, sovrascrivibile per il deploy ----
    private static final String BASE_URL = config("API_BASE_URL", "http://localhost:5231");
    private static final String USERNAME = config("API_USER", "mrossi");          // utente di test del seed locale
    private static final String PASSWORD = config("API_PASSWORD", "password123");

    // Cerca un valore in quest'ordine:
    // 1) parametro -D di Maven/IntelliJ   (es. -DAPI_BASE_URL=https://...)
    // 2) variabile d'ambiente             (es. $env:API_BASE_URL)
    // 3) altrimenti usa il valore predefinito (locale)
    private static String config(String chiave, String predefinito) {
        String valore = System.getProperty(chiave);
        if (valore == null || valore.isBlank()) {
            valore = System.getenv(chiave);
        }
        if (valore == null || valore.isBlank()) {
            return predefinito;
        }
        return valore;
    }

    // POST /api/Auth/login → restituisce il token JWT
    public static String login() {
        return given()
                .baseUri(BASE_URL)
                .contentType(ContentType.JSON)
                .body(Map.of("username", USERNAME, "password", PASSWORD))
                .when()
                .post("/api/Auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    // GET /api/Ordine/{id} → restituisce la risposta completa dell'ordine
    public static Response getOrdine(int id) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
                .when()
                .get("/api/Ordine/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }

    // GET /api/Prodotto/{id} → restituisce il prodotto (nome, prezzo, ...)
    public static Response getProdotto(int id) {
        return given()
                .baseUri(BASE_URL)
                .header("Authorization", "Bearer " + login())
                .when()
                .get("/api/Prodotto/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .response();
    }
}