package houser;

import java.util.ArrayList;

public class RoomSearch {
    public enum Step2Radio {
        BUILDING,
        CLUSTER,
        AREA,
        ALL
    }
    public enum Step3Radio {
        TYPE,
        DESIGN,
        FLOOR,
        OPTION
    }
    public enum Step4Radio {
        NONE,
        ROOM_OPEN,
        SUITE_OPEN,
        TYPE,
        OPEN_DOUBLE,
        OPEN_TRIPLE
    }

    private Step2Radio step2Radio;
    private Step3Radio step3Radio;
    private Step4Radio step4Radio;
    private String step1TermSelect;
    private String step1ProcessSelect;
    private String step2Select;
    private String step3Select;
    private String step4Select;
    private ArrayList<Room> results;

    public RoomSearch() {
        this.step2Radio = null;
        this.step3Radio = null;
        this.step4Radio = null;
        this.step1TermSelect = "";
        this.step1ProcessSelect = "";
        this.step2Select = "";
        this.step3Select = "";
        this.step4Select = "";
        results = new ArrayList<Room>();
    }

    public Step2Radio getStep2Radio() {
        return step2Radio;
    }

    public Step3Radio getStep3Radio() {
        return step3Radio;
    }

    public Step4Radio getStep4Radio() {
        return step4Radio;
    }

    public String getStep1TermSelect() {
        return step1TermSelect;
    }

    public String getStep1ProcessSelect() {
        return step1ProcessSelect;
    }

    public String getStep2Select() {
        return step2Select;
    }

    public String getStep3Select() {
        return step3Select;
    }

    public String getStep4Select() {
        return step4Select;
    }

    public ArrayList<Room> getResults() {
        return results;
    }

    public void setStep2Radio(Step2Radio step2Radio) {
        this.step2Radio = step2Radio;
    }

    public void setStep3Radio(Step3Radio step3Radio) {
        this.step3Radio = step3Radio;
    }

    public void setStep4Radio(Step4Radio step4Radio) {
        this.step4Radio = step4Radio;
    }

    public void setStep1TermSelect(String step1TermSelect) {
        this.step1TermSelect = step1TermSelect;
    }

    public void setStep1ProcessSelect(String step1ProcessSelect) {
        this.step1ProcessSelect = step1ProcessSelect;
    }

    public void setStep2Select(String step2Select) {
        this.step2Select = step2Select;
    }

    public void setStep3Select(String step3Select) {
        this.step3Select = step3Select;
    }

    public void setStep4Select(String step4Select) {
        this.step4Select = step4Select;
    }

    public void setResults(ArrayList<Room> results) {
        this.results = results;
    }
}