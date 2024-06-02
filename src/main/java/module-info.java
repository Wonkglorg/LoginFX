module com.wonkglorg.loginfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.wonkglorg.fxutility;
    requires bcrypt;
    requires java.desktop;
    requires JavaUtil;
    requires org.jetbrains.annotations;


    opens com.wonkglorg.loginfx to javafx.fxml;
    exports com.wonkglorg.loginfx;
    exports com.wonkglorg.loginfx.pages.editor;
    exports com.wonkglorg.loginfx.pages.login;
    exports com.wonkglorg.loginfx.pages.register;
    opens com.wonkglorg.loginfx.pages.register to javafx.fxml;
    opens com.wonkglorg.loginfx.pages.editor to javafx.fxml;
    opens com.wonkglorg.loginfx.pages.login to javafx.fxml;
    opens com.wonkglorg.loginfx.objects to javafx.base;
    exports com.wonkglorg.loginfx.objects;
}