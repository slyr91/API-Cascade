module org.daryl.apicascade {
    requires javafx.controls;
    requires javafx.fxml;

    requires commons.cli;
    requires org.yaml.snakeyaml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;

    opens org.daryl.apicascade to javafx.fxml;
    exports org.daryl.apicascade;
}