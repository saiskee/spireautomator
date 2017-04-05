package autohouser;

/**
 * An implementation of an open room shown as
 * a search result in the SPIRE housing portal.
 */
//TODO: Delete this class when doubly-linked-nested-hash-map structure is functional.
public class RoomString {
    private String building;
    private String number;
    private String design;
    private String type;

    public RoomString(String building, String number, String design, String type) {
        this.building = building;
        this.number = number;
        this.design = design;
        this.type = type;
    }

    /**
     * Returns the building that this room is in.
     * Building name is in lower case.
     * @return  Lower-case String of the room's building.
     */
    public String getBuilding() {
        return building;
    }

    public String getNumber() {
        return number;
    }

    /**
     * Returns the layout of the room.
     * Example layouts include "corner", "z room", "standard"
     * @return  Lower-case String of the room's design/layout.
     */
    public String getDesign() {
        return design;
    }

    /**
     * Returns the type of the room.
     * Example types include "double", "triple", "single"
     * @return  Lower-case String of the amount of people the room houses.
     */
    public String getType() {
        return type;
    }
}
