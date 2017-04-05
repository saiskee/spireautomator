package autohouser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import spireautomator.UMass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * This class automates the housing selection process
 * on SPIRE.
 */
public class SpireHousing {
    private WebDriver driver;
    private Map<String, Appointment> appointments;
    private String[][] searchCriteria = new String[3][8];
    private Map<String, ResidentialArea> residentialAreas;

    public SpireHousing() {
        this.driver = null;
        this.appointments = new HashMap<>();
        this.residentialAreas = new HashMap<>();
    }

    public SpireHousing(WebDriver driver) {
        this();
        this.driver = driver;
        this.appointments = new HashMap<>();
        this.residentialAreas = new HashMap<>();
    }

    public SpireHousing(WebDriver driver, Map<String, ResidentialArea> residentialAreas) {
        this();
        this.driver = driver;
        this.appointments = new HashMap<>();
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
            // Assume remaining opportunities number is always in 6th column of each row.
            Appointment appointment = new Appointment(
                    UMass.findElementHsgApptTable(driver, row, 1).getText(),
                    UMass.findElementHsgApptTable(driver, row, 2).getText()+" "+
                    UMass.findElementHsgApptTable(driver, row, 3).getText(),
                    UMass.findElementHsgApptTable(driver, row, 4).getText()+" "+
                    UMass.findElementHsgApptTable(driver, row, 5).getText(),
                    UMass.findElementHsgApptTable(driver, row, 6).getText());
            appointments.put(appointment.getId(), appointment);
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
            // Step 1 - Required - Select Assignment Term and Assignment Process
            // Select term.
            new Select(UMass.waitForElement(driver, By.cssSelector(UMass.STEP1_TERM_SELECTOR)))
                    .selectByVisibleText(step1SelectTerm());
            //Select housing appointment.
            new Select(UMass.waitForElement(driver, By.cssSelector(UMass.STEP1_APPT_SELECTOR)))
                    .selectByVisibleText(step1SelectAppointment());
            // Step 2 - Required - Select the Building, Cluster or Area for your search
            UMass.waitForElement(driver, By.cssSelector(step2SelectBuildingClusterAreaAll())).click();
            // If the selection above created a dropdown menu, it must be found and selected for.
            String buildingClusterAreaDrop = step2GetBuildingClusterAreaDrop();
            if(buildingClusterAreaDrop != null) {
                new Select(UMass.waitForElement(driver, By.cssSelector(buildingClusterAreaDrop)))
                        .selectByVisibleText(step2SelectBuildingClusterAreaDrop(driver, buildingClusterAreaDrop));
            }
            // Step 3 - Required - Select one search option below
            // Step 4 - Optional - Refine your Search
            //TODO: CLICK "SEARCH NOW" BUTTON ON SEARCH CRITERIA
            //TODO: PARSE ROOMS INTO RESIDENTIAL AREA STRUCTURE
            //TODO: CLICK "NEW SEARCH" BUTTON ON SEARCH RESULTS
        }
    }

    private String step1SelectTerm() {
        return "Fall 2017";
    }

    private String step1SelectAppointment() {
        return "Multi-Year Room Selection";
//        Appointment result = null;
//        // selectedAppointment may be null if no specific appointment has been selected.
//        if(selectedAppointment != null) {
//            result = appointments.get(selectedAppointment);
//        }
//        // runs if selectedAppointmen was
//        if(selectedAppointment == null) {
//            // If there are multiple appointments to choose from.
//            if(appointments.values().size() > 1) {
//
//            } else {
//                // If there is only one appointment available, get the first (and only) element.
//                selectedAppointment = appointments.keySet().toArray()[0].toString();
//            }
//        }
//        return result;
    }

    private String step2SelectBuildingClusterAreaAll() {
        System.out.println("Filter rooms by Building, Cluster, Area, or All?\n1: Building\n2: Cluster\n3: Area\n4:All");
        return UMass.STEP2_ALL_HALLS_SELECTOR;
    }

    private String step2GetBuildingClusterAreaDrop() {
        String[] dropSelectors = {UMass.STEP2_BUILDING_DROP_SELECTOR, UMass.STEP2_CLUSTER_DROP_SELECTOR, UMass.STEP2_AREA_DROP_SELECTOR};
        for(String dropSelector : dropSelectors) {
            if(driver.findElements(By.cssSelector(dropSelector)).size() > 0) {
                return dropSelector;
            }
        }
        return null;
    }

    private String step2SelectBuildingClusterAreaDrop(WebDriver driver, String dropSelector) {
        String result = "Central";
        List<WebElement> dropOptions = driver.findElements(By.cssSelector(dropSelector));
        for(int i = 0; i < dropOptions.size()-1; i++) {
            System.out.println(i+": "+dropOptions.get(i).getText());
        }
        do {
            try {
                result = dropOptions.get(new Scanner(System.in).nextInt()).getText();
            } catch(IndexOutOfBoundsException e) {
                System.out.println("Out of bounds.");
            }
        } while(result == null);
        return result;
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
