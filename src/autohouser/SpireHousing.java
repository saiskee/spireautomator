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
    private ArrayList<RoomSearch> searches;
    private boolean searchForever;
    private boolean changed;

    public SpireHousing() {
        this.driver = null;
        this.searches = new ArrayList<>();
        this.searchForever = false;
        this.changed = false;
    }

    public SpireHousing(WebDriver driver) {
        this();
        this.driver = driver;
    }

    public SpireHousing(WebDriver driver, ArrayList<RoomSearch> searches) {
        this();
        this.driver = driver;
        this.searches = searches;
    }

    public SpireHousing(WebDriver driver, ArrayList<RoomSearch> searches, boolean searchForever) {
        this();
        this.driver = driver;
        this.searches = searches;
        this.searchForever = searchForever;
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
        while(!changed || searchForever) {
            for(RoomSearch curSearch : searches) {
                // Enter the current search criteria into the DOM.
                enterSearchCriteria(driver, curSearch);
                // Wait an interval, then click the "Search Now" button after finishing entering search criteria.
                UMass.sleep(UMass.WAIT_INTERVAL);
                driver.findElement(By.cssSelector(UMass.S5_SEARCH_NOW_SELECTOR)).click();
                // Parse the results and save them to the current search configuration.
                curSearch.setResults(parseRooms());
                //TODO: How to select which room to assign, if multiple?
                if(false) {
                    assignRoom(driver, curSearch.getResults().get(0));
                    changed = true;
                } else {
                    // Click "New Search" button on results page to return to search page.
                    UMass.waitForElement(driver, By.cssSelector(UMass.ROOMS_NEW_SEARCH_SELECTOR)).click();
                }
                // If it has been less time than the load interval since the last refresh, wait an extra load interval.
                if ((System.currentTimeMillis() - previousTime) < UMass.LOAD_INTERVAL) {
                    UMass.sleep(UMass.LOAD_INTERVAL);
                }
                // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
                // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
                previousTime = System.currentTimeMillis();
            }
        }
    }

    /**
     *Performs the action of changing the user's housing assignment to the given roomId.
     * Returns the amount of successful changes made (1 or 0).
     * @param driver    The WebDriver being used by Selenium.
     * @param room      Room to be assigned into.
     * @return          1 if change succeeded, 0 if change failed.
     */
    private void assignRoom(WebDriver driver, Room room) {
        UMass.findElementRoomsResults(driver, room.getRow(), 11).click();
        // Select first student in Section I. Students to Assign
        UMass.waitForElement(driver, By.cssSelector(UMass.ASSIGN_SECTION_1_SELECTOR)).click();
        // Select available assignment in Section II. Available Assignment(s)
        UMass.waitForElement(driver, By.cssSelector(UMass.ASSIGN_SECTION_2_SELECTOR)).click();
        // Click the Choose button to go to the next page.
        UMass.waitForElement(driver, By.cssSelector(UMass.ASSIGN_CHOOSE_SELECTOR)).click();
        // Select first student in Section I. Students to Assign
        UMass.waitForElement(driver, By.cssSelector(UMass.CONFIRM_SECTION_1_SELECTOR)).click();
        // Click the Save button to go to the next page.
        UMass.waitForElement(driver, By.cssSelector(UMass.CONFIRM_SAVE_SELECTOR)).click();
        // Click the You're Done! Return button to go back to search page.
        UMass.waitForElement(driver, By.cssSelector(UMass.COMPLETE_RETURN_SELECTOR)).click();
    }

    /**
     * Enter the curSearch's criteria into the DOM.
     * If any criteria are not set, prompt the user for input and save.
     * @param driver    The WebDriver being used by Selenium.
     * @param curSearch The search criteria to enter into the DOM.
     */
    private void enterSearchCriteria(WebDriver driver, RoomSearch curSearch) {
        // Enter the selected term. Prompt user once if needed.
        Select s1TermSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S1_TERM_SELECT_SELECTOR)));
        s1TermSelect.selectByVisibleText(s1GetTermSelectOption(curSearch, s1TermSelect));
        // Enter the selected assignment process. Prompt user once if needed.
        Select s1ProcessSelect = new Select(UMass.waitForElement(driver, By.cssSelector(UMass.S1_PROCESS_SELECT_SELECTOR)));
        s1ProcessSelect.selectByVisibleText(s1GetProcessSelectOption(curSearch, s1ProcessSelect));
        // Since some radios prompt a dropdown selection and others don't, switch cases allow us to specify.
        switch(s2GetRadioOption(curSearch)) {
            // All radios with associated selects work the same, just values are different. Radios without associated
            // selects work the same as each other too. Only the first case (step 2 radio, building) is commented.
            case BUILDING:  UMass.waitForElement(driver, By.cssSelector(UMass.S2_BUILDING_RADIO_SELECTOR)).click();
                // Wait a short time to allow the hidden element to appear after the click.
                UMass.sleep(UMass.WAIT_INTERVAL);
                // Gets the dropdown menu of buildings.
                Select s2BuildingSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S2_BUILDING_SELECT_SELECTOR)));
                // Gets input from user for which dropdown option they want, and select it.
                s2BuildingSelect.selectByVisibleText(s2GetSelectOption(curSearch, s2BuildingSelect));
                break;
            case CLUSTER:   UMass.waitForElement(driver, By.cssSelector(UMass.S2_CLUSTER_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s2ClusterSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S2_CLUSTER_SELECT_SELECTOR)));
                s2ClusterSelect.selectByVisibleText(s2GetSelectOption(curSearch, s2ClusterSelect));
                break;
            case AREA:      UMass.waitForElement(driver, By.cssSelector(UMass.S2_AREA_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s2AreaSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S2_AREA_SELECT_SELECTOR)));
                s2AreaSelect.selectByVisibleText(s2GetSelectOption(curSearch, s2AreaSelect));
                break;
            case ALL:       UMass.waitForElement(driver, By.cssSelector(UMass.S2_ALL_RADIO_SELECTOR)).click();
                break;
        }
        switch(s3GetRadioOption(curSearch)) {
            case TYPE:      UMass.waitForElement(driver, By.cssSelector(UMass.S3_TYPE_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s3TypeSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S3_TYPE_SELECT_SELECTOR)));
                s3TypeSelect.selectByVisibleText(s3GetSelectOption(curSearch, s3TypeSelect));

                break;
            case DESIGN:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_DESIGN_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s3DesignSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S3_DESIGN_SELECT_SELECTOR)));
                s3DesignSelect.selectByVisibleText(s3GetSelectOption(curSearch, s3DesignSelect));
                break;
            case FLOOR:     UMass.waitForElement(driver, By.cssSelector(UMass.S3_FLOOR_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s3FloorSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S3_FLOOR_SELECT_SELECTOR)));
                s3FloorSelect.selectByVisibleText(s3GetSelectOption(curSearch, s3FloorSelect));
                break;
            case OPTION:    UMass.waitForElement(driver, By.cssSelector(UMass.S3_OPTION_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s3OptionSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S3_OPTION_SELECT_SELECTOR)));
                s3OptionSelect.selectByVisibleText(s3GetSelectOption(curSearch, s3OptionSelect));
                break;
        }
        switch(s4GetRadioOption(curSearch)) {
            case NONE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_NONE_RADIO_SELECTOR)).click();
                break;
            case ROOM_OPEN:     UMass.waitForElement(driver, By.cssSelector(UMass.S4_ROOM_OPEN_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s4RoomOpenSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S4_ROOM_OPEN_SELECT_SELECTOR)));
                s4RoomOpenSelect.selectByVisibleText(s4GetSelectOption(curSearch, s4RoomOpenSelect));
                break;
            case SUITE_OPEN:    UMass.waitForElement(driver, By.cssSelector(UMass.S4_SUITE_OPEN_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s4SuiteOpenSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S4_SUITE_OPEN_SELECT_SELECTOR)));
                s4SuiteOpenSelect.selectByVisibleText(s4GetSelectOption(curSearch, s4SuiteOpenSelect));
                break;
            case TYPE:          UMass.waitForElement(driver, By.cssSelector(UMass.S4_TYPE_RADIO_SELECTOR)).click();
                UMass.sleep(UMass.WAIT_INTERVAL);
                Select s4TypeSelect = new Select(UMass.waitForElement(driver,
                        By.cssSelector(UMass.S4_TYPE_SELECT_SELECTOR)));
                s4TypeSelect.selectByVisibleText(s4GetSelectOption(curSearch, s4TypeSelect));
                break;
            case OPEN_DOUBLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_DOUBLE_RADIO_SELECTOR)).click();
                break;
            case OPEN_TRIPLE:   UMass.waitForElement(driver, By.cssSelector(UMass.S4_OPEN_TRIPLE_RADIO_SELECTOR)).click();
                break;
        }
    }

    /**
     * Read in the table of rooms shown on the DOM and construct the Room objects.
     * @return  An ArrayList of Room objects shown on the DOM.
     */
    private ArrayList<Room> parseRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        // Gets the size of the rooms search results table and iterates over each row.
        // Skips the first row; it's just header labels, -3 due to non-room rows.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.ROOMS_RESULTS_SELECTOR)).findElements(By.tagName("tr")).size()-3; row++) {
            String building =   UMass.findElementRoomsResults(driver, row, 1).getText();
            String number =     UMass.findElementRoomsResults(driver, row, 2).getText();
            String design =     UMass.findElementRoomsResults(driver, row, 6).getText();
            String type =       UMass.findElementRoomsResults(driver, row, 7).getText();
            Room room =         new Room(row, building, number, design, type);
            rooms.add(room);
        }
        return rooms;
    }

    // Check that the user has provided preferences for all the necessary radio buttons.
    // If any of the radio button preferences are not set, prompt for them.
    // Radio options are few and static, therefore they may be hardcoded.
    private RoomSearch.Step2Radio s2GetRadioOption(RoomSearch curSearch) {
        while(curSearch.getStep2Radio() == null) {
            System.out.println("Step 2 filter?\n1: Building\n2: Cluster\n3: Area\n4: All");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     curSearch.setStep2Radio(RoomSearch.Step2Radio.BUILDING);  break;
                case 2:     curSearch.setStep2Radio(RoomSearch.Step2Radio.CLUSTER);   break;
                case 3:     curSearch.setStep2Radio(RoomSearch.Step2Radio.AREA);      break;
                case 4:     curSearch.setStep2Radio(RoomSearch.Step2Radio.ALL);       break;
                default:    break;
            }
        }
        return curSearch.getStep2Radio();
    }

    private RoomSearch.Step3Radio s3GetRadioOption(RoomSearch curSearch) {
        while(curSearch.getStep3Radio() == null) {
            System.out.println("Step 3 filter?\n1: Room Type\n2: Room Design\n3: Floor\n4: Living/Housing Option");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     curSearch.setStep3Radio(RoomSearch.Step3Radio.TYPE);      break;
                case 2:     curSearch.setStep3Radio(RoomSearch.Step3Radio.DESIGN);    break;
                case 3:     curSearch.setStep3Radio(RoomSearch.Step3Radio.FLOOR);     break;
                case 4:     curSearch.setStep3Radio(RoomSearch.Step3Radio.OPTION);    break;
                default:    break;
            }
        }
        return curSearch.getStep3Radio();
    }

    private RoomSearch.Step4Radio s4GetRadioOption(RoomSearch curSearch) {
        while(curSearch.getStep4Radio() == null) {
            System.out.println("Step 4 filter?\n1: None\n2: Number of open spaces in a Room\n" +
                    "3: Number of open spaces in a Suite/Apartment\n4: Room Type\n5. Open Doubles\n6. Open Triples");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     curSearch.setStep4Radio(RoomSearch.Step4Radio.NONE);          break;
                case 2:     curSearch.setStep4Radio(RoomSearch.Step4Radio.ROOM_OPEN);     break;
                case 3:     curSearch.setStep4Radio(RoomSearch.Step4Radio.SUITE_OPEN);    break;
                case 4:     curSearch.setStep4Radio(RoomSearch.Step4Radio.TYPE);          break;
                case 5:     curSearch.setStep4Radio(RoomSearch.Step4Radio.OPEN_DOUBLE);   break;
                case 6:     curSearch.setStep4Radio(RoomSearch.Step4Radio.OPEN_TRIPLE);   break;
                default:    break;
            }
        }
        return curSearch.getStep4Radio();
    }

    private String s1GetTermSelectOption(RoomSearch curSearch, Select dropdown) {
        if(curSearch.getStep1TermSelect() == null || curSearch.getStep1TermSelect().equals("")) {
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
            curSearch.setStep1TermSelect(options.get(selection));
        }
        return curSearch.getStep1TermSelect();
    }

    private String s1GetProcessSelectOption(RoomSearch curSearch, Select dropdown) {
        if(curSearch.getStep1ProcessSelect() == null || curSearch.getStep1ProcessSelect().equals("")) {
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
            curSearch.setStep1ProcessSelect(options.get(selection));
        }
        return curSearch.getStep1ProcessSelect();
    }

    private String s2GetSelectOption(RoomSearch curSearch, Select dropdown) {
        if(curSearch.getStep2Select() == null || curSearch.getStep2Select().equals("")) {
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
                switch (curSearch.getStep2Radio()) {
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
            curSearch.setStep2Select(options.get(selection));
        }
        return curSearch.getStep2Select();
    }

    private String s3GetSelectOption(RoomSearch curSearch, Select dropdown) {
        if(curSearch.getStep3Select() == null || curSearch.getStep3Select().equals("")) {
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
                switch (curSearch.getStep3Radio()) {
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
            curSearch.setStep3Select(options.get(selection));
        }
        return curSearch.getStep3Select();
    }

    private String s4GetSelectOption(RoomSearch curSearch, Select dropdown) {
        if(curSearch.getStep4Select() == null || curSearch.getStep4Select().equals("")) {
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
                switch (curSearch.getStep4Radio()) {
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
            curSearch.setStep4Select(options.get(selection));
        }
        return curSearch.getStep4Select();
    }

}
