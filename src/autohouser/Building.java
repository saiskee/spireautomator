package autohouser;

import spireautomator.UMass;

import java.util.HashMap;
import java.util.Map;

/**
 * Building instances have a Map that contains
 * all of the Rooms in that building.
 * A Building also has a pointer "upwards" to
 * its Residential Area which is set by a
 * Residential Area when the Building is mapped to one.
 * A Building ID follows the format
 * "ResidentialAreaID-BuildingNameNormalized" which derives to
 * "ResidentialAreaNameNormalized-BuildingNameNormalized".
 */
public class Building {
    private String name;
    private ResidentialArea area;
    private Map<String, Room> rooms;

    public Building() {
        this.name = "";
        this.area = null;
        this.rooms = new HashMap<>();
    }

    public Building(String name) {
        this();
        this.name = name;
    }

    public void put(Room room) {
        this.rooms.put(room.getId(), room);
        room.setBuilding(this);
    }

    /**
     * Used to get a {@link Room} when you already
     * know the Room's ID.
     * @param roomId  Key/ID of the Room to get.
     * @return      The Room the key/ID maps to.
     */
    public Room getByKey(String roomId) {
        return rooms.get(roomId);
    }

    /**
     * Used to search if a {@link Room} is in this Building.
     * @param room  String number of the Room being searched for.
     * @return      The Room, if it is found in this Building.
     */
    public Room get(String room) {
        return getByKey(Room.getId(getArea().getName(), this.getName(), room));
    }

    public void setArea(ResidentialArea area) {
        this.area = area;
    }

    public ResidentialArea getArea() {
        return area;
    }

    public String getId() {
        return Building.getId(area.getName(), this.getName());
    }

    public static String getId(String area, String name) {
        return ResidentialArea.getId(area)+UMass.SEPARATOR+name.trim().toLowerCase().replace(" ", "").replace("-", "").replace("_", "");
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return area.toString()+UMass.SEPARATOR+name;
    }
}
