package Tests;

import SQL.SQLRow;

/**
 * Created by John on 4/16/2015.
 */
public class ClassB extends SQLRow {
    public int repeats;
    public String name;
    //public ClassA nonLoop;


    public ClassB(){
        this.repeats = 0;
        this.name = "Neat, this is a child.";
        //this.nonLoop = null;
    }

    public ClassB(ClassA parent){
        repeats = 20;
        name = "I'm glad you're here!";
        //nonLoop = new ClassA();
        //this.nonLoop = parent;
        //nonLoop.name = "Hello, second item";
        //nonLoop.numRepeats = 40;
    }
}
