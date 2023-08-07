import client.MovieSession;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    private MovieSession movieSession;

    @BeforeClass(alwaysRun = true)
    protected void init() {
        movieSession = MovieSession.getInstance();
    }

    @AfterClass(alwaysRun = true)
    protected void teardown() {
        movieSession.deleteSession();
    }

}
