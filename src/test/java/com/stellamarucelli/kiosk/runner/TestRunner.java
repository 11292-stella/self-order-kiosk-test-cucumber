package com.stellamarucelli.kiosk.runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"com.stellamarucelli.kiosk.steps", "com.stellamarucelli.kiosk.hooks"},
        plugin = {
                "pretty",                                           // output leggibile in console
                "html:target/cucumber-report.html",                 // report HTML di Cucumber
                "junit:target/cucumber-junit.xml",                  // per la CI (GitLab/GitHub leggono JUnit XML)
                "io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm"  // risultati per il report Allure
        },
        // Di default: tutto tranne i bug noti. Da fuori si può cambiare, es.
        //   -Dcucumber.filter.tags="@smoke"          → solo i test rapidi
        //   -Dcucumber.filter.tags="not @nessuno"    → tutto, bug compresi
        tags = "not @bug"
)
public class TestRunner extends AbstractTestNGCucumberTests {
}
