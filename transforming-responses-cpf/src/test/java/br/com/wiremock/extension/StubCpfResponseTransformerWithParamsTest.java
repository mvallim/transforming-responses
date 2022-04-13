package br.com.wiremock.extension;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.jayway.restassured.path.json.JsonPath;

// @formatter:off
public class StubCpfResponseTransformerWithParamsTest {

  private static int PORT = 8082;

  @ClassRule
  public static WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(PORT).extensions(new StubCpfResponseTransformerWithParams()));

  @BeforeClass
  public static void beforeClass() {
    wireMockRule.start();
  }

  @AfterClass
  public static void afterClass() {
    wireMockRule.stop();
  }

  @Test
  public void willReturnValidCnpj() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .queryParam("cpf", "83672069086")
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(200)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("200"));
    assertThat(jsonPath.getString("message"), is("CPF válido"));
    assertThat(jsonPath.getString("value"), is("83672069086"));
  }

  @Test
  public void willReturnInvalidCnpj() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .queryParam("cpf", "01234567891")
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(404)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("404"));
    assertThat(jsonPath.getString("message"), is("CPF inválido"));
    assertThat(jsonPath.getString("value"), is("01234567891"));
  }

  @Test
  public void willReturnInvalidCnpjWhenIsEmpty() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .queryParam("cpf", "")
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(404)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("404"));
    assertThat(jsonPath.getString("message"), is("CPF inválido"));
    assertThat(jsonPath.getString("value"), is(""));
  }

  @Test
  public void willReturnInvalidCnpjWhenIsInvalid() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .queryParam("cpf", "11111111111")
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(404)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("404"));
    assertThat(jsonPath.getString("message"), is("CPF inválido"));
    assertThat(jsonPath.getString("value"), is("11111111111"));
  }

  @Test
  public void willReturnInvalidCpfWhenIsAlpha() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .queryParam("cpf", "abcdefghijk")
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(404)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("404"));
    assertThat(jsonPath.getString("message"), is("CPF inválido"));
    assertThat(jsonPath.getString("value"), is("abcdefghijk"));
  }

  @Test
  public void willReturnInvalidCnpjWhenIsNull() {
    wireMockRule.stubFor(
      post(urlPathMatching("/validate"))
      .willReturn(aResponse()
        .withStatus(200)
        .withHeader("content-type", "application/json")
        .withTransformers("stub-cpf-with-params")));

    final JsonPath jsonPath = given()
      .port(PORT)
      .contentType("application/json")
      .post("/validate")
      .then()
      .log().all()
      .statusCode(404)
      .extract()
      .jsonPath();

    assertThat(jsonPath.getString("code"), is("404"));
    assertThat(jsonPath.getString("message"), is("CPF inválido"));
    assertThat(jsonPath.getString("value"), nullValue());
  }

}
// @formatter:on
