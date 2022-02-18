package com.desafio.klok.desafioklokautomacao.tests;

import com.desafio.klok.desafioklokautomacao.utils.Base;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChallengeTest extends Base {

    private static String webSite;
    private static String googleUrl;

    @BeforeAll
    public static void startDriver() {
        webSite = "Magazine Luiza";
        googleUrl = "http://www.google.com";
        start();
    }

    /*Esse teste serve para garantir a busca e o acesso ao site de compras*/
    @Test
    @Order(1)
    public void searchWebsiteSuccess() {
        driver.get(googleUrl);
        driver.manage().window().maximize();

        WebElement field = driver.findElement(By.name("q"));
        field.sendKeys(webSite);

        driver.findElement(By.name("btnK")).click();
        driver.findElement(By.xpath("//*[@id=\"rso\"]/div[1]/div/div/div/div/div/div/div[1]/a/h3")).click();
        assertTrue(driver.getTitle().contains(webSite));
    }

    /*Esse teste foi realizado na intenção de validar o resultado da busca do produto, o nome do produto
    * do campo de busca e o nome do produto encontrado foi manipulado para caixa baixa, solução encontrada para conseguir
    * comparar o nome do produto com o resultado da pesquisa e na tela de produtos encontrados.*/
    @Order(2)
    @ParameterizedTest
    @ValueSource(strings = {"Ventilador", "geladEira", "iphoNE", "Cama Box"})
    public void searchProductSuccess(String product){
        WebElement field = driver.findElement(By.xpath("//*[@id=\"input-search\"]"));
        field.clear();
        field.sendKeys(product);

        driver.findElement(By.cssSelector("#search-container > div > svg")).click();
        timeWith(3000);

        String resultOpOne = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/section[4]/div[1]/div/h1")).getText();
        String resultOpTwo = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/section[4]/div[3]/div/ul/li[1]/a/div[2]/h2")).getText();

        assertEquals(product.toLowerCase(), resultOpOne.toLowerCase());
        assertTrue(resultOpTwo.toLowerCase().contains(product.toLowerCase()));

    }

    /*Esse teste foi realizado na intenção de validar o comportamento do campo de busca ao inserir caracteres especiais
    * e aguardar a mensagem default de produto nao encontrado do site, foi encontrado um comportamento estranho,
    * considero um bug, a partir do caracter $ todos os outros caracteres encontram produtos sem sentido, por exemplo,
    * na busca por &, o caracter é convertido automaticamente por and no campo de busca e no resultado. Por essa razão
    * o teste foi quebrado, era esperado que fosse encontrado um WebElement com a mensagem de busca não encontrada para
    * todos os cenários a seguir EX: [$ = dollar, < = less, > = greater, | = or, & = and]*/
    @Order(3)
    @ParameterizedTest
    @ValueSource(strings = {"!", "@", "%", "*", "=", "$", "<", ">", "|" , "&"})
    public void searchFailed(String product){
        String messageNotMatch = "Sua busca não encontrou resultado algum :(";

        WebElement field = driver.findElement(By.xpath("//*[@id=\"input-search\"]"));
        field.clear();
        field.sendKeys(product);

        driver.findElement(By.cssSelector("#search-container > div > svg")).click();
        timeWith(3000);
        WebElement elementMessageNotMatch = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/main/section[5]/div[1]/div/h1"));

        assertNotNull(elementMessageNotMatch);
        assertEquals(messageNotMatch, elementMessageNotMatch.getText());
    }

    @AfterAll
    public static void endDriver() {
        driver.quit();
    }

}
