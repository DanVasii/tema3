module org.example.tema3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.apache.poi.ooxml;


    opens org.example.tema3 to javafx.fxml;
    opens view to javafx.fxml;
    opens model to javafx.base;
    opens controller to javafx.fxml;
    exports org.example.tema3;
    exports controller;
    exports view;
}