package trackmaniac;

public class Data {

    Project project;
    String user;
    Controller controller;

    Data (Controller controller) {
        this.controller = controller;
        this.project = new Project();
    }
}
