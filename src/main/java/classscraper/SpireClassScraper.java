package classscraper;

import org.apache.commons.lang3.StringEscapeUtils;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import spire.UMass;

import java.util.*;
import java.util.logging.Logger;

public class SpireClassScraper {
	private final static Logger LOGGER = Logger.getLogger("spireautomator.enroller");
	private WebDriver driver;
	private String term;
	private String courseSubject = "Comparative Literature";

	public SpireClassScraper(WebDriver driver) {
		this.driver = driver;
		this.term = "";
		
	}

	public SpireClassScraper(WebDriver driver, String term) {
		this.driver = driver;
		this.term = term;
		
	}

	public SpireClassScraper(WebDriver driver, String term, String courseSubject) {
		this(driver, term);
		this.courseSubject = courseSubject;
	}

	public void run() {
		// Click on Button that goes to class search page
		LOGGER.info("Clicking id selector \"" + UMass.CLASS_SEARCH_BUTTON_SELECTOR_ID + "\"");
		UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_BUTTON_SELECTOR_ID)).click();
		// Select appropriate term for class search
		// new Select(UMass.waitForElement(driver,
		// By.cssSelector(UMass.CLASS_SEARCH_TERM_SELECTOR))).selectByVisibleText("2017
		// Spring");
		// The above (new Select...) does not work because of the term syntax, but it
		// would be optimal to get it working, below is work around
		LOGGER.info("Selecting Term, Selecting css selector \"" + UMass.CLASS_SEARCH_TERM_SELECTOR + "\"");
		UMass.waitForElement(driver, By.cssSelector(UMass.CLASS_SEARCH_TERM_SELECTOR)).click();
		UMass.waitForElement(driver, By.cssSelector(UMass.CLASS_SEARCH_TERM_SELECTOR)).sendKeys("2019 Spring",
				Keys.RETURN);
		// Select course subject, default for now is Computer Science
		LOGGER.info("Selecting Major, Selecting id selector \"" + UMass.CLASS_SEARCH_COURSE_SUBJECT_SELECTOR_ID + "\"");
		new Select(UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_COURSE_SUBJECT_SELECTOR_ID)))
				.selectByVisibleText(courseSubject);
		// Enter Course Number, for maximum scraping ability, entering "greater than or
		// equal to" 100
		LOGGER.info("Selecting Course Number, Selecting id selector \"" + UMass.CLASS_SEARCH_COURSE_NUMBER_SELECTOR_ID
				+ "\"");
		new Select(UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_COURSE_NUMBER_SELECTOR_ID)))
				.selectByVisibleText("greater than or equal to");
		UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_COURSE_NUMBER_INPUT_ID)).sendKeys("100");
		// Unselect Search Box for "Open Classes Only"
		UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_OPEN_CLASSES_ONLY_CHECKBOX_ID)).click();
		// Hit Search Button
		LOGGER.info("Searching courses for" + term + " term and " + courseSubject + " subject");
		UMass.waitForElement(driver, By.id(UMass.CLASS_SEARCH_SEARCH_BUTTON_ID)).click();
		
		classSearchScrape(driver);

	}

	private void classSearchScrape(WebDriver driver) {
		// Parse Search Results Page
		for (int courseIndex = 0; courseIndex <= 75; courseIndex++) {
			// loops through all the courses on the page
			LOGGER.info("Parsing for " + courseIndex + "th Course Title");
			// Gets Course Title ("COMPSCI 105 Computer Literacy")
			String courseTitle = UMass
					.waitForElement(driver,
							By.xpath("//div[@id='" + UMass.SEARCH_RESULTS_COURSE_NAME_DIV_ID + courseIndex
									+ "']//span[@id='" + UMass.SEARCH_RESULTS_COURSE_NAME_SPAN_ID + courseIndex + "']"))
					.getText();
			// Create new course for every course found above
			Course course = new Course();

			int subjectIndex = 0;
			int courseNumIndex = 0;
			int courseNameIndex = 0;

			for (int splitIndex = 0, k = 0; splitIndex < courseTitle.length() && k < 3; splitIndex++) {
				if ((" ").charAt(0) == courseTitle.charAt(splitIndex)) {

					if (k == 0)
						subjectIndex = splitIndex;
					if (k == 1)
						courseNumIndex = splitIndex;
					if (k == 2)
						courseNameIndex = splitIndex;
					k++;
				}
			}
			// Course Subject: "COMPSCI"
			String courseSubject = courseTitle.substring(0, subjectIndex);
			// Course Num: "105"
			String courseNum = courseTitle.substring(subjectIndex + 2, courseNameIndex);
			// Course Name "Computer Literacy"
			String courseName = courseTitle.substring(courseNameIndex);
			LOGGER.info("Setting Course Name " + courseName + " and Course Num " + courseNum + " and Course Subject "
					+ courseSubject);
			course.setCoursename(courseName);
			course.setCoursenum(courseNum);
			course.setSubject(courseSubject);
			String genEd = "";

			try {
				// Gened: "R2"
				genEd = driver.findElement(By.id(UMass.SEARCH_RESULTS_GENED_SPAN_ID + courseIndex)).getText();
				LOGGER.info("Parsing for GenEd for " + courseIndex + "th class: " + genEd);
			} catch (NoSuchElementException e) {
				LOGGER.info(courseName + " does not fulfill any genEds");
			}
			course.setGened(genEd);

			// Finds courseIndex'th GROUPBOX (Which contains all the sections)
			WebElement courseSection = driver
					.findElement(By.xpath("//div[@id = 'win0divDERIVED_CLSRCH_GROUPBOX1$133$$" + courseIndex + "']"));
			LOGGER.info("Course " + courseIndex + " : " + courseName);
			//System.out.println(courseSection.getText());
			course.setSections(parseAndCreateSections(courseSection));
			
			System.out.println(course.getSections());

		}

	}

	/**
	 * @param course
	 *            The Course which you are finding sections for
	 * @param courseSection
	 *            The WebElement "Group Box" in which you are looking for sections
	 */
	private List<CourseSection> parseAndCreateSections(WebElement courseSection) {
		String sectionString = courseSection.getText();
		//System.out.println(sectionString);
		List<String> groupBoxArray = new ArrayList<String>(Arrays.asList(sectionString.split("\n")));

		groupBoxArray.removeAll(new ArrayList<String>(Arrays.asList("Group box", "Section", "Status", "Enroll", "Cap",
				"select class", "Session", "Days & Times Room Instructor Topic Restrictions/Notes")));
		groupBoxArray.remove(0); // Removes "1 of 12 Last First" -- i think these are the arrow boxes
		
		List<CourseSection> sections = new ArrayList<CourseSection>();
		while (!groupBoxArray.isEmpty()) {
		sections.add(parseGroupBoxArray(groupBoxArray, groupBoxArray.get(1).equals("Units")));
		}
		return sections;
	}

	/**
	 * Helper method for parseAndCreateSections
	 * @return CourseSection that is parsed and filled out
	 * @param groupBoxArray
	 *            String List of Group Box Elements that must be parsed
	 * @param hasUnits
	 *            if the section has a units count or not
	 */
	private CourseSection parseGroupBoxArray(List<String> groupBoxArray, boolean hasUnits) {
		/*
		 * if sessionarray has Units in [1], [Enroll cap, "Units", Class Num Long
		 * String, Units, Enrolled, Session, Meeting Time, Room, Instructor, Topic
		 * (Always TBA?)]
		 * 
		 * if array does not have Units [Enroll cap, Class Num Long String, Enrolled,
		 * Session, Meeting Time, Room, Instructor, Topic (Always TBA?)]
		 */
		
		
			CourseSection section = new CourseSection();

			if (hasUnits) {
				//if "Units" is there
				groupBoxArray.remove(1);
				section.enrollmentCap = Integer.parseInt(groupBoxArray.remove(0));
				// "01-LEC(12830)"
				String info = groupBoxArray.remove(0);
				int dashIndex = info.indexOf('-'), paranthesesIndex = info.indexOf('(');
				// "01"
				section.setSectionnum(info.substring(0, dashIndex));
				// "LEC"
				section.setSectionType(SectionType.valueOf(info.substring(dashIndex + 1, paranthesesIndex)));
				// "12830"
				section.setSectionid(Integer.parseInt(info.substring(paranthesesIndex + 1, info.length() - 1)));
				section.setUnits(groupBoxArray.remove(0));

			} else {

				section.enrollmentCap = Integer.parseInt(groupBoxArray.remove(0));
				// "01-LEC(12830)"
				String info = groupBoxArray.remove(0);
				int dashIndex = info.indexOf('-'), paranthesesIndex = info.indexOf('(');
				// "01"
				section.setSectionnum(info.substring(0, dashIndex));
				// "LEC"
				section.setSectionType(SectionType.valueOf(info.substring(dashIndex + 1, paranthesesIndex)));
				// "12830"
				section.setSectionid(Integer.parseInt(info.substring(paranthesesIndex + 1, info.length() - 1)));
			}
			section.setEnrolled(Integer.parseInt(groupBoxArray.remove(0)));
			if (groupBoxArray.get(0).equals("Combined Section")) {groupBoxArray.remove(0);}
			section.setSession(groupBoxArray.remove(0));
			section.setMeetingTime(groupBoxArray.remove(0));
			section.setRoom(groupBoxArray.remove(0));
			String instructor = groupBoxArray.get(0);
			if(instructor.charAt(instructor.length()-1) == ',') {
				section.setInstructor(groupBoxArray.remove(0).substring(0, instructor.length()-1) + groupBoxArray.remove(0));
			}
			else {
			section.setInstructor(groupBoxArray.remove(0));
			}
			section.setTopic(groupBoxArray.remove(0));
			if(!groupBoxArray.isEmpty() && groupBoxArray.get(0).equals("No Restrictions/Notes")) {groupBoxArray.remove(0);}
		
			return section;
	}
	

}
