package Tests;

import SQL.LazyList;
import org.junit.Test;

/**
 * Created by John on 4/16/2015.
 */
public class SQLTableTests {

    public ClassA aItem;
    public ClassB bItem;


    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.After
    public void tearDown() throws Exception {
    }

    @Test
    public void testSave(){
        aItem = new ClassA();
        ClassA someItem = new ClassA();
        bItem = new ClassB(aItem);
        aItem.drop();
        bItem.drop();


        aItem.name = "Item A";
        bItem.name = "Item B";
        aItem.save();
        bItem.save();
        someItem.save();
    }

    @Test
    public void testLoad(){
        ClassA found = new ClassA().where("id='0'");
        System.out.println(found);
    }

    @Test
    public void testLazyLoad(){
        ClassA newItem = new ClassA();
        newItem.name = "Some Second Class A Object";
        newItem.save();

        LazyList<ClassA> lazyList = new ClassA().lazyAll();
        int count = 0;
        while(lazyList.hasNext()){
            System.out.println("New item: ");
            ClassA item = lazyList.next();
            System.out.println("name: " + item.name + ", partner Name: " + item.someItem.name + ", partner ID: " + item.someItem.id());
            count++;
        }

        System.out.println("Count from lazy list: " + count);
    }

    @Test
    public void testIterable(){
        LazyList<ClassA> someList = new ClassA().lazyAll();

        for (Object item : someList){
            ClassA a = (ClassA) item;
            System.out.println("Item Name: " + a.name);
        }
    }
}