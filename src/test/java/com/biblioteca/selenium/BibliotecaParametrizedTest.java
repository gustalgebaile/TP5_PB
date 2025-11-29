package com.biblioteca.selenium;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;
import com.biblioteca.test.config.WebDriverConfig;
import com.biblioteca.test.pageobjects.FormularioLivroPage;
import com.biblioteca.test.pageobjects.ListaLivrosPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BibliotecaParametrizedTest {

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

        // Espera até 30s a aplicação estar disponível
        waitForApp();

        driver.get(baseUrl + "/lista.html");
        listaPage = new ListaLivrosPage(driver);
    }

    private void waitForApp() {
        int attempts = 0;
        boolean up = false;
        while (attempts < 15 && !up) {
            try {
                driver.get(baseUrl + "/lista.html");
                up = true;
            } catch (Exception e) {
                attempts++;
                try { Thread.sleep(2000); } catch (InterruptedException ignored) {}
            }
        }
        if (!up) {
            throw new RuntimeException("Aplicação não está disponível em " + baseUrl + "/lista.html");
        }
    }

    @AfterEach
    void tearDown() {
        if (driver != null) driver.quit();
    }

    @ParameterizedTest(name = "Categoria {0}")
    @EnumSource(Categoria.class)
    @Order(1)
    void testCadastroCategorias(Categoria categoria) {
        String titulo = "Livro" + categoria.name();

        formularioPage = listaPage.clickNovoLivro();
        formularioPage
                .preencherTitulo(titulo)
                .preencherAutor("Autor")
                .selecionarCategoria(categoria.name());

        listaPage = formularioPage.submitForm();

        // Aguarda até 10s a lista conter o livro cadastrado
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> listaPage.existeLivro(titulo));

        assertThat(listaPage.existeLivro(titulo))
                .as("Deve cadastrar categoria " + categoria)
                .isTrue();
    }

    @ParameterizedTest(name = "Tamanho {0}")
    @ValueSource(ints = {1, 50, 100})
    @Order(2)
    void testBoundary(int size) {
        String txt = "a".repeat(size);

        formularioPage = listaPage.clickNovoLivro();
        formularioPage
                .preencherTitulo(txt)
                .preencherAutor("Autor")
                .selecionarCategoria("FICCAO");

        listaPage = formularioPage.submitForm();

        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(d -> listaPage.existeLivro(txt));

        assertThat(listaPage.existeLivro(txt)).isTrue();
    }
}
