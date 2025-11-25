package com.biblioteca.test.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Page Object para a página de listagem de livros.
 *
 * Encapsula toda a lógica de interação com a página de lista,
 * incluindo busca, navegação e operações CRUD nos livros.
 */
public class ListaLivrosPage extends BasePage {
    @FindBy(id="books-table")    private WebElement booksTable;
    @FindBy(id="btn-novo-livro") private WebElement btnNovo;
    @FindBy(css=".book-title")   private List<WebElement> bookTitles;
    @FindBy(css=".book-author")  private List<WebElement> bookAuthors;
    @FindBy(css=".book-category")private List<WebElement> bookCategories;
    @FindBy(css=".edit-btn")     private List<WebElement> editButtons;
    @FindBy(css=".delete-btn")   private List<WebElement> deleteButtons;

    public ListaLivrosPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return waitForElement(booksTable).isDisplayed();
    }

    public com.biblioteca.test.pageobjects.FormularioLivroPage clickNovoLivro() {
        waitForElementToBeClickable(btnNovo).click();
        return new FormularioLivroPage(driver);
    }

    public List<String> getTitulos() {
        return bookTitles.stream().map(WebElement::getText).toList();
    }

    public List<String> getAutores() {
        return bookAuthors.stream().map(WebElement::getText).toList();
    }

    public List<String> getCategorias() {
        return bookCategories.stream().map(WebElement::getText).toList();
    }

    public boolean existeLivro(String titulo) {
        return getTitulos().contains(titulo);
    }

    public int countLivros() {
        return getTitulos().size();
    }

    public ListaLivrosPage carregar() {
        waitForElement(booksTable);
        return this;
    }
}