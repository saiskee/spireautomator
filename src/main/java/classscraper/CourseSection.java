package classscraper;

public class CourseSection {
	public SectionType sectionType;
	public String instructor;
	public String room;
	public String session;
	public String meetingTime;
	public String topic;
	public int sectionid; // 12381
	public String sectionnum;//99-LN or 01 
	public String units;
	public int enrollmentCap;
	public int enrolled;
	
	public CourseSection(String sectionnum, int sectionID, SectionType sectionType) {
		this.sectionnum = sectionnum;
		this.sectionid = sectionID;
		this.sectionType = sectionType;
		this.units = "";
	}
	public CourseSection() {
		
	}
	public String getUnits() {
		return units;
	}
	public void setUnits(String units) {
		this.units = units;
	}
	public SectionType getSectionType() {
		return sectionType;
	}
	public void setSectionType(SectionType sectionType) {
		this.sectionType = sectionType;
	}
	public String getInstructor() {
		return instructor;
	}
	public void setInstructor(String instructor) {
		this.instructor = instructor;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public String getMeetingTime() {
		return meetingTime;
	}
	public void setMeetingTime(String meetingTime) {
		this.meetingTime = meetingTime;
	}
	public String getTopic() {
		return topic;
	}
	public void setTopic(String topic) {
		this.topic = topic;
	}
	public int getSectionid() {
		return sectionid;
	}
	public void setSectionid(int sectionid) {
		this.sectionid = sectionid;
	}
	public String getSectionnum() {
		return sectionnum;
	}
	public void setSectionnum(String sectionnum) {
		this.sectionnum = sectionnum;
	}
	public int getEnrollmentCap() {
		return enrollmentCap;
	}
	public void setEnrollmentCap(int enrollmentCap) {
		this.enrollmentCap = enrollmentCap;
	}
	public int getEnrolled() {
		return enrolled;
	}
	public void setEnrolled(int enrolled) {
		this.enrolled = enrolled;
	}
	public String getSession() {
		return session;
	}
	public void setSession(String session) {
		this.session = session;
	}
	@Override
	public String toString() {
		return "CourseSection [sectionType=" + sectionType + ", instructor=" + instructor + ", room=" + room
				+ ", meetingTime=" + meetingTime + ", topic=" + topic + ", sectionid=" + sectionid + ", sectionnum="
				+ sectionnum + ", units=" + units + ", enrollmentCap=" + enrollmentCap + ", enrolled=" + enrolled + "]";
	}
	

}
