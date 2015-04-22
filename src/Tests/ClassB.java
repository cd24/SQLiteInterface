package Tests;

import SQL.SQLRow;

/**
 * Created by John on 4/16/2015.
 */
public class ClassB extends SQLRow {
    public int repeats;
    public String name;

    public ClassB(){
        repeats = 20;
        name = "I'm glad you're here!";
    }
}
