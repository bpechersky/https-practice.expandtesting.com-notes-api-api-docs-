package com.expandtesting;

import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

import static com.expandtesting.RegisterUserTest.authToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Notes {



    @Test
    public void createNoteTest() throws UnsupportedEncodingException {

        new RegisterUserTest().loginUserTest();
        RestAssured
                .given()
                .header("accept", "application/json")
                .header("x-auth-token", authToken)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")

                .formParam("title", "Best Title of the note")
                .formParam("description", "Best description of the note")
                .formParam("category", "Home")
                .when()
                .post("https://practice.expandtesting.com/notes/api/notes")
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Note successfully created"))
                .body("data", notNullValue())
                .body("data.title", equalTo("Best Title of the note"))
                .body("data.description", equalTo("Best description of the note"))
                .body("data.category", equalTo("Home"));
    }
}
