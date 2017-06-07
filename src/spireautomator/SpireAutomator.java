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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
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
        System.out.println(Arrays.toString(args));
        WebDriver driver;
        Automator automator = Automator.NONE;
        Browser browser = Browser.NONE;
        String username = "";
        String password = "";
        String term = "";
//        File propertiesFile = new File(".spire.properties").getAbsoluteFile();
//        Properties properties = new Properties();
//        if(propertiesFile.exists()) {
//            try {
//                properties.load(new FileInputStream(propertiesFile));
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
        for(String arg : args) {
            String param = arg.split("=")[0].trim().toLowerCase();
            String value = arg.split("=")[1].trim().toLowerCase();
            switch(param) {
                case "automator":   switch(value) {
                                        case "enroller":    automator = Automator.ENROLLER; break;
                                        case "houser":      automator = Automator.HOUSER;   break;
                                        default:            break;
                                    }   break;
                case "browser":     switch(value) {
                                        case "chrome":  browser = Browser.CHROME;     break;
                                        case "firefox": browser = Browser.FIREFOX;    break;
                                        default:        break;
                                    }   break;
                case "username":    username = value;   break;
                case "password":    password = value;   break;
                case "term":        term = value;       break;
                default:            break;
            }
        }
//        // Determine if the program is automating enrollment or housing.
//        // Selection may be set in and loaded from properties.
//        if(args.length > 0 && (args[0].equals("enroller") || args[0].equals("houser"))) {
//            automator = args[0];
//            System.out.println("Save automator? (y/n)");
//            if(new Scanner(System.in).nextLine().equals("y")) {
//                setAndStoreProperties("automator", automator, properties, propertiesFile);
//            }
//        }else if(properties.get("automator") != null) {
//            automator = properties.get("automator").toString();
//        } else {
//            System.out.println("This program requires one parameter. The options are:\nenroller\nhouser");
//        }
        //TODO: Find a better way to store automated-specific persistent data. Individual .properties files?
        driver = getWebDriver(browser);
        spireLogon(driver, username, password, UMass.LOGIN_URL);

        // SPIRE is normally shown as a webpage within a webpage.
        // The subwebpage's code is hard to access while it is nested.
        // This line explicitly waits until the internal frame is present
        // and then loads it into the driver as the main webpage.
        driver.get(UMass.waitForElement(driver, By.tagName("iframe")).getAttribute("src"));
        // Wait in case there is an error popup (seen on Firefox, not Chrome).
        UMass.sleep(500);

        while(automator == Automator.NONE) {
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
            case NONE:          break;
            default:            break;
        }
    }

    public static void spireLogon(WebDriver driver, String username, String password, String url) {
        driver.get(url);
        do {
            if(username.equals("")) {
                System.out.println("Username:");
                username = new Scanner(System.in).nextLine();
            }
            if(password.equals("")) {
                System.out.println("Password:");
                Console console = System.console();
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
        } while(driver.getTitle().equals("SPIRE Logon"));
    }

//    private static String getUsername(Properties properties, File propertiesFile) {
//        if(properties.getProperty("username") != null) {
//            return properties.getProperty("username");
//        } else {
//            System.out.println("Username:");
//            String username = new Scanner(System.in).nextLine();
//            System.out.println("Save username? (y/n)");
//            if(new Scanner(System.in).nextLine().equals("y")) {
//                properties.setProperty("username", username);
//                storeProperties(properties, propertiesFile);
//            }
//            return username;
//        }
//    }

//    private static String getPassword(Properties properties, File propertiesFile) {
//        if(properties.getProperty("password") != null) {
//            return properties.getProperty("password");
//        } else {
//            System.out.println("Password:");
//            String password;
//            Console console = System.console();
//            if(console != null) {
//                password = new String(console.readPassword());
//            } else {
//                password = new Scanner(System.in).nextLine();
//            }
//            System.out.println("Save password? (y/n)");
//            if(new Scanner(System.in).nextLine().equals("y")) {
//                properties.setProperty("password", password);
//                storeProperties(properties, propertiesFile);
//            }
//            return password;
//        }
//    }

    public static WebDriver getWebDriver(Browser browser) {
        WebDriver driver = null;
        while (browser == Browser.NONE) {
            System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     browser = Browser.CHROME;   break;
                case 2:     browser = Browser.FIREFOX;  break;
                default:    break;
            }
        }
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
            case NONE:      break;
        }
        return driver;
    }

//    public static WebDriver getWebDriver(Properties properties, File propertiesFile) {
//        WebDriver driver = null;
//        int browserNum;
//        if (properties.getProperty("browser") != null) {
//            browserNum = Integer.valueOf(properties.getProperty("browser"));
//        } else {
//            do {
//                System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox");
//                browserNum = new Scanner(System.in).nextInt();
//                if (!(browserNum == 1 || browserNum == 2)) {
//                    browserNum = -1;
//                }
//            } while (browserNum == -1);
//            System.out.println("Save browser choice? (y/n)");
//            if (new Scanner(System.in).nextLine().equals("y")) {
//                setAndStoreProperties("browser", ""+browserNum, properties, propertiesFile);
//            }
//        }
//        if (browserNum == 1) {
//            if (IS_OS_WINDOWS) {
//                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_WIN32);
//            } else if (IS_OS_MAC) {
//                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_MAC64);
//            } else if (IS_OS_LINUX) {
//                System.setProperty("webdriver.chrome.driver", WebDriverExecutable.CHROME_LINUX64);
//            }
//            driver = new ChromeDriver();
//        } else if (browserNum == 2) {
//            if (IS_OS_WINDOWS) {
//                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_WIN64);
//            } else if (IS_OS_MAC) {
//                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_MACOS);
//            } else if (IS_OS_LINUX) {
//                System.setProperty("webdriver.gecko.driver", WebDriverExecutable.FIREFOX_LINUX64);
//            }
//            driver = new FirefoxDriver();
//        }
//        return driver;
//    }

//    public static void setAndStoreProperties(String key, String value, Properties properties, File propertiesFile) {
//        properties.setProperty(key, value);
//        storeProperties(properties, propertiesFile);
//    }

//    public static void storeProperties(Properties properties, File propertiesFile) {
//        if(!propertiesFile.exists()) {
//            try {
//                if(propertiesFile.createNewFile()) {
//                    //TODO: "FileNotFoundException (access is denied)" when writing to hidden file, works on normal file.
//                    // Files.setAttribute(propertiesFile.toPath(), "dos:hidden", true);
//                }
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            properties.store(new FileOutputStream(propertiesFile), "");
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//    }

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
