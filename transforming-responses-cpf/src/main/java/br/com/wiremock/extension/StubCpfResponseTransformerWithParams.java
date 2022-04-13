package br.com.wiremock.extension;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

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
  public boolean applyGlobally() {
    return false;
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

    final int[] array = cpf.chars().map(c -> c - 48).toArray();

    final int soma10 = IntStream.range(0, 9).map(i -> array[i] * (10 - i)).sum();
    final int soma11 = IntStream.range(0, 10).map(i -> array[i] * (11 - i)).sum();

    final int resto10 = soma10 * 10 % 11;
    final int resto11 = soma11 * 10 % 11;

    final int digito10 = resto10 == 10 ? 0 : resto10;
    final int digito11 = resto11 == 10 ? 0 : resto11;

    return digito10 == array[9] && digito11 == array[10];

  }

}
