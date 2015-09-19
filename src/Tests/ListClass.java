package Tests;

import SQL.SQLRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 9/19/15.
 */
public class ListClass extends SQLRow {
    public ArrayList<Long> elements;

    public ListClass(){
        elements = new ArrayList<>();
    }
    public ListClass(long[] elements){
        this();
        for (int i = 0; i < elements.length; ++i){
            this.elements.add(elements[i]);
        }
    }
}
