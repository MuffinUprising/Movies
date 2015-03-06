import java.sql.*;

public class MovieDatabase {
    private static String protocol = "jdbc:derby:";
    private static String dbName = "movieDB";
    private static final String USER = "user";
    private static final String PASS = "password";
    static Statement statement = null;
    static Connection conn = null;
    static ResultSet rs = null;

    public final static String movieTableName = "movies";
    public final static String titleColumn = "title";
    public final static String yearColumn = "year_released";
    public final static String ratingColumn = "rating";

    private static MovieDataModel movieDataModel;

    public static void main(String args[]) {
        //(If needed) create database and add sample data
        setup();
        reloadAllMovies();
        MovieForm tableGUI = new MovieForm(movieDataModel);
        if (rs!=null) {

        } else {
            shutdown();
        }
    }

    public static void reloadAllMovies(){
        try{
            //Query the database to fetch all of the data

            if (rs!=null) {
                rs.close();
            }
            String getAllData = "SELECT * FROM movies";
            rs = statement.executeQuery(getAllData);

            if (movieDataModel == null) {
                movieDataModel = new MovieDataModel(rs);
            } else {
                movieDataModel.updateResultSet(rs);
            }


           // movieDataModel.updateResultSet(rs);

        } catch (Exception e) {
            e.printStackTrace();
            shutdown();
        }


    }

    public static void setup(){
        try {
            conn = DriverManager.getConnection(protocol + dbName + ";create=true", USER, PASS);

            //The first argument allows us to move both forward and backwards through the RowSet
            // we get from this statement.
            // The TableModel will need to do go backward and forward.
            // by default, you can only move forward - it's what most apps do and it's less            
            // resource-intensive than being able to go in both directions.            
            // If you set one argument, you need the other. 
            // The second one (CONCUR_UPDATABLE) means you will be able to change the ResultSet and see the changes in the DB
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE, ResultSet.HOLD_CURSORS_OVER_COMMIT);

            //Create a table in the database with 3 columns: Movie title, year and rating
            
            String createTableSQL = "CREATE TABLE " + movieTableName + " ("+ titleColumn + " varchar(50), "+ yearColumn + " int, " + ratingColumn + " int)";
            statement.executeUpdate(createTableSQL);
            System.out.println("Created movies table");
            //Add some test data          
            String addDataSQL = "INSERT INTO movies VALUES ('Terminator', 1995, 5)";
            statement.executeUpdate(addDataSQL);
             addDataSQL = "INSERT INTO movies VALUES ('Lego', 1985, 3)";
            statement.executeUpdate(addDataSQL);
             addDataSQL = "INSERT INTO movies VALUES ('Grrr', 1999, 4)";

            statement.executeUpdate(addDataSQL);

        } catch (SQLException se) {
            System.out.println("The Movie table (probably) already exists, verify with following error message.");
            System.out.println(se);
        }
    }




    public static void shutdown(){
        try {
            if (rs != null) {
                rs.close();
                System.out.println("Result set closed");
            }
        } catch (SQLException se) {
                se.printStackTrace();
        }

        try {
            if (statement != null) {
                statement.close();
                System.out.println("Statement closed");
            }
        } catch (SQLException se){
            //Closing the connection could throw an exception too         
            se.printStackTrace();
        }

        try {
            if (conn != null) {
                conn.close();  //Close connection to database             
                System.out.println("Database connection closed");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
