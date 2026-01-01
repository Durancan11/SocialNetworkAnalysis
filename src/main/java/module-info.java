module com.socialnetwork.socialnetworkanalysis {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires javafx.swing;

    opens com.socialnetwork.socialnetworkanalysis to javafx.fxml;
    exports com.socialnetwork.socialnetworkanalysis;
}