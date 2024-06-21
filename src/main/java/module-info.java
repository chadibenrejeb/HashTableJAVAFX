module com.example.hashtable {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.hashtable to javafx.fxml;
    exports com.example.hashtable;
}