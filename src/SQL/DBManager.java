package SQL;

import java.sql.*;

public class DBManager {
    private static DBManager ourInstance = new DBManager();
    private Connection connection;
    private boolean logging_verbose = false;

    public static DBManager getInstance() {
        return ourInstance;
    }

    private DBManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String dataBaseName() {
        return "SQLiteDatabase.db";
    }

    public int command(String command){
        int row = -1;
        try{
            validateDB();
            PreparedStatement query = this.connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            query.setQueryTimeout(1000);

            query.executeUpdate();

            ResultSet Keys = query.getGeneratedKeys();
            if (Keys.next()) {
                row = Keys.getInt(1);
            }
        } catch (SQLException e){
        }
        return row;
    }

    public ResultSet query(String query){
        ResultSet set = null;

        try{
            validateDB();
            Statement q = this.connection.createStatement();
            q.setQueryTimeout(1000);
            if (logging_verbose)
                System.out.println(query);

            set = q.executeQuery(query);
        } catch (SQLException e){

            e.printStackTrace();
        }

        return set;
    }

    public void refresh() throws SQLException {
        if (logging_verbose)
            System.out.println("Refreshing connection to database: " + dataBaseName());
        if (connection != null){
            connection.close();
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName());
    }

    public void validateDB() throws SQLException {
        if (logging_verbose)
            System.out.println("Validating connection to database: " + dataBaseName());
        if (connection != null && !connection.isValid(1000)){
            return;
        }
        refresh();
    }

    @Deprecated
    public boolean dbLocked(){
        String query = "SELECT COUNT(*) FROM sqlite_master";
        try{
            validateDB();
            PreparedStatement q = this.connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            q.setQueryTimeout(1000);
            q.executeUpdate();
            return false;
        } catch (SQLException e){
            return true;
        }
    }
}
