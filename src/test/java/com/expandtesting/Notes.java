package com.expandtesting;

import io.restassured.RestAssured;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;

import static com.expandtesting.RegisterUserTest.authToken;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class Notes {

    @BeforeClass
    public void setup() throws UnsupportedEncodingException {
        // ensure login is called once before tests and token is stored
        new RegisterUserTest().loginUserTest();
    }


    @Test
    public void createNoteTest() throws UnsupportedEncodingException {


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
    @Test
    public void getAllNotesTest() throws Exception {
        // Ensure the token is available
        createNoteTest();

        RestAssured
                .given()
                .header("accept", "application/json")
                .header("x-auth-token", authToken)
                .when()
                .get("https://practice.expandtesting.com/notes/api/notes")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("status", equalTo(200))
               // .body("message", equalTo("Notes successfully retrieved"))
                .body("data", notNullValue())
                .body("data.size()", greaterThan(0))
                .body("data[0].id", notNullValue())
                .body("data[0].title", equalTo("Best Title of the note"))
                .body("data[0].description", equalTo("Best description of the note"))
                .body("data[0].category", equalTo("Home"))
                .body("data[0].completed", equalTo(false))
                .body("data[0].created_at", notNullValue())
                .body("data[0].updated_at", notNullValue())
                .body("data[0].user_id", notNullValue());
    }
}
