package autohouser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import spireautomator.UMass;

/**
 * This class automates the housing selection process
 * on SPIRE.
 */
public class SpireHousing {
    private WebDriver driver;

    public SpireHousing(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * This function runs the SpireHousing automation from the point of
     * entering the housing portal to conclusion. It is called by the
     * {@link spireautomator.SpireAutomator} controller after instantiation.
     */
    public void run() {
        // Click on the link that goes to Room Selection Home.
        UMass.waitForElement(driver, By.cssSelector(UMass.HOUSING_LINK_SELECTOR)).click();
        // Gets the size of the housing appointments table and iterates over each row.
        // Skips the first row; it's just header labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.HOUSING_APPTS_SELECTOR)).findElements(By.tagName("tr")).size(); row++) {
            // Checks if there are any appointments with at least 1 opportunity remaining.
            // Assume remaining opportunities number is always in 6th column of each row.
            if(UMass.tryToInt(UMass.findElementHsgApptTable(driver, row, 6).getText()) > 0) {
                // Make a HousingAppointment class in case there are multiple?
            }
        }
        // Click on the Search for a Room button.
        UMass.waitForElement(driver, By.cssSelector(UMass.SEARCH_FOR_ROOM_SELECTOR)).click();
    }
}
