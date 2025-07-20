package com.expandtesting;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class RegisterUserTest {

    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    private String registeredEmail;
    private final String password = "Test123";
    private final String name = "Boris Pechersky";
    private static String registeredUserId;

    @Test
    public void registerNewUser() throws Exception {
        RestAssured.baseURI = "https://practice.expandtesting.com";

        // 🔁 Generate unique name and email


        String cookie = "express:sess=eyJmbGFzaCI6e319; express:sess.sig=tdt42nQZiQvICqmrvHQ1_16fHk0";

        String uniqueId = UUID.randomUUID().toString().substring(0, 6);
        registeredEmail = "bp" + uniqueId + "@gmp.com";

        String body = "name=" + URLEncoder.encode(name, "UTF-8") +
                "&email=" + URLEncoder.encode(registeredEmail, "UTF-8") +
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
                .body("data.email", equalTo(registeredEmail))
                .extract().response();
        registeredUserId = response.jsonPath().getString("data.id");

        System.out.println("✅ Registered: " + name + " | " + registeredEmail);
    }

    @Test
    public void loginUserTest() throws Exception {
        RestAssured.baseURI = "https://practice.expandtesting.com";

       // String email = "11bp@gmp.com";
       // String password = "Test123";
        registerNewUser(); // manually ensure email is initialized
        String encodedBody = "email=" + URLEncoder.encode(registeredEmail, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8");

        Response response = RestAssured
                .given()
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .body(encodedBody)
                .when()
                .post("/notes/api/users/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo(200))
                .body("message", equalTo("Login successful"))
                .body("data.token", notNullValue())
                .body("data.email", equalTo(registeredEmail))
                .extract().response();
        String loginUserId = response.jsonPath().getString("data.id");
        Assert.assertEquals(loginUserId, registeredUserId, "User ID from login should match registration");

        System.out.println("✅ Logged in as: " + registeredEmail);
        System.out.println("🔁 Token: " + response.path("data.token"));
    }

}
