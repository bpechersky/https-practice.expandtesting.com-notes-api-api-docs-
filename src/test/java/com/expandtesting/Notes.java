package com.expandtesting;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
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

    private static String noteId;

    @Test
    public void createNoteTest() {
        Response response = given()
                .header("accept", "application/json")
                .header("x-auth-token", RegisterUserTest.authToken)
                .contentType("application/json")
                .body("{ \"title\": \"Best Title of the note\", " +
                        "\"description\": \"Best description of the note\", " +
                        "\"category\": \"Home\" }")
                .post("https://practice.expandtesting.com/notes/api/notes");

        response.then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Note successfully created"))
                .body("data.title", equalTo("Best Title of the note"));

        noteId = response.path("data.id");
        System.out.println("Note ID: " + noteId);
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
    @Test
    public void getNoteByIdTest() {

        createNoteTest();
        given()
                .header("accept", "application/json")
                .header("x-auth-token", RegisterUserTest.authToken)
                .get("https://practice.expandtesting.com/notes/api/notes/" + noteId)
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("data.id", equalTo(noteId))
                .body("data.title", equalTo("Best Title of the note"));

    }

    @Test
    public void updateNoteTest() {
        // Ensure note is created before updating
        createNoteTest();  // This sets noteId

        given()
                .baseUri("https://practice.expandtesting.com")
                .basePath("/notes/api/notes/" + noteId)
                .header("accept", "application/json")
                .header("x-auth-token", RegisterUserTest.authToken)
                .contentType(ContentType.JSON)
                .body("{ \"title\": \"Updated title of the note\", " +
                        "\"description\": \"Updated descripton of the note\", " +
                        "\"completed\": true, " +
                        "\"category\": \"Home\" }")
                .when()
                .put()
                .then()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", containsString("Updated"));
    }

    @Test
    public void patchNoteTest() {
        createNoteTest();

        given()
                .log().all()
                .baseUri("https://practice.expandtesting.com")
                .basePath("/notes/api/notes/" + noteId)
                .header("x-auth-token", authToken)
                .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .formParam("completed", "true")
                .when()
                .patch()
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void deleteNoteTest() {
        patchNoteTest();

        RestAssured.baseURI = "https://practice.expandtesting.com";

        given()
                .log().all()
                .header("accept", "application/json")
                .header("x-auth-token", authToken)
                .when()
                .delete("/notes/api/notes/" + noteId)
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("message", equalTo("Note successfully deleted"));
    }

}
