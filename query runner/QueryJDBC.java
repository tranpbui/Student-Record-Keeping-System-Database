package queryrunner;

import java.sql.Connection;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC data access layer that allows to create a connection to a SQL database, execute queries,
 * retrieve results (or errors), and close the connection to the database when done.
 * Improved for by Group 5.
 * Improvements we did are:
 * - BUG FIX:
 *   CloseDatabase() method was throwing an error before due to SSL connection failing.
 *   We added "useSSL=false" to the JDBC connection string to fix that problem.
 * - UI FIX:
 *   Error string was not getting constructed consistently before.
 *   It was missing new-line for some cases, didn't label SQL state and vendor error in other cases.
 * - Removed unused or unnecessary members: Default constructor, m_url and m_user fields.
 * - Reorganized fields and methods.
 * - Added code comments for readability.
 */
public class QueryJDBC {
    public Connection m_conn = null; 
    static final String DB_DRV = "com.mysql.jdbc.Driver";
    String m_error = "";
    String [] m_headers;
    String [][] m_allRows;
    int m_updateAmount = 0;

    // Returns the the last error.
    public String GetError()
    {
        return m_error;
    }

    // Returns the headers of the last query result.
    public String [] GetHeaders()
    {
        return this.m_headers;
    }

    // Returns the data of the last query result.
    public String [][] GetData()
    {
        return this.m_allRows;
    }

    // Returns the number of queries updated by the last query.
    public int GetUpdateCount()
    {
        return m_updateAmount;
    }
    
    // Executes a non-action (SELECT) query.
    public boolean ExecuteQuery(String szQuery, String [] parms, boolean [] likeparms) {
        PreparedStatement preparedStatement = null;        
        ResultSet resultSet = null;
        int nColAmt;
        boolean bOK = true;
        // Try to get the columns and the amount of columns
        try  {
            preparedStatement=this.m_conn.prepareStatement(szQuery);
            int nParamAmount = parms.length;
            for (int i=0; i < nParamAmount; i++) {
                String parm = parms[i];
                if (likeparms[i] == true) {
                    parm += "%";
                }
                preparedStatement.setString(i+1, parm);
            }

            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData rsmd = resultSet.getMetaData(); 
            nColAmt = rsmd.getColumnCount();
            m_headers = new String [nColAmt];
            
            for (int i=0; i< nColAmt; i++) {
                m_headers[i] = rsmd.getColumnLabel(i+1);
            }

            int amtRow = 0;
            while (resultSet.next()){
                amtRow++;
            }

            if (amtRow > 0) {
                this.m_allRows= new String [amtRow][nColAmt];
                resultSet.beforeFirst();
                int nCurRow = 0;
                while(resultSet.next()) {
                    for (int i=0; i < nColAmt; i++) {
                       m_allRows[nCurRow][i] = resultSet.getString(i+1);
                    }
                    nCurRow++;
                }                                
            } else {
                this.m_allRows= new String [1][nColAmt];               
                for (int i=0; i < nColAmt; i++) {
                   m_allRows[0][i] = "";
                }               
            }
                  
            preparedStatement.close();
            resultSet.close();            
        } catch (SQLException ex) {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "\nSQLState: " + ex.getSQLState();
            this.m_error += "\nVendorError: " + ex.getErrorCode();
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
                
        return true;
    }

    // Executes an action (INSERT or UPDATE) query.
    public boolean ExecuteUpdate(String szQuery, String [] parms) {
        PreparedStatement preparedStatement = null;        

        boolean bOK = true;
        m_updateAmount = 0;
        
        // Try to get the columns and the amount of columns
        try {
            preparedStatement = this.m_conn.prepareStatement(szQuery);
            int nParamAmount = parms.length;
            for (int i=0; i < nParamAmount; i++) {
                preparedStatement.setString(i+1, parms[i]);
            }
            
            m_updateAmount = preparedStatement.executeUpdate();
            preparedStatement.close();          
        } catch (SQLException ex) {
            bOK = false;
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "\nSQLState: " + ex.getSQLState();
            this.m_error += "\nVendorError: " + ex.getErrorCode();
            
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
            return false;
        }
                
        return true;
    }
   
    // Establishes a connection to the database.
    public boolean ConnectToDatabase(String host, String user, String pass, String database) {
        String url;
        url = "jdbc:mysql://";
        url += host;
        url +=":3306/";
        url += database;
        // BUGFIX: Added "useSSL=false" to the JDBC connections string because otherwise
        // and SSL connection error occurs when closing the connection.
        url +="?useSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        try {
            Class.forName(DB_DRV).newInstance();
            m_conn = DriverManager.getConnection(url,user,pass);
        } catch (SQLException ex) {
            this.m_error = "SQLException: " + ex.getMessage();
            this.m_error += "\nSQLState: " + ex.getSQLState();
            this.m_error += "\nVendorError: " + ex.getErrorCode();
            return false;
        } catch (Exception ex) {
            // handle the error
            m_error = "Exception: " + ex.getMessage();
            return false;
        }     
        
        return true;
    }

    // Closes the connection to the database.
    public boolean CloseDatabase() {
        try {
            m_conn.close();
        } catch (SQLException ex) {
            m_error = "SQLException: " + ex.getMessage();
            m_error = "\nSQLState: " + ex.getSQLState();
            m_error = "\nVendorError: " + ex.getErrorCode();
            return false;
        } catch (Exception ex) {
            m_error = "Exception: " + ex.toString();
            return false;
        }     
        
        return true;
    }
}
