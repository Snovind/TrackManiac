package trackmaniac;

import java.util.*;
import java.io.*;

public class Project {

    File folder;
    File metadata;
    String title;
    ArrayList<Incident> incidents = new ArrayList<Incident>();

    Project () {
        title = "Empty project";
        folder = null;
    }

    Project (String title, File metadata, File folder) {
        this.title = title;
        this.metadata = metadata;
        this.folder = folder;
    }

    void addIncident(Incident incident) {
        incidents.add(incident);
    }

    static Project create(String title, File folder) {
        File metadata = new File(folder, "metadata");
        Project p = new Project(title, folder, metadata);
        return p;
    }

    static Project load(File folder) {

        return null;
    }

    void save() {
        if (folder != null && metadata != null && title != null && incidents != null) {
            System.out.println("in save");
            PrintWriter pw;
            try {
                pw = new PrintWriter(metadata);
            } catch (Exception e) {
                System.out.println(e);
                return;
            }
            pw.print(title);
            for (Incident i: incidents) {
                pw.print(i.id);
                File incidentFile = new File(folder, "" + i.id);

                PrintWriter incidentWriter;
                try {
                    incidentWriter = new PrintWriter(incidentFile);
                }
                catch (Exception e) {
                    System.out.println(e);
                    break;
                };

                incidentWriter.close();
            }
            pw.close();
        }
    }
}
