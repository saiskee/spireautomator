package autohouser;

import spireautomator.UMass;

import java.util.Map;

/**
 * Rooms have a pointer "upwards" to its Building
 * which points "upwards" to its Residential Area.
 * This is set by a Building when the Room is mapped to one.
 * Rooms also have enumerated attributes for the design and type.
 * A Room ID follows the format
 * "BuildingID-RoomNumberNormalized" which derives to
 * "ResidentialAreaNameNormalized-BuildingNameNormalized-RoomNumberNormalized".
 */
public class Room {
    private Building building;
    private String number;
    private DesignEnum design;
    private TypeEnum type;

    public Room() {
        this.building = null;
        this.number = null;
        this.design = null;
        this.type = null;
    }

    public Room(Building building, String number, DesignEnum design, TypeEnum type) {
        this();
        this.building = building;
        this.number = number;
        this.design = design;
        this.type = type;
    }

    public Room(Map<String, ResidentialArea> residentialAreas, String buildingInput, String numberInput, String designInput, String typeInput) {
        this();
        Building building = null;
//        this.building = new Building(building);
//        this.number = new Number(number);
//        this.design = new Design(design);
//        this.type = new Type(type);
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public String getId() {
        return Room.getId(building.getArea().getName(), building.getName(), this.getNumber());
    }

    public static String getId(String area, String building, String number) {
        return Building.getId(area, building)+UMass.SEPARATOR+number.trim().toLowerCase().replace(" ", "").replace("-", "").replace("_", "");
    }

    public String getNumber() {
        return number;
    }

    public String toString() {
        return building.toString()+UMass.SEPARATOR+number;
    }
}
