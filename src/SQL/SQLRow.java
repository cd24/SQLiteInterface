package SQL;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by John on 4/16/2015.
 */
public class SQLRow extends SQLizable{

    protected int id;

    public boolean isTable(Object o){
        return o.getClass().isAssignableFrom(SQLRow.class);
    }

    public SQLRow getRelationFromID(Field f, int id) throws IllegalAccessException, InstantiationException {
        Class<?> objClass = f.get(this).getClass();
        String[] names = objClass.getName().split("\\.");
        String className = names[names.length - 1].toLowerCase();
        System.out.println(className);

        int item = (int) f.get(this);
        ResultSet attributes = query("SELECT * FROM " + className + " WHERE id='" + item + "'");
        SQLRow object = (SQLRow) objClass.newInstance();

        object.getFromResultSet(attributes);
        return object;
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
            String fullName = getClass().getName();
            String[] components = fullName.split("\\.");
            String cName = components[components.length - 1];

            return cName.toLowerCase();
        }
        public int id() {
            return id;
        }
        public String dataBaseName() {
            return "SQLiteDatabase.db";
        }

        public void afterSave(){
            //This is a placeholder method that gives the user the ability to perform specific tasks immidiately after the save.
        }

        public void save(){
            verifyDataBase();
            String cmd = "INSERT INTO " + tableName() + " " + asSQLValue();

            if (existsInTable()){
                cmd = "UPDATE " + tableName() + " SET " + updateValue() + " WHERE id = " + this.id();
                command(cmd);
            }
            else {
                int id = command(cmd);
                this.id = id;
            }
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
                command("DELETE FROM " + tableName() + " WHERE id = " + id());
            }

            this.id = -1;
        }

        public void drop(){
            command("DROP TABLE if exists " + tableName());
        }

        public void verifyDataBase(){
            command("CREATE TABLE if not exists " + tableName() + " (" + tableSchema() + ")");
        }

        public int command(String command){
            Connection conn = null;
            int row = -1;
            try{
                Class.forName("org.sqlite.JDBC");

                conn = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName());
                PreparedStatement query = conn.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
                query.setQueryTimeout(30);

                query.executeUpdate();

                ResultSet Keys = query.getGeneratedKeys();
                if (Keys.next()) {
                    row = Keys.getInt(1);
                }

            } catch (SQLException e){
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return row;
        }

        public ResultSet query(String query){

            Connection conn = null;
            ResultSet set = null;

            try{
                Class.forName("org.sqlite.JDBC");

                conn = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName());
                Statement q = conn.createStatement();
                q.setQueryTimeout(30);
                System.out.println(query);
                set = q.executeQuery(query);

            } catch (SQLException e){
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return set;
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

         public QuerySet lazyAllWhere(String where){
             if (tableExists()){
                 QuerySet items = new QuerySet();
                 items.setQuery(this.tableName(), where);
                 items.prepare(this.getClass());
                 items.setDatabase(this.dataBaseName());

                 return items;
             }
             else {
                 return new QuerySet<>();
             }
        }

        public ArrayList getFromResultSet(ResultSet data)
        {
            ArrayList<SQLRow> results = new ArrayList<>();


            try {
                if (data == null){
                    return new ArrayList<>();
                }
                while(data.next()){
                    SQLRow item = getClass().newInstance();
                    Field[] fields = item.getClass().getFields();
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
            else if (type.isAssignableFrom(SQLRow.class)){
                int id = s.getInt(name);
                SQLRow object = getRelationFromID(f, id);
                f.set(obj, object);
            }
        }

        public Date getDateFromString(String d){
            Date date = new Date();
            Long t = Long.parseLong(d);
            date.setTime(t);
            return date;
        }

}
