package spireautomator;

import autoenroller.*;
import autohouser.RoomSearch;
import autohouser.SpireHousing;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

import java.io.*;
import java.net.URL;
import java.util.*;

import static org.apache.commons.lang3.SystemUtils.IS_OS_LINUX;
import static org.apache.commons.lang3.SystemUtils.IS_OS_MAC;
import static org.apache.commons.lang3.SystemUtils.IS_OS_WINDOWS;

/**
 * Provides logistical functions that are used throughout the
 * SPIRE enrolling and housing programs at different levels of depth.
 */
public class SpireAutomator {
    public enum OS {
        WIN, MAC, NIX;
        private static OS getOS() {
            OS result = null;
            if(IS_OS_WINDOWS) {
                result = WIN;
            } else if(IS_OS_MAC) {
                result = MAC;
            } else if(IS_OS_LINUX) {
                result = NIX;
            }
            return result;
        }
    }
    public enum Browser {
        CHROME, FIREFOX;
        private static Browser getBrowser(String input) {
            Browser result = null;
            switch(input.trim().toLowerCase()) {
                case "chrome":  result = CHROME;    break;
                case "firefox": result = FIREFOX;   break;
                case "gecko":   result = FIREFOX;   break;
                default:        break;
            }
            return result;
        }
        private static Browser promptBrowser() {
            Browser result = null;
            System.out.println("Web browser?\n1: Google Chrome\n2: Mozilla Firefox");
            switch(new Scanner(System.in).nextInt()) {
                case 1:     result = CHROME;   break;
                case 2:     result = FIREFOX;  break;
                default:    break;
            }
            return result;
        }
    }

    public static void main(String[] args) {
        OS os = OS.getOS();
        Browser browser = null;
        File driverPath = null;
        WebDriver driver = null;
        Automator automator = null;
        String username = null;
        String password = null;
        String term = null;
        File asciiArt = new File("asciiArt");

        if(args.length > 0) {
            // Process each command-line argument.
            for(String arg : args) {
                String[] argSplit = arg.split("=", 2);
                // Only parse the argument if it is splittable, such as "param=value"
                if(argSplit.length > 1) {
                    // Do not alter the input strings, fields may need to be exact (ex. password).
                    String param = argSplit[0];
                    String value = argSplit[1];
                    switch(param) {
                        case "automator":   switch(value) {
                            case "enroller":    automator = Automator.ENROLLER; break;
                            case "houser":      automator = Automator.HOUSER;   break;
                            default:            break;
                        }   break;
                        case "timeout":     try {
                            int timeout = Integer.valueOf(value);
                            if(timeout > 0) {
                                UMass.TIMEOUT_INTERVAL = timeout;
                            } else {
                                System.out.println("Timeout input may not be negative. Ignoring.");
                            }
                        } catch(NumberFormatException e) {
                            System.out.println("Timeout input must be a number. Ignoring.");
                        }   break;
                        case "verbose":     switch(value.trim().toLowerCase()) {
                            case "true":    UMass.VERBOSE = true;           break;
                            case "false":   UMass.VERBOSE = false;          break;
                            default:        break;
                        }   break;
                        case "driver_path": driverPath = new File(value);           break;
                        case "browser":     browser = Browser.getBrowser(value);    break;
                        case "url":         UMass.SPIRE_HOME_URL = value;           break;
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

        // If no valid preferred browser was provided, prompt for one.
        while (browser == null) {
            browser = Browser.promptBrowser();
        }
        // Check user-provided executable now that we have OS and browser preferences.
        if(!isDriverPathValid(os, browser, driverPath)) {
            // User input was invalid; check temporary directory to see if it contains a valid executable.
            File tempDir = getTemporaryDirectory();
            boolean driverFoundInTempDir = false;
            for(File tempDirFile: tempDir.listFiles()) {
                // If a file in the temporary directory is a valid executable, designate it.
                if(isDriverPathValid(os, browser, tempDirFile)) {
                    driverPath = tempDirFile;
                    driverFoundInTempDir = true;
                }
            }
            if(!driverFoundInTempDir) {
                driverPath = downloadExecutable(tempDir, os, browser);
            }
        }
        // Now set system properties and construct driver.
        switch(browser) {
            case CHROME:    System.setProperty("webdriver.chrome.driver", driverPath.getAbsolutePath());
                            driver = new ChromeDriver();
                            break;
            case FIREFOX:   System.setProperty("webdriver.gecko.driver", driverPath.getAbsolutePath());
                            driver = new FirefoxDriver();
                            break;
            default:        break;
        }

        // Go to the target website in the browser. Default is UMass SPIRE homepage.
        driver.get(UMass.SPIRE_HOME_URL);
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
        UMass.sleep(UMass.WAIT_INTERVAL);

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
            case HOUSER:        // Used to indicate if the automator should quit after making one housing change,
                                // or if it should keep searching for a better room forever (until quit).
                                boolean searchForever = false;
                                // Default to one search criteria configuration.
                                // If user calls for more configurations, a larger array will be created & copied into.
                                ArrayList<RoomSearch> searches = new ArrayList<>();
                                // Configure only the first search configuration with runtime arguments.
                                // Additional configurations need to be configured in runtime.
                                // Term is relevant to several automators so it is retrieved earlier.
                                searches.add(new RoomSearch());
                                searches.get(0).setStep1TermSelect(term);
                                // Reprocess each command-line argument to find arguments for houser.
                                for(String arg : args) {
                                    String[] argSplit = arg.split("=", 2);
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
                                            case "forever":     switch(value.toLowerCase()) {
                                                case "true":        searchForever = true;   break;
                                                case "false":       searchForever = false;  break;
                                                default:            break;
                                            }   break;
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
                                SpireHousing spireHousing = new SpireHousing(driver, searches, searchForever);
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

    private static boolean isDriverPathValid(OS os, Browser browser, File driverPath) {
        boolean result = false;
        // Checks that the path isn't null, that it exists, that it's a file, and that we have execute permissions.
        if(driverPath != null && driverPath.exists() && driverPath.isFile() && driverPath.canExecute()) {
            String ext = FilenameUtils.getExtension(driverPath.getName());
            switch(os) {
                case WIN:   if(ext.equals("exe")) {
                                result = isDriverBrowserValid(browser, driverPath.getName());
                            }   break;
                case MAC:   if(!ext.equals("exe")) {
                                result = isDriverBrowserValid(browser, driverPath.getName());
                            }   break;
                case NIX:   if(!ext.equals("exe")) {
                                result = isDriverBrowserValid(browser, driverPath.getName());
                            }   break;
            }
        } else {
            result = false;
        }
        return result;
    }

    private static boolean isDriverBrowserValid(Browser browser, String name) {
        boolean result = false;
        switch(browser) {
            case CHROME:    if(name.contains("chrome")) {
                                result = true;
                            }   break;
            case FIREFOX:   if(name.contains("firefox") || name.contains("gecko")) {
                                result = true;
                            }   break;
        }
        return result;
    }

    private static File downloadExecutable(File destDir, OS os, Browser browser) {
        WebDriverExecutable exec = WebDriverExecutable.getWebDriverExecutable(os, browser);
        File destFile = new File(destDir, FilenameUtils.getName(exec.getUrl()));
        UMass.verbosePrint("Downloading \""+exec.getUrl()+"\" to \""+destFile.getAbsolutePath()+"\"... ");
        try {
            // This library function takes care of all of the logistics of downloading from the internet.
            FileUtils.copyURLToFile(new URL(exec.getUrl()), destFile);
            if(destFile.exists()) {
                // Checks if it is a compressed file.
                switch(FilenameUtils.getExtension(destFile.getAbsolutePath())) {
                    case "zip": UMass.verbosePrint("extracting... ");
                                destFile = extractZipFile(destFile, destDir);
                                break;
                    case "gz":  UMass.verbosePrint("extracting... ");
                                destFile = extractTarGzFile(destFile, destDir);
                                break;
                    default:    break;
                }
                UMass.verbosePrintln("completed.");
            }
        } catch(IOException e) {
            UMass.verbosePrintln("failed.");
            UMass.verbosePrintln(e.getMessage());
            destFile = null;
        }
        return destFile;
    }

    private static File extractZipFile(File zip, File destDir) throws IOException {
        File result = null;
        int BUFFER = 2048;
        FileInputStream fin = new FileInputStream(zip);
        BufferedInputStream bin = new BufferedInputStream(fin);
        ZipArchiveInputStream zipIn = new ZipArchiveInputStream(bin);
        ZipArchiveEntry entry;
        while((entry = zipIn.getNextZipEntry()) != null) {
            if(entry.isDirectory()) {
                result = new File(destDir, entry.getName());
                result.mkdirs();
            } else {
                result = new File(destDir, entry.getName());
                FileOutputStream fos = new FileOutputStream(result);
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
                int count;
                byte data[] = new byte[BUFFER];
                while((count = zipIn.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.close();
            }
        }
        zipIn.close();
        return result;
    }

    private static File extractTarGzFile(File tarGz, File destDir) throws IOException {
        File result = null;
        int BUFFER = 2048;
        FileInputStream fin = new FileInputStream(tarGz);
        BufferedInputStream bin = new BufferedInputStream(fin);
        GzipCompressorInputStream gzIn = new GzipCompressorInputStream(bin);
        TarArchiveInputStream tarIn = new TarArchiveInputStream(gzIn);
        TarArchiveEntry entry;
        while((entry = tarIn.getNextTarEntry()) != null) {
            if(entry.isDirectory()) {
                result = new File(destDir, entry.getName());
                result.mkdirs();
            } else {
                result = new File(destDir, entry.getName());
                FileOutputStream fos = new FileOutputStream(result);
                BufferedOutputStream bos = new BufferedOutputStream(fos, BUFFER);
                int count;
                byte data[] = new byte[BUFFER];
                while((count = tarIn.read(data, 0, BUFFER)) != -1) {
                    bos.write(data, 0, count);
                }
                bos.close();
            }
        }
        tarIn.close();
        return result;
    }

    private static File getTemporaryDirectory() {
        // Windows: C:\Users\<USER>\AppData\Local\Temp\spireautomator
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "spireautomator");
        if (!tempDir.exists()) {
            UMass.verbosePrint("Making new temporary directory \"" + tempDir.getAbsolutePath() + "\"... ");
            // If the directory doesn't exist, then attempt to make a directory.
            if (!tempDir.mkdir()) {
                UMass.verbosePrintln("failed.");
                // If creating the directory failed, revert the returned File to null.
                tempDir = null;
            } else {
                UMass.verbosePrintln("completed.");
            }
        } else {
            UMass.verbosePrintln("Found temporary directory \""+tempDir.getAbsolutePath()+"\"");
        }
        return tempDir;
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
        System.out.println("\tjava -jar spireautomator.jar \"browser=chrome\" \"automator=enroller\" \"term=Fall 1863\"");
        System.out.println(getHeaderSeparator("GENERAL", separatorLength));
        System.out.println("The following are runtime arguments used universally by all automators:");
        System.out.println("\tdriver");
        System.out.println("\tverbose=[true, false]");
        System.out.println("\ttimeout=[>0]");
        System.out.println("\turl");
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
        System.out.println("\tforever=[true,false]");
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
