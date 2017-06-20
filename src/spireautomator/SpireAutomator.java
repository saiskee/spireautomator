package spireautomator;

import autoenroller.*;
import autohouser.RoomSearch;
import autohouser.SpireHousing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * Provides logistical functions that are used throughout the
 * SPIRE enrolling and housing programs at different levels of depth.
 */
public class SpireAutomator {
    public static void main(String[] args) {
        Browser browser = null;
        WebDriver driver = null;
        Automator automator = null;
        String username = null;
        String password = null;
        String term = null;
        File asciiArt = new File("asciiArt");

        if(args.length > 0) {
            // Process each command-line argument.
            for(String arg : args) {
                String[] argSplit = arg.split("=");
                // Only parse the argument if it is splittable, such as "param=value"
                if(argSplit.length > 1) {
                    // Do not alter the input strings, fields may need to be exact (ex. password).
                    String param = argSplit[0];
                    String value = argSplit[1];
                    switch(param) {
                        case "browser":     switch(value) {
                            case "chrome":  browser = Browser.CHROME;     break;
                            case "firefox": browser = Browser.FIREFOX;    break;
                            default:        break;
                        }   break;
                        case "automator":   switch(value) {
                            case "enroller":    automator = Automator.ENROLLER; break;
                            case "houser":      automator = Automator.HOUSER;   break;
                            default:            break;
                        }   break;
                        case "username":    username = value;                       break;
                        case "password":    password = value;                       break;
                        case "term":        term = value;                           break;
                        default:            break;
                    }
                } else if(arg.trim().toLowerCase().equals("help")) {
                    printHelp(asciiArt);
                }
            }
        } else {
            System.out.println("Use the \"help\" program argument/parameter to learn how to use this program.");
            // Program will still run, prompting for all needed inputs, even if no arguments are given.
        }

        // If no preferred browser was provided, prompt for one.
        while (browser == null) {
            System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     browser = Browser.CHROME;   break;
                case 2:     browser = Browser.FIREFOX;  break;
                default:    break;
            }
        }
        // Assign the appropriate web driver for the preferred browser and operating system.
        switch(browser) {
            case CHROME:    if (IS_OS_WINDOWS) {
                                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_WIN32);
                            } else if (IS_OS_MAC) {
                                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_MAC64);
                            } else if (IS_OS_LINUX) {
                                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_LINUX64);
                            }
                            driver = new ChromeDriver();
                            break;
            case FIREFOX:    if (IS_OS_WINDOWS) {
                                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_WIN64);
                            } else if (IS_OS_MAC) {
                                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_MACOS);
                            } else if (IS_OS_LINUX) {
                                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_LINUX64);
                            }
                            driver = new FirefoxDriver();
                            break;
        }

        // Go to the UMass SPIRE homepage in the browser.
        driver.get(UMass.LOGIN_URL);
        do {
            // If no username was provided, prompt for one.
            if(username == null) {
                System.out.println("Username?");
                username = new Scanner(System.in).nextLine();
            }
            // If no password was provided, prompt for one.
            if(password == null) {
                System.out.println("Password?");
                Console console = System.console();
                // Use the console to securely read the password without displaying it on-screen.
                if(console != null) {
                    password = new String(console.readPassword());
                } else {
                    password = new Scanner(System.in).nextLine();
                }
            }
            // Explicitly waits for the Username field to load and types username.
            UMass.waitForElement(driver, By.id(UMass.USERNAME_ID)).sendKeys(username);
            // Presence of Username means Password and Go button are loaded too.
            driver.findElement(By.id(UMass.PASSWORD_ID)).sendKeys(password);
            driver.findElement(By.cssSelector(UMass.LOGIN_BUTTON_SELECTOR)).click();
            UMass.sleep(1000);
            // The page will be "SPIRE Logon" as long as the user is not logged in.
            // Repeat until the page has changed, assuming that means the user is successfully logged in.
        } while(driver.getTitle().equals("SPIRE Logon"));

        // SPIRE is normally shown as a webpage within a webpage.
        // The subwebpage's code is hard to access while it is nested.
        // This line explicitly waits until the internal frame is present
        // and then loads it into the driver as the main webpage.
        driver.get(UMass.waitForElement(driver, By.tagName("iframe")).getAttribute("src"));
        // Wait in case there is an error popup (seen on Firefox, not Chrome).
        UMass.sleep(500);

        // If no preferred automator was provided, prompt for one.
        while(automator == null) {
            System.out.println("Automator?\n1: Enroller\n2: Houser");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     automator = Automator.ENROLLER;   break;
                case 2:     automator = Automator.HOUSER;     break;
                default:    break;
            }
        }
        // Go into the appropriate automator program.
        switch(automator) {
            case ENROLLER:      Map<String, Lecture> currentSchedule = new HashMap<>();
                                Map<String, Lecture> shoppingCart = new HashMap<>();
                                ArrayList<Action> actions = new ArrayList<>();
                                setEnrollerConfiguration(driver, currentSchedule, shoppingCart, actions);
                                SpireEnrollment spireEnrollment = new SpireEnrollment(driver, term, currentSchedule, shoppingCart, actions);
                                spireEnrollment.run();
                                break;
            case HOUSER:        // Default to one search criteria configuration.
                                // If user calls for more configurations, a larger array will be created & copied into.
                                ArrayList<RoomSearch> searches = new ArrayList<>();
                                // Configure only the first search configuration with runtime arguments.
                                // Additional configurations need to be configured in runtime.
                                // Term is relevant to several automators so it is retrieved earlier.
                                searches.add(new RoomSearch());
                                searches.get(0).setStep1TermSelect(term);
                                // Reprocess each command-line argument to find arguments for houser.
                                for(String arg : args) {
                                    String[] argSplit = arg.split("=");
                                    // Only parse the argument if it is splittable, such as "param=value"
                                    if(argSplit.length > 1) {
                                        // Do not alter the input strings, fields may need to be exact (ex. password).
                                        String param = argSplit[0];
                                        String value = argSplit[1];
                                        switch(param.toLowerCase()) {
                                            case "searches":    // Subtract one because one RoomSearch already exists.
                                                                int numSearches = Integer.valueOf(value)-1;
                                                                // Only make more search criteria if this value is greater than current size.
                                                                while(numSearches > 0) {
                                                                    searches.add(new RoomSearch());
                                                                    numSearches--;
                                                                }
                                            // Inside this switch statement it is okay to lowercase the input strings
                                            // because the input strings themselves are not passed to something important.
                                            case "s2radio":     switch(value.toLowerCase()) {
                                                case "building":    searches.get(0).setStep2Radio(RoomSearch.Step2Radio.BUILDING);     break;
                                                case "cluster":     searches.get(0).setStep2Radio(RoomSearch.Step2Radio.CLUSTER);      break;
                                                case "area":        searches.get(0).setStep2Radio(RoomSearch.Step2Radio.AREA);         break;
                                                case "all":         searches.get(0).setStep2Radio(RoomSearch.Step2Radio.ALL);          break;
                                                default:            break;
                                            }   break;
                                            case "s3radio":     switch(value.toLowerCase()) {
                                                case "type":        searches.get(0).setStep3Radio(RoomSearch.Step3Radio.TYPE);         break;
                                                case "design":      searches.get(0).setStep3Radio(RoomSearch.Step3Radio.DESIGN);       break;
                                                case "floor":       searches.get(0).setStep3Radio(RoomSearch.Step3Radio.FLOOR);        break;
                                                case "option":      searches.get(0).setStep3Radio(RoomSearch.Step3Radio.OPTION);       break;
                                                default:            break;
                                            }   break;
                                            case "s4radio":     switch(value.toLowerCase()) {
                                                case "none":        searches.get(0).setStep4Radio(RoomSearch.Step4Radio.NONE);         break;
                                                case "room_open":   searches.get(0).setStep4Radio(RoomSearch.Step4Radio.ROOM_OPEN);    break;
                                                case "suite_open":  searches.get(0).setStep4Radio(RoomSearch.Step4Radio.SUITE_OPEN);   break;
                                                case "type":        searches.get(0).setStep4Radio(RoomSearch.Step4Radio.TYPE);         break;
                                                case "open_double": searches.get(0).setStep4Radio(RoomSearch.Step4Radio.OPEN_DOUBLE);  break;
                                                case "open_triple": searches.get(0).setStep4Radio(RoomSearch.Step4Radio.OPEN_TRIPLE);  break;
                                                default:            break;
                                            }   break;
                                            // Here the values are passed along to something case-sensitive
                                            // so the input strings cannot be lowercased.
                                            case "process":     searches.get(0).setStep1ProcessSelect(value);   break;
                                            case "s2select":    searches.get(0).setStep2Select(value);          break;
                                            case "s3select":    searches.get(0).setStep3Select(value);          break;
                                            case "s4select":    searches.get(0).setStep4Select(value);          break;
                                            default:            break;
                                        }
                                    }
                                }
                                SpireHousing spireHousing = new SpireHousing(driver, searches, getResidentialAreaConfig());
                                spireHousing.run();
                                break;
            default:            break;
        }
    }

    /**
     * Sets an example configuration of the current schedule,
     * shopping cart, and actions list. Can be used as a template
     * for custom hardcoded structure.
     * @param currentSchedule   Hardcoded current schedule in SPIRE.
     * @param shoppingCart      Hardcoded shopping cart in SPIRE.
     * @param actions           Hardcoded actions to perform on SPIRE.
     */
    private static void setEnrollerConfiguration(WebDriver driver, Map<String, Lecture> currentSchedule, Map<String, Lecture> shoppingCart, ArrayList<Action> actions) {
        // Start current schedule.
        Lecture compsci311_01 = new Lecture("COMPSCI 311-01", "Introduction to Algorithms", "14784");
        Discussion compsci311_01aa = new Discussion("COMPSCI 311-01AA", "14785");
        compsci311_01.setEnrolledDiscussion(compsci311_01aa);
        Lecture compsci320_01 = new Lecture("COMPSCI 320-01", "Software Engineering", "14786");
        Discussion compsci320_01aa = new Discussion("COMPSCI 320-01AA", "14809");
        compsci320_01.setEnrolledDiscussion(compsci320_01aa);
        Lecture math235_07 = new Lecture("MATH 235-07", "Intro Linear Algebra", "15184");
        currentSchedule.put(compsci311_01.getClassId(), compsci311_01);
        currentSchedule.put(compsci320_01.getClassId(), compsci320_01);
        currentSchedule.put(math235_07.getClassId(), math235_07);
        // End current schedule.

        // Start shopping cart.
        Discussion compsci311_01ab = new Discussion("COMPSCI 311-01AB", "20764");
        compsci311_01.addDiscussion(compsci311_01ab);
        Lecture compsci326_01 = new Lecture("COMPSCI 326-01", "Web Programming", "14842");
        Lecture compsci240_01 = new Lecture("COMPSCI 240-01", "Reasoning Under Uncertainty", "14812");
        Discussion compsci240_01aa = new Discussion("COMPSCI 240-01AA", "14836");
        Discussion compsci240_01ab = new Discussion("COMPSCI 240-01AB", "14841");
        Discussion compsci240_01ac = new Discussion("COMPSCI 240-01AC", "14851");
        Discussion compsci240_01ad = new Discussion("COMPSCI 240-01AD", "14864");
        compsci240_01.addDiscussions(compsci240_01aa, compsci240_01ab, compsci240_01ac, compsci240_01ad);
        shoppingCart.put(compsci311_01.getClassId(), compsci311_01);
        shoppingCart.put(compsci326_01.getClassId(), compsci326_01);
        shoppingCart.put(compsci240_01.getClassId(), compsci240_01);
        // End shopping cart.

        // Start actions.
        Swap swap_compsci326_01_compsci320_01 = (Swap) new Swap(compsci326_01, compsci320_01).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.get(compsci320_01) != null
                        && compsci326_01.isOpen(driver) == UMass.TRUE;
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci320_01.getNameAndSection()+" and "
                        +compsci326_01.getNameAndSection()+" is open";
            }
        });
        Edit edit_compsci311_01_compsci311_01ab = (Edit) new Edit(compsci311_01, compsci311_01ab).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.get(compsci311_01.getClassId()) != null
                        && !currentSchedule.get(compsci311_01.getClassId()).getEnrolledDiscussion().equals(compsci311_01ab)
                        && compsci311_01ab.isOpen(driver) == UMass.TRUE;
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci311_01.getNameAndSection()+", "
                        +"not enrolled in "+compsci311_01ab.getNameAndSection()+", and "
                        +compsci311_01ab.getNameAndSection()+" is open";
            }
        });
        Edit edit_compsci240_01_compsci240_01ab = (Edit) new Edit(compsci240_01, compsci240_01ab).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.get(compsci240_01.getClassId()) != null
                        && (currentSchedule.get(compsci311_01.getClassId()) != null
                        && !currentSchedule.get(compsci311_01.getClassId()).getEnrolledDiscussion().equals(compsci311_01aa))
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01aa)
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01ab)
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01ad)
                        && compsci240_01aa.isOpen(driver) != UMass.TRUE
                        && compsci240_01ab.isOpen(driver) == UMass.TRUE;
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci240_01.getNameAndSection() +", "
                        +"not enrolled in "+compsci311_01aa.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01aa.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01ab.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01ad.getNameAndSection()+", "
                        +compsci240_01aa.getNameAndSection()+" is not open, and "
                        +compsci240_01ab.getNameAndSection()+" is open";
            }
        });
        Edit edit_compsci240_01_compsci240_01aa = (Edit) new Edit(compsci240_01, compsci240_01aa).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.get(compsci240_01.getClassId()) != null
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01aa)
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01ad)
                        && compsci240_01aa.isOpen(driver) == UMass.TRUE;
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci240_01.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01aa.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01ad.getNameAndSection()+", and "
                        +compsci240_01aa.getNameAndSection()+" is open";
            }
        }).setSatisfiableActions(edit_compsci240_01_compsci240_01ab);
        Edit edit_compsci240_01_compsci240_01ad = (Edit) new Edit(compsci240_01, compsci240_01ad).addCondition(new Condition() {
            @Override
            public boolean isMet() {
                return currentSchedule.get(compsci240_01.getClassId()) != null
                        && !currentSchedule.get(compsci240_01.getClassId()).getEnrolledDiscussion().equals(compsci240_01ad)
                        && currentSchedule.get(compsci320_01.getClassId()) == null
                        && compsci240_01ad.isOpen(driver) == UMass.TRUE;
            }
            @Override
            public String toString() {
                return "Enrolled in "+compsci240_01.getNameAndSection()+", "
                        +"not enrolled in "+compsci240_01ad.getNameAndSection()+", "
                        +"not enrolled in "+compsci320_01.getNameAndSection()+", and "
                        +compsci240_01ad.getNameAndSection()+" is open";
            }
        }).setSatisfiableActions(edit_compsci240_01_compsci240_01aa);
        actions.add(swap_compsci326_01_compsci320_01);
        actions.add(edit_compsci311_01_compsci311_01ab);
        actions.add(edit_compsci240_01_compsci240_01ad);
        actions.add(edit_compsci240_01_compsci240_01aa);
        actions.add(edit_compsci240_01_compsci240_01ab);
        // End actions
    }

    /**
     * Creates a Map that associates buildings
     * with residential areas.
     */
    private static Map<String, String> getResidentialAreaConfig(){
        Map<String, String> residentialAreas = new HashMap<>();
        residentialAreas.put("Baker", "CE");
        residentialAreas.put("Birch", "CH");
        residentialAreas.put("Brett", "CE");
        residentialAreas.put("Brooks", "CE");
        residentialAreas.put("Brown", "SY");
        residentialAreas.put("Butterfield", "CE");
        residentialAreas.put("Cance", "SW");
        residentialAreas.put("Cashin", "SY");
        residentialAreas.put("Chadbourne", "CE");
        residentialAreas.put("Coolidge", "SW");
        residentialAreas.put("Crabtree", "NE");
        residentialAreas.put("Crampton", "SW");
        residentialAreas.put("Dickinson", "OH");
        residentialAreas.put("Dwight", "NE");
        residentialAreas.put("Elm", "CH");
        residentialAreas.put("Emerson", "SW");
        residentialAreas.put("Field", "OH");
        residentialAreas.put("Gorman", "CE");
        residentialAreas.put("Grayson", "OH");
        residentialAreas.put("Greenough", "CE");
        residentialAreas.put("Hamlin", "NE");
        residentialAreas.put("James", "SW");
        residentialAreas.put("John Adams", "SW");
        residentialAreas.put("John Quincy Adams", "SW");
        residentialAreas.put("Johnson", "NE");
        residentialAreas.put("Kennedy", "SW");
        residentialAreas.put("Knowlton", "NE");
        residentialAreas.put("Leach", "NE");
        residentialAreas.put("Lewis", "NE");
        residentialAreas.put("Lincoln Building 01", "LN");
        residentialAreas.put("Lincoln Building 02", "LN");
        residentialAreas.put("Lincoln Building 03", "LN");
        residentialAreas.put("Lincoln Building 04", "LN");
        residentialAreas.put("Lincoln Building 05", "LN");
        residentialAreas.put("Lincoln Building 06", "LN");
        residentialAreas.put("Lincoln Building 07", "LN");
        residentialAreas.put("Lincoln Building 08", "LN");
        residentialAreas.put("Lincoln Building 09", "LN");
        residentialAreas.put("Lincoln Building 10", "LN");
        residentialAreas.put("Lincoln Building 11", "LN");
        residentialAreas.put("Linden", "CH");
        residentialAreas.put("MacKimmie", "SW");
        residentialAreas.put("Maple", "CH");
        residentialAreas.put("Mary Lyon", "NE");
        residentialAreas.put("McNamara", "SY");
        residentialAreas.put("Melville", "SW");
        residentialAreas.put("Moore", "SW");
        residentialAreas.put("North Hall A", "NO");
        residentialAreas.put("North Hall B", "NO");
        residentialAreas.put("North Hall C", "NO");
        residentialAreas.put("North Hall D", "NO");
        residentialAreas.put("Oak", "CH");
        residentialAreas.put("Patterson", "SW");
        residentialAreas.put("Pierpont", "SW");
        residentialAreas.put("Prince", "SW");
        residentialAreas.put("Sycamore", "CH");
        residentialAreas.put("Thatcher", "NE");
        residentialAreas.put("Thoreau", "SW");
        residentialAreas.put("VanMeter", "CE");
        residentialAreas.put("Washington", "SW");
        residentialAreas.put("Webster", "OH");
        residentialAreas.put("Wheeler", "CE");
        return residentialAreas;
    }

    private static void printHelp(File asciiArt) {
        int separatorLength = 80;
        try {
            printAsciiArt(asciiArt);
        } catch(IOException e) {
            // Do not print IOException stack trace.
        }
        System.out.println("");
        System.out.println(getHeaderSeparator("INTRODUCTION", separatorLength));
        System.out.println("This SPIRE Automator takes runtime arguments to set its functional configurations.");
        System.out.println("Each section describes an automator and lists its needed runtime arguments.");
        System.out.println("Some arguments can understand only the values that are listed below.");
        System.out.println("Arguments listed below that do not list predefined values may take any input.");
        System.out.println("If an argument with predefined values is given a value that is not listed below,");
        System.out.println("\tthe program will treat that argument as if it had not been set at all.");
        System.out.println("If an argument is not set, the automator will prompt the user for it if it is needed.");
        System.out.println("The automator will not prompt the user for unnecessary arguments.");
        System.out.println("It is recommended to wrap all parameter/value arguments in quotes to preserve spaces,");
        System.out.println("\tespecially arguments without predefined values. Here is an example of a good command:");
        System.out.println("\tjava spireautomator.SpireAutomator \"browser=chrome\" \"automator=enroller\" \"term=Fall 1863\"");
        System.out.println(getHeaderSeparator("GENERAL", separatorLength));
        System.out.println("The following are runtime arguments used universally by all automators:");
        System.out.println("\tbrowser=[chrome, firefox]");
        System.out.println("\tautomator=[enroller, houser]");
        System.out.println("\tusername");
        System.out.println("\tpassword");
        System.out.println("\tterm");
        System.out.println(getHeaderSeparator("ENROLLER", separatorLength));
        System.out.println("The enroller automates the process of searching for and adding/dropping/editing/swapping");
        System.out.println("\tclasses in SPIRE. Complex performing conditions for actions may be specified as well.");
        System.out.println("There are no runtime arguments needed for the enrollment automator.");
        System.out.println("Enroller configurations must be hardcoded and passed to the automator.");
        System.out.println("An editable example of enroller configurations may be found in:");
        System.out.println("\tspireautomator.SpireAutomator.setEnrollerConfiguration()");
        System.out.println(getHeaderSeparator("HOUSER", separatorLength));
        System.out.println("The houser automates the process of searching for and assigning oneself to a room in SPIRE.");
        System.out.println("The houser is capable of searching for rooms using the same search criteria that the main");
        System.out.println("\tSPIRE website provides, parsing the available rooms, determining whether an available room");
        System.out.println("\tis better than the room currently assigned to the user, and assigning oneself to the room.");
        System.out.println("The automator will prompt the user for input if a value is needed but not set.");
        System.out.println("The following arguments are used by this automator:");
        System.out.println("\tsearches=[>1]");
        System.out.println("\tprocess");
        System.out.println("\ts2radio=[building, cluster, area, all]");
        System.out.println("\ts2select");
        System.out.println("\ts3radio=[type, design, floor, option]");
        System.out.println("\ts3select");
        System.out.println("\ts4radio=[none, room_open, suite_open, type, open_double, open_triple]");
        System.out.println("\ts4select");
        System.exit(0);
    }

    private static String getHeaderSeparator(String header, int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < Math.floor((length-header.length())/2); i++) {
            sb.append("-");
        }
        sb.append(header.toUpperCase());
        for(int i = 0; i < Math.ceil((length-header.length())/2); i++) {
            sb.append("-");
        }
        return sb.toString();
    }

    private static void printAsciiArt(File file) throws IOException {
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while((line = br.readLine()) != null) {
            System.out.println(line);
        }
        br.close();
        fr.close();
    }
}
