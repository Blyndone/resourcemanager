package osproject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringObserver {

    private final SimpleStringProperty str = new SimpleStringProperty("value");

    public StringObserver() {
        str.set("");

    }

    // public String getStr(){
    // return str.get();
    // }

    public void setValue(String value) {
        this.str.set(value);
    }

    public StringProperty valueProperty() {
        return str;
    }

    public String get() {
        return str.getValue();
    }
}