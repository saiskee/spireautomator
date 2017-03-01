package autohouser;

public class Appointment {
    private String process;
    private int opportunities;

    public Appointment(String process, int opportunities) {
        this.process = process;
        this.opportunities = opportunities;
    }

    public String getProcess() {
        return process;
    }

    public int getOpportunities() {
        return opportunities;
    }
}
