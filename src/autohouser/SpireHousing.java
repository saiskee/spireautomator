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
    private String step1TermSelect;
    private String step1ProcessSelect;
    private String step2Select;
    private String step3Select;
    private String step4Select;
    private Map<String, ResidentialArea> residentialAreas;
    private int waitInterval;

    public SpireHousing() {
        this.driver = null;
        this.step2Radio = null;
        this.step3Radio = null;
        this.step4Radio = null;
        this.step1TermSelect = "";
        this.step1ProcessSelect = "";
        this.step2Select = "";
        this.step3Select = "";
        this.step4Select = "";
        this.residentialAreas = new HashMap<>();
        this.waitInterval = 300;
    }

    public SpireHousing(WebDriver driver) {
        this();
        this.driver = driver;
        this.residentialAreas = new HashMap<>();
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

            // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
            // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
            previousTime = System.currentTimeMillis();

            // Enter the selected term. Prompt user once if needed.
            Select s1TermSelect = new Select(UMass.waitForElement(driver,
                    By.cssSelector(UMass.S1_TERM_SELECT_SELECTOR)));
            s1TermSelect.selectByVisibleText(s1GetTermSelectOption(s1TermSelect));
            // Enter the selected assignment process. Prompt user once if needed.
            Select s1ProcessSelect = new Select(UMass.waitForElement(driver,
                    By.cssSelector(UMass.S1_PROCESS_SELECT_SELECTOR)));
            s1ProcessSelect.selectByVisibleText(s1GetProcessSelectOption(s1ProcessSelect));

            // Check that the user has provided preferences for all the necessary radio buttons.
            // If any of the radio button preferences are not set, prompt for them.
            while(step2Radio == null) {
                System.out.println("Step 2 filter?\n1: Building\n2: Cluster\n3: Area\n4: All");
                switch(new Scanner(System.in).nextInt()) {
                    case 1:     setStep2Radio(RoomSearch.Step2Radio.BUILDING);  break;
                    case 2:     setStep2Radio(RoomSearch.Step2Radio.CLUSTER);   break;
                    case 3:     setStep2Radio(RoomSearch.Step2Radio.AREA);      break;
                    case 4:     setStep2Radio(RoomSearch.Step2Radio.ALL);       break;
                    default:    break;
                }
            }
            while(step3Radio == null) {
                System.out.println("Step 3 filter?\n1: Room Type\n2: Room Design\n3: Floor\n4: Living/Housing Option");
                switch(new Scanner(System.in).nextInt()) {
                    case 1:     setStep3Radio(RoomSearch.Step3Radio.TYPE);      break;
                    case 2:     setStep3Radio(RoomSearch.Step3Radio.DESIGN);    break;
                    case 3:     setStep3Radio(RoomSearch.Step3Radio.FLOOR);     break;
                    case 4:     setStep3Radio(RoomSearch.Step3Radio.OPTION);    break;
                    default:    break;
                }
            }
            while(step4Radio == null) {
                System.out.println("Step 4 filter?\n1: None\n2: Number of open spaces in a Room\n" +
                        "3: Number of open spaces in a Suite/Apartment\n4: Room Type\n5. Open Doubles\n6. Open Triples");
                switch(new Scanner(System.in).nextInt()) {
                    case 1:     setStep4Radio(RoomSearch.Step4Radio.NONE);          break;
                    case 2:     setStep4Radio(RoomSearch.Step4Radio.ROOM_OPEN);     break;
                    case 3:     setStep4Radio(RoomSearch.Step4Radio.SUITE_OPEN);    break;
                    case 4:     setStep4Radio(RoomSearch.Step4Radio.TYPE);          break;
                    case 5:     setStep4Radio(RoomSearch.Step4Radio.OPEN_DOUBLE);   break;
                    case 6:     setStep4Radio(RoomSearch.Step4Radio.OPEN_TRIPLE);   break;
                    default:    break;
                }
            }
            // Since some radios prompt a dropdown selection and others don't, switch cases allow us to specify.
            switch(step2Radio) {
                // All radios with associated selects work the same, just values are different. Radios without associated
                // selects work the same as each other too. Only the first case (step 2 radio, building) is commented.
                case BUILDING:  UMass.waitForElement(driver, By.cssSelector(UMass.S2_BUILDING_RADIO_SELECTOR)).click();
                                // Wait a short time to allow the hidden element to appear after the click.
                                UMass.sleep(waitInterval);
                                // Gets the dropdown menu of buildings.
                                Select s2BuildingSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_BUILDING_SELECT_SELECTOR)));
                                // Gets input from user for which dropdown option they want, and select it.
                                s2BuildingSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2BuildingSelect));
                                break;
                case CLUSTER:   UMass.waitForElement(driver, By.cssSelector(UMass.S2_CLUSTER_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s2ClusterSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_CLUSTER_SELECT_SELECTOR)));
                                s2ClusterSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2ClusterSelect));
                                break;
                case AREA:      UMass.waitForElement(driver, By.cssSelector(UMass.S2_AREA_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s2AreaSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S2_AREA_SELECT_SELECTOR)));
                                s2AreaSelect.selectByVisibleText(s2GetSelectOption(step2Radio, s2AreaSelect));
                                break;
                case ALL:       UMass.waitForElement(driver, By.cssSelector(UMass.S2_ALL_RADIO_SELECTOR)).click();
                                break;
            }
            switch(step3Radio) {
                case TYPE:      UMass.waitForElement(driver, By.cssSelector(UMass.S3_TYPE_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s3TypeSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_TYPE_SELECT_SELECTOR)));
                                s3TypeSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3TypeSelect));

                                break;
                case DESIGN:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_DESIGN_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s3DesignSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_DESIGN_SELECT_SELECTOR)));
                                s3DesignSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3DesignSelect));
                                break;
                case FLOOR:     UMass.waitForElement(driver, By.cssSelector(UMass.S3_FLOOR_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s3FloorSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_FLOOR_SELECT_SELECTOR)));
                                s3FloorSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3FloorSelect));
                                break;
                case OPTION:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_OPTION_RADIO_SELECTOR)).click();
                                UMass.sleep(waitInterval);
                                Select s3OptionSelect = new Select(UMass.waitForElement(driver,
                                        By.cssSelector(UMass.S3_OPTION_SELECT_SELECTOR)));
                                s3OptionSelect.selectByVisibleText(s3GetSelectOption(step3Radio, s3OptionSelect));
                                break;
            }
            switch(step4Radio) {
                case NONE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_NONE_RADIO_SELECTOR)).click();
                                    break;
                case ROOM_OPEN:     UMass.waitForElement(driver, By.cssSelector(UMass.S4_ROOM_OPEN_RADIO_SELECTOR)).click();
                                    UMass.sleep(waitInterval);
                                    Select s4RoomOpenSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_ROOM_OPEN_SELECT_SELECTOR)));
                                    s4RoomOpenSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4RoomOpenSelect));
                                    break;
                case SUITE_OPEN:    UMass.waitForElement(driver, By.cssSelector(UMass.S4_SUITE_OPEN_RADIO_SELECTOR)).click();
                                    UMass.sleep(waitInterval);
                                    Select s4SuiteOpenSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_SUITE_OPEN_SELECT_SELECTOR)));
                                    s4SuiteOpenSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4SuiteOpenSelect));
                                    break;
                case TYPE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_TYPE_RADIO_SELECTOR)).click();
                                    UMass.sleep(waitInterval);
                                    Select s4TypeSelect = new Select(UMass.waitForElement(driver,
                                            By.cssSelector(UMass.S4_TYPE_SELECT_SELECTOR)));
                                    s4TypeSelect.selectByVisibleText(s4GetSelectOption(step4Radio, s4TypeSelect));
                                    break;
                case OPEN_DOUBLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_DOUBLE_RADIO_SELECTOR)).click();
                                    break;
                case OPEN_TRIPLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_TRIPLE_RADIO_SELECTOR)).click();
                                    break;
            }
            // Click the "Search Now" button after finishing entering search criteria.
            driver.findElement(By.cssSelector(UMass.S5_SEARCH_NOW_SELECTOR)).click();
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

    private String s1GetTermSelectOption(Select dropdown) {
        if(step1TermSelect == null || step1TermSelect.equals("")) {
            // Declare a list of options to choose from. Populated next line.
            List<String> options = new ArrayList<>();
            // For each WebElement option, add its extracted visible string to the list.
            for (WebElement option : dropdown.getOptions()) {
                options.add(option.getText());
            }
            int selection = -1;
            // As long as the selection value is out of bounds...
            while (selection < 0 || selection > options.size()) {
                System.out.println("Term?");
                // Print out the options and their indices.
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((i) + ":\t" + options.get(i));
                }
                // Get user input, subtract 1 to start at 0.
                selection = new Scanner(System.in).nextInt();
            }
            // Assign the string at the selected index.
            setStep1TermSelect(options.get(selection));
        }
        return step1TermSelect;
    }

    private String s1GetProcessSelectOption(Select dropdown) {
        if(step1ProcessSelect == null || step1ProcessSelect.equals("")) {
            // Declare a list of options to choose from. Populated next line.
            List<String> options = new ArrayList<>();
            // For each WebElement option, add its extracted visible string to the list.
            for (WebElement option : dropdown.getOptions()) {
                options.add(option.getText());
            }
            int selection = -1;
            // As long as the selection value is out of bounds...
            while (selection < 0 || selection > options.size()) {
                System.out.println("Assignment Process?");
                // Print out the options and their indices.
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((i) + ":\t" + options.get(i));
                }
                // Get user input, subtract 1 to start at 0.
                selection = new Scanner(System.in).nextInt();
            }
            // Assign the string at the selected index.
            setStep1ProcessSelect(options.get(selection));
        }
        return step1ProcessSelect;
    }

    private String s2GetSelectOption(RoomSearch.Step2Radio radio, Select dropdown) {
        if(step2Select == null || step2Select.equals("")) {
            // Declare a list of options to choose from. Populated next line.
            List<String> options = new ArrayList<>();
            // For each WebElement option, add its extracted visible string to the list.
            for (WebElement option : dropdown.getOptions()) {
                options.add(option.getText());
            }
            int selection = -1;
            // As long as the selection value is out of bounds...
            while (selection < 0 || selection > options.size()) {
                // Print out the kind of options about to be queried.
                switch (radio) {
                    case BUILDING:
                        System.out.println("Building?");
                        break;
                    case CLUSTER:
                        System.out.println("Cluster?");
                        break;
                    case AREA:
                        System.out.println("Residential Area?");
                        break;
                    case ALL:
                        break;
                }
                // Print out the options and their indices.
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((i) + ":\t" + options.get(i));
                }
                // Get user input.
                selection = new Scanner(System.in).nextInt();
            }
            // Assign the string at the selected index.
            setStep2Select(options.get(selection));
        }
        return step2Select;
    }

    private String s3GetSelectOption(RoomSearch.Step3Radio radio, Select dropdown) {
        if(step3Select == null || step3Select.equals("")) {
            // Declare a list of options to choose from. Populated next line.
            List<String> options = new ArrayList<>();
            // For each WebElement option, add its extracted visible string to the list.
            for (WebElement option : dropdown.getOptions()) {
                options.add(option.getText());
            }
            int selection = -1;
            // As long as the selection value is out of bounds...
            while (selection < 0 || selection > options.size()) {
                // Print out the kind of options about to be queried.
                switch (radio) {
                    case TYPE:
                        System.out.println("Room Type?");
                        break;
                    case DESIGN:
                        System.out.println("Room Design?");
                        break;
                    case FLOOR:
                        System.out.println("Floor?");
                        break;
                    case OPTION:
                        System.out.println("Housing/Living Option?");
                        break;
                }
                // Print out the options and their indices.
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((i) + ":\t" + options.get(i));
                }
                // Get user input.
                selection = new Scanner(System.in).nextInt();
            }
            // Return the string at the selected index.
            setStep3Select(options.get(selection));
        }
        return step3Select;
    }

    private String s4GetSelectOption(RoomSearch.Step4Radio radio, Select dropdown) {
        if(step4Select == null || step4Select.equals("")) {
            // Declare a list of options to choose from. Populated next line.
            List<String> options = new ArrayList<>();
            // For each WebElement option, add its extracted visible string to the list.
            for (WebElement option : dropdown.getOptions()) {
                options.add(option.getText());
            }
            int selection = -1;
            // As long as the selection value is out of bounds...
            while (selection < 0 || selection > options.size()) {
                // Print out the kind of options about to be queried.
                switch (radio) {
                    case NONE:
                        break;
                    case ROOM_OPEN:
                        System.out.println("Number of open spaces in a room?");
                        break;
                    case SUITE_OPEN:
                        System.out.println("Number of open spaces in a suite/apartment?");
                        break;
                    case TYPE:
                        System.out.println("Room type?");
                        break;
                    case OPEN_DOUBLE:
                        break;
                    case OPEN_TRIPLE:
                        break;
                }
                // Print out the options and their indices.
                for (int i = 0; i < options.size(); i++) {
                    System.out.println((i) + ":\t" + options.get(i));
                }
                // Get user input.
                selection = new Scanner(System.in).nextInt();
            }
            // Return the string at the selected index.
            setStep4Select(options.get(selection));
        }
        return step4Select;
    }

    public void setStep2Radio(RoomSearch.Step2Radio step2Radio) {
        this.step2Radio = step2Radio;
    }

    public void setStep3Radio(RoomSearch.Step3Radio step3Radio) {
        this.step3Radio = step3Radio;
    }

    public void setStep4Radio(RoomSearch.Step4Radio step4Radio) {
        this.step4Radio = step4Radio;
    }

    public void setStep1TermSelect(String step1TermSelect) {
        this.step1TermSelect = step1TermSelect;
    }

    public void setStep1ProcessSelect(String step1ProcessSelect) {
        this.step1ProcessSelect = step1ProcessSelect;
    }

    public void setStep2Select(String step2Select) {
        this.step2Select = step2Select;
    }

    public void setStep3Select(String step3Select) {
        this.step3Select = step3Select;
    }

    public void setStep4Select(String step4Select) {
        this.step4Select = step4Select;
    }
}
