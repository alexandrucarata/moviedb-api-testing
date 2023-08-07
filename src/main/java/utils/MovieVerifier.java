package utils;

import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.testng.asserts.SoftAssert;
import pojo.Movie;

import java.util.List;

public class MovieVerifier {
    private final String SUCCESSFUL_DELETE_MESSAGE = ConfigFileReader.getProperty("deleteSuccessfulMessage");
    private final String FAILED_AUTH_MESSAGE = ConfigFileReader.getProperty("authFailedMessage");
    private final String NOT_FOUND_MESSAGE = ConfigFileReader.getProperty("notFoundMessage");
    private final String INVALID_API_KEY_MESSAGE = ConfigFileReader.getProperty("invalidApiKeyMessage");
    private final String MAX_RATING_MESSAGE = ConfigFileReader.getProperty("maxRatingMessage");
    private final int OK_STATUS = Integer.parseInt(ConfigFileReader.getProperty("okStatus"));
    private final int CREATED_STATUS = Integer.parseInt(ConfigFileReader.getProperty("createdStatus"));
    private final int BAD_STATUS = Integer.parseInt(ConfigFileReader.getProperty("badStatus"));
    private final int UNAUTHORIZED_STATUS = Integer.parseInt(ConfigFileReader.getProperty("unauthorizedStatus"));
    private final int NOT_FOUND_STATUS = Integer.parseInt(ConfigFileReader.getProperty("notFoundStatus"));

    public void verifyGetFavoriteMovie(Response response, List<Movie> favoriteMovieList, Movie movie) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), OK_STATUS);
        softAssert.assertTrue(favoriteMovieList.contains(movie));
        softAssert.assertAll();
    }

    public void verifyGetUnauthorizedFavorites(Response response, String statusMessage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), UNAUTHORIZED_STATUS);
        softAssert.assertEquals(statusMessage, FAILED_AUTH_MESSAGE);
        softAssert.assertAll();
    }

    public void verifyGetNonexistentFavorites(Response response, String statusMessage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), NOT_FOUND_STATUS);
        softAssert.assertEquals(statusMessage, NOT_FOUND_MESSAGE);
        softAssert.assertAll();
    }

    public void verifyPostSuccess(Response response) {
        response.then()
                .statusCode(CREATED_STATUS)
                .body("success", Matchers.equalTo(true))
                .log().body();
    }

    public void verifyPostInvalidMovieRating(Response response, String statusMessage, boolean successStatus) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), BAD_STATUS);
        softAssert.assertEquals(statusMessage, MAX_RATING_MESSAGE);
        softAssert.assertFalse(successStatus);
        softAssert.assertAll();
    }

    public void verifyPostUnauthorizedMovieRating(Response response, String statusMessage, boolean successStatus) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), UNAUTHORIZED_STATUS);
        softAssert.assertEquals(statusMessage, INVALID_API_KEY_MESSAGE);
        softAssert.assertFalse(successStatus);
        softAssert.assertAll();
    }

    public void verifyDeleteMovieRating(Response response, String statusMessage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), OK_STATUS);
        softAssert.assertEquals(statusMessage, SUCCESSFUL_DELETE_MESSAGE);
        softAssert.assertAll();
    }

    public void verifyDeleteUnauthorizedMovieRating(Response response, String statusMessage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), UNAUTHORIZED_STATUS);
        softAssert.assertEquals(statusMessage, FAILED_AUTH_MESSAGE);
        softAssert.assertAll();
    }

    public void verifyDeleteNonexistentMovieRating(Response response, String statusMessage) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(response.getStatusCode(), NOT_FOUND_STATUS);
        softAssert.assertEquals(statusMessage, NOT_FOUND_MESSAGE);
        softAssert.assertAll();
    }
}
