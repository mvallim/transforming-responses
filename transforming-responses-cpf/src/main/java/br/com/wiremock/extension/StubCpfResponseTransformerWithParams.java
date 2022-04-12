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

public final class StubCpfResponseTransformerWithParams extends ResponseTransformer {

  private static final Set<String> INVALID = new HashSet<>();

  public StubCpfResponseTransformerWithParams() {
    INVALID.add("00000000000");
    INVALID.add("11111111111");
    INVALID.add("22222222222");
    INVALID.add("33333333333");
    INVALID.add("44444444444");
    INVALID.add("55555555555");
    INVALID.add("66666666666");
    INVALID.add("77777777777");
    INVALID.add("88888888888");
    INVALID.add("99999999999");
  }

  @Override
  public String getName() {
    return "stub-cpf-with-params";
  }

  @Override
  public Response transform(final Request request, final Response response, final FileSource files, final Parameters parameters) {
    final QueryParameter queryParameter = request.queryParameter("cpf");

    if (queryParameter.isPresent()) {
      final String cpf = queryParameter.firstValue();

      if (validate(cpf)) {
        return Response.Builder.like(response).status(200).body("{ \"code\": \"200\", \"message\": \"CPF válido\", \"value\": \"" + cpf + "\" }").build();
      }

      return Response.Builder.like(response).status(404).body("{ \"code\": \"404\", \"message\": \"CPF inválido\", \"value\": \"" + cpf + "\" }").build();
    }

    return Response.Builder.like(response).status(404).body("{ \"code\": \"404\", \"message\": \"CPF inválido\", \"value\": null }").build();
  }

  private static boolean validate(final String cpf) {

    if (INVALID.contains(cpf) || cpf.length() != 11) {
      return false;
    }

    char digito10;
    char digito11;
    int soma;
    int resto;
    int valor;
    int peso;

    try {
      soma = 0;
      peso = 10;

      for (int i = 0; i < 9; i++) {
        valor = cpf.charAt(i) - 48;
        soma = soma + valor * peso;
        peso = peso - 1;
      }

      resto = 11 - soma % 11;

      if (resto == 10 || resto == 11) {
        digito10 = '0';
      } else {
        digito10 = (char) (resto + 48);
      }

      soma = 0;
      peso = 11;
      for (int i = 0; i < 10; i++) {
        valor = cpf.charAt(i) - 48;
        soma = soma + valor * peso;
        peso = peso - 1;
      }

      resto = 11 - soma % 11;
      if (resto == 10 || resto == 11) {
        digito11 = '0';
      } else {
        digito11 = (char) (resto + 48);
      }

      if (digito10 == cpf.charAt(9) && digito11 == cpf.charAt(10)) {
        return true;
      } else {
        return false;
      }
    } catch (final InputMismatchException erro) {
      return false;
    }

  }

}
