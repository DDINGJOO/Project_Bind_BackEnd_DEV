package data.enums.auth;

public enum ReportType {
    SPAM("스팸"),
    ABUSE("악용"),
    INAPPROPRIATE_CONTENT("부적절한 콘텐츠"),
    OTHER("기타");

    private final String description;

    ReportType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
