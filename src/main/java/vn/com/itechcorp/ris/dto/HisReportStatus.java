package vn.com.itechcorp.ris.dto;

public enum HisReportStatus {
    NOT_READY(0),
    SENDING(1),
    SUCCEEDED(2),
    FAILED(3),;

    private final int value;

    HisReportStatus(int value) {
        this.value = value;
    }

    public static HisReportStatus fromValue(int value) {
        switch (value) {
            case 1:
                return SENDING;
            case 2:
                return SUCCEEDED;
            case 3:
                return FAILED;
            default:
                return NOT_READY;
        }
    }

    public int getValue() {
        return value;
    }

    public int compare(HisReportStatus other) {
        return value - other.value;
    }
}
