package autohouser;

import java.util.HashMap;
import java.util.Map;

/**
 * ResidentialArea instances have a Map that contains
 * all of the Buildings in the residential area.
 * Since it is a top-level structure, A residential area
 * ID is simply "ResidentialAreaNameNormalized".
 */
public class ResidentialArea {
    private String name;
    private Map<String, Building> buildings;

    public ResidentialArea() {
        this.name = null;
        this.buildings = new HashMap<>();
    }

    public ResidentialArea(String name) {
        this();
        this.name = name;
    }

    public void put(Building building) {
        this.buildings.put(building.getId(), building);
        building.setArea(this);
    }

    /**
     * Used to get a {@link Building} when you already
     * know the Building's ID.
     * @param buildingId  Key/ID of the Building to get.
     * @return          The Building the key/ID maps to.
     */
    public Building getByKey(String buildingId) {
        return buildings.get(buildingId);
    }

    /**
     * Used to search if a {@link Building} is in this Residential Area.
     * @param building  String name of the Building being searched for.
     * @return          The Building, if it is found in this Residential Area.
     */
    public Building get(String building) {
        return getByKey(Building.getId(this.getName(), building));
    }

    public String getId() {
        return ResidentialArea.getId(this.getName());
    }

    public static String getId(String name) {
        return name.trim().toLowerCase().replace(" ", "").replace("-", "").replace("_", "");
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return name;
    }
}
