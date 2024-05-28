module com.wonkglorg.loginfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.wonkglorg.fxutility;
    requires JavaUtil.ed97a0806a;


    opens com.wonkglorg.loginfx to javafx.fxml;
    exports com.wonkglorg.loginfx;
    exports com.wonkglorg.loginfx.pages.border;
    exports com.wonkglorg.loginfx.pages.editor;
    exports com.wonkglorg.loginfx.pages.login;
    opens com.wonkglorg.loginfx.pages.border to javafx.fxml;
    opens com.wonkglorg.loginfx.pages.editor to javafx.fxml;
    opens com.wonkglorg.loginfx.pages.login to javafx.fxml;
}