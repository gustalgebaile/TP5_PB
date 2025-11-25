package com.biblioteca.selenium;

import com.biblioteca.BibliotecaWebApplication;
import com.biblioteca.test.config.WebDriverConfig;
import com.biblioteca.test.pageobjects.FormularioLivroPage;
import com.biblioteca.test.pageobjects.ListaLivrosPage;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BibliotecaParametrizedTest {

    private static final int PORT = 7000;
    private static final String BASE_URL = "http://localhost:" + PORT;

    private Javalin app;
    private WebDriver driver;
    private ListaLivrosPage listaPage;
    private FormularioLivroPage formularioPage;

    @BeforeAll
    void startServer() {
        app = BibliotecaWebApplication.createApp(PORT);
        app.start(PORT);
    }

    @AfterAll
    void stopServer() {
        if (app != null) {
            app.stop();
        }
    }

    @BeforeEach
    void setUp() {
        driver = new WebDriverConfig().createWebDriver(WebDriverConfig.BrowserType.CHROME, true);
        driver.get(BASE_URL + "/lista.html");
        listaPage = new ListaLivrosPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("Deve abrir lista vazia inicialmente")
    void testaListaVazia() {
        assertThat(listaPage.isPageLoaded())
                .as("Página lista.html deve estar carregada")
                .isTrue();
        assertThat(listaPage.countLivros())
                .as("Inicialmente deve não ter livros cadastrados")
                .isEqualTo(0);
    }

    @Test
    @DisplayName("Deve cadastrar um livro")
    void testaCadastroLivro() {
        formularioPage = listaPage.clickNovoLivro();
        formularioPage
                .preencherTitulo("Livro Teste")
                .preencherAutor("Autor Teste")
                .selecionarCategoria("FICCAO")
                .submitForm();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.urlContains("lista.html"));

        listaPage = new ListaLivrosPage(driver);
        assertThat(listaPage.existeLivro("Livro Teste"))
                .as("Livro cadastrado deve aparecer na lista")
                .isTrue();
    }
}
