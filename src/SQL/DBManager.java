package SQL;

import java.sql.*;

/**
 * Created by john on 9/15/15.
 */
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
            if (!connection.isValid(100)){
                refresh();
            }
            PreparedStatement query = this.connection.prepareStatement(command, Statement.RETURN_GENERATED_KEYS);
            query.setQueryTimeout(30);

            query.executeUpdate();

            ResultSet Keys = query.getGeneratedKeys();
            if (Keys.next()) {
                row = Keys.getInt(1);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }
        return row;
    }

    public ResultSet query(String query){
        ResultSet set = null;

        try{
            if (!connection.isValid(100)){
                refresh();
            }
            Statement q = this.connection.createStatement();
            q.setQueryTimeout(30);
            if (logging_verbose)
                System.out.println(query);

            set = q.executeQuery(query);
        } catch (SQLException e){

            e.printStackTrace();
        }

        return set;
    }

    public void refresh() throws SQLException {
        if (connection != null){
            connection.close();
        }
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataBaseName());
    }
}
