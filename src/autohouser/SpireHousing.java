package autohouser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import spireautomator.UMass;

import java.util.*;

/**
 * This class automates the housing selection process
 * on SPIRE.
 */
public class SpireHousing {
    private WebDriver driver;
    private RoomSearch.Step2Radio step2Radio;
    private RoomSearch.Step3Radio step3Radio;
    private RoomSearch.Step4Radio step4Radio;
    private String step2Select;
    private String step3Select;
    private String step4Select;
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
//        // Gets the size of the housing appointments table and iterates over each row.
//        // Skips the first row; it's just header labels.
//        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.HOUSING_APPTS_SELECTOR)).findElements(By.tagName("tr")).size(); row++) {
//            // Assume remaining opportunities number is always in 6th column of each row.
//            Appointment appointment = new Appointment(
//                    UMass.findElementHsgApptTable(driver, row, 1).getText(),
//                    UMass.findElementHsgApptTable(driver, row, 2).getText()+" "+
//                    UMass.findElementHsgApptTable(driver, row, 3).getText(),
//                    UMass.findElementHsgApptTable(driver, row, 4).getText()+" "+
//                    UMass.findElementHsgApptTable(driver, row, 5).getText(),
//                    UMass.findElementHsgApptTable(driver, row, 6).getText());
//            appointments.put(appointment.getId(), appointment);
//        }
        // Click on the Search for a Room button.
        UMass.waitForElement(driver, By.cssSelector(UMass.SEARCH_FOR_ROOM_SELECTOR)).click();

        System.out.println("Beginning automated refresh.");
        long previousTime = System.currentTimeMillis();
        while(true) {
            // Reload current shopping cart page at least every 5 seconds.
            // If it has been less than 5 seconds since the last refresh, wait an extra 5 seconds.
            if ((System.currentTimeMillis() - previousTime) / 1000 < 5) {
                UMass.sleep(5000);
            }
            //TODO: Use WebDriver to set search criteria and click search button.
//            Select s1TermSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S1_TERM_SELECT_SELECTOR)));
//            Select s1ProcessSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S1_PROCESS_SELECT_SELECTOR)));
//            WebElement s2BuildingRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S2_BUILDING_RADIO_SELECTOR));
//            WebElement s2ClusterRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S2_CLUSTER_RADIO_SELECTOR));
//            WebElement s2AreaRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S2_AREA_RADIO_SELECTOR));
//            WebElement s2AllRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S2_ALL_RADIO_SELECTOR));
//            Select s2BuildingSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S2_BUILDING_SELECT_SELECTOR)));
//            Select s2ClusterSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S2_CLUSTER_SELECT_SELECTOR)));
//            Select s2AreaSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S2_AREA_SELECT_SELECTOR)));
//            WebElement s3TypeRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S3_TYPE_RADIO_SELECTOR));
//            WebElement s3DesignRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S3_DESIGN_RADIO_SELECTOR));
//            WebElement s3FloorRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S3_FLOOR_RADIO_SELECTOR));
//            WebElement s3OptionRadio = UMass.waitForElement(driver, By.cssSelector(UMass.S3_OPTION_RADIO_SELECTOR));
//            Select s3TypeSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S3_TYPE_SELECT_SELECTOR)));
//            Select s3DesignSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S3_DESIGN_SELECT_SELECTOR)));
//            Select s3FloorSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S3_FLOOR_SELECT_SELECTOR)));
//            Select s3OptionSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S3_OPTION_SELECT_SELECTOR)));
//            WebElement s4NoneRadio;
//            WebElement s4RoomOpenRadio;
//            WebElement s4SuiteOpenRadio;
//            WebElement s4TypeRadio;
//            WebElement s4OpenDoubleRadio;
//            WebElement s4OpenTripleRadio;
            //Select s4RoomOpenSelect;
            //Select s4SuiteOpenSelect;
            //Select s4TypeSelect;

            // Values in list indicate that only one (at head) may be selected.
//        WebElement[] s2Radios = {s2BuildingRadio, s2ClusterRadio, s2AreaRadio, s2AllRadio};
//        WebElement[] s3Radios = {s3TypeRadio, s3DesignRadio, s3FloorRadio, s3OptionRadio};
//        WebElement[] s4Radios = {s4NoneRadio, s4RoomOpenRadio, s4SuiteOpenRadio, s4TypeRadio, s4OpenDoubleRadio, s4OpenTripleRadio};
            // Keys = elements that have requirements, values = required elements themselves.
//            Map<Select, WebElement> requiredField = new HashMap<>();
//            requiredField.put(s2BuildingSelect, s2BuildingRadio);
//            requiredField.put(s2ClusterSelect, s2ClusterRadio);
//            requiredField.put(s2AreaSelect, s2AreaRadio);
//            requiredField.put(s3TypeSelect, s3TypeRadio);
//            requiredField.put(s3DesignSelect, s3DesignRadio);
//            requiredField.put(s3FloorSelect, s3DesignRadio);
//            requiredField.put(s3OptionSelect, s3OptionRadio);
//            requiredField.put(s4RoomOpenSelect, s4RoomOpenRadio);
//            requiredField.put(s4SuiteOpenSelect, s4SuiteOpenRadio);
//            requiredField.put(s4TypeSelect, s4TypeRadio);

            // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
            // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
            previousTime = System.currentTimeMillis();
            //TODO: ENTER SEARCH CRITERIA
            // Since some radios prompt a dropdown selection and others don't, switch cases allow us to specify.
            switch(step2Radio) {
                case BUILDING:  UMass.waitForElement(driver, By.cssSelector(UMass.S2_BUILDING_RADIO_SELECTOR)).click();
                                // Gets the dropdown menu of buildings.
                                Select s2BuildingSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_BUILDING_SELECT_SELECTOR)));
                                // Gets input from user for which dropdown option they want, and select it.
                                s2BuildingSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2BuildingSelect));
                                break;
                case CLUSTER:   UMass.waitForElement(driver, By.cssSelector(UMass.S2_CLUSTER_RADIO_SELECTOR)).click();
                                Select s2ClusterSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_CLUSTER_SELECT_SELECTOR)));
                                s2ClusterSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2ClusterSelect));
                                break;
                case AREA:      UMass.waitForElement(driver, By.cssSelector(UMass.S2_AREA_RADIO_SELECTOR)).click();
                                Select s2AreaSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_AREA_SELECT_SELECTOR)));
                                s2AreaSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2AreaSelect));
                                break;
                case ALL:       UMass.waitForElement(driver, By.cssSelector(UMass.S2_ALL_RADIO_SELECTOR)).click();
                                break;
            }
            switch(step3Radio) {
                case TYPE:      UMass.waitForElement(driver, By.cssSelector(UMass.S3_TYPE_RADIO_SELECTOR)).click();
                                Select s3TypeSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_TYPE_SELECT_SELECTOR)));
                                s3TypeSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3TypeSelect));

                                break;
                case DESIGN:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_DESIGN_RADIO_SELECTOR)).click();
                                Select s3DesignSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_DESIGN_SELECT_SELECTOR)));
                                s3DesignSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3DesignSelect));
                                break;
                case FLOOR:     UMass.waitForElement(driver, By.cssSelector(UMass.S3_FLOOR_RADIO_SELECTOR)).click();
                                Select s3FloorSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_FLOOR_SELECT_SELECTOR)));
                                s3FloorSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3FloorSelect));
                                break;
                case OPTION:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_OPTION_RADIO_SELECTOR)).click();
                                Select s3OptionSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_OPTION_SELECT_SELECTOR)));
                                s3OptionSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3OptionSelect));
                                break;
            }
            switch(step4Radio) {
                case NONE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_NONE_RADIO_SELECTOR)).click();
                                    break;
                case ROOM_OPEN:     UMass.waitForElement(driver, By.cssSelector(UMass.S4_ROOM_OPEN_RADIO_SELECTOR)).click();
                                    Select s4RoomOpenSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_ROOM_OPEN_SELECT_SELECTOR)));
                                    s4RoomOpenSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4RoomOpenSelect));
                                    break;
                case SUITE_OPEN:    UMass.waitForElement(driver, By.cssSelector(UMass.S4_SUITE_OPEN_RADIO_SELECTOR)).click();
                                    Select s4SuiteOpenSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_SUITE_OPEN_SELECT_SELECTOR)));
                                    s4SuiteOpenSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4SuiteOpenSelect));
                                    break;
                case TYPE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_TYPE_RADIO_SELECTOR)).click();
                                    Select s4TypeSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_TYPE_SELECT_SELECTOR)));
                                    s4TypeSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4TypeSelect));
                                    break;
                case OPEN_DOUBLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_DOUBLE_RADIO_SELECTOR)).click();
                                    break;
                case OPEN_TRIPLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_TRIPLE_RADIO_SELECTOR)).click();
                                    break;
            }
            //TODO: CLICK "SEARCH NOW" BUTTON ON SEARCH CRITERIA
            driver.findElement(By.cssSelector(UMass.S5_SEARCH_NOW_SELECTOR)).click();
            //TODO: PARSE ROOMS INTO RESIDENTIAL AREA STRUCTURE
            //TODO: CLICK "NEW SEARCH" BUTTON ON SEARCH RESULTS
        }
    }

    private String s2GetSelectOption(RoomSearch.Step2Radio radio, Select dropdown) {
        // Declare a list of options to choose from. Populated next line.
        List<String> options = new ArrayList<>();
        // For each WebElement option, add its extracted visible string to the list.
        for(WebElement option : dropdown.getOptions()) {
            options.add(option.getText());
        }
        int selection = -1;
        // As long as the selection value is out of bounds...
        while(selection < 0 || selection > options.size()) {
            // Print out the kind of options about to be queried.
            switch(radio) {
                case BUILDING:  System.out.println("Building?");
                                break;
                case CLUSTER:   System.out.println("Cluster?");
                                break;
                case AREA:      System.out.println("Residential Area?");
                                break;
                case ALL:       break;
            }
            // Print out the options and their indices starting at 1.
            for(int i = 0; i < options.size(); i++) {
                System.out.println((i+1)+":\t"+options.get(i));
            }
            // Get user input, subtract 1 to start at 0.
            selection = new Scanner(System.in).nextInt()-1;
        }
        // Return the string at the selected index.
        return options.get(selection-1);
    }

    private String s3GetSelectOption(RoomSearch.Step3Radio radio, Select dropdown) {
        // Declare a list of options to choose from. Populated next line.
        List<String> options = new ArrayList<>();
        // For each WebElement option, add its extracted visible string to the list.
        for(WebElement option : dropdown.getOptions()) {
            options.add(option.getText());
        }
        int selection = -1;
        // As long as the selection value is out of bounds...
        while(selection < 0 || selection > options.size()) {
            // Print out the kind of options about to be queried.
            switch(radio) {
                case TYPE:      System.out.println("Room Type?");
                                break;
                case DESIGN:    System.out.println("Room Design?");
                                break;
                case FLOOR:     System.out.println("Floor?");
                                break;
                case OPTION:    System.out.println("Housing/Living Option?");
                                break;
            }
            // Print out the options and their indices starting at 1.
            for(int i = 0; i < options.size(); i++) {
                System.out.println((i+1)+":\t"+options.get(i));
            }
            // Get user input, subtract 1 to start at 0.
            selection = new Scanner(System.in).nextInt()-1;
        }
        // Return the string at the selected index.
        return options.get(selection-1);
    }

    private String s4GetSelectOption(RoomSearch.Step4Radio radio, Select dropdown) {
        // Declare a list of options to choose from. Populated next line.
        List<String> options = new ArrayList<>();
        // For each WebElement option, add its extracted visible string to the list.
        for(WebElement option : dropdown.getOptions()) {
            options.add(option.getText());
        }
        int selection = -1;
        // As long as the selection value is out of bounds...
        while(selection < 0 || selection > options.size()) {
            // Print out the kind of options about to be queried.
            switch(radio) {
                case NONE:          break;
                case ROOM_OPEN:     System.out.println("Number of open spaces in a room?");
                                    break;
                case SUITE_OPEN:    System.out.println("Number of open spaces in a suite/apartment?");
                                    break;
                case TYPE:          System.out.println("Room type?");
                                    break;
                case OPEN_DOUBLE:   break;
                case OPEN_TRIPLE:   break;
            }
            // Print out the options and their indices starting at 1.
            for(int i = 0; i < options.size(); i++) {
                System.out.println((i+1)+":\t"+options.get(i));
            }
            // Get user input, subtract 1 to start at 0.
            selection = new Scanner(System.in).nextInt()-1;
        }
        // Return the string at the selected index.
        return options.get(selection-1);
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
