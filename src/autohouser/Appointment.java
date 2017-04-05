package autohouser;

import spireautomator.UMass;

import java.text.ParseException;
import java.util.Date;

public class Appointment {
    private String process;
    private Date startDateTime;
    private Date endDateTime;
    private int opportunities;

    public Appointment(String process, int opportunities) {
        this.process = process;
        this.opportunities = opportunities;
    }

    public Appointment(String process, String startDateTime, String endDateTime, int opportunities) {
        this.process = process;
        try {
            this.startDateTime = UMass.dateTimeFormat.parse(startDateTime);
            this.endDateTime = UMass.dateTimeFormat.parse(endDateTime);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        this.opportunities = opportunities;
    }

    public Appointment(String process, String startDateTime, String endDateTime, String opportunities) {
        this.process = process;
        try {
            this.startDateTime = UMass.dateTimeFormat.parse(startDateTime);
            this.endDateTime = UMass.dateTimeFormat.parse(endDateTime);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        this.opportunities = UMass.tryToInt(opportunities);
    }

    public Appointment(String process, String startDate, String startTime, String endDate, String endTime, int opportunities) {
        this.process = process;
        try {
            this.startDateTime = UMass.dateTimeFormat.parse(startDate+" "+startTime);
            this.endDateTime = UMass.dateTimeFormat.parse(endDate+" "+endTime);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        this.opportunities = opportunities;
    }

    public Appointment(String process, String startDate, String startTime, String endDate, String endTime, String opportunities) {
        this.process = process;
        try {
            this.startDateTime = UMass.dateTimeFormat.parse(startDate+" "+startTime);
            this.endDateTime = UMass.dateTimeFormat.parse(endDate+" "+endTime);
        } catch(ParseException e) {
            e.printStackTrace();
        }
        this.opportunities = UMass.tryToInt(opportunities);
    }

    public String getProcess() {
        return process;
    }

    public int getOpportunities() {
        return opportunities;
    }

    public boolean isActive() {
        Date now = new Date();
        return now.after(startDateTime) && now.before(endDateTime) && getOpportunities() > 0;
    }

    public String getStartToEnd() {
        return startDateTime.toString()+" to "+endDateTime.toString();
    }

    public String getId() {
        return process.trim().toLowerCase().replace(" ", "").replace("-", "").replace("_", "")+UMass.SEPARATOR+opportunities;
    }

    public String toString() {
        return process;
    }
}
