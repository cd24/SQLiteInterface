# SQLiteInterface
A simple SQLite interface for the java language.  make sure you have JDBC added to the project hierarchy or use the jar.  
Unfortunately, this tool doesn't support looped data structures.  Objects cannot point to one another and be saved.  However, you can automatically relationalize your data structure, so long as it dosn't loop.

There are some restrictions to be aware of:
- private fields will not be preserved to database
- when an unkown type is encountered, the toString() value is written to the database.
- this library makes extensive use of reflection and can result in some unfortunate errors, throw the error and as much information as you can into an issue and we will resolve it as quickly as possible
- this library only presently supports sqllite but we are looking into expanding to other relational databases (e.g. MySQL, MSSQl...etc)

An example of use can be found in the tests package.

To enable saving a class, simply extend SQLRow.  This class MUST implement an empty constructor (used for rehydrating objects).  You may have as many alternate constructors as needed, they will not affect the process of saving.

```
public class ClassA extends SQLRow {
    public int numRepeats;
    public String name;

    public ClassA(){
        numRepeats = 10;
        name = "Hello World!";
    }
}
```

now anywhere we have an object of type ClassA, we can call .save() to write the object to the database. 

```
ClassA myClass = new ClassA();
myClass.name = "some new name";
myClass.save() //Writes to database
```

Additionally, you can read in all of the items saved or a set of items saved using a standard SQL where. 

```
ClassA myClass = new ClassA().where("name='some new name'");
```
would load in the object we just saved to the database.  

There are also several standard utility functions for maintaing and destroying objects/tables at runtime.

```
myClass.delete(); //deletes object
myClass.drop(); //drops the table generated for ClassA
myClass.deleteAllRecords(); //empties the table, but preserves the table structure
myClass.tableExists(); // used to determine if the table already exists
myClass.all(); // returns an ArrayList of all entries.  DO NOT USE for a lot of rows, this will take a long time to evaluate.
myClass.lazyAll(); //returns a lazy evaluator, good for not destroying your RAM with lots of entries
myClass.lazyAllWhere(String where); // return a lazy list which returns rows that meet the provided where clause
myClass.query(String query); //for when you need to run a query we failed to provide.
```
