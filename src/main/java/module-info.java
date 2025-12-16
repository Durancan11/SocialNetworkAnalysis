module com.socialnetwork.socialnetworkanalysis {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.socialnetwork.socialnetworkanalysis to javafx.fxml;
    exports com.socialnetwork.socialnetworkanalysis;
}