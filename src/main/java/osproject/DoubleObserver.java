package osproject;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;



public class DoubleObserver {

    private final SimpleDoubleProperty num = new SimpleDoubleProperty(0.0);

    public DoubleObserver() {
        num.set(0.0);
        
    }

    // public String getStr(){
    //     return str.get();
    // }

    public void setValue(double value){
        this.num.set(value);
    }

    public DoubleProperty valueProperty() {
        return num;
    }
    
    public Double get(){
        return num.getValue();
    }
}