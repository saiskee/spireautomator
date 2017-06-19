package autohouser;

public enum ResAreaEnum {
    CE ("Central"),
    CH ("Commonwealth Honors Community"),
    LN ("Lincoln Apartments"),
    NO ("North"),
    NE ("Northeast"),
    OH ("Orchard Hill"),
    SW ("Southwest"),
    SY ("Sylvan");

    private final String name;
    ResAreaEnum(String name) {
        this.name = name;
    }

    private String getName() {
        return name;
    }
}
