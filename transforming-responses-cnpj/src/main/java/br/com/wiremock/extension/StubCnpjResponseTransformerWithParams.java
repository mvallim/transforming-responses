package br.com.wiremock.extension;

import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.Set;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.QueryParameter;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public final class StubCnpjResponseTransformerWithParams extends ResponseTransformer {

  private static final Set<String> INVALID = new HashSet<>();

  public StubCnpjResponseTransformerWithParams() {
    INVALID.add("00000000000000");
    INVALID.add("11111111111111");
    INVALID.add("22222222222222");
    INVALID.add("33333333333333");
    INVALID.add("44444444444444");
    INVALID.add("55555555555555");
    INVALID.add("66666666666666");
    INVALID.add("77777777777777");
    INVALID.add("88888888888888");
    INVALID.add("99999999999999");
  }

  @Override
  public String getName() {
    return "stub-cnpj-with-params";
  }

  @Override
  public Response transform(final Request request, final Response response, final FileSource files, final Parameters parameters) {
    final QueryParameter queryParameter = request.queryParameter("cnpj");

    if (queryParameter.isPresent()) {
      final String cnpj = queryParameter.firstValue();

      if (validate(cnpj)) {
        return Response.Builder.like(response).status(200).body("{ \"code\": \"200\", \"message\": \"CNPJ válido\", \"value\": \"" + cnpj + "\" }").build();
      }

      return Response.Builder.like(response).status(404).body("{ \"code\": \"404\", \"message\": \"CNPJ inválido\", \"value\": \"" + cnpj + "\" }").build();
    }

    return Response.Builder.like(response).status(404).body("{ \"code\": \"404\", \"message\": \"CNPJ inválido\", \"value\": null }").build();
  }

  private static boolean validate(final String cnpj) {

    if (INVALID.contains(cnpj) || cnpj.length() != 14) {
      return false;
    }

    char digito13;
    char digito14;
    int resto;
    int soma;
    int value;
    int peso;

    try {
      soma = 0;
      peso = 2;
      for (int i = 11; i >= 0; i--) {
        value = cnpj.charAt(i) - 48;
        soma = soma + value * peso;
        peso = peso + 1;
        if (peso == 10) {
          peso = 2;
        }
      }

      resto = soma % 11;

      if (resto == 0 || resto == 1) {
        digito13 = '0';
      } else {
        digito13 = (char) (11 - resto + 48);
      }

      soma = 0;
      peso = 2;
      for (int i = 12; i >= 0; i--) {
        value = cnpj.charAt(i) - 48;
        soma = soma + value * peso;
        peso = peso + 1;
        if (peso == 10) {
          peso = 2;
        }
      }

      resto = soma % 11;
      if (resto == 0 || resto == 1) {
        digito14 = '0';
      } else {
        digito14 = (char) (11 - resto + 48);
      }

      if (digito13 == cnpj.charAt(12) && digito14 == cnpj.charAt(13)) {
        return true;
      } else {
        return false;
      }
    } catch (final InputMismatchException erro) {
      return false;
    }

  }

}
