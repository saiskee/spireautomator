package classscraper;

import java.util.ArrayList;
import java.util.List;

public class Course {
	private List<CourseSection> sections = new ArrayList<>();
	public String courseName;
	public String courseSubject;
	public String courseNum;
	public int courseID;
	public String genEd;
	
	
	
	public Course() {
		
	}

	public Course(String name, String subject, String coursenum, int id, String gened) {
		this.courseName = name;
		this.courseSubject = subject;
		this.courseNum = coursenum;
		this.genEd = gened;
	}
	
	public int getCourseid() {
		return courseID;
	}

	public void setCourseid(int courseid) {
		this.courseID = courseid;
	}
	
	
	public String getCoursename() {
		return courseName;
	}

	public void setCoursename(String coursename) {
		this.courseName = coursename;
	}

	public String getSubject() {
		return courseSubject;
	}

	public void setSubject(String subject) {
		this.courseSubject = subject;
	}

	public String getCoursenum() {
		return courseNum;
	}

	public void setCoursenum(String coursenum) {
		this.courseNum = coursenum;
	}

	public String getGened() {
		return genEd;
	}

	public void setGened(String gened) {
		this.genEd = gened;
	}

	public void setSections(List<CourseSection> sections) {
		this.sections = sections;
	}

	public void addCourseSection(CourseSection section) {
		sections.add(section);
	}
	public List<CourseSection> getSections(){
		return sections;
	}
	
}
