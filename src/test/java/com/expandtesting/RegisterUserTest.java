package com.expandtesting;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.hamcrest.Matchers.equalTo;

public class RegisterUserTest {

    private static String registeredEmail;
    private static String registeredUserId;
    private static String authToken;
    String password = "Test123";


    @Test
    public void registerNewUser() throws UnsupportedEncodingException {
        registeredEmail = "bp" + System.currentTimeMillis() + "@gmp.com";
         password = "Test123";
        String name = "Boris Pechersky";

        String encodedBody = "name=" + URLEncoder.encode(name, "UTF-8") +
                "&email=" + URLEncoder.encode(registeredEmail, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8");

        Response response = RestAssured
                .given()
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .body(encodedBody)
                .when()
                .post("https://practice.expandtesting.com/notes/api/users/register")
                .then()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("data.email", equalTo(registeredEmail))
                .extract().response();

        registeredUserId = response.jsonPath().getString("data.id");
    }

    @Test
    public void loginUserTest() throws UnsupportedEncodingException {
        registerNewUser();
         password = "Test123";

        String encodedBody = "email=" + URLEncoder.encode(registeredEmail, "UTF-8") +
                "&password=" + URLEncoder.encode(password, "UTF-8");

        Response response = RestAssured
                .given()
                .header("accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .body(encodedBody)
                .when()
                .post("https://practice.expandtesting.com/notes/api/users/login")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.email", equalTo(registeredEmail))
                .extract().response();

        String loginUserId = response.jsonPath().getString("data.id");
        authToken = response.jsonPath().getString("data.token");

        Assert.assertEquals(loginUserId, registeredUserId, "User ID from login should match registration");
        Assert.assertNotNull(authToken, "Token should not be null");
    }

    @Test
    public void getUserProfileTest() throws UnsupportedEncodingException {
        loginUserTest();
        RestAssured
                .given()
                .header("accept", "application/json")
                .header("x-auth-token", authToken)
                .when()
                .get("https://practice.expandtesting.com/notes/api/users/profile")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.email", equalTo(registeredEmail));
    }
}
