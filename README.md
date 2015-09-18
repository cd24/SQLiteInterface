# SQLiteInterface
A simple SQLite interface for the java language.  make sure you have JDBC added to the project hierarchy or use the jar.  
Unfortunately, this tool doesn't support looped data structures.  Objects cannot point to one another and be saved.  However, you can automatically relationalize your data structure, so long as it dosn't loop.

There are some restrictions to be aware of:
- private fields will not be preserved to database
- when an unkown type is encountered, the toString() value is written to the database.
- this library makes extensive use of reflection and can result in some unfortunate errors, throw the error and as much information as you can into an issue and we will resolve it as quickly as possible
- this library only presently supports sqllite but we are looking into expanding to other relational databases (e.g. MySQL, MSSQl...etc)

