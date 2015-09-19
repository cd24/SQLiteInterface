package Tests;

import SQL.DBManager;
import SQL.LazyList;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SQLTableTests {

    public ClassA aItem;
    public ClassB bItem;


    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
        new ClassA().drop();
        new ClassB().drop();
    }

    @Test
    public void testSave(){
        aItem = new ClassA();
        ClassA someItem = new ClassA();
        ClassB someOtherItem = new ClassB(someItem);
        bItem = new ClassB(aItem);
        aItem.drop();
        bItem.drop();
        someItem.someItem = someOtherItem;


        aItem.name = "Item A";
        someItem.name = "Item A";
        bItem.name = "Item B";
        aItem.save();
        bItem.save();
        someItem.save();
    }

    @Test
    public void testLoad(){
        ClassA found = new ClassA().where("id='1'");
        assertTrue(found != null);
    }

    @Test
    public void testLazyLoad(){
        ClassA newItem = new ClassA();
        newItem.name = "Some Second Class A Object";
        newItem.save();

        LazyList<ClassA> lazyList = new ClassA().lazyAll();
        while(lazyList.hasNext()){
            ClassA item = lazyList.next();
            assertTrue(isValidReturnA(item));
            System.out.println("Name: " + item.name + ", partner Name: " + item.someItem.name + ", partner ID: " + item.someItem.id());
        }

        LazyList<ClassA> lazyWhere = new ClassA().lazyAllWhere("name='Hello World!'");
        for (ClassA item : lazyWhere){
            assertTrue(isValidReturnA(item));
            System.out.println("LazyWhere name: " + item.name + ", partner Name: " + item.someItem.name + ", partner ID: " + item.someItem.id());
        }

        LazyList<ClassA> lazyWhere2 = new ClassA().lazyAllWhere("name='Item A'");
        for (ClassA item : lazyWhere2){
            assertTrue(isValidReturnA(item));
            System.out.println("LazyWhere2 name: " + item.name + ", partner Name: " + item.someItem.name + ", partner ID: " + item.someItem.id());
        }
    }

    @Test
    public void testIterable(){
        LazyList<ClassA> someList = new ClassA().lazyAll();

        for (ClassA item : someList){
            assertTrue(isValidReturnA(item));
            System.out.println("Item Name: " + item.name);
        }
    }

    @Test
    public void testLazyEvalWithSkips(){
        new ClassA().drop();

        for (int i = 0; i < 20; ++i){
            ClassA newItem = new ClassA();
            newItem.name = i % 2 == 0 ? Integer.toString(i) : "30";
            newItem.numRepeats = i;
            newItem.save();
        }

        LazyList<ClassA> items = new ClassA().lazyAllWhere("name='30'");
        LazyList<ClassA> allItems = new ClassA().lazyAll();
        assertEquals(10, items.count());
        assertEquals(20, allItems.count());
        for (ClassA item : items){
            System.out.println("ID in Mod: " + item.id() + " name " + item.name);
            assertTrue(item.id() % 2 == 0);
        }
    }

    @Test
    public void testSaveTime(){

    }

    public boolean isValidReturnA(ClassA element){
        return element != null && element.someItem != null && element.someItem.id() > 0 && element.id() > 0;
    }
}