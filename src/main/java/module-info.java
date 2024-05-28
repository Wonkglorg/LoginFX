module com.wonkglorg.loginfx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.wonkglorg.loginfx to javafx.fxml;
    exports com.wonkglorg.loginfx;
    exports com.wonkglorg.loginfx.controller;
    opens com.wonkglorg.loginfx.controller to javafx.fxml;
}