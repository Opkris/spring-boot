package springBoot.service.PO;

import org.openqa.selenium.WebDriver;
import springBoot.service.PO.ui.MatchPO;
import springBoot.testutils.selenium.PageObject;

import static org.junit.Assert.assertTrue;

public class IndexPO extends PageObject {


    public IndexPO(WebDriver driver, String host, int port) {
        super(driver, host, port);
    }

    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("Quiz Game");
    }

    public MatchPO startNewMatch(){
        clickAndWait("newMatchBtnId");
        MatchPO po = new MatchPO(this);
        assertTrue(po.isOnPage());

        return po;
    }
    public void toStartingPage(){
        getDriver().get(host + ":" + port);
    }
}
