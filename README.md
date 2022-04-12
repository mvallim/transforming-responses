# transformer-responses
Transforming responses wiremock

![Java CI with Maven](https://github.com/mvallim/transforming-responses/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)
![CodeQL](https://github.com/mvallim/transforming-responses/workflows/CodeQL/badge.svg?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=transforming-responses&metric=alert_status)](https://sonarcloud.io/dashboard?id=transforming-responses)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=transforming-responses&metric=coverage)](https://sonarcloud.io/dashboard?id=transforming-responses)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.mvallim/transforming-responses/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.mvallim/transforming-responses)
[![Hex.pm](https://img.shields.io/hexpm/l/plug.svg)](http://www.apache.org/licenses/LICENSE-2.0)

# Mock CPF API (Java)

```java
final WireMockRule wireMockRule = new WireMockRule(
  wireMockConfig().port(8080).extensions(new StubCpfResponseTransformerWithParams()));

wireMockRule.stubFor(
    post(urlPathMatching("/validate")).withQueryParam("cpf", matching("^.*$"))
    .willReturn(aResponse()
      .withStatus(200)
      .withHeader("content-type", "application/json")
      .withTransformers("stub-cpf-with-params")));
        
```

# Mock CPF API (JSON)

```json
{
  "request": {
    "method": "POST",
    "urlPath": "/validate",
    "queryParameters": {
      "cpf": {
        "matches": "^.*$"
      }
    },
    "headers": {
      "Content-Type": {
        "equalTo": "application/json",
        "caseInsensitive": true
      }
    }
  },
  "response": {
    "status": 200,
    "transformers": ["stub-cpf-with-params"]
  }
}
```

# Mock CNPJ API (Java)

```java
final WireMockRule wireMockRule = new WireMockRule(
  wireMockConfig().port(8080).extensions(new StubCnpjResponseTransformerWithParams()));

wireMockRule.stubFor(post(urlPathMatching("/validate")).withQueryParam("cnpj", matching("^.*$"))
  .willReturn(aResponse()
    .withStatus(200)
    .withHeader("content-type", "application/json")
    .withTransformers("stub-cnpj-with-params")));
        
```

# Mock CNPJ API (JSON)

```json
{
  "request": {
    "method": "POST",
    "urlPath": "/validate",
    "queryParameters": {
      "cnpj": {
        "matches": "^.*$"
      }
    },
    "headers": {
      "Content-Type": {
        "equalTo": "application/json",
        "caseInsensitive": true
      }
    }
  },
  "response": {
    "status": 200,
    "transformers": ["stub-cnpj-with-params"]
  }
}
