package steps;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BDDMethods {
    static String API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4NmI0ZDA1NjczNjE0NGE5ZWQ5NDEzOGEyNTk5ODI4OSIsInN1YiI6IjYwNWUwOGM4N2M2ZGUzMDA2YzVkNWQ2NCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.bmPsLFvI3vji3hQs8-LgMNY9LxmOwgx68TUbEHZdKts";
    int movieID = 0;

    public void shouldAuthenticationSuccess()
    {
        given()
                .auth()
                .oauth2(API_KEY)
        .when()
                .get("https://api.themoviedb.org/3/movie/76341")
        .then()
                .statusCode(200);
    }

    public void shouldAuthenticationFail()
    {
        given()
                .auth()
                .oauth2("API_KEY_WRONG")
        .when()
                .get("https://api.themoviedb.org/3/movie/76341")
        .then()
                .statusCode(401)
                .body("status_code", equalTo(7))
                .body("success", is(false));
    }

    // MARK: - List trending movies

    public void shouldListTrendingMoviesForAWeek() {
        Response response =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/trending/movie/week")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);
    }

    public void shouldListTrendingMoviesForADay() {
        Response response =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/trending/movie/day")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);
    }

    public void shouldListTrendingMoviesFailWhenPageIsNotValid() {
        Response response =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .queryParam("page", 0)
                .when()
                        .get("https://api.themoviedb.org/3/trending/movie/week")
                .then()
                        .statusCode(400)
                        .body("success", is(false))
                        .body("status_code", equalTo(22))
                        .extract()
                        .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);
    }

    public void shouldListTrendingMoviesFailWhenAPIKEYWrong() {
        Response response =
                given()
                        .auth()
                        .oauth2("API_KEY_WRONG")
                        .queryParam("page", 1)
                .when()
                        .get("https://api.themoviedb.org/3/trending/movie/week")
                .then()
                        .statusCode(401)
                        .body("success", is(false))
                        .body("status_code", equalTo(7))
                        .extract()
                        .response();

        String responseBody = response.getBody().asString();
        System.out.println(responseBody);
    }

    // MARK: - Pick 1 randomly
    // MARK: - Get details of the movie

    public void shouldListMovieDetailsSuccess() {
        // Trending movies in a page
        Response responseTM =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/trending/movie/week")
                .then()
                        .statusCode(200)
                        .extract()
                        .response();

        // Pick a random movie index in the trending list
        // For this example I pulled the random movie from the first page

        List<String> movies = responseTM.path("results");
        Random rand = new Random();
        int randomMovieIndex = rand.nextInt(movies.size());

        // Get movie ID from the trending list.
        movieID = responseTM.path(String.format("results[%d].id", randomMovieIndex));
        System.out.println(movieID);

        // Get the movie details
        Response responseMD =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get(String.format("https://api.themoviedb.org/3/movie/%s", movieID))
                .then()
                        .statusCode(200)
                        .body("id", equalTo(movieID))
                        .extract()
                        .response();

        String movieDetails = responseMD.getBody().asString();
        System.out.println(movieDetails);
    }

    public void shouldListMovieDetailsFailWhenAPIKEYWrong() {
        // Get the movie details for unknown ID
        Response responseMD =
                given()
                        .auth()
                        .oauth2("API_KEY_WRONG")
                .when()
                        .get(String.format("https://api.themoviedb.org/3/movie/"))
                .then()
                        .statusCode(401)
                        .body("success", is(false))
                        .body("status_code", equalTo(7))
                        .extract()
                        .response();

        String movieDetails = responseMD.getBody().asString();
        System.out.println(movieDetails);
    }

    public void shouldListMovieDetailsFailWhenMovieIDUnknown() {
        // Get the movie details for unknown ID
        int _movieID = 0000000;
        Response responseMD =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get(String.format("https://api.themoviedb.org/3/movie/%s", _movieID))
                .then()
                        .statusCode(404)
                        .body("success", is(false))
                        .body("status_code", equalTo(34))
                        .extract()
                        .response();

        String movieDetails = responseMD.getBody().asString();
        System.out.println(movieDetails);
    }

    // MARK: - Post a rating for the movie

    public void shouldPostARatingForAMovieSuccess() {
        // Create a guest session to rate a movie
        Response responseRT =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/authentication/guest_session/new")
                .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .extract()
                        .response();

        String guestSessionID = responseRT.path("guest_session_id");
        System.out.println(guestSessionID);

        // Rate a movie by using guest session id
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "7.0");

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .body(jsonAsMap)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .post(String.format("https://api.themoviedb.org/3/movie/%s/rating", movieID))
                .then()
                        .statusCode(201)
                        .body("success", is(true))
                        .body("status_code", equalTo(1))
                        .extract()
                        .response();

        String ratedMovie = responsePR.getBody().asString();
        System.out.println(ratedMovie);
    }

    public void shouldPostARatingForAMovieNotValidSessionIDFail() {
        String guestSessionID = "xyzvabcde";
        System.out.println(guestSessionID);

        // Rate a movie by using guest session id
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "7.0");

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .body(jsonAsMap)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .post(String.format("https://api.themoviedb.org/3/movie/%s/rating", movieID))
                .then()
                        .statusCode(401)
                        .body("success", is(false))
                        .body("status_code", equalTo(3))
                        .extract()
                        .response();

        String ratedMovie = responsePR.getBody().asString();
        System.out.println(ratedMovie);
    }

    public void shouldPostARatingForAMovieNotValidDataFail() {
        // Create a guest session to rate a movie
        Response responseRT =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/authentication/guest_session/new")
                .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .extract()
                        .response();

        String guestSessionID = responseRT.path("guest_session_id");
        System.out.println(guestSessionID);

        // Rate a movie by using guest session id
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "A"); // A is not valid data

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .body(jsonAsMap)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .post(String.format("https://api.themoviedb.org/3/movie/%s/rating", movieID))
                .then()
                        .statusCode(400)
                        .body("success", is(false))
                        .body("status_code", equalTo(18))
                        .extract()
                        .response();

        String ratedMovie = responsePR.getBody().asString();
        System.out.println(ratedMovie);
    }

    public void shouldPostARatingForAMovieNoneValidIDFail() {
        // Create a guest session to rate a movie
        Response responseRT =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/authentication/guest_session/new")
                .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .extract()
                        .response();

        String guestSessionID = responseRT.path("guest_session_id");
        System.out.println(guestSessionID);

        // Rate a movie by using guest session id
        int _movieID = 0000000; // movieID is not valid
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "8.5");

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .body(jsonAsMap)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .post(String.format("https://api.themoviedb.org/3/movie/%s/rating", _movieID))
                .then()
                        .statusCode(404)
                        .body("success", is(false))
                        .body("status_code", equalTo(34))
                        .extract()
                        .response();

        String ratedMovie = responsePR.getBody().asString();
        System.out.println(ratedMovie);
    }

    // MARK: - Delete the rating of the movie

    public void shouldDeleteARatingFromAMovieSuccess() {
        // Create a guest session to delete rating of a movie
        Response responseRT =
                given()
                        .auth()
                        .oauth2(API_KEY)
                .when()
                        .get("https://api.themoviedb.org/3/authentication/guest_session/new")
                .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .extract()
                        .response();

        String guestSessionID = responseRT.path("guest_session_id");
        System.out.println(guestSessionID);

        // Delete rating of a movie by using guest session id
        System.out.println(movieID);

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .delete(String.format("https://api.themoviedb.org/3/movie/%s/rating", movieID))
                .then()
                        .statusCode(200)
                        .body("success", is(true))
                        .body("status_code", equalTo(13))
                        .extract()
                        .response();

        String deletedRateMovie = responsePR.getBody().asString();
        System.out.println(deletedRateMovie);
    }

    public void shouldDeleteARatingFromAMovieNotValidSessionIDFail() {
        // Create a guest session to delete rating of a movie
        String guestSessionID = "xyzvabcde";
        System.out.println(guestSessionID);

        // Delete rating of a movie by using guest session id
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("value", "A"); // A is not valid data

        Response responsePR =
                given()
                        .auth()
                        .oauth2(API_KEY)
                        .contentType(ContentType.JSON)
                        .body(jsonAsMap)
                        .queryParam("guest_session_id", guestSessionID)
                .when()
                        .delete(String.format("https://api.themoviedb.org/3/movie/%s/rating", movieID))
                .then()
                        .statusCode(401)
                        .body("success", is(false))
                        .body("status_code", equalTo(3))
                        .extract()
                        .response();

        String deletedRateMovie = responsePR.getBody().asString();
        System.out.println(deletedRateMovie);
    }

}
