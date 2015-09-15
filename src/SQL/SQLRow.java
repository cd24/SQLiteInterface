package SQL;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class SQLRow extends SQLizable {

    protected int id;
    protected boolean saving = false;
    protected static boolean logging_verbose = false;

    public boolean isTable(Object o){
        return o.getClass().isAssignableFrom(SQLRow.class);
    }

    public <T extends SQLRow> T getRelationFromID(Field f, int id) throws IllegalAccessException, InstantiationException {
        Class<? extends SQLRow> objClass = (Class<? extends SQLRow>) f.getType();
        String className = tableNameForClass(objClass);

        ResultSet attributes = query("SELECT * FROM " + className + " WHERE id='" + id + "'");
        ArrayList<SQLRow> object = new SQLRow().getFromResultSet(attributes, objClass);

        return (T) object.get(0);
    }
    public String tableSchema(){
        Field[] fields = getClass().getFields();
        String schema = "id INTEGER PRIMARY KEY AUTOINCREMENT";
        for (int i = 0; i < fields.length; ++i){
            Field field = fields[i];
            if (field.getName().equals("id"))
                continue;
            else
                schema += ", " + getSchemaComponentForField(field);
        }
        return schema;
    }

    public String getSchemaComponentForField(Field f){
        Class<?> type = f.getType();
        System.out.printf("");
        if (type == int.class || type == boolean.class){
            return f.getName() + " INTEGER";
        }
        else if (type == float.class) {
            return f.getName() + " REAL";
        }
        else if (type == String.class) {
            return f.getName() + " TEXT";
        }
        else if (type == Date.class){
            return f.getName() + " TEXT";
        }
        else if (type.isAssignableFrom(SQLRow.class)){
            return f.getName() + " INTEGER";
        }
        else {
            return f.getName() + " INTEGER";
        }
    }

    public String tableName(){
        return tableNameForClass(this.getClass());
    }

    public static String tableNameForClass(Class<?> clazz){
        String fullName = clazz.getName();
        String[] components = fullName.split("\\.");
        String cName = components[components.length - 1];

        return cName.toLowerCase();
    }
    public int id() {
        return id;
    }

    public void afterSave(){
        //This is a placeholder method that gives the user the ability to perform specific tasks immediately after the save.
    }

    public void beforeSave(){
        //This is a placeholder method that gives the user the ability to perform specific tasks immediately before the save.
    }

    public void save(){
        if (saving){
            return;
        }
        DBManager database = DBManager.getInstance();
        beforeSave();
        saving = true;
        verifyDataBase();
        String cmd = "";

        if (existsInTable()){
            cmd = "UPDATE " + tableName() + " SET " + updateValue() + " WHERE id = '" + this.id() + "'";
            database.command(cmd);
        }
        else {
            cmd = "INSERT INTO " + tableName() + " " + asSQLValue();
            this.id = database.command(cmd);
        }
        saving = false;
        afterSave();
    }

    public boolean existsInTable(){

        if (!tableExists()) return false;

        ArrayList<SQLRow> item = this.allWhere(getAsQueryable());
        if (item.size() > 0){
            this.id = item.get(0).id();
        }

        return item.size() > 0 || this.id > 0;
    }

    public boolean tableExists() {
        try {
            String check = "SELECT name FROM sqlite_master WHERE type='table' AND name='" + tableName() + "'";
            ResultSet data = query(check);
            boolean tableExists = isResultSetEmpty(data);
            data.close();
            return tableExists;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isResultSetEmpty(ResultSet set) throws SQLException {
        if (set.isBeforeFirst()){
            return true;
        }
        return false;
    }

    public void delete(){

        if (existsInTable()) {
            DBManager.getInstance().command("DELETE FROM " + tableName() + " WHERE id = " + id());
        }

        this.id = -1;
    }

    public void drop(){
        DBManager.getInstance().command("DROP TABLE if exists " + tableName());
    }

    public void deleteAllRecords(){ DBManager.getInstance().command("DELETE FROM " + tableName()); }

    public void verifyDataBase(){
        DBManager.getInstance().command("CREATE TABLE if not exists " + tableName() + " (" + tableSchema() + ")");
    }

    public ResultSet query(String query){
        return DBManager.getInstance().query(query);
    }

    public ArrayList all() {
        if (tableExists()) {
            ResultSet data = this.query("SELECT * FROM " + tableName());
            return getFromResultSet(data);
        }
        else {
            return new ArrayList();
        }

    }


    public ArrayList allWhere(String where) {
        if (tableExists()) {
            ResultSet data = this.query("SELECT * FROM " + tableName() + " WHERE " + where);
            return getFromResultSet(data);
        }
        else {
            return new ArrayList();
        }
    }

    public <T extends SQLRow> LazyList<T> lazyAllWhere(String where){
        if (tableExists()){
            String tablename = this.tableName();
            LazyList items = new LazyList<T>(tablename, where, this.getClass());

            return items;
        }
        else {
            return new LazyList<T>();
        }
    }

    public <T extends SQLRow> LazyList<T> lazyAll(){
        return lazyAllWhere("");
    }

    public ArrayList<SQLRow> getFromResultSet(ResultSet data) {
        return getFromResultSet(data, getClass());
    }

    public ArrayList<SQLRow> getFromResultSet(ResultSet data, Class<? extends SQLRow> type)  {
        ArrayList<SQLRow> results = new ArrayList<>();


        try {
            if (data == null){
                return new ArrayList<>();
            }
            while(data.next()){
                SQLRow item = type.newInstance();
                Field[] fields = item.getClass().getFields();
                item.id = data.getInt("id");
                for (Field field : fields){
                    populateField(item, field, data);
                }
                results.add(item);
            }
        } catch (SQLException e) {
            //todo: find a better use than this
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            //todo: also this
            e.printStackTrace();
        } catch (InstantiationException e) {
            //todo: also this
            e.printStackTrace();
        }

        return results;
    }

    public <T> T where(String where){
        ArrayList <T> returned = this.allWhere(where);
        if (returned.size() > 0){
            return returned.get(0);
        }
        else {
            return null;
        }
    }

    public void populateField(SQLRow obj, Field f, ResultSet s) throws SQLException, IllegalAccessException, InstantiationException {
        Class<?> type = f.getType();
        String name = f.getName();
        if (type == int.class){
            int value = s.getInt(name);
            f.setInt(obj, value);
        }
        else if (type == float.class){
            float value = s.getFloat(name);
            f.setFloat(obj, value);
        }
        else if (type == boolean.class){
            boolean tf = s.getInt(name) == 1 ? true: false;
            f.setBoolean(obj, tf);
        }
        else if (type == String.class){
            String value = s.getString(name);
            f.set(obj, value);
        }
        else if (type == Date.class){
            String time = s.getString(name);
            Date date = getDateFromString(time);
            f.set(obj, date);
        }
        else if (SQLRow.class.isAssignableFrom(type)){
            int id = s.getInt(name);
            SQLRow object = getRelationFromID(f, id);
            f.set(obj, object);
        }
    }

    public static Date getDateFromString(String d){
        Date date = new Date();
        Long t = Long.parseLong(d);
        date.setTime(t);
        return date;
    }

}
