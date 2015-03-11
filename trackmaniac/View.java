package trackmaniac;
import javax.swing.table.AbstractTableModel;
import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.UIManager.*;
import java.io.*;

/**
 * Class for everything that involves the graphical user interface
 */
public class View {

    Controller controller;

    JFrame mainFrame;
    JMenuBar menuBar;
    JPanel panels;

    ReportIncidentPanel incidentPanel;
    NewProjectPanel newProjectPanel;
    StartPanel startPanel;
    StatusPanel statusPanel;
    DisplayIncidentPanel displayIncidentPanel;

    public View (Controller controller) {
        this.controller = controller;

        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {}

        mainFrame = new JFrame("Track maniac");
        mainFrame.setSize(500, 500);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        newProjectPanel = new NewProjectPanel();
        statusPanel = new StatusPanel();
        startPanel = new StartPanel();
        incidentPanel = new ReportIncidentPanel();
        displayIncidentPanel = new DisplayIncidentPanel();

        panels = new JPanel(new CardLayout());
        panels.add(startPanel, startPanel.name);
        panels.add(statusPanel, statusPanel.name);
        panels.add(incidentPanel, incidentPanel.name);
        panels.add(newProjectPanel, newProjectPanel.name);
        panels.add(displayIncidentPanel, displayIncidentPanel.name);

        mainFrame.add(panels);
        mainFrame.setJMenuBar(new MainMenuBar());
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    class MainMenuBar extends JMenuBar {

        MainMenuBar() {
            JMenuItem newItem = new JMenuItem("New");
            JMenuItem loadItem = new JMenuItem("Load");
            JMenuItem exitItem = new JMenuItem("Exit");
            JMenu fileMenu = new JMenu("File");

            newItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchPanel(newProjectPanel.name);
                }
            });
            loadItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.load();
                }
            });

            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.exit();
                }
            });

            fileMenu.add(newItem);
            fileMenu.add(loadItem);
            fileMenu.add(exitItem);
            this.add(fileMenu);
            this.setVisible(true);
        }
    }

    File selectFolder(String path) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnValue = chooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile();
        } else {
            return null;
        }
    }

    class NewProjectPanel extends JPanel {

        String name = "NewProjectPanel";
        JLabel labelProjectName;
        JTextField inputProjectName;
        JButton btnSelectFolder;
        JLabel labelSelectedFolder;
        JButton btnNewProject;
        File selectedFile;

        NewProjectPanel() {
            super(new GridBagLayout());
            btnNewProject = new JButton("Create");
            labelProjectName = new JLabel("Project name: ");
            inputProjectName = new JTextField();
            btnSelectFolder = new JButton("select folder");
            labelSelectedFolder = new JLabel("");

            btnSelectFolder.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    selectedFile = selectFolder(null);
                    if (selectedFile != null) {
                        labelSelectedFolder.setText(selectedFile.getAbsolutePath());
                    }
                }
            });

            btnNewProject.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.newProject(inputProjectName.getText(), selectedFile);
                }
            });

            SimpleConstraint c = new SimpleConstraint();

            this.add(labelProjectName, c);

            c.column();
            c.weightx = 1;
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(inputProjectName, c);

            c.row();
            c.fill = GridBagConstraints.NONE;
            this.add(btnSelectFolder, c);

            c.column();
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(labelSelectedFolder, c);

            c.row();
            c.fill = GridBagConstraints.NONE;
            this.add(btnNewProject, c);
        }
    }

    private class SimpleConstraint extends GridBagConstraints {

        SimpleConstraint() {
            super();
            resetEtc();
            resetPos();
        }
        public void row() {
            this.gridy += 1;
            this.gridx = 0;
            resetEtc();
        }

        public void column() {
            this.gridx += 1;
            resetEtc();
        }

        void resetPos() {
            this.gridx = 0;
            this.gridy = 0;
        }

        void resetEtc() {
            this.weightx = 0.1;
            this.weighty = 0;
            this.gridwidth = 1;
            this.gridheight = 1;
            this.anchor = GridBagConstraints.LINE_START;
            this.fill = GridBagConstraints.NONE;
        }
    }

    class StartPanel extends JPanel {

        String name = "StartPanel";

        StartPanel() {
            super(new GridBagLayout());
            JButton btnNew = new JButton("New");
            JButton btnLoad = new JButton("Load");;

            btnNew.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchPanel(newProjectPanel.name);
                }
            });

            btnLoad.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.load();
                }
            });

            SimpleConstraint c = new SimpleConstraint();
            c.gridwidth = 2;
            c.anchor = GridBagConstraints.CENTER;
            this.add(btnNew, c);

            c.row();
            c.anchor = GridBagConstraints.CENTER;
            c.gridwidth = 2;
            this.add(btnLoad, c);
        }
    }

    class StatusPanel extends JPanel {

        String name = "StatusPanel";
        JButton btnReportIncident;
        JButton btnDisplayIncident;
        JLabel labelProjectName;
        JLabel labelProjectBugs;
        JTable incidentTable;

        JScrollPane incidentScrollPane;

        StatusPanel() {
            super(new GridBagLayout());
            labelProjectName = new JLabel("Project name: ");
            labelProjectBugs = new JLabel("Project bugs: ");

            btnReportIncident = new JButton("Report incident");
            btnReportIncident.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent e) {
                    switchPanel(incidentPanel.name);
                }
            });

            btnDisplayIncident = new JButton("Display incident");
            btnDisplayIncident.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int incident = incidentTable.getSelectedRow();
                    controller.displayIncident(incident);
                }
            });

            incidentTable = new JTable();
            incidentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            incidentTable.setAutoCreateRowSorter(true);
            incidentScrollPane = new JScrollPane(incidentTable);

            SimpleConstraint c = new SimpleConstraint();

            this.add(labelProjectName, c);

            c.row();
            this.add(labelProjectBugs, c);

            c.row();
            this.add(btnReportIncident, c);

            c.row();
            this.add(btnDisplayIncident, c);

            c.row();
            c.anchor = GridBagConstraints.CENTER;
            c.fill = GridBagConstraints.HORIZONTAL;
            c.gridwidth = 1;
            this.add(incidentScrollPane, c);

            setVisible(true);

        }
    }

    class DisplayIncidentPanel extends JPanel {
        String name = "DisplayIncidentPanel";

        JTextField incidentTitle;
        JTextArea incidentDescription;

        JButton btnSendIncident;
        JButton btnBack;
        JLabel labelCbSeverity;
        JComboBox<Severity> cbSeverity;

        JLabel labelIncidentTitle;
        JLabel labelIncidentDescription;
        JLabel labelSeverity;

        DisplayIncidentPanel() {
            super(new GridBagLayout());

            incidentDescription = new JTextArea(15, 10);
            incidentDescription.setPreferredSize(new Dimension(100, 100));
            incidentDescription.setLineWrap(true);

            btnBack = new JButton("Back");
            labelIncidentDescription = new JLabel("Description");
            labelIncidentTitle = new JLabel("Title");
            labelSeverity = new JLabel("Severity");
            incidentTitle = new JTextField();
            cbSeverity = new JComboBox<Severity>(Severity.values());

            SimpleConstraint c = new SimpleConstraint();

            btnBack.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchPanel(statusPanel.name);
                }
            });

            this.add(labelIncidentTitle, c);

            c.column();
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(incidentTitle, c);

            c.row();
            this.add(labelIncidentDescription, c);

            c.column();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            this.add(incidentDescription, c);

            c.row();
            this.add(labelSeverity, c);

            c.row();
            this.add(btnBack, c);
        }
    }

    class ReportIncidentPanel extends JPanel {

        String name = "ReportIncidentPanel";
        JLabel labelInputIncidentTitle;
        JLabel labelInputIncidentDescription;
        JTextField inputIncidentTitle;
        JTextArea inputIncidentDescription;

        JButton btnSendIncident;
        JButton btnSendCancel;
        JLabel labelCbSeverity;
        JComboBox<Severity> cbSeverity;

        ReportIncidentPanel() {
            super(new GridBagLayout());
            inputIncidentTitle = new JTextField(15);
            inputIncidentDescription = new JTextArea(15, 10);
            inputIncidentDescription.setPreferredSize(new Dimension(100, 100));

            inputIncidentDescription.setLineWrap(true);
            labelInputIncidentTitle = new JLabel("Title ");
            labelInputIncidentDescription = new JLabel("Description");

            btnSendIncident = new JButton("Report incident");
            btnSendIncident.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    controller.reportIncident();
                }
            });

            btnSendCancel = new JButton("Cancel");
            btnSendCancel.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    switchPanel(statusPanel.name);
                }
            });

            cbSeverity = new JComboBox<Severity>(Severity.values());
            labelCbSeverity = new JLabel("Severity");

            SimpleConstraint c = new SimpleConstraint();

            this.add(labelInputIncidentTitle, c);

            c.column();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            this.add(inputIncidentTitle, c);

            c.row();
            this.add(labelInputIncidentDescription, c);

            c.column();
            c.fill = GridBagConstraints.HORIZONTAL;
            this.add(inputIncidentDescription, c);

            c.row();
            this.add(labelCbSeverity, c);

            c.column();
            this.add(cbSeverity, c);

            c.row();
            this.add(btnSendCancel, c);

            c.column();
            this.add(btnSendIncident, c);

            setVisible(true);
        }
    }

    void displayMessage(String message) {
        JOptionPane.showMessageDialog(mainFrame, message);
    }

    void clearReportIncident() {
        incidentPanel.inputIncidentTitle.setText("");
        incidentPanel.inputIncidentDescription.setText("");
    }

    void setStatusPanelProjectName(String s) {
        statusPanel.labelProjectName.setText(s);
    }
    void setStatusPanelProjectBugs(String s) {
        statusPanel.labelProjectBugs.setText(s);
    }

    void switchPanel(String newPanel) {
        CardLayout cardLayout = (CardLayout) panels.getLayout();
        cardLayout.show(panels, newPanel);
    }

    void setStatusPanelTable(AbstractTableModel model) {
        if(model != null) {
            statusPanel.incidentTable.setModel(model);
        }
    }
}
