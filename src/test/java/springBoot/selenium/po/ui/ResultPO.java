package springBoot.selenium.po.ui;

import org.openqa.selenium.By;
import springBoot.testutils.selenium.PageObject;

public class ResultPO extends PageObject {

    public ResultPO(PageObject other) {
        super(other);
    }

    // we are finding the "id" in the result.xhtml file
    @Override
    public boolean isOnPage() {
        return getDriver().getTitle().contains("Match Result");
    }

    public boolean haveWon(){
        return getDriver().findElements(By.id("wonId")).size() > 0;
    }

    public boolean haveLost(){
        return getDriver().findElements(By.id("lostId")).size() > 0;
    }
}
