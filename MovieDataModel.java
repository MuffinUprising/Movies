import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by admin on 3/5/15.
 */
public class MovieDataModel extends AbstractTableModel {

    private int rowCount = 0;
    private int colCount = 0;
    ResultSet resultSet;
    public MovieDataModel(ResultSet rs) {
        this.resultSet = rs;
        setup();
    }

    private void setup(){

        countRows();
        try{
            colCount = resultSet.getMetaData().getColumnCount();

        } catch (SQLException se) {
            System.out.println("setup colcount error" + se);
        }

    }

    public void updateResultSet(ResultSet newRS){
        resultSet = newRS;
        setup();
    }


    private void countRows() {
        rowCount = 0;
        try {
            resultSet.beforeFirst();
            while (resultSet.next()) {
                rowCount++;

            }
            resultSet.beforeFirst();

        } catch (SQLException se) {
            //TODO
            System.out.println("Count rows " + se);
        }
      //  System.out.println("There are this many rows " + rowCount);
    }
    @Override
    public int getRowCount() {
        countRows();
        return rowCount;
    }

    @Override
    public int getColumnCount(){
        return colCount;
    }

    @Override
    public Object getValueAt(int row, int col){
        try{
          //  System.out.println("get value at, row = " +row);
            resultSet.absolute(row+1);
            Object o = resultSet.getObject(col+1);
            return o.toString();
        }catch (SQLException se) {
            System.out.println(se);
            //se.printStackTrace();
            return se.toString();

        }
    }

    @Override
    //This is called when user edits an editable cell
    public void setValueAt(Object newValue, int row, int col) {
        System.out.println("set value at" + newValue + row + " " + col);

        //Make sure o is an integer

        int newRating;

        try {
            newRating = Integer.parseInt(newValue.toString());

        } catch (NumberFormatException ne) {
            //TODO error dialog box
            System.out.println("Try entering a number");
            return;
        }

        try {
            resultSet.absolute(row + 1);
            resultSet.updateInt(MovieDatabase.ratingColumn, newRating);
            resultSet.updateRow();
            fireTableDataChanged();
        } catch (SQLException e) {
            System.out.println("error changing rating " + e);
        }


    }


    @Override
    //We only want user to be able to edit column 2 - the rating column.
    //If this method always returns true, the whole table will be editable
    public boolean isCellEditable(int row, int col){
        if (col == 2) {
            return true;
        }
        return false;
    }


    public boolean deleteRow(int row){
        //Delete current row
        try {
            resultSet.absolute(row + 1);
            resultSet.deleteRow();
            fireTableDataChanged();
            return true;
        }catch (SQLException se) {
            System.out.println("Delete row error " + se);
            return false;
        }
    }

    //returns true if successful, false if error occurs
    public boolean insertRow(String title, int year, int rating) {

       try {

            resultSet.moveToInsertRow();
            resultSet.updateString(MovieDatabase.titleColumn, title);
            resultSet.updateInt(MovieDatabase.yearColumn, year);
            resultSet.updateInt(MovieDatabase.ratingColumn, rating);
            resultSet.insertRow();
            resultSet.moveToCurrentRow();
            System.out.println("ADDED ROW rowcount is now" + rowCount);
            fireTableDataChanged();

           //TODO change goes to DB but is not reflected in this result set
           //TODO so need to close and re-open result set to see latest data
           //
           return true;

        } catch (SQLException e) {
            //TODO
            System.out.println(e);
            e.printStackTrace();
           return false;
        }

    }


}
