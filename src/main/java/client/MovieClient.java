package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import pojo.FavoriteMovie;
import utils.ConfigFileReader;
import utils.RequestType;

import static io.restassured.RestAssured.given;

public class MovieClient {
    private final MovieSession SESSION = MovieSession.getInstance();
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String ADD_FAVORITE_PATH = ConfigFileReader.getProperty("addFavorite");
    private final String FAVORITES_PATH = ConfigFileReader.getProperty("favorites");
    private final String NON_FAVORITES_PATH = ConfigFileReader.getProperty("nonexistentFavorites");
    private final String MOVIE_RATING_PATH = ConfigFileReader.getProperty("movieRating");

    public Response getFavoriteMovies(RequestType requestType) {
        RequestSpecification reqSpec;
        String path;
        switch (requestType) {
            case VALID:
                reqSpec = SESSION.getSessionRequestSpec();
                path = FAVORITES_PATH;
                break;
            case UNAUTHORIZED:
                reqSpec = SESSION.getFailedSessionRequestSpec();
                path = FAVORITES_PATH;
                break;
            case NONEXISTENT:
                reqSpec = SESSION.getSessionRequestSpec();
                path = NON_FAVORITES_PATH;
                break;
            default:
                throw new IllegalArgumentException("Invalid request type: " + requestType);
        }

        return given()
                .spec(reqSpec)
                .pathParams("account_id", SESSION.getAccountId())
                .when()
                .get(path);
    }

    @SneakyThrows
    public Response postMovieToFavorites(FavoriteMovie favoriteMovie) {
        return given()
                .spec(SESSION.getSessionRequestSpec())
                .pathParams("account_id", SESSION.getAccountId())
                .body(OBJECT_MAPPER.writeValueAsString(favoriteMovie))
                .when()
                .post(ADD_FAVORITE_PATH);
    }

    public Response postMovieRating(int movieId, String rating, RequestType requestType) {
        RequestSpecification reqSpec;
        switch (requestType) {
            case UNAUTHORIZED:
                reqSpec = SESSION.getUnauthorizedRequestSpec();
                break;
            case VALID:
                reqSpec = SESSION.getSessionRequestSpec();
                break;
            default:
                throw new IllegalArgumentException("Invalid request type: " + requestType);
        }

        return given()
                .spec(reqSpec)
                .pathParams("movie_id", movieId)
                .body(rating)
                .when()
                .post(MOVIE_RATING_PATH);
    }

    public Response deleteMovieRating(int movieId, RequestType requestType) {
        RequestSpecification reqSpec;
        switch (requestType) {
            case UNAUTHORIZED:
                reqSpec = SESSION.getFailedSessionRequestSpec();
                break;
            case VALID:
                reqSpec = SESSION.getSessionRequestSpec();
                break;
            default:
                throw new IllegalArgumentException("Invalid request type: " + requestType);
        }

        return given()
                .spec(reqSpec)
                .pathParams("movie_id", movieId)
                .when()
                .delete(MOVIE_RATING_PATH);
    }

}
