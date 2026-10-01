# Checklist test mobile – anche senza codice sorgente

Guida pratica nata da questa suite (kiosk Flutter, Appium + Cucumber + REST Assured).
Serve per orientarsi su **un'app aziendale di cui si ha solo l'APK** (black box):
come trovare i selettori, quali casi provare schermata per schermata, cosa controllare
oltre alla funzionalità e come scrivere i test.

Indice:
1. [Primo giorno su un'app sconosciuta](#1-primo-giorno-su-unapp-sconosciuta)
2. [Trovare i selettori senza codice](#2-trovare-i-selettori-senza-codice)
3. [Casi da provare, schermata per schermata](#3-casi-da-provare-schermata-per-schermata)
4. [Tecniche per inventare i casi](#4-tecniche-per-inventare-i-casi)
5. [Il lato API senza codice: cosa parte davvero dall'app](#5-il-lato-api-senza-codice-cosa-parte-davvero-dallapp)
6. [Non funzionale: dispositivo, rete, accessibilità, prestazioni](#6-non-funzionale-dispositivo-rete-accessibilità-prestazioni)
7. [Sicurezza: controlli rapidi](#7-sicurezza-controlli-rapidi)
8. [Come organizzare la suite](#8-come-organizzare-la-suite)
9. [Segnalare un bug](#9-segnalare-un-bug)
10. [Troubleshooting Appium](#10-troubleshooting-appium)

---

## 1. Primo giorno su un'app sconosciuta

- [ ] Installare l'APK sull'emulatore e **usarla a mano** come un utente, prendendo appunti sui flussi principali
- [ ] Capire la tecnologia: nativa Android, Flutter, React Native, WebView? (Appium Inspector: un unico `android.view.View`
      che contiene tutto = Flutter; `android.webkit.WebView` = ibrida)
- [ ] Recuperare il **package** e l'**activity** principale:
  ```powershell
  adb shell pm list packages | Select-String nomeapp
  adb shell dumpsys window | Select-String mCurrentFocus     # con l'app aperta in primo piano
  ```
- [ ] Chiedere: ambiente di test, utenti di test, dati di seed, documentazione API (Swagger), chi sono gli sviluppatori
- [ ] Disegnare la **mappa delle schermate** e delle transizioni (diventerà l'ordine delle feature: `01_`, `02_`…)

Capability minime per un'app nativa/ibrida (driver UiAutomator2):

```java
UiAutomator2Options options = new UiAutomator2Options()
        .setDeviceName("Pixel_4")
        .setApp("C:\\percorso\\app.apk")          // oppure setAppPackage + setAppActivity se già installata
        .setAutoGrantPermissions(true)
        .setNoReset(true)                         // non cancella i dati dell'app tra una sessione e l'altra
        .setNewCommandTimeout(Duration.ofSeconds(60));
AndroidDriver driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
```

---

## 2. Trovare i selettori senza codice

Strumento: **Appium Inspector** (vede l'albero Android). Ordine di preferenza dei locator:

| Priorità | Locator | Java | Note |
|---|---|---|---|
| 1 | accessibility id (`content-desc`) | `AppiumBy.accessibilityId("Conferma")` | stabile se gli sviluppatori lo impostano |
| 2 | resource-id | `AppiumBy.id("com.app:id/btn_conferma")` | tipico delle app native |
| 3 | UiAutomator | `AppiumBy.androidUIAutomator("new UiSelector().text(\"Conferma\")")` | potente, anche per scrollare |
| 4 | testo | `By.xpath("//*[@text='Conferma']")` | si rompe se cambia il testo o la lingua |
| 5 | XPath strutturale | `By.xpath("//android.view.View[2]/...")` | **ultima scelta**: fragilissimo |

Scroll fino a un elemento (app native):

```java
driver.findElement(AppiumBy.androidUIAutomator(
        "new UiScrollable(new UiSelector().scrollable(true)).scrollIntoView(new UiSelector().text(\"Cappuccino\"))"));
```

**App Flutter senza codice**: Android vede solo testi e `content-desc` (le etichette `Semantics`).
Si può lavorare con quelli, ma la cosa giusta è **chiedere agli sviluppatori** di aggiungere:
- `Key('btn_conferma_ordine')` sui widget interattivi, e una build di test con `appium_flutter_server`
  (così si usa `automationName: FlutterIntegration` e `AppiumBy.flutterKey(...)`, come in questa suite), oppure
- `Semantics(label: 'btn_conferma_ordine', ...)`, visibile come `accessibility id` anche con UiAutomator2.

È una richiesta normale: si chiama *testability* e va fatta presto.

Locator Flutter usati in questa suite (driver Flutter Integration):

```java
AppiumBy.flutterKey("btn_apri_carrello")               // per Key: sempre la prima scelta
AppiumBy.flutterKey("btn_piu_" + prodottoId)           // Key costruita con un dato
AppiumBy.flutterText("Cappuccino")                     // testo identico
AppiumBy.flutterTextContaining("confermato")           // testo con parti variabili ("Ordine #12 confermato")
AppiumBy.flutterType("BackButton")                     // widget standard senza Key
```

Verificare un locator "a mano", senza scrivere il test (è quello che fa il client Java):

```powershell
$body = '{"using":"-flutter key","value":"splash_touch_area"}'     # oppure "accessibility id", "id", "xpath"
$el = Invoke-RestMethod -Method Post -Uri "http://127.0.0.1:4723/session/$s/element" -Body $body -ContentType "application/json"
$el | ConvertTo-Json -Depth 5
```

---

## 3. Casi da provare, schermata per schermata

### Avvio / splash
- [ ] primo avvio dopo l'installazione (permessi, onboarding)
- [ ] avvio con backend spento o rete assente: messaggio chiaro e pulsante "Riprova"? Riprova funziona quando il backend torna?
- [ ] avvio lento: l'app mostra un caricamento o resta bianca?

### Liste e menu
- [ ] lista vuota ("Nessun prodotto in questa categoria")
- [ ] tanti elementi: lo scroll funziona, l'ultimo elemento è raggiungibile
- [ ] filtri: mostrano **solo** gli elementi giusti, si possono togliere, **non restano** al giro dopo (bug 4 di questa suite)
- [ ] elementi disabilitati (esaurito, non disponibile): visibili ma non cliccabili
- [ ] i dati si aggiornano se cambiano nel back office? (bug 3: menu mai ricaricato)

### Dettaglio / form
- [ ] **valori limite** della quantità: minimo (0? 1?), massimo, "−" al minimo, "+" ripetuto
- [ ] prezzi e totali mostrati = prezzo × quantità, arrotondamenti, separatore decimale
- [ ] campi testo: vuoto, solo spazi, lunghissimo, emoji, apostrofi e accenti, caratteri speciali `' " < > & %`
- [ ] tornare indietro senza confermare **non** salva nulla

### Carrello
- [ ] più prodotti diversi: totale = somma delle righe
- [ ] stesso prodotto aggiunto due volte: si somma? crea due righe? cosa succede alle note?
- [ ] "−" a 1 rimuove la riga? Rimuovi l'ultima riga → messaggio "carrello vuoto"
- [ ] modifiche fatte nel carrello (quantità, note) arrivano davvero all'ordine?

### Checkout / riepilogo
- [ ] campi obbligatori vuoti o con solo spazi → messaggio, l'ordine **non** parte
- [ ] dopo l'errore si può correggere e completare
- [ ] **doppio tocco** sul pulsante di invio → un solo ordine
- [ ] invio con backend spento → messaggio, il carrello non si perde
- [ ] riepilogo coerente con il carrello (righe, quantità, totale)

### Conferma
- [ ] numero d'ordine mostrato = quello salvato nel backend
- [ ] timer di ritorno automatico, pulsante "torna al menu"
- [ ] dopo la conferma: carrello svuotato, filtri azzerati, nessun dato del cliente precedente

### Sessione lunga (tipico dei kiosk)
- [ ] dopo molti ordini di fila l'app è ancora fluida? la memoria cresce?
- [ ] token scaduto durante la giornata: l'app si riprende da sola?

---

## 4. Tecniche per inventare i casi

| Tecnica | Domanda | Esempio |
|---|---|---|
| **Valori limite** | cosa succede al minimo, al massimo, appena fuori? | quantità 0, 1, 2, 99, 100 |
| **Classi di equivalenza** | quali input "si comportano allo stesso modo"? basta uno per classe | nome normale / vuoto / solo spazi / con apostrofo / lunghissimo |
| **Transizioni di stato** | da ogni schermata dove posso andare? e tornando indietro? | menu → dettaglio → indietro → carrello vuoto? |
| **Errori dell'utente** | e se fa la cosa sbagliata, o la fa due volte? | doppio tocco su "Conferma" |
| **Tempo** | e se aspetta? e se è troppo veloce? | timer di 8 s, SnackBar che resta 4 s (falso positivo!) |
| **Interruzioni** | chiamata, app in background, rotazione, rete che cade | §6 |
| **Coerenza UI ↔ backend** | quello che vedo è quello che è stato salvato? | §5 |

Gli stessi casi, con dati diversi, si scrivono una volta sola con **Scenario Outline**:

```gherkin
Schema dello scenario: Il nome "<nome>" viene salvato esattamente com'è
  Quando il cliente inserisce il nome "<nome>"
  E conferma l'ordine
  Allora nel backend il cliente dell'ordine è "<nome>"

  Esempi:
    | nome                |
    | Anna-Maria D'Angelo |
    | José Núñez          |
    | Lu                  |
```

Faker o valori fissi?

| Faker (`net.datafaker`) | Valori fissi |
|---|---|
| quando il valore preciso non conta (un nome qualsiasi) | quando il test dipende da quel valore (limiti, confronti, "vince l'ultima nota") |
| `faker.name().firstName()` | `"Senza zucchero"` vs `"Ben caldo"` |
| **stampa sempre il valore usato** per riprodurre un fallimento | |

---

## 5. Il lato API senza codice: cosa parte davvero dall'app

Il bug più importante di questa suite (le note che non arrivano in cucina) si trova anche **senza codice**:
si fa un ordine con una nota e poi si guarda cosa è stato salvato.

### Vedere le chiamate dell'app
- **Proxy** (mitmproxy o Charles) sul PC, emulatore che passa dal proxy:
  ```powershell
  adb shell settings put global http_proxy 10.0.2.2:8080     # 10.0.2.2 = il PC visto dall'emulatore
  adb shell settings put global http_proxy :0                # per toglierlo
  ```
  Per l'HTTPS serve installare il certificato del proxy sull'emulatore; le app di produzione spesso lo impediscono
  (*certificate pinning*): chiedere una **build di debug** che si fidi dei certificati utente.
- **Logcat** filtrato sull'app:
  ```powershell
  adb logcat -d | Select-String "com.example.app|flutter" | Select-Object -Last 50
  ```
- **Swagger** del backend, se esiste: elenco degli endpoint e dei campi.

### Verificare via API nei test (REST Assured)

```java
// given → when → then → extract
String token = given().baseUri(BASE_URL).contentType(ContentType.JSON)
        .body(Map.of("username", USER, "password", PASSWORD))
        .when().post("/api/Auth/login")
        .then().statusCode(200).extract().path("token");

JsonPath ordine = given().baseUri(BASE_URL).header("Authorization", "Bearer " + token)
        .when().get("/api/Ordine/{id}", numeroOrdine)
        .then().statusCode(200).extract().jsonPath();

assertEquals(ordine.getString("cliente"), nomeInserito);
assertEquals(ordine.getInt("righe[0].quantita"), 2);
assertEquals(ordine.getDouble("totale"), prezzo * 2, 0.001);   // tolleranza sui decimali
```

Interrogare una lista con JsonPath (passare i valori con `param`, mai incollarli nella stringa: un apostrofo la rompe):

```java
int id = prodotti.param("nome", "Cappuccino").get("find { it.nome == nome }.id");
int quanti = ordini.param("c", cliente).param("d", ultimoId).get("findAll { it.cliente == c && it.id > d }.size()");
```

Controlli utili sulla risposta, anche senza codice:
- [ ] i dati salvati coincidono con quelli inseriti (testi, quantità, prezzi, totale)
- [ ] il totale lo calcola il **server** (il client potrebbe mandare un prezzo falso)
- [ ] la risposta **non** contiene campi interni (costi, ruoli, hash, dati di altri utenti) → bug 2
- [ ] codici di stato giusti sugli errori (400 dati non validi, 401 senza token, 403 senza permessi, 404, 409)

---

## 6. Non funzionale: dispositivo, rete, accessibilità, prestazioni

Comandi Appium (java-client) utili:

```java
driver.rotate(ScreenOrientation.LANDSCAPE);                 // rotazione: il layout regge? i dati restano?
driver.runAppInBackground(Duration.ofSeconds(10));          // app in background e ritorno: stato conservato?
driver.toggleWifi();                                        // rete giù / su durante un'operazione
driver.toggleAirplaneMode();
driver.terminateApp("com.example.app");                     // chiusura e riapertura
driver.activateApp("com.example.app");
```

Da riga di comando (ADB):

```powershell
adb shell settings put system font_scale 1.3                # testo ingrandito (accessibilità): niente testi tagliati?
adb shell settings put system font_scale 1.0
adb shell wm size 1920x1200                                 # simula un tablet/kiosk più grande
adb shell wm size reset
adb shell dumpsys gfxinfo com.example.app                   # fluidità (frame lenti)
adb shell dumpsys meminfo com.example.app                   # memoria (ripeterlo dopo molti ordini)
adb shell am start -W -n com.example.app/.MainActivity      # tempo di avvio
```

(Per lanciarli dai test con `mobile: shell` Appium va avviato con `appium --allow-insecure=uiautomator2:adb_shell`.)

Checklist:
- [ ] **Accessibilità**: TalkBack legge pulsanti e prezzi in modo comprensibile? contrasto sufficiente? aree di tocco ≥ 48 dp?
- [ ] **Schermi**: telefono, tablet, risoluzione del kiosk vero, orientamento
- [ ] **Rete**: lenta (timeout?), assente, che cade a metà invio
- [ ] **Ciclo di vita**: background, rotazione, blocco schermo, chiamata in arrivo
- [ ] **Prestazioni**: avvio a freddo, scroll con molti elementi, memoria dopo ore
- [ ] **Localizzazione**: lingua, formato della valuta e dei decimali

Molti di questi si verificano meglio a mano o con strumenti dedicati: l'importante è **sapere che esistono** e
dichiarare cosa è stato coperto e cosa no.

---

## 7. Sicurezza: controlli rapidi

- [ ] credenziali o chiavi scritte nell'app (in un APK si leggono decompilandolo; in questa suite: bug 5)
- [ ] token salvato in modo sicuro (Keystore / secure storage), cancellato al logout
- [ ] dati sensibili nei log (`adb logcat`)
- [ ] API che espongono più dati del necessario (bug 2)
- [ ] un utente "cliente" che chiama endpoint da "staff" (403?)
- [ ] ruolo scelto dal client in registrazione (privilege escalation)

---

## 8. Come organizzare la suite

```
features/   01_…, 02_…            → l'ordine alfabetico è l'ordine di esecuzione (una sola sessione)
pages/      una classe per schermata: DOVE sono gli elementi + COSA si può fare (niente assert)
steps/      frase Gherkin → chiamate alle Page + asserzioni
support/    driver, client API, dati di test, contesto condiviso
hooks/      @BeforeAll apri app, @After screenshot se fallisce, pulizie per tag, @AfterAll chiudi
```

Regole che hanno funzionato:
- **attese su condizione** (`WebDriverWait` finché compare/sparisce), mai `Thread.sleep`
- `clear()` prima di `sendKeys()`
- **step autonomi**: ogni step riusabile crea le sue Page e non dipende da campi preparati da altri step
- dati tra scenari diversi in un **contesto statico** (Cucumber ricrea le classi Steps a ogni scenario)
- ogni feature lascia l'app **pulita** (menu, carrello vuoto, filtri azzerati)
- tag: `@smoke` (rapidi), `@regressione`, `@api`, `@negativo`, `@bug` (bug noti esclusi di default)
- i test che modificano dati li **ripristinano** in un `@After` con tag
- valori attesi **letti dal backend**, non scritti a mano (prezzi, id)
- stampare i dati casuali usati

Esempio di filtri da riga di comando:

```powershell
mvn test "-Dcucumber.filter.tags=@smoke"
mvn test "-Dcucumber.filter.tags=@regressione and not @api"
mvn test "-Dcucumber.filter.tags=not @nessuno"     # tutto, bug compresi
```

---

## 9. Segnalare un bug

```
Titolo:     [Kiosk] Le note scritte dal cliente non vengono salvate nell'ordine
Ambiente:   APK debug x.y.z, emulatore Pixel 4 API 3x, backend locale (commit ...)
Passi:      1. aprire "Cappuccino"  2. scrivere la nota "Poca schiuma"  3. aggiungere e confermare l'ordine
            4. GET /api/Ordine/{id}
Atteso:     la riga dell'ordine contiene la nota "Poca schiuma"
Ottenuto:   nessun campo nota nella risposta (la nota viene persa)
Impatto:    alto – istruzioni del cliente (es. allergie/intolleranze) non arrivano in cucina
Evidenze:   screenshot, JSON della risposta, test automatico 05_verifica_api (@bug)
```

---

## 10. Troubleshooting Appium

| Sintomo | Causa probabile | Cosa fare |
|---|---|---|
| `Connection error … :4723` | Appium non è acceso | `appium` e aspettare `listener started` |
| `FlutterServer not reachable … Retrying` | installata l'app "normale" invece della build di test | `setEnforceAppInstall(true)`, `adb uninstall <package>`, `adb forward --remove-all` |
| due sessioni (porte 10000 / 10001) | Inspector o test lanciati due volte | chiudere tutto, riavviare `appium` |
| emulatore lento, app di sistema in crash | stato sporco | Cold Boot dall'AVD Manager |
| elemento visibile ma "non trovato" | `findElements` non aspetta | `WebDriverWait` su condizione |
| test che passa per sbaglio | elemento "vecchio" ancora a schermo (SnackBar, schermata sotto) | aspettare che sparisca / verificare prima di essere sulla schermata giusta |
| popup "Choose input method" | tasto back di Android con la tastiera di Appium | usare i pulsanti dell'app |
| `UndefinedStepException` | testo dello step diverso o import sbagliato | frase identica, `io.cucumber.java.it.*` |
| Appium Inspector non trova le Key Flutter | Inspector vede solo l'albero Android | Flutter DevTools (`flutter run` → `v`), codice, o chiedere Key/Semantics |
