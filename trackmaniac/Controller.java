package trackmaniac;

import javax.swing.table.AbstractTableModel;
import java.io.*;

/**
 * Controls data flow between components
 */
public class Controller {
    View view;
    Data data;

    public Controller () {
        view = new View(this);
        data = new Data(this);

        setStatusPanel();
    }

    public void reportIncident() {

        String title = view.incidentPanel.inputIncidentTitle.getText();;
        String description = view.incidentPanel.inputIncidentDescription.getText();
        Severity severity = (Severity) view.incidentPanel.cbSeverity.getSelectedItem();

        if (title.length() == 0) {
            view.displayMessage("Must enter a incident title");
            return;
        }
        if (description.length() == 0) {
            view.displayMessage("Must enter a description");
            return;
        }

        Incident incident = new Incident(title, description, severity);
        data.project.addIncident(incident);
        view.displayMessage("Incident sucessfully reported");
        view.clearReportIncident();
        setStatusPanel();
        view.setStatusPanelTable(new IncidentTableModel());
    }

    public void setStatusPanel() {
        view.setStatusPanelProjectName("Project name: " + data.project.title);
        view.setStatusPanelProjectBugs("Project incidents: " + data.project.incidents.size());
    }

    public void exit () {
        if(data != null && data.project != null) {
            data.project.save();
        }
        System.exit(0);
    }

    public void load () {
        String path = "";
        File folder = view.selectFolder(path);
        data.project = Project.load(folder);
    }

    public void newProject (String projectTitle, File folder) {
        if(projectTitle == null || projectTitle.length() == 0) {
            view.displayMessage("Must enter a title");
            return;
        }
        if (folder == null) {
            view.displayMessage("Must select a project folder");
            return;
        }
        data.project = Project.create(projectTitle, folder);
        if (data.project != null) {
            view.displayMessage("Project sucessfully created");
            view.switchPanel(view.statusPanel.name);
            setStatusPanel();
        }
    }

    public void displayIncident(int id) {
        if (id < 0) {
            view.displayMessage("Must select an incident before displaying it");
            return;
        }
        Incident i = data.project.incidents.get(id);
        view.displayIncidentPanel.incidentTitle.setText(i.title);
        view.displayIncidentPanel.incidentDescription.setText(i.description);
        view.switchPanel(view.displayIncidentPanel.name);
    }

    class IncidentTableModel extends AbstractTableModel {
        String[] columnNames = {"Title", "Severity", "Date"};

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            if (data.project != null) {
                return data.project.incidents.size();
            } else {
                return 0;
            }
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            Incident i = data.project.incidents.get(row);
            if(col == 0) {
                return i.title;
            }
            else if(col == 1) {
                return i.severity;
            }
            else if (col == 2) {
                return i.reportedDate;
            }
            else {
                return "Error";
            }
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
    }
}
