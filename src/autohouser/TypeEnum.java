package autohouser;

/**
 * An enumeration of the types of Rooms offered.
 * The number value indicates the amount of people
 * that can live in such a Room.
 */
public enum TypeEnum {
    //TODO: TypeEnum numbers map to dropdown HTML value?
    SINGLE(1),
    DOUBLE(2),
    TRIPLE(3),
    QUAD(4);

    private final int number;

    TypeEnum(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }
}
