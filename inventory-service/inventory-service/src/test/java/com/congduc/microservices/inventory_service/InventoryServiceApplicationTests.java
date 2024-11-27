package com.congduc.microservices.inventory_service;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.MySQLContainer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Import(TestcontainersConfiguration.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

class InventoryServiceApplicationTests {

	@ServiceConnection
	static MySQLContainer<?> mysqlContainer() {
		return new MySQLContainer<>("mysql:8.3.0");
	}
	@LocalServerPort
	private Integer port;

	@BeforeEach
	void setUp() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.port = port;
	}

	static {
		mysqlContainer().start();
	}

	@Test
	void shouldReadInventory() {
		var resopnse = RestAssured.given()
				.when()
				.get("api//v1/inventory?skuCode=iphone12&quantity=1")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertTrue(resopnse);
		
		var nagativeResponse = RestAssured.given()
				.when()
				.get("api//v1/inventory?skuCode=iphone12&quantity=1000")
				.then()
				.log().all()
				.statusCode(200)
				.extract().response().as(Boolean.class);
		assertFalse(nagativeResponse);

	}

}
