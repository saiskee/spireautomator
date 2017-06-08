package autohouser;

import spireautomator.UMass;

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

    // Step 1
    String s1TermSelect;
    String s1ProcessSelect;

    // Step 2
    String s2BuildingRadio;
    String s2ClusterRadio;
    String s2AreaRadio;
    String s2AllRadio;
    String s2BuildingSelect;
    String s2ClusterSelect;
    String s2AreaSelect;

    // Step 3
    String s3TypeRadio;
    String s3DesignRadio;
    String s3FloorRadio;
    String s3OptionRadio;
    String s3TypeSelect;
    String s3DesignSelect;
    String s3FloorSelect;
    String s3OptionSelect;

    // Step 4
    String s4NoneRadio;
    String s4RoomOpenRadio;
    String s4SuiteOpenRadio;
    String s4TypeRadio;
    String s4OpenDoubleRadio;
    String s4OpenTripleRadio;
    String s4RoomOpenSelect;
    String s4SuiteOpenSelect;
    String s4TypeSelect;
}
