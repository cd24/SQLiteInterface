package Tests;

import org.junit.Test;

/**
 * Created by John on 4/16/2015.
 */
public class SQLTableTests {

    public ClassA aItem;
    public ClassB bItem;


    @org.junit.Before
    public void setUp() throws Exception {
        aItem = new ClassA();
        bItem = new ClassB();
        aItem.someItem = bItem;
    }

    @org.junit.After
    public void tearDown() throws Exception {

    }

    @Test
    public void testSave(){
        aItem.save();
    }

    @Test
    public void testLoad(){
        ClassA found = new ClassA().where("id='0'");
        System.out.println(found);
    }
}