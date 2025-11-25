package com.biblioteca.test.pageobjects;

import com.biblioteca.test.pageobjects.FormularioLivroPage;
import com.biblioteca.test.pageobjects.ListaLivrosPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    @FindBy(id = "navbar-brand")
    protected WebElement navbarBrand;

    @FindBy(id = "nav-lista")
    protected WebElement navLista;

    @FindBy(id = "nav-novo")
    protected WebElement navNovo;

    @FindBy(id = "alert-success")
    protected WebElement successAlert;

    @FindBy(id = "alert-error")
    protected WebElement errorAlert;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        PageFactory.initElements(driver, this);
    }

    protected WebElement waitForElement(WebElement element) {
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    protected WebElement waitForElementToBeClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    protected void waitForElementToDisappear(WebElement element) {
        wait.until(ExpectedConditions.invisibilityOf(element));
    }

    public abstract boolean isPageLoaded();

    public String getPageTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public ListaLivrosPage navigateToListaLivros() {
        waitForElementToBeClickable(navLista).click();
        return new ListaLivrosPage(driver);
    }

    public FormularioLivroPage navigateToNovoLivro() {
        waitForElementToBeClickable(navNovo).click();
        return new FormularioLivroPage(driver);
    }

    public ListaLivrosPage navigateToHome() {
        waitForElementToBeClickable(navbarBrand).click();
        return new ListaLivrosPage(driver);
    }

    public boolean hasSuccessAlert() {
        try {
            return successAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasErrorAlert() {
        try {
            return errorAlert.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public String getSuccessAlertText() {
        try {
            return waitForElement(successAlert).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public String getErrorAlertText() {
        try {
            return waitForElement(errorAlert).getText();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean waitForSuccessAlert() {
        try {
            waitForElement(successAlert);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean waitForErrorAlert() {
        try {
            waitForElement(errorAlert);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected Object executeScript(String script, WebElement element) {
        return ((JavascriptExecutor) driver).executeScript(script);
    }

    protected void scrollToElement(WebElement element) {
        executeScript("arguments[0].scrollIntoView(true);", element);
    }

    protected void waitForPageLoad() {
        WebElement element = null;
        wait.until(driver -> executeScript("return document.readyState", element).equals("complete"));
    }
}