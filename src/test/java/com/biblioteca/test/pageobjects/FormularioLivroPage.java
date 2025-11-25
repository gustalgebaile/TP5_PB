package com.biblioteca.test.pageobjects;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

public class FormularioLivroPage extends BasePage {
    @FindBy(id="titulo")      private WebElement tituloInput;
    @FindBy(id="autor")       private WebElement autorInput;
    @FindBy(id="categoria")   private WebElement categoriaSelect;
    @FindBy(id="btn-salvar")  private WebElement btnSalvar;
    @FindBy(id="btn-cancelar")private WebElement btnCancelar;

    public FormularioLivroPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isPageLoaded() {
        return waitForElement(tituloInput).isDisplayed()
                && waitForElement(autorInput).isDisplayed();
    }

    public FormularioLivroPage preencherTitulo(String txt) {
        WebElement e = waitForElement(tituloInput);
        e.clear();
        e.sendKeys(txt);
        return this;
    }

    public FormularioLivroPage preencherAutor(String txt) {
        WebElement e = waitForElement(autorInput);
        e.clear();
        e.sendKeys(txt);
        return this;
    }

    public FormularioLivroPage selecionarCategoria(String val) {
        new Select(waitForElement(categoriaSelect)).selectByValue(val);
        return this;
    }

    public com.biblioteca.test.pageobjects.ListaLivrosPage submitForm() {
        waitForElementToBeClickable(btnSalvar).click();
        waitForPageLoad();
        return new com.biblioteca.test.pageobjects.ListaLivrosPage(driver);
    }

    public com.biblioteca.test.pageobjects.ListaLivrosPage cancelar() {
        waitForElementToBeClickable(btnCancelar).click();
        return new ListaLivrosPage(driver);
    }
}

