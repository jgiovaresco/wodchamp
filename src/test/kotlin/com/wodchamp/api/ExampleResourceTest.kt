package com.wodchamp.api

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured
import org.hamcrest.CoreMatchers
import org.junit.jupiter.api.Test

@QuarkusTest
class ExampleResourceTest {

  @Test
  fun testHelloEndpoint() {
    RestAssured.given()
      .`when`()
      .get("/hello")
      .then()
      .statusCode(200)
      .body(CoreMatchers.`is`("Hello RESTEasy"))
  }
}
