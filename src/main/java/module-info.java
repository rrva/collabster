module collabster {
    requires kotlin.stdlib;
    requires java.desktop;
    requires javafx.graphics;
    requires tornadofx;

    requires javafx.controls;
    requires javafx.fxml;
    requires kotlinx.serialization.runtime;
    exports se.rrva.collabster;
    opens se.rrva.collabster;
}