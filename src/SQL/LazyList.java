package SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by John on 4/17/2015.
 */
public class LazyList<T extends SQLRow> implements Iterable{

    /*
    * Lazy Evaluator for sql sets.
    * Customizable lazyness
    */

    private int size, currentIndex, retrieved;
    private String query, tableName, where, database;
    private Class<? extends SQLRow> type;

    public LazyList(){
        this.size = 0;
        this.currentIndex = 0;
        this.retrieved = 0;
        this.query = "";
    }

    public LazyList(String database, String tableName, String where, Class<? extends SQLRow> type){
        this();
        this.database = database;
        this.where = where;
        this.type = type;
        this.tableName = tableName;
        this.size = this.getNumEntries();
    }

    public void setQuery(String tableName, String where){
        this.tableName = tableName;
        this.where = where;
        this.query = "SELECT id FROM " + tableName + " WHERE " + where;
        this.size = this.getNumEntries();
    }

    public void setQuery(String tableName){
        this.tableName = tableName;
        this.where = "";
        this.size = this.getNumEntries();
    }

    public void setDatabase(String database){
        this.database = database;
    }

    public void setType(Class<?> type){

    }

    public int getNumEntries() {
        String query = "SELECT COUNT(*) FROM " + this.tableName;
        if (!this.where.equals(""))
            query +=  " WHERE " + this.where;
        ResultSet set = this.query(query);
        try {
            int size = set.getInt(1);
            return size;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public T next(){
        if (this.retrieved >= this.size){
            throw new IndexOutOfBoundsException("There are no more results\nCurrent Index: " + this.currentIndex + "\nMax Index: " + this.size);
        }
        String query = "SELECT * FROM " + tableName + " WHERE id>'" + this.currentIndex + "'";
        if (!this.where.equals(""))
            query += " AND " + this.where;
        ResultSet resultSet = this.query(query + " LIMIT 1");

        ArrayList returned = new SQLRow().getFromResultSet(resultSet, this.type);
        T value = (T) returned.get(0);
        ++this.retrieved;
        ++this.currentIndex;
        return value;
    }

    public boolean hasNext(){
        return this.retrieved < this.size;
    }

    private ResultSet query(String query){
        return SQLRow.query(query, this.database);
    }

    @Override
    public Iterator iterator() {
        LazyIterator<T> iter =  new LazyIterator<T>(this);
        return iter;
    }
}
