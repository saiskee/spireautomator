package autohouser;

public enum BuildingEnum {
    BAKE ("Baker", ResAreaEnum.CE),
    BIRC ("Birch", ResAreaEnum.CH),
    BRET ("Brett", ResAreaEnum.CE),
    BROO ("Brooks", ResAreaEnum.CE),
    BROW ("Brown", ResAreaEnum.SY),
    BUTT ("Butterfield", ResAreaEnum.CE),
    CANC ("Cance", ResAreaEnum.SW),
    CASH ("Cashin", ResAreaEnum.SY),
    CHAD ("Chadbourne", ResAreaEnum.CE),
    COOL ("Coolidge", ResAreaEnum.SW),
    CRAB ("Crabtree", ResAreaEnum.NE),
    CRAM ("Crampton", ResAreaEnum.SW),
    DICK ("Dickinson", ResAreaEnum.OH),
    DWIG ("Dwight", ResAreaEnum.NE),
    ELMH ("Elm", ResAreaEnum.CH),
    EMER ("Emerson", ResAreaEnum.SW),
    FIEL ("Field", ResAreaEnum.OH),
    GORM ("Gorman", ResAreaEnum.CE),
    GRAY ("Grayson", ResAreaEnum.OH),
    GREE ("Greenough", ResAreaEnum.CE),
    HAML ("Hamlin", ResAreaEnum.NE),
    JAME ("James", ResAreaEnum.SW),
    JADA ("John Adams", ResAreaEnum.SW),
    JQAD ("John Quincy Adams", ResAreaEnum.SW),
    JOHN ("Johnson", ResAreaEnum.NE),
    KENN ("Kennedy", ResAreaEnum.SW),
    KNOW ("Knowlton", ResAreaEnum.NE),
    LEAC ("Leach", ResAreaEnum.NE),
    LEWI ("Lewis", ResAreaEnum.NE),
    LN01 ("Lincoln Building 01", ResAreaEnum.LN),
    LN02 ("Lincoln Building 02", ResAreaEnum.LN),
    LN03 ("Lincoln Building 03", ResAreaEnum.LN),
    LN04 ("Lincoln Building 04", ResAreaEnum.LN),
    LN05 ("Lincoln Building 05", ResAreaEnum.LN),
    LN06 ("Lincoln Building 06", ResAreaEnum.LN),
    LN07 ("Lincoln Building 07", ResAreaEnum.LN),
    LN08 ("Lincoln Building 08", ResAreaEnum.LN),
    LN09 ("Lincoln Building 09", ResAreaEnum.LN),
    LN10 ("Lincoln Building 10", ResAreaEnum.LN),
    LN11 ("Lincoln Building 11", ResAreaEnum.LN),
    LIND ("Linden", ResAreaEnum.CH),
    MACK ("MacKimmie", ResAreaEnum.SW),
    MAPL ("Maple", ResAreaEnum.CH),
    LYON ("Mary Lyon", ResAreaEnum.NE),
    MCNA ("McNamara", ResAreaEnum.SY),
    MELV ("Melville", ResAreaEnum.SW),
    MOOR ("Moore", ResAreaEnum.SW),
    NORA ("North Hall A", ResAreaEnum.NO),
    NORB ("North Hall B", ResAreaEnum.NO),
    NORC ("North Hall C", ResAreaEnum.NO),
    NORD ("North Hall D", ResAreaEnum.NO),
    OAKH ("Oak", ResAreaEnum.CH),
    PATT ("Patterson", ResAreaEnum.SW),
    PIER ("Pierpont", ResAreaEnum.SW),
    PRIN ("Prince", ResAreaEnum.SW),
    SYCA ("Sycamore", ResAreaEnum.CH),
    THAT ("Thatcher", ResAreaEnum.NE),
    THOR ("Thoreau", ResAreaEnum.SW),
    VANM ("VanMeter", ResAreaEnum.CE),
    WASH ("Washington", ResAreaEnum.SW),
    WEBS ("Webster", ResAreaEnum.OH),
    WHEE ("Wheeler", ResAreaEnum.CE);

    private final String name;
    private final ResAreaEnum area;

    BuildingEnum(String name, ResAreaEnum area) {
        this.name = name;
        this.area= area;
    }

    private String getName() {
        return name;
    }

    private ResAreaEnum getArea() {
        return area;
    }
}
