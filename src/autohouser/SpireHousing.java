package autohouser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import spireautomator.UMass;

import java.util.HashMap;
import java.util.Map;

/**
 * This class automates the housing selection process
 * on SPIRE.
 */
public class SpireHousing {
    private WebDriver driver;
    private Map<String, ResidentialArea> residentialAreas;

    public SpireHousing() {
        this.driver = null;
        this.residentialAreas = new HashMap<>();
    }

    public SpireHousing(WebDriver driver) {
        this();
        this.driver = driver;
    }

    public SpireHousing(WebDriver driver, Map<String, ResidentialArea> residentialAreas) {
        this();
        this.driver = driver;
        this.residentialAreas = residentialAreas;
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
            int opportunities = UMass.tryToInt(UMass.findElementHsgApptTable(driver, row, 6).getText());
            if(opportunities > 0) {
                // Create an instance of an Appointment if it still has available opportunities.

            }
        }
        // Click on the Search for a Room button.
        UMass.waitForElement(driver, By.cssSelector(UMass.SEARCH_FOR_ROOM_SELECTOR)).click();

        //TODO: Set preferences and priorities.
        //TODO: Use WebDriver to set search criteria and click search button.

        System.out.println("Beginning automated refresh.");
        long previousTime = System.currentTimeMillis();
        while(true) {
            // Reload current shopping cart page at least every 5 seconds.
            // If it has been less than 5 seconds since the last refresh, wait an extra 5 seconds.
            if ((System.currentTimeMillis() - previousTime) / 1000 < 5) {
                UMass.sleep(5000);
            }
            // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
            // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
            previousTime = System.currentTimeMillis();
            //TODO: ENTER SEARCH CRITERIA
            //TODO: CLICK "SEARCH NOW" BUTTON ON SEARCH CRITERIA
            //TODO: PARSE ROOMS INTO RESIDENTIAL AREA STRUCTURE
            //TODO: CLICK "NEW SEARCH" BUTTON ON SEARCH RESULTS
        }
    }

    private Map<String, Room> parseRooms() {
        Map<String, Room> rooms = new HashMap<>();
        // Gets the size of the rooms search results table and iterates over each row.
        // Skips the first row; it's just header labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.ROOMS_RESULTS_SELECTOR)).findElements(By.tagName("tr")).size(); row++) {
            String building = UMass.findElementRoomsResults(driver, row, 0).getText();
            String number = UMass.findElementRoomsResults(driver, row, 1).getText();
            String design = UMass.findElementRoomsResults(driver, row, 5).getText();
            String type =  UMass.findElementRoomsResults(driver, row, 6).getText();
            //TODO: Construct rooms.
            Room room = new Room(residentialAreas, building, number, design, type);
        }
        return rooms;
    }
}
