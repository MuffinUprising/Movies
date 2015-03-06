import java.sql.*;

public class MovieDatabase {
    private static final String PROTOCOL = "jdbc:derby:";
    private static final String DB_NAME = "movieDB";
    private static final String USER = "user";
    private static final String PASS = "password";

    static Statement statement = null;
    static Connection conn = null;
    static ResultSet rs = null;

    public final static String MOVIE_TABLE_NAME = "movies";
    public final static String TITLE_COLUMN = "title";
    public final static String YEAR_COLUMN = "year_released";
    public final static String RATING_COLUMN = "rating";

    public final static int MOVIE_MIN_RATING = 1;
    public final static int MOVIE_MAX_RATING = 5;

    private static MovieDataModel movieDataModel;

    public static void main(String args[]) {

        //setup creates database (if it doesn't exist), opens connection, and adds sample data
        setup();
        loadAllMovies();

        //Start GUI

        MovieForm tableGUI = new MovieForm(movieDataModel);

    }

    //Create or recreate a ResultSet containing the whole database, and give it to movieDataModel
    public static void loadAllMovies(){

        try{

            if (rs!=null) {
                rs.close();
            }

            String getAllData = "SELECT * FROM movies";
            rs = statement.executeQuery(getAllData);

            if (movieDataModel == null) {
                //If no current movieDataModel, then make one
                movieDataModel = new MovieDataModel(rs);
            } else {
                //Or, if one already exists, update its ResultSet
                movieDataModel.updateResultSet(rs);
            }

        } catch (Exception e) {
            System.out.println("Error loading or reloading movies");
            System.out.println(e);
        }

    }

    public static void setup(){
        try {
            conn = DriverManager.getConnection(PROTOCOL + DB_NAME + ";create=true", USER, PASS);

            // The first argument ResultSet.TYPE_SCROLL_INSENSITIVE
            // allows us to move the cursor both forward and backwards through the RowSet
            // we get from this statement.

            // (Some databases support TYPE_SCROLL_SENSITIVE, which means the ResultSet will be updated when
            // something else changes the database. Since Derby is embedded we don't need to worry about anything
            // else updating the database. If you were using a server DB you might need to be concerned about this.)

            // The TableModel will need to go forward and backward through the ResultSet.
            // by default, you can only move forward - it's less
            // resource-intensive than being able to go in both directions.            
            // If you set one argument, you need the other. 
            // The second one (CONCUR_UPDATABLE) means you will be able to change the ResultSet and see the changes in the DB
            statement = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

            //Create a table in the database with 3 columns: Movie title, year and rating
            String createTableSQL = "CREATE TABLE " + MOVIE_TABLE_NAME + " ("+ TITLE_COLUMN + " varchar(50), "+ YEAR_COLUMN + " int, " + RATING_COLUMN + " int)";
            statement.executeUpdate(createTableSQL);

            System.out.println("Created movies table");
            //Add some test data          
            String addDataSQL = "INSERT INTO movies VALUES ('Back to the future', 1985, 5)";
            statement.executeUpdate(addDataSQL);
            addDataSQL = "INSERT INTO movies VALUES ('Back to the Future II', 1989, 4)";
            statement.executeUpdate(addDataSQL);
            addDataSQL = "INSERT INTO movies VALUES ('Back to the Future III', 1990, 3)";
            statement.executeUpdate(addDataSQL);

        } catch (SQLException se) {
            System.out.println("The Movie table (probably) already exists, verify with following error message.");
            System.out.println(se);
        }
    }

    //Close the ResultSet, statement and connection, in that order.
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
                conn.close();
                System.out.println("Database connection closed");
            }
        }
        catch (SQLException se) {
            se.printStackTrace();
        }
    }
}
