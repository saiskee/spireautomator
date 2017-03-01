package autohouser;

/**
 * An enumeration of the designs of rooms offered.
 * The design numbers have no meaningful indication.
 * They may be removed in the future.
 */
public enum DesignEnum {
    //TODO: Remove DesignEnum integer values?
    STANDARD(1),
    ZROOM(2),
    CORNER(3),
    APARTMENTSINGLE(4);

    private final int design;

    DesignEnum(int design) {
        this.design = design;
    }

    public int getDesign() {
        return design;
    }
}
