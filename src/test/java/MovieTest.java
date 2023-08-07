import client.MovieClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import pojo.FavoriteMovie;
import pojo.FavoriteSearch;
import pojo.Movie;
import utils.ConfigFileReader;
import utils.MovieVerifier;
import utils.RequestType;

import java.io.File;
import java.util.List;

@Slf4j
@Epic("Movie Tests")
public class MovieTest extends BaseTest {
    private MovieClient movieClient;
    private MovieVerifier verifier;
    private Movie movie;
    private int movieId;
    private final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final String MOVIE_PATH = ConfigFileReader.getProperty("moviePath");
    private final int INVALID_MOVIE_ID = Integer.parseInt(ConfigFileReader.getProperty("invalidMovieId"));
    private final double RATING = Double.parseDouble(ConfigFileReader.getProperty("rating"));
    private final double INVALID_RATING = Double.parseDouble(ConfigFileReader.getProperty("invalidRating"));

    @BeforeClass
    @SneakyThrows
    public void setup() {
        movieClient = new MovieClient();
        verifier = new MovieVerifier();
        movie = OBJECT_MAPPER.readValue(new File(MOVIE_PATH), Movie.class);
        movieId = movie.getId();
    }

    @Story("Favorite Movie Management")
    @Feature("Ability to Get Favorite Movies")
    @Description("Check ability to get favorite movies")
    @Test(dependsOnMethods = "postMovieToFavoritesTest", description = "Get Favorite Movie Test")
    public void getFavoriteMovieTest() {
        Response response = movieClient.getFavoriteMovies(RequestType.VALID);
        List<Movie> favoriteMovieList = response.as(FavoriteSearch.class).getResults();
        log.info(response.prettyPrint());

        verifier.verifyGetFavoriteMovie(response, favoriteMovieList, movie);
    }

    @Story("Favorite Movie Management")
    @Feature("Inability to Get Favorite Movies without Authorization")
    @Description("Check inability to get favorite movies rating without authorization")
    @Test(description = "Get Unauthorized Favorite Movie Test")
    public void getUnauthorizedFavoriteMovieTest() {
        Response response = movieClient.getFavoriteMovies(RequestType.UNAUTHORIZED);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        log.info(response.prettyPrint());

        verifier.verifyGetUnauthorizedFavorites(response, statusMessage);
    }

    @Story("Favorite Movie Management")
    @Feature("Inability to Get Favorite Movies when nonexistent")
    @Description("Check inability to get nonexistent favorite movies")
    @Test(description = "Get Nonexistent Favorite Movie Test")
    public void getNonexistentFavoriteMovieTest() {
        Response response = movieClient.getFavoriteMovies(RequestType.NONEXISTENT);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        log.info(response.prettyPrint());

        verifier.verifyGetNonexistentFavorites(response, statusMessage);
    }

    @Story("Favorite Movie Management")
    @Feature("Ability to Add Favorite Movies")
    @Description("Check ability to add favorite movies")
    @Test(description = "Post Movie to Favorites Test")
    @SneakyThrows
    public void postMovieToFavoritesTest() {
        FavoriteMovie favoriteMovie = FavoriteMovie.builder()
                .mediaType("movie")
                .mediaId(movie.getId())
                .favorite(true)
                .build();

        Response response = movieClient.postMovieToFavorites(favoriteMovie);
        verifier.verifyPostSuccess(response);
    }

    @Story("Movie Rating")
    @Feature("Ability to Add Movie Rating")
    @Description("Check ability to add movie rating")
    @Test(description = "Post Movie Rating Test")
    public void postMovieRatingTest() {
        JSONObject rating = new JSONObject();
        rating.put("value", RATING);

        Response response = movieClient.postMovieRating(movieId, rating.toString(), RequestType.VALID);
        verifier.verifyPostSuccess(response);
    }

    @Story("Movie Rating")
    @Feature("Inability to Add Invalid Movie Rating")
    @Description("Check inability to add invalid movie rating")
    @Test(description = "Post Invalid Movie Rating Test")
    public void postInvalidMovieRatingTest() {
        JSONObject rating = new JSONObject();
        rating.put("value", INVALID_RATING);

        Response response = movieClient.postMovieRating(movieId, rating.toString(), RequestType.VALID);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        boolean successStatus = response.getBody().jsonPath().getBoolean("success");
        log.info(response.prettyPrint());

        verifier.verifyPostInvalidMovieRating(response, statusMessage, successStatus);
    }

    @Story("Movie Rating")
    @Feature("Inability to Add Movie Rating without Authorization")
    @Description("Check inability to add movie rating without authorization")
    @Test(description = "Post Unauthorized Movie Rating Test")
    public void postUnauthorizedMovieRatingTest() {
        JSONObject rating = new JSONObject();
        rating.put("value", RATING);

        Response response = movieClient.postMovieRating(movieId, rating.toString(), RequestType.UNAUTHORIZED);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        boolean successStatus = response.getBody().jsonPath().getBoolean("success");
        log.info(response.prettyPrint());

        verifier.verifyPostUnauthorizedMovieRating(response, statusMessage, successStatus);
    }

    @Story("Movie Rating")
    @Feature("Ability to Delete Movie Rating")
    @Description("Check ability to delete movie rating")
    @Test(dependsOnMethods = "postMovieRatingTest", description = "Delete Movie Rating Test")
    public void deleteMovieRatingTest() {
        Response response = movieClient.deleteMovieRating(movieId, RequestType.VALID);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        log.info(response.prettyPrint());

        verifier.verifyDeleteMovieRating(response, statusMessage);
    }

    @Story("Movie Rating")
    @Feature("Inability to Delete Movie Rating without Authorization")
    @Description("Check inability to delete movie rating without authorization")
    @Test(description = "Delete Unauthorized Movie Rating Test")
    public void deleteUnauthorizedMovieRatingTest() {
        Response response = movieClient.deleteMovieRating(movieId, RequestType.UNAUTHORIZED);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        log.info(response.prettyPrint());

        verifier.verifyDeleteUnauthorizedMovieRating(response, statusMessage);
    }

    @Story("Movie Rating")
    @Feature("Inability to Delete Nonexistent Movie Rating")
    @Description("Check inability to delete nonexistent movie rating")
    @Test(description = "Delete Nonexistent Movie Rating Test")
    public void deleteNonexistentMovieRatingTest() {
        Response response = movieClient.deleteMovieRating(INVALID_MOVIE_ID, RequestType.VALID);
        String statusMessage = response.getBody().jsonPath().getString("status_message");
        log.info(response.prettyPrint());

        verifier.verifyDeleteNonexistentMovieRating(response, statusMessage);
    }

}
