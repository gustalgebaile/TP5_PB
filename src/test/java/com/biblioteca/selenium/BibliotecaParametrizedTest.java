package com.biblioteca.selenium;

import com.biblioteca.model.Categoria;
import com.biblioteca.repository.LivroRepository;
import com.biblioteca.service.BibliotecaService;
import com.biblioteca.test.config.WebDriverConfig;
import com.biblioteca.test.pageobjects.FormularioLivroPage;
import com.biblioteca.test.pageobjects.ListaLivrosPage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.assertj.core.api.Assertions.*;

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
        // Crie manualmente o serviço, igual à sua main class
        bibliotecaService = new BibliotecaService(new LivroRepository());
        bibliotecaService.limparBase();

        driver = new WebDriverConfig().webDriver();
        baseUrl = "http://localhost:" + PORT;
        driver.get(baseUrl + "/lista.html");
        listaPage = new ListaLivrosPage(driver);
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
                .selecionarCategoria(categoria.name())
                .submitForm();

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

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.urlContains("lista.html"));

        listaPage = new ListaLivrosPage(driver);

        assertThat(listaPage.existeLivro(txt)).isTrue();
    }
}
