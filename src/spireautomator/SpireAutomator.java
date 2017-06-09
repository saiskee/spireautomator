package spireautomator;

import autoenroller.*;
import autohouser.Building;
import autohouser.ResidentialArea;
import autohouser.SpireHousing;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.Console;
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
                    case "username":    username = value;   break;
                    case "password":    password = value;   break;
                    case "term":        term = value;       break;
                    default:            break;
                }
            }
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
                                setExampleEnrollerConfig(driver, currentSchedule, shoppingCart, actions);
                                SpireEnrollment spireEnrollment = new SpireEnrollment(driver, term, currentSchedule, shoppingCart, actions);
                                spireEnrollment.run();
                                break;
            case HOUSER:        Map<String, ResidentialArea> residentialAreas = new HashMap<>();
                                setResidentialAreaConfig(residentialAreas);
                                SpireHousing spireHousing = new SpireHousing(driver, residentialAreas);
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
    private static void setExampleEnrollerConfig(WebDriver driver, Map<String, Lecture> currentSchedule, Map<String, Lecture> shoppingCart, ArrayList<Action> actions) {
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
     * Constructs the {@link ResidentialArea}s
     * and {@link Building}s on the UMass campus.
     * @param residentialAreas A Map of all of the residential areas on campus, containing all of the Buildings.
     */
    private static void setResidentialAreaConfig(Map<String, ResidentialArea> residentialAreas){
        ResidentialArea orchardHill = new ResidentialArea("Orchard Hill");
        ResidentialArea central = new ResidentialArea("Central");
        ResidentialArea northeast = new ResidentialArea("Northeast");
        ResidentialArea southwest = new ResidentialArea("Southwest");
        ResidentialArea honors = new ResidentialArea("Honors");
        ResidentialArea north = new ResidentialArea("North");
        ResidentialArea sylvan = new ResidentialArea("Sylvan");

        orchardHill.put(new Building("Dickinson"));
        orchardHill.put(new Building("Webster"));
        orchardHill.put(new Building("Grayson"));
        orchardHill.put(new Building("Field"));

        central.put(new Building("Van Meter"));
        central.put(new Building("Gorman"));
        central.put(new Building("Wheeler"));
        central.put(new Building("Butterfield"));
        central.put(new Building("Greenough"));
        central.put(new Building("Chadbourne"));
        central.put(new Building("Baker"));
        central.put(new Building("Brooks"));
        central.put(new Building("Brett"));

        northeast.put(new Building("Hamlin"));
        northeast.put(new Building("Leach"));
        northeast.put(new Building("Dwight"));
        northeast.put(new Building("Knowlton"));
        northeast.put(new Building("Crabtree"));
        northeast.put(new Building("Mary Lyon"));
        northeast.put(new Building("Johnson"));
        northeast.put(new Building("Lewis"));
        northeast.put(new Building("Thatcher"));

        north.put(new Building("North A"));
        north.put(new Building("North B"));
        north.put(new Building("North C"));
        north.put(new Building("North D"));

        sylvan.put(new Building("Brown"));
        sylvan.put(new Building("McNamara"));
        sylvan.put(new Building("Cashin"));

        honors.put(new Building("Oak"));
        honors.put(new Building("Sycamore"));
        honors.put(new Building("Birch"));
        honors.put(new Building("Elm"));
        honors.put(new Building("Maple"));
        honors.put(new Building("Linden"));

        southwest.put(new Building("Melville"));
        southwest.put(new Building("Thoreau"));
        southwest.put(new Building("Pierpont"));
        southwest.put(new Building("Moore"));
        southwest.put(new Building("James"));
        southwest.put(new Building("Emerson"));
        southwest.put(new Building("Kennedy"));
        southwest.put(new Building("Cance"));
        southwest.put(new Building("Coolidge"));
        southwest.put(new Building("Crampton"));
        southwest.put(new Building("John Adams"));
        southwest.put(new Building("John Quincy Adams"));
        southwest.put(new Building("MacKimmie"));
        southwest.put(new Building("Patterson"));
        southwest.put(new Building("Prince"));
        southwest.put(new Building("Washington"));

        residentialAreas.put(orchardHill.getId(), orchardHill);
        residentialAreas.put(central.getId(), central);
        residentialAreas.put(northeast.getId(), northeast);
        residentialAreas.put(southwest.getId(), southwest);
        residentialAreas.put(honors.getId(), honors);
        residentialAreas.put(north.getId(), north);
        residentialAreas.put(sylvan.getId(), sylvan);
    }
}
