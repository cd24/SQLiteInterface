package Tests;

import SQL.SQLRow;

public class ClassA extends SQLRow {
    public ClassB someItem;
    public int numRepeats;
    public String name;

    public ClassA(){
        someItem = new ClassB();
        numRepeats = 10;
        name = "Hello World!";
    }
}
