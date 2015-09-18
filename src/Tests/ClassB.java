package Tests;

import SQL.SQLRow;

public class ClassB extends SQLRow {
    public int repeats;
    public String name;
    //public ClassA nonLoop;


    public ClassB(){
        this.repeats = 0;
        this.name = "Neat, this is a child.";
    }

    public ClassB(ClassA parent){
        repeats = 20;
        name = "I'm glad you're here!";
    }
}
