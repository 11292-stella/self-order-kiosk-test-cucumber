# Self-Order Kiosk – Test E2E mobile con Appium + Cucumber (Java)

Suite di test end-to-end per l'app **Flutter "self-order kiosk"** del progetto
[Restaurant Management System](https://github.com/11292-stella/restaurant-management-system)
(backend ASP.NET Core + PostgreSQL, gestionale Angular, kiosk Flutter/Riverpod).

I test pilotano l'app vera su un emulatore Android come farebbe un cliente
(splash → menu → dettaglio → carrello → riepilogo → conferma) e poi **verificano nel
backend, via API, che l'ordine sia stato salvato correttamente** (approccio ibrido UI + API).

Gli scenari sono scritti in **Gherkin in italiano**, leggibili anche da chi non programma.

---

## Stack

| Cosa | Tecnologia |
|---|---|
| Automazione mobile | **Appium 3** + driver **Flutter Integration** (`automationName: FlutterIntegration`) |
| Client | `io.appium:java-client` 10 (API Selenium WebDriver) – Java 21, Maven |
| BDD | **Cucumber 7** (`cucumber-java`, `cucumber-testng`), Gherkin in italiano |
| Runner / asserzioni | **TestNG** |
| Verifiche API | **REST Assured** (+ JsonPath) |
| Dati di test | **Datafaker** (nomi cliente, note) |
| Report | Cucumber HTML, JUnit XML (per la CI), **Allure** |
| Pattern | Page Object Model, step riusabili, contesto condiviso tra scenari |

---

## Cosa viene testato

| Feature | Scenari | Cosa verifica |
|---|---|---|
| `01_splash` | 1 | la splash si apre e il tocco porta al menu |
| `02_menu` | 1 | apertura del dettaglio di un prodotto |
| `03_carrello` | 1 | quantità, nota casuale (Datafaker), aggiunta al carrello |
| `04_ordine` | 1 | riepilogo, nome cliente, conferma, ritorno al menu |
| `05_verifica_api` | 1 + 1 `@bug` | l'ordine nel backend ha cliente, prodotto, quantità e totale giusti |
| `06_carrello_avanzato` | 2 | +/− nel carrello, totale = prezzo × quantità, riga che sparisce a 0, carrello vuoto |
| `07_regole_carrello` | 1 | stesso prodotto aggiunto due volte: **somma** le quantità e tiene **l'ultima** nota |
| `08_riepilogo_negativo` | 3 | nome vuoto o solo spazi rifiutati; dopo l'errore l'ordine si completa |
| `09_categorie` | 2 | il filtro per categoria mostra solo i suoi prodotti; "Tutte" ripristina il menu |
| `10_ordini_da_tabella` | 3 (Scenario Outline) | ordini diversi da una tabella, verificati via API al prezzo di listino |
| `11_dettaglio_limiti` | 2 | la quantità non scende sotto 1; il prezzo nel pulsante segue la quantità; tornare indietro non aggiunge nulla |
| `12_carrello_piu_prodotti` | 2 | totale = somma delle righe, rimozione di un prodotto, modifica della nota dal carrello |
| `13_nomi_cliente` | 4 (Scenario Outline) | apostrofi, accenti, nomi cortissimi e lunghi salvati esattamente com'erano |
| `14_robustezza` | 2 | doppio tocco su "Conferma" = un solo ordine; ritorno automatico al menu dopo 8 s |
| `90_bug_noti` | 3 `@bug` | bug trovati, documentati da test che falliscono finché non vengono corretti |

Totale: **26 scenari di regressione + 4 scenari `@bug`**.

Ogni scenario che crea dati li verifica **dal backend**, con valori attesi calcolati dalle API
(prezzi di listino, id dei prodotti, categorie) e non scritti a mano: se cambia il seed del
database i test continuano a funzionare.

---

## Bug trovati

| # | Bug | Come è emerso | Test |
|---|---|---|---|
| 1 | **Le note del cliente non arrivano al backend.** Il kiosk fa scrivere le note (nel dettaglio e nel carrello) ma l'ordine inviato contiene solo prodotto e quantità; il modello `Ordine` non ha un campo note. Un "senza lattosio" non arriva in cucina. | verifica ibrida UI + API | `05_verifica_api` `@bug` |
| 2 | **Esposizione di dati interni.** `GET /api/Ordine/{id}`, usato dal kiosk pubblico, restituisce anche `costoProduzione` dei prodotti (margine del ristorante). *Excessive data exposure*, OWASP API Top 10. | lettura della risposta JSON | `90_bug_noti` |
| 3 | **Il menu del kiosk non si aggiorna mai.** I prodotti sono caricati una volta all'avvio (cache Riverpod che non scade): un prodotto segnato esaurito dal gestionale resta ordinabile, un prezzo cambiato resta vecchio finché l'app non viene riavviata. | analisi del flusso + test | `90_bug_noti` (prodotto esaurito) |
| 4 | **Il filtro categoria resta al cliente successivo.** La categoria scelta è uno stato globale mai azzerato dopo l'ordine: il cliente dopo trova il menu già filtrato. | test del flusso completo | `90_bug_noti` |
| 5 | **Credenziali nel codice dell'app.** Il login automatico del kiosk usa un utente scritto in chiaro nel sorgente (marcato "provvisorio"). | code review | non automatizzabile, segnalato |

---

## Come si esegue

### Prerequisiti
- Java 21, Maven, Android SDK con un emulatore (qui `Pixel_4`, `emulator-5554`)
- Appium 3 con i driver `uiautomator2` e `flutter-integration`
  ```powershell
  npm i -g appium
  appium driver install uiautomator2
  appium driver install --source npm appium-flutter-integration-driver
  ```
- Il backend del Restaurant Management System acceso (`docker compose up -d`), raggiungibile su `http://localhost:5231`

### 1. Costruire l'APK "di test" del kiosk
L'app deve includere il server Appium per Flutter (`appium_flutter_server` tra le dev_dependencies
e il file `integration_test/appium_test.dart`). L'APK **non è nel repo** (pesa più di 100 MB):

```powershell
cd self_order_kiosk
flutter build apk --debug -t integration_test\appium_test.dart
Copy-Item build\app\outputs\flutter-apk\app-debug.apk ..\self_order_kiosk-test\src\test\resources\self_order_kiosk-test.apk
```

### 2. Checklist prima di ogni esecuzione
1. backend acceso (`docker compose up -d`)
2. emulatore acceso (`adb devices` → `emulator-5554  device`)
3. `appium` acceso in un terminale (aspettare `listener started on …:4723`)
4. nessuna sessione di Appium Inspector o `flutter run` aperta

### 3. Lanciare i test

```powershell
mvn test                                              # tutto tranne i bug noti
mvn test "-Dcucumber.filter.tags=@smoke"              # solo il flusso principale (01-04)
mvn test "-Dcucumber.filter.tags=@api"                # solo gli scenari con verifica via API
mvn test "-Dcucumber.filter.tags=not @nessuno"        # tutto, bug noti compresi
```

Da IntelliJ: Run su `runner/TestRunner`. Le stesse opzioni vanno in *Run → Edit Configurations → VM options*.

| Tag | Significato |
|---|---|
| `@smoke` | flusso principale, da lanciare per primo |
| `@regressione` | tutta la suite di regressione |
| `@api` | scenari con verifica nel backend |
| `@negativo` | input sbagliati e limiti |
| `@robustezza` | doppio tocco, timer |
| `@bug` | bug noti: falliscono finché il bug non viene corretto (esclusi di default) |

### 4. Configurazione per un altro ambiente
Il backend è configurabile senza toccare il codice (priorità: `-D` → variabile d'ambiente → default locale):

```powershell
mvn test -DAPI_BASE_URL=https://mio-backend.example.com -DAPI_USER=... -DAPI_PASSWORD=...
```

Le credenziali predefinite sono quelle dell'utente di test del seed locale; quelle vere non vanno mai nel codice.
Per un backend online serve anche un APK che punti a quell'indirizzo (oggi l'app usa `10.0.2.2:5231`).

### 5. Report
- `target/cucumber-report.html`: report Cucumber, con screenshot degli scenari falliti
- `target/cucumber-junit.xml`: per GitLab CI / GitHub Actions
- Allure: `npx allure-commandline serve target/allure-results` (oppure `allure serve target/allure-results`)

---

## Struttura

```
src/test/
├── java/com/stellamarucelli/kiosk/
│   ├── hooks/Hooks.java            # @BeforeAll apre l'app, @After screenshot + pulizie dei test @bug, @AfterAll chiude
│   ├── pages/                      # Page Object: una classe per schermata (DOVE sono gli elementi + COSA si può fare)
│   │   ├── SplashPage, MenuPage, DettaglioProdottoPage, CarrelloPage, RiepilogoPage, ConfermaPage
│   ├── steps/                      # step definition: frase Gherkin → chiamate alle Page + asserzioni
│   ├── support/
│   │   ├── DriverManager.java      # sessione Appium (FlutterAndroidDriver)
│   │   ├── ApiClient.java          # REST Assured: login, ordini, prodotti, modifiche per i test @bug
│   │   ├── DatiTest.java           # Datafaker
│   │   └── ContestoTest.java       # dati condivisi tra scenari (numero d'ordine, cliente, ...)
│   └── runner/TestRunner.java      # @CucumberOptions: feature, glue, plugin, tag
└── resources/
    ├── features/                   # 01_… 14_ + 90_bug_noti (l'ordine alfabetico è l'ordine di esecuzione)
    └── allure.properties
```

Il flusso di ogni riga di uno scenario:

```
riga Gherkin  →  step Java (stesso testo)  →  metodo della Page  →  Appium  →  widget Flutter
"Quando il cliente apre il prodotto "Cappuccino""  →  menu.apriProdotto("Cappuccino")  →  flutterText("Cappuccino").click()
```

---

## Scelte tecniche

- **Driver Flutter Integration**: gli elementi si trovano con le `Key` Flutter (`AppiumBy.flutterKey("btn_apri_carrello")`),
  più stabili di XPath o testi. Per gli elementi senza Key: `flutterText`, `flutterTextContaining` (testi con parti variabili),
  `flutterType("BackButton")` (widget standard).
- **Attese su condizione, mai `Thread.sleep`**: `WebDriverWait` finché un elemento compare *o sparisce*
  (anche "sparire" va atteso: la griglia si ridisegna, una SnackBar resta qualche secondo).
- **Una sola sessione per tutta l'esecuzione** (`@BeforeAll` / `@AfterAll`) e scenari in ordine (`01_`, `02_`…):
  emulatore stabile e niente reinstallazioni. Ogni feature parte dal menu e ci lascia l'app,
  pulita (carrello vuoto, filtro "Tutte").
- **Step autonomi**: ogni step riusabile crea da sé le Page e ricava gli id dai nomi, così funziona in qualsiasi scenario.
- **Dati**: Datafaker quando il valore non conta (nome cliente, nota); valori fissi quando il test dipende proprio
  da quel valore (quale nota vince, limiti, nomi con apostrofi); valori attesi letti dal backend (prezzi, id, categorie).
- **Asserzioni solo negli step**, le Page restituiscono valori (`true`/`false`, testo, numeri).
- **Bug noti con tag `@bug`**: la suite di default resta verde e segnala solo le regressioni nuove;
  i test `@bug` che modificano dati li ripristinano negli hook `@After` con tag.

---

## Lezioni imparate (troubleshooting)

| Sintomo | Causa | Soluzione |
|---|---|---|
| `FlutterServer not reachable … Retrying` | sull'emulatore c'è il kiosk "normale" (da `flutter run`), senza server Appium | `setEnforceAppInstall(true)`, `adb uninstall com.example.self_order_kiosk`, `adb forward --remove-all` |
| `Connection error … :4723` | Appium non è acceso | `appium` |
| `UndefinedStepException` | la frase del `.feature` è diversa dall'annotazione | testo identico; attenzione agli import `io.cucumber.java.it.*` (non `.bs.`!) |
| elemento "non trovato" ma visibile nello screenshot | `findElements` non aspetta | `WebDriverWait` su condizione |
| `expected [2] but found [3]` sulla quantità | stato iniziale dato per scontato (partiva da 1) | leggere lo stato prima di agire |
| popup "Choose input method" | `driver.navigate().back()` intercettato dalla tastiera di Appium | toccare la freccia dell'app (`flutterType("BackButton")`) |
| emulatore instabile, Gboard in crash | stato dell'emulatore sporco | Cold Boot |

---

## Cosa non è automatizzato (e perché)

Accessibilità con TalkBack, prestazioni e memoria dopo ore di utilizzo, backend spento all'avvio o durante l'invio,
rete lenta, rotazione e schermi diversi: richiedono il controllo dell'infrastruttura o del dispositivo, oppure una
valutazione umana. Sono elencati, con i comandi per provarli, in [`CHECKLIST_MOBILE.md`](CHECKLIST_MOBILE.md),
la guida che uso anche per testare un'app **senza avere il codice sorgente**.
