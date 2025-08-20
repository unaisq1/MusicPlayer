module com.example.musicplayer {
    requires javafx.controls;
    requires javafx.media;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.musicplayer to javafx.fxml;
    exports com.example.musicplayer;
}