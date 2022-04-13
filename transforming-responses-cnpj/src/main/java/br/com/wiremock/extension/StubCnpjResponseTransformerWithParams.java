package br.com.wiremock.extension;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

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
  public boolean applyGlobally() {
    return false;
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

    if (cnpj.chars().noneMatch(Character::isDigit)) {
      return false;
    }

    final int[] array = cnpj.chars().map(c -> c - 48).toArray();

    final int soma13 = IntStream.range(0, 12).boxed().sorted(Collections.reverseOrder()).map(i -> array[i] * weight(13 - i)).reduce(0, (a, b) -> a + b);
    final int soma14 = IntStream.range(0, 13).boxed().sorted(Collections.reverseOrder()).map(i -> array[i] * weight(14 - i)).reduce(0, (a, b) -> a + b);

    final int resto13 = soma13 * 10 % 11;
    final int resto14 = soma14 * 10 % 11;

    final int digito13 = resto13 == 10 ? 0 : resto13;
    final int digito14 = resto14 == 10 ? 0 : resto14;

    return digito13 == array[12] && digito14 == array[13];

  }

  private static int weight(final int value) {
    return value >= 10 ? value - 8 : value;
  }

}
