package Tests;

import SQL.SQLRow;

/**
 * Created by John on 4/16/2015.
 */
public class ClassA extends SQLRow {
    public ClassB someItem;
    public int numRepeats;
    public String name;

    public ClassA(){
        someItem = new ClassB(this);
        numRepeats = 10;
        name = "Hello World!";
    }
}
