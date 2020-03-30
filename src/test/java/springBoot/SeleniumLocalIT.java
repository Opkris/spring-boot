package springBoot;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import springBoot.service.PO.IndexPO;
import springBoot.service.PO.ui.MatchPO;
import springBoot.service.PO.ui.ResultPO;
import springBoot.service.QuizService;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
public class SeleniumLocalIT {

    private static WebDriver driver;

    @LocalServerPort
    private int port;

    @Autowired
    private QuizService quizService;

    @BeforeAll
    public static void initClass(){

        driver = org.tsdes.misc.testutils.selenium.SeleniumDriverHandler.getChromeDriver();

        assumeTrue(driver != null, "Cannot find/initialize Chrome driver");
    }

    @AfterAll
    public static void shutDown(){
        if (driver != null) {
            driver.close();
        }
    }

    private IndexPO home;

    @BeforeEach
    public void initTest(){

    /*
        We want to have a new session, otherwise the tests
        will share the same Session beans
     */

        driver.manage().deleteAllCookies();
        home = new IndexPO(driver, "localhost", port);
        home.toStartingPage();
        assertTrue(home.isOnPage(), "Failed to start from Home Page");
    }

    @Test
    public void testNewMatch(){
        MatchPO po = home.startNewMatch();
        assertTrue(po.canSelectCategory());
    }

    @Test
    public void testFirstQuiz(){

        MatchPO po = home.startNewMatch();
        // creating a String to get the first category/quiz form the list getCategoryIds()
        String categoryId = po.getCategoryIds().get(0);

        // make sure that we can select a category/ quiz
        assertTrue(po.canSelectCategory());
        // at this point we have not initialized the "game" so we are making sure that its not displayed
        assertFalse(po.isQuestionDisplayed());

        // now we are assigning the category/ quiz to the game
        po.chooseCategory(categoryId);
        // and now we should be able to se the quiz
        assertTrue(po.isQuestionDisplayed());
        assertFalse(po.canSelectCategory());

        assertEquals(1, po.getQuestionCounter());

    }

    @Test
    public void testWrongAnswer(){

        MatchPO po = home.startNewMatch();
        String categoryId = po.getCategoryIds().get(0);
        po.chooseCategory(categoryId);

        long quizId = po.getQuizId();
        int rightAnswer = quizService.getQuiz(quizId).getIndexOfCorrectAnswer();
        int wrongAnswer = (rightAnswer +1) % 4;


        ResultPO rpo = po.answerQuestion(wrongAnswer);
        // making sure that we got a response when we are inserting the answer
        assertNotNull(rpo);
        //when we do, have lost should be displayed
        assertTrue(rpo.haveLost());
        // making sure that we are not getting both a response as true
        assertFalse(rpo.haveWon());
    }

    @Test
    public void testWinAMatch(){

        MatchPO match = home.startNewMatch();
        String categoryId = match.getCategoryIds().get(0);
        match.chooseCategory(categoryId);

        ResultPO result = null;

        // creating a for-loop to go throng all the questions
        for (int i = 1; i <= 5; i++) {
            assertTrue(match.isQuestionDisplayed());
            assertEquals (i, match.getQuestionCounter());

            long quizId = match.getQuizId();
            int rightAnswer = quizService.getQuiz(quizId).getIndexOfCorrectAnswer();

            result = match.answerQuestion(rightAnswer);

            if( i != 5){
                assertNull(result);
            }
        }

        assertTrue(result.haveWon());
        assertFalse(result.haveLost());
    }

}
