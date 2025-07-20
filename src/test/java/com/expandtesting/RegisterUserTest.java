package com.expandtesting;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.net.URLEncoder;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RegisterUserTest {

    @Test
    public void registerNewUser() throws Exception {
        RestAssured.baseURI = "https://practice.expandtesting.com";

        // 🔁 Generate unique name and email
        String uniqueId = UUID.randomUUID().toString().substring(0, 8);
        String name = "BorisPechersky_" + uniqueId;
        String email = "bp_" + uniqueId + "@gmp.com";
        String password = "Test123";

        String cookie = "express:sess=eyJmbGFzaCI6e319; express:sess.sig=tdt42nQZiQvICqmrvHQ1_16fHk0";



        String body = "name=" + URLEncoder.encode(name, "UTF-8") +
                "&email=" + URLEncoder.encode(email, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8");

        Response response = RestAssured
                .given()
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8") // 🔥 CRITICAL FIX
                .header("Cookie", cookie)
                .body(body)
                .when()
                .post("/notes/api/users/register")
                .then()
                .log().all()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("status", equalTo(201))
                .body("message", equalTo("User account created successfully"))
                .body("data.id", notNullValue())
                .body("data.name", equalTo(name))
                .body("data.email", equalTo(email))
                .extract().response();

        System.out.println("✅ Registered: " + name + " | " + email);
    }
}
