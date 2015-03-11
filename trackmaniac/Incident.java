package trackmaniac;

import java.util.Date;

public class Incident {

    Date reportedDate;
    Severity severity;
    String description;
    String title;
    boolean confirmed;
    int id;

    Incident(String title, String description, Severity severity) {
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.confirmed = false;
        this.reportedDate = new Date();
        id = newId();
    }

    static int nextId = 0;
    static int newId() {
        int id = nextId;
        nextId += 1;
        return id;
    }
}
