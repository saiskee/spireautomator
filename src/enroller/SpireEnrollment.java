package enroller;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import spire.UMass;

import java.util.*;
import java.util.logging.Logger;

/**
 * This class automates the enrollment process on SPIRE.
 * The current schedule and shopping cart are stored as
 * {@link Map}s in order to prevent duplicate Lectures
 * that may come up when combining hardcoded and parsed
 * Lectures. Keys are class IDs and return Lectures.
 */
public class SpireEnrollment {
    private final static Logger LOGGER = Logger.getLogger("spireautomator.enroller");
    private WebDriver driver;
    private String term;
    private Map<String, Lecture> currentSchedule;
    private Map<String, Lecture> shoppingCart;
    private ArrayList<Action> actions;

    public SpireEnrollment(WebDriver driver) {
        this.driver = driver;
        this.term = "";
        this.currentSchedule = new HashMap<>();
        this.shoppingCart = new HashMap<>();
        this.actions = new ArrayList<>();
    }

    public SpireEnrollment(WebDriver driver, ArrayList<Action> actions) {
        this(driver);
        this.actions = actions;
    }

    public SpireEnrollment(WebDriver driver, String term, Map<String, Lecture> currentSchedule, Map<String, Lecture> shoppingCart, ArrayList<Action> actions) {
        this(driver);
        this.term = term;
        this.currentSchedule = currentSchedule;
        this.shoppingCart = shoppingCart;
        this.actions = actions;
    }

    /**
     * This function runs the SpireEnrollment automation from the point of
     * entering the enrollment portal to conclusion. It is called by the
     * {@link spire.SpireAutomator} controller after instantiation
     * and adding the schedule, shopping cart, and actions.
     */
    public void run() {
        // Click on the link that goes to enrollment.
        LOGGER.info("Clicking CSS selector \""+UMass.ENROLLMENT_LINK_SELECTOR+"\"");
        UMass.waitForElement(driver, By.cssSelector(UMass.ENROLLMENT_LINK_SELECTOR)).click();
        // Check if SPIRE first needs to have a term selected.
        if(UMass.checkSelectTerm(this)) {
            LOGGER.info("Need to select term.");
            UMass.selectTerm(driver, this.term);
        }

        // Currently in the shopping cart. There may be some hardcoded Classes
        // used to create Actions, but this parses the actual current schedule and
        // shopping cart to prevent discrepancies. Duplicate Classes are prevented
        // with the use of a Map with keys of class IDs.

        // When there are no enrolled classes, this table will not exist, and will not be parsed.
        if(UMass.isElementFound(driver, UMass.TIMEOUT_INTERVAL, By.cssSelector(UMass.CART_SCHEDULE_SELECTOR))) {
            LOGGER.info("About to parse the current schedule.");
            currentSchedule.putAll(parseCurrentSchedule());
        }
        LOGGER.info("About to parse the shopping cart.");
        shoppingCart.putAll(parseShoppingCart());
        printCurrentSchedule();
        printShoppingCart();
        if(actions.isEmpty()) {
            // Create Actions based on current schedule and shopping cart.
            LOGGER.info("Prompting user to create actions during runtime.");
            actions = createActions();
        }
        printActions();

        LOGGER.info("Beginning automated refresh.");
        long previousTime = System.currentTimeMillis();
        while(!actions.isEmpty()) {
            // Reload current shopping cart page at least every 5 seconds; checked after loop.
            driver.get(driver.getCurrentUrl());
            // For each Action, check Conditions, meet if able, perform if able.
            for(Action action : actions) {
                if(action.allConditionsMet()) {
                    LOGGER.info("All conditions met for action \""+action.toString()+"\", performing action... ");
                    if(action.perform(this)) {
                        LOGGER.info("Successfully performed action \""+action.toString()+"\"");
                        // If the successful performance of this Action satisfies other Actions, mark them as such.
                        action.setSatisfied(true);
                        action.satisfyOtherActions();
                        // After every successful action performance, refresh the schedule and shopping cart.
                        // May remove hardcoded Classes if they do not exist in actual schedule or cart.
                        currentSchedule = parseCurrentSchedule();
                        shoppingCart = parseShoppingCart();
                        printCurrentSchedule();
                        printShoppingCart();
                        printActions();
                    } else {
                        LOGGER.info("Failed to perform action \""+action.toString()+"\"");
                    }
                }
            }
            // Remove all satisfied actions.
            for(Action action: actions) {
                if(action.isSatisfied()) {
                    LOGGER.info("Removing satisfied action \""+action.toString()+"\" from action list.");
                    actions.remove(action);
                }
            }
            if ((System.currentTimeMillis() - previousTime) < UMass.LOAD_INTERVAL) {
                LOGGER.info("Not enough time has passed since last page load; sleeping for "+
                        UMass.LOAD_INTERVAL+" milliseconds.");
                UMass.sleep(UMass.LOAD_INTERVAL);
            }
            // Uncomment this line to show the number of seconds since the last refresh, on every refresh.
            // System.out.println("Refreshing "+(System.currentTimeMillis()-previousTime)/1000+" seconds later...");
            previousTime = System.currentTimeMillis();
        }
        LOGGER.info("All actions performed.");
        printCurrentSchedule();
    }

    private void printCurrentSchedule() {
        System.out.println("Current schedule:");
        for(Class c : currentSchedule.values()) {
            LOGGER.config("Printing class in schedule \""+c+"\"");
            System.out.println(c.toString());
        }
        System.out.println();
    }

    private void printShoppingCart() {
        System.out.println("Shopping cart:");
        for(Lecture l : shoppingCart.values()) {
            LOGGER.config("Printing lecture in shopping cart \""+l+"\"");
            System.out.println(l.toString());
        }
        System.out.println();
    }

    private void printActions() {
        System.out.println("Actions:");
        for(Action a : actions.toArray(new Action[0])) {
            LOGGER.config("Printing action \""+a+"\"");
            System.out.println(a.toString());
        }
        System.out.println();
    }

    private Map<String, Lecture> parseCurrentSchedule() {
        Map<String, Lecture> schedule = new HashMap<>();
        // Gets the size of the current schedule table on the shopping cart page
        // and iterates over each row. Skips the first row; it's just header labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.CART_SCHEDULE_SELECTOR)).findElements(By.tagName("tr")).size(); row++) {
            // Assume enrollment status is always in 7th column of each row.
            if(UMass.findElementCartSchedule(driver, row, 7).getAttribute("innerHTML").contains(UMass.ENROLLED_IMAGE)) {
                // Lecture rows' names are hyperlink classes. Discussions are hyperlink-disabled classes.
                if(UMass.findElementCartSchedule(driver, row, 1).getAttribute("innerHTML").contains(UMass.HYPERLINK_CLASS_HTML)) {
                    // Create new Lecture by parsing text from this row.
                    String[] lectureInfo = UMass.findElementCartSchedule(driver, row, 1).getText().split("\n");
                    Lecture scheduleLecture = new Lecture(lectureInfo[0], lectureInfo[1]);
                    scheduleLecture.setDescription(UMass.findElementCartSchedule(driver, row, 2).getText());
                    schedule.put(scheduleLecture.getClassId(), scheduleLecture);
                } else if(UMass.findElementCartSchedule(driver, row, 1).getAttribute("innerHTML").contains(UMass.HYPERLINKDISABLED_CLASS_HTML)) {
                    String[] discussionInfo = UMass.findElementCartSchedule(driver, row, 1).getText().split("\n");
                    Discussion scheduleDiscussion = new Discussion(discussionInfo[0], discussionInfo[1]);
                    scheduleDiscussion.setDescription(UMass.findElementCartSchedule(driver, row, 2).getText());
                    // Finds the Lecture of the same name as Discussion and sets as enrolled Discussion.
                    for(Lecture l : schedule.values()) {
                        if(l.getName().equals(scheduleDiscussion.getName())) {
                            l.setEnrolledDiscussion(scheduleDiscussion);
                            break;
                        }
                    }
                }
            }
        }
        // Goes to each Lecture's edit tab and parses the other Discussion sections.
        // A Lecture that is supposed to have Discussions will already contain one.
        // A Lecture with no Discussions has none at all.
        for(Lecture l : schedule.values()) {
            if(l.hasDiscussions()) {
                l.addDiscussions(getOtherScheduleDiscussions(l));
            }
        }
        return schedule;
    }

    private ArrayList<Discussion> getOtherScheduleDiscussions(Lecture lecture) {
        ArrayList<Discussion> otherDiscussions = new ArrayList<>();
        LOGGER.info("Clicking the \"Edit\" tab on the top of the enrollment portal of SPIRE.");
        UMass.findElementTab(driver, "edit").click();
        if(UMass.checkSelectTerm(this)) {
            LOGGER.info("SPIRE needs a term to be selected.");
            UMass.selectTerm(driver, this.term);
        }
        LOGGER.info("Selecting \""+lecture.getClassId()+"\" in dropdown CSS selector \""+UMass.ENROLLED_DROPDOWN_SELECTOR+"\"");
        new Select(UMass.waitForElement(driver, By.cssSelector(UMass.ENROLLED_DROPDOWN_SELECTOR))).selectByValue(lecture.getClassId());
        driver.findElement(By.cssSelector(UMass.EDIT_CONFIRM_STEP_1_SELECTOR)).click();
        // Waits for the discussions table to load, then iterates over all Discussions, regardless of open/closed.
        LOGGER.info("Iterating over all discussions in CSS selector \""+UMass.DISCUSSIONS_TABLE_SELECTOR+"\"");
        for(int i = 1; i < UMass.waitForElement(driver, By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR))
                .findElements(By.tagName("tr")).size(); i++) {
            Discussion otherDiscussion = new Discussion();
            otherDiscussion.setName(lecture.getName());
            otherDiscussion.setDescription(lecture.getDescription());
            otherDiscussion.setClassId(UMass.findElementDiscussionTable(driver, i, 2).getText());
            otherDiscussion.setSection(UMass.findElementDiscussionTable(driver, i, 3).getText());
            otherDiscussions.add(otherDiscussion);
            LOGGER.config("Discussion found \""+otherDiscussion.getClassId()+"\"");
        }
        LOGGER.info("Going back to \"Add\" tab on the top of the enrollment portal of SPIRE.");
        UMass.findElementTab(driver, "add").click();
        // Check if SPIRE first needs to have a term selected.
        if(UMass.checkSelectTerm(this)) {
            LOGGER.info("SPIRE needs a term to be selected.");
            UMass.selectTerm(driver, this.getTerm());
        }
        return otherDiscussions;
    }

    private Map<String, Lecture> parseShoppingCart() {
        Map<String, Lecture> cart = new HashMap<>();
        // Subtract 2 from table length because first 2 rows are headers and labels.
        for(int row = 1; row < UMass.waitForElement(driver, By.cssSelector(UMass.CART_SHOPPING_SELECTOR)).findElements(By.tagName("tr")).size()-2; row++) {
            // In the shopping cart, Lectures have checkboxes and Discussions do not.
            if(UMass.findElementShoppingCart(driver, row, 1).getAttribute("innerHTML").contains(UMass.CART_CHECKBOX_HTML)) {
                String[] lectureInfo = UMass.findElementShoppingCart(driver, row, 2).getText().split("\n");
                Lecture cartLecture = new Lecture(lectureInfo[0], lectureInfo[1]);
                //Goes into the Lecture to fetch the description.
                UMass.findElementShoppingCart(driver, row, 2).findElement(By.className(UMass.HYPERLINK_CLASS)).click();
                // Gets the whole Lecture name and splits by the dash (with spaces) for description.
                cartLecture.setDescription(UMass.waitForElement(driver, By.cssSelector(UMass.LECTURE_DESC_SELECTOR)).getText().split(" - ")[1]);
                // While we're on this Lecture's page, check if there are Discussions to add.
                if (driver.findElements(By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR)).size() > 0) {
                    for (int i = 1; i < driver.findElement(By.cssSelector(UMass.DISCUSSIONS_TABLE_SELECTOR)).findElements(By.tagName("tr")).size(); i++) {
                        Discussion discussion = new Discussion();
                        discussion.setName(cartLecture.getName());
                        discussion.setDescription(cartLecture.getDescription());
                        discussion.setClassId(UMass.findElementDiscussionTable(driver, i, 2).getText());
                        discussion.setSection(UMass.findElementDiscussionTable(driver, i, 3).getText());
                        cartLecture.addDiscussion(discussion);
                    }
                }
                UMass.findElementTab(driver, "add").click();
                // Check if SPIRE first needs to have a term selected.
                if(UMass.checkSelectTerm(this)) {
                    UMass.selectTerm(driver, this.getTerm());
                }
                cart.put(cartLecture.getClassId(),cartLecture);
            }
        }
        return cart;
    }

    private ArrayList<Action> createActions() {
        ArrayList<Action> actions = new ArrayList<>();
        Scanner s = new Scanner(System.in);
        String input;
        System.out.println("Create basic Actions (Actions with Conditions must be hardcoded):");
        do {
            System.out.println("What kind of action do you want to create? Enter one of the following:");
            System.out.println("\"add\", \"drop\", \"edit\", \"swap\", \"done\"");
            input = s.nextLine();
            switch(input) {
                case "add":     Add add = new Add();
                                System.out.println("Select the class to add:");
                                add.setLectureToAdd(selectLecture(getShoppingCart()));
                                System.out.println("Select the discussion to add:");
                                add.setDiscussionToAdd(selectDiscussion(add.getLectureToAdd().getDiscussions()));
                                getActions().add(add);
                                break;
                case "drop":    Drop drop = new Drop();
                                System.out.println("Select the class to drop:");
                                drop.setLectureToDrop(selectLecture(getCurrentSchedule()));
                                getActions().add(drop);
                                break;
                case "edit":    Edit edit = new Edit();
                                System.out.println("Select the class to edit:");
                                edit.setLectureToEdit(selectLecture(getCurrentSchedule()));
                                do {
                                    System.out.println("Select the discussion to edit into:");
                                    Discussion d = selectDiscussion(edit.getLectureToEdit().getDiscussions());
                                    if(!d.equals(edit.getLectureToEdit().getEnrolledDiscussion())) {
                                        edit.setDiscussionToAdd(d);
                                    } else {
                                        System.out.println("Already enrolled in this discussion.");
                                    }
                                } while(edit.getDiscussionToAdd() == null);
                                getActions().add(edit);
                                break;
                case "swap":    Swap swap = new Swap();
                                System.out.println("Select the class to drop:");
                                swap.setLectureToDrop(selectLecture(getCurrentSchedule()));
                                System.out.println("Select the class to add:");
                                swap.setLectureToAdd(selectLecture(getShoppingCart()));
                                System.out.println("Select the discussion to add:");
                                swap.setDiscussionToAdd(selectDiscussion(swap.getLectureToAdd().getDiscussions()));
                                getActions().add(swap);
                                break;
                case "done":    break;
                default:        System.out.println("Invalid input.");
            }
        } while(!input.equals("done"));
        return actions;
    }

    private Lecture selectLecture(Map<String, Lecture> lectures) {
        for(Lecture l : lectures.values()) {
            System.out.println(l.getClassId()+": "+l.getNameAndDescription());
        }
        Lecture result;
        do {
            System.out.println("Enter the class ID:");
            result = lectures.get(new Scanner(System.in).nextLine());
        } while(result == null);
        return result;
    }

    private Discussion selectDiscussion(Map<String, Discussion> discussions) {
        for(Discussion d : discussions.values()) {
            System.out.println(d.getClassId()+"+ "+d.getNameAndDescription());
        }
        Discussion result;
        do {
            System.out.println("Enter the class ID:");
            result = discussions.get(new Scanner(System.in).nextLine());
        } while(result == null);
        return result;
    }

    public WebDriver getDriver() {
        return driver;
    }

    public String getTerm() {
        return term;
    }

    public Map<String, Lecture> getCurrentSchedule() {
        return currentSchedule;
    }

    public Map<String, Lecture> getShoppingCart() {
        return shoppingCart;
    }

    public ArrayList<Action> getActions() {
        return actions;
    }
}
