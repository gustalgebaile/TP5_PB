package com.biblioteca.selenium;

import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;
import com.biblioteca.test.config.WebDriverConfig;
import com.biblioteca.test.pageobjects.FormularioLivroPage;
import com.biblioteca.test.pageobjects.ListaLivrosPage;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BibliotecaSeleniumTest {

    private WebDriver driver;
    private ListaLivrosPage listaPage;
    private FormularioLivroPage formularioPage;

    private static final int PORT = 7000;
    private String baseUrl;

    private BibliotecaService bibliotecaService;

    @BeforeEach
    void setUp() {
        bibliotecaService = new BibliotecaService(new LivroRepository());
        bibliotecaService.limparBase();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);

        baseUrl = "http://localhost:" + PORT;
        driver.get(baseUrl + "/lista.html");
        listaPage = new ListaLivrosPage(driver);
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    @Order(1)
    @DisplayName("CT01 - Carregamento inicial")
    void testInicial() {
        assertThat(listaPage.isPageLoaded())
                .as("Tabela deve estar visível")
                .isTrue();

        assertThat(driver.getCurrentUrl())
                .contains("lista.html");

        assertThat(listaPage.countLivros())
                .as("Inicialmente não há livros")
                .isEqualTo(10);
    }

    @Test
    @Order(2)
    @DisplayName("CT02 - Navegação")
    void testNavegacao() {
        formularioPage = listaPage.clickNovoLivro();
        assertThat(driver.getCurrentUrl()).contains("formulario.html");

        listaPage = formularioPage.cancelar();
        assertThat(driver.getCurrentUrl()).contains("lista.html");
    }

    @Test
    @Order(3)
    @DisplayName("CT03 - Cadastro Fluxo Positivo")
    void testCadastroPositivo() {
        String titulo = "Teste Livro";
        String autor = "Autor Exemplo";
        String cat = "FICCAO";

        // Navega ao formulário
        formularioPage = listaPage.clickNovoLivro();
        formularioPage
                .preencherTitulo(titulo)
                .preencherAutor(autor)
                .selecionarCategoria(cat)
                .submitForm();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("lista.html"));

        listaPage = new ListaLivrosPage(driver);

        assertThat(driver.getCurrentUrl())
                .contains("lista.html");

        assertThat(listaPage.existeLivro(titulo))
                .as("Livro deve aparecer na lista")
                .isTrue();
    }
}
