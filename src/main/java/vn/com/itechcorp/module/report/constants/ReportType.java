package vn.com.itechcorp.module.report.constants;

public enum ReportType {
    CREATE("NW"),
    UPDATE("RF"),

    DELETE("CA");
    private final String type;

    ReportType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
