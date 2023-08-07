package client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.json.JSONObject;
import utils.ConfigFileReader;

import static io.restassured.RestAssured.given;

public class MovieSession {
    private static final ThreadLocal<MovieSession> session = new ThreadLocal<>();
    private final String BASE_URI = ConfigFileReader.getProperty("baseUri");
    private final String API_KEY = ConfigFileReader.getProperty("apiKey");
    private final String INVALID_API_KEY = ConfigFileReader.getProperty("invalidApiKey");
    private final String FAILED_SESSION_ID = ConfigFileReader.getProperty("failedSessionId");
    private final String USERNAME = ConfigFileReader.getProperty("username");
    private final String PASSWORD = ConfigFileReader.getProperty("password");
    private final int OK_STATUS = Integer.parseInt(ConfigFileReader.getProperty("okStatus"));
    private String requestToken;
    private String sessionId;

    private MovieSession() {
        initSession();
    }

    public static MovieSession getInstance() {
        if (session.get() != null) {
            return session.get();
        }
        session.set(new MovieSession());
        return session.get();
    }

    private void initSession() {
        requestToken = getRequestToken();
        validateToken();
        sessionId = getSessionId();
    }

    private String getRequestToken() {
        return given()
                .spec(getInitRequestSpec())
                .when()
                .get("token/new")
                .then()
                .statusCode(200)
                .log().body().extract().jsonPath().get("request_token").toString();
    }

    private void validateToken() {
        JSONObject req = new JSONObject();
        req.put("username", USERNAME);
        req.put("password", PASSWORD);
        req.put("request_token", requestToken);

        given()
                .spec(getInitRequestSpec())
                .body(req.toString())
                .when()
                .post("token/validate_with_login")
                .then().log().body();
    }

    private String getSessionId() {
        JSONObject req = new JSONObject();
        req.put("request_token", requestToken);

        return given()
                .spec(getInitRequestSpec())
                .body(req.toString())
                .when()
                .post("session/new")
                .then()
                .log().body().extract().jsonPath().get("session_id").toString();
    }

    private RequestSpecification getInitRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .setBasePath("/authentication/")
                .addQueryParam("api_key", API_KEY)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected RequestSpecification getSessionRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addQueryParam("api_key", API_KEY)
                .addQueryParam("session_id", sessionId)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected RequestSpecification getFailedSessionRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addQueryParam("api_key", API_KEY)
                .addQueryParam("session_id", FAILED_SESSION_ID)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected RequestSpecification getUnauthorizedRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(BASE_URI)
                .addQueryParam("api_key", INVALID_API_KEY)
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())
                .build();
    }

    protected String getAccountId() {
        return given()
                .spec(getSessionRequestSpec())
                .when()
                .get("/account")
                .then()
                .statusCode(OK_STATUS)
                .log().body().extract().path("id").toString();
    }

    public void deleteSession() {
        JSONObject req = new JSONObject();
        req.put("session_id", sessionId);

        given()
                .spec(getInitRequestSpec())
                .body(req.toString())
                .when()
                .delete("session")
                .then()
                .statusCode(OK_STATUS)
                .log().body();
    }
}
