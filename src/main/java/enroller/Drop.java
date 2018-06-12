package enroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import spire.UMass;

import java.util.List;

/**
 * The Drop {@link Action} drops a particular {@link Lecture}
 * when the appropriate {@link Condition}s are met.
 *
 * In the goal of dropping a Lecture and {@link Add}ding
 * a different one, use the {@link Swap} Action. It will
 * only perform (including drop) if it is able to.
 * Performing Add/Drop independently runs the risk of
 * Dropping a Lecture, being unable to Add a different one,
 * and losing the spot in the Dropped Lecture.
 */
public class Drop extends Action {
    private Lecture lectureToDrop;

    public Drop() {
        this.lectureToDrop = null;
    }

    public Drop(Lecture lectureToDrop) {
        this();
        this.lectureToDrop = lectureToDrop;
    }

    @Override
    public boolean perform(SpireEnrollment spireEnrollment) {
        boolean result = false;
        WebDriver driver = spireEnrollment.getDriver();
        // Check if Drop is the current tab.
        if(!UMass.waitForElement(spireEnrollment.getDriver(), By.cssSelector(UMass.SECTION_TITLE_SELECTOR))
                .getText().contains("Select classes to drop")) {
            UMass.findElementTab(driver, "drop").click();
        }
        // Check if SPIRE first needs to have a term selected.
        if(UMass.checkSelectTerm(spireEnrollment)) {
            UMass.selectTerm(driver, spireEnrollment.getTerm());
        }
        // Get a list of all of the Lectures that can be dropped.
        List<WebElement> dropTable = UMass.waitForElement(driver, By.cssSelector(UMass.DROP_TABLE_SELECTOR)).findElements(By.tagName("tr"));
        for(int row = 1; row < dropTable.size(); row++) {
            // If this row of the classes to drop table has the class ID of the Lecture to remove.
            if(UMass.findElementDropTable(driver, row, 2).getText().contains(lectureToDrop.getClassId())) {
                // Check the checkbox for this Lecture.
                UMass.findElementDropTable(driver, row, 1).findElement(By.className(UMass.CHECKBOX_CLASS)).click();
                // Click the button to confirm drop Lecture selection.
                driver.findElement(By.className(UMass.CONFIRM_BUTTON_CLASS)).click();
                // Wait, then click the button to again confirm drop Lecture selection.
                UMass.waitForElement(driver, 30, By.cssSelector(UMass.FINISH_BUTTON_SELECTOR)).click();
                // Wait, then if the success text is found, set to true and exit for-loop.
                if(UMass.waitForElement(driver, By.cssSelector(UMass.RESULT_ICON_SELECTOR))
                        .getAttribute("innerHTML").contains(UMass.SUCCESS_ICON_HTML)) {
                    result = true;
                }
                break;
            }
        }
        // Go back to the shopping cart.
        UMass.findElementTab(driver, "add").click();
        this.setSatisfied(result);
        return result;
    }

    public Lecture getLectureToDrop() {
        return lectureToDrop;
    }

    public void setLectureToDrop(Lecture lectureToDrop) {
        this.lectureToDrop = lectureToDrop;
    }

    @Override
    public String toString() {
        String string = "Drop "+lectureToDrop.getNameAndSection();
        if(hasConditions()) {
            string += " under conditions:"+super.toString();
        }
        return string;
    }
}
