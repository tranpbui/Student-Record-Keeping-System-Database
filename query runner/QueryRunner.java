package queryrunner;

import javax.swing.*;
import java.awt.*;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

/**
 * QueryRunner takes a list of Queries that are initialized in it's constructor
 * and provides functions that will call the various functions in the QueryJDBC class 
 * which will enable MYSQL queries to be executed. It also has functions to provide the
 * returned data from the Queries. Currently the eventHandlers in QueryFrame call these
 * functions in order to run the Queries.
 * Improved for by Group 5.
 * Improvements we did are:
 * - Enabled the console mode exceeding the requirements
 *   o We display all query options the user can choose from.
 *   o We allow the user to pick a pre-listed query or enter a custom one.
 *   o We display labels and get user input for the query parameters.
 *   o We execute the query and display the results.
 *   o We properly display the errors if there are any database operation failures.
 * - BUGFIX : In the QueryRunner object, error was not getting updated upon database
 *   operation failure before.
 *   Due to that, it wasn't showing errors properly in the GUI or on the console.
 * - Added new fields: host, database, username, password to QueryRunner object.
 *   That way we can prepopulate them and share between GUI and console
 * - Reorganized fields and methods.
 * - Added code comments for readability.
 */
public class QueryRunner {
    private QueryJDBC m_jdbcData = new QueryJDBC();
    private String m_error = "";
    private String m_projectTeamApplication = "Webistrar";
    private ArrayList<QueryData> m_queryArray = new ArrayList<>();
    private int m_updateAmount = 0;
    String host = "nguyencase1.ckn4e94iihzi.us-east-1.rds.amazonaws.com";
    String user = "admin";
    String pass = "SUStudents808";
    String database = "Group5Collab";

    // Constructor that sets up the query runner with the connection to the database and
    // the set of pre-listed queries.
    public QueryRunner() {
        // Each row that is added to m_queryArray is a separate query.
        // It does not work on Stored procedure calls.
        // The 'new' Java keyword is a way of initializing the data that will be added to QueryArray.
        // Format for each row of m_queryArray is:
        // (QueryText, ParamaterLabelArray[], LikeParameterArray[], IsItActionQuery, IsItParameterQuery)
        
        //    QueryText: a String that represents your query. It can be anything but Stored Procedure.
        //    Parameter Label Array: null if there are no Parameters in the query, param names otherwise.
        //    LikeParameter Array: an array to tell which parameter has a LIKE Clause.
        //    IsItActionQuery: true if it is, otherwise false.
        //    IsItParameterQuery: true if it is, otherwise false.
        
//        m_queryArray.add(new QueryData(
//            "Select * from contact",
//            null,
//            null,
//            false,
//            false));   // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
//        m_queryArray.add(new QueryData(
//            "Select * from contact where contact_id=?",
//            new String [] {"CONTACT_ID"},
//            new boolean [] {false},
//            false,
//            true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
//        m_queryArray.add(new QueryData(
//            "Select * from contact where contact_name like ?",
//            new String [] {"CONTACT_NAME"},
//            new boolean [] {true},
//            false,
//            true));        // THIS NEEDS TO CHANGE FOR YOUR APPLICATION
//        m_queryArray.add(new QueryData(
//            "insert into contact (contact_id, contact_name, contact_salary) values (?,?,?)",
//            new String [] {"CONTACT_ID", "CONTACT_NAME", "CONTACT_SALARY"},
//            new boolean [] {false, false, false},
//            true,
//            true));// THIS NEEDS TO CHANGE FOR YOUR APPLICATION
        // 1: Show the average exam grade.
        m_queryArray.add(new QueryData(
                "SELECT AVG(ExamGrade)\n" +
                   "FROM Exam",
                null,
                null,
                false,
                false));

        // 2: List all of the instructors that work in "A" building, sorted by room number.
        m_queryArray.add(new QueryData(
                "SELECT DISTINCT InstructorLName, InstructorFName\n" +
                        "FROM Section JOIN Instructor USING(InstructorID)\n" +
                        "WHERE RoomNumber LIKE ?\n" +
                        "ORDER BY RoomNumber",
                new String [] {"RoomNumber"},
                new boolean [] {true},
                false,
                true));

        // 3: List all class times in room A202.
        m_queryArray.add(new QueryData(
                "SELECT RoomNumber, SectionDays, SectionTime\n" +
                        "FROM Section\n" +
                        "WHERE RoomNumber = ?\n" +
                        "ORDER BY SectionTime DESC",
                new String [] {"RoomNumber"},
                new boolean [] {false},
                false,
                true));

        // 4: Find the contact information for instructors with the last name "Durnall".
        m_queryArray.add(new QueryData(
                "SELECT InstructorLName, InstructorFName, InstructorEmail, InstructorPhone\n" +
                        "FROM Instructor\n" +
                        "WHERE InstructorLNAME = ?",
                new String [] {"InstructorLNAME"},
                new boolean [] {false},
                false,
                true));

        // 5: List the courses offered in the Winter quarter.
        m_queryArray.add(new QueryData(
                "SELECT QuarterDesc, CourseName\n" +
                        "FROM Quarter JOIN Section USING(QuarterID) JOIN Course USING(CourseID)\n" +
                        "WHERE QuarterDesc = ?",
                new String [] {"QuarterDesc"},
                new boolean [] {false},
                false,
                true));

        // 6: Return the number of students that have graduated before 2020.
        m_queryArray.add(new QueryData(
                "SELECT COUNT(StudentID)\n" +
                        "FROM Student\n" +
                        "WHERE StudentGradYear < ?",
                new String [] {"StudentGradYear"},
                new boolean [] {false},
                false,
                true));

        // 7: Return the name of students that were in the group named "Gorilla gorilla".
        m_queryArray.add(new QueryData(
                "SELECT CONCAT(StudentFNAME, ' ' ,StudentLNAME) StudentFULLNAME\n" +
                        "FROM Student JOIN Record USING(RecordID) JOIN Assignment USING (AssignmentID) JOIN Project USING (ProjectID)\n" +
                        "WHERE GroupName = ?\n" +
                        "ORDER BY StudentFULLNAME",
                new String [] {"GroupName"},
                new boolean [] {false},
                false,
                true));

        // 8: List the exam history of the student with the ID '41908376'.
        m_queryArray.add(new QueryData(
                "SELECT RecordID, AssignmentID, ExamID, ExamGrade\n" +
                        "FROM Student JOIN Record USING(RecordID) JOIN Assignment USING(AssignmentID) JOIN Exam USING(ExamID)\n" +
                        "WHERE StudentID = ?",
                new String [] {"StudentID"},
                new boolean [] {false},
                false,
                true));

        // 9: Show the class schedule for student(s) named "Tilly Lepope".
        m_queryArray.add(new QueryData(
                "SELECT StudentID, SectionID, CourseName, CourseCredit, SectionDays, SectionTime, RoomNumber\n" +
                        "FROM Student JOIN SectionStudent USING(StudentID) JOIN Section USING(SectionID) JOIN Course USING(CourseID)\n" +
                        "WHERE StudentFName = ? AND StudentLName = ?",
                new String [] {"StudentFName", "StudentLName"},
                new boolean [] {false, false},
                false,
                true));

        // 10: Show the names of all students in the secton with ID '17009066', ordered by last name.
        m_queryArray.add(new QueryData(
                "SELECT SectionID, StudentID, StudentLName, StudentFName\n" +
                        "FROM Section JOIN SectionStudent USING(SectionID) JOIN Student USING(StudentID)\n" +
                        "WHERE SECTIONID = ?\n" +
                        "ORDER BY StudentLName",
                new String [] {"SECTIONID"},
                new boolean [] {false},
                false,
                true));

        // 11: Show the full names of the graduated (2021) students with AVG(ExamGrade) >= 90, order by name.
        m_queryArray.add(new QueryData(
                "SELECT CONCAT(StudentFNAME, ' ' ,StudentLNAME) StudentFullName, RecordID, AssignmentID, ExamID, ROUND(AVG(ExamGrade),2) AS AVG_ExamGrade\n" +
                        "FROM Student JOIN Record USING(RecordID) JOIN Assignment USING(AssignmentID) \n" +
                        "JOIN Exam USING (ExamID) JOIN Quarter USING (QuarterID)\n" +
                        "WHERE StudentGradYear <= ?\n" +
                        "GROUP BY CONCAT(StudentFNAME, ' ' ,StudentLNAME), RecordID, AssignmentID, ExamID\n" +
                        "HAVING AVG_ExamGrade >= ?\n" +
                        "ORDER BY StudentFullName",
                new String [] {"StudentGradYear", "AVG_ExamGrade"},
                new boolean [] {false, false},
                false,
                true));

        // 12: update Student StudentGradYear = 2009 where StudentID = 61749685
        m_queryArray.add(new QueryData(
                "UPDATE Student SET StudentGradYear = ? where StudentID = ?",
                new String [] {"StudentGradYear", "StudentID"},
                new boolean [] {false, false},
                true,
                true));

        // WRITE YOUR OWN: SELECT StudentGradYear FROM Student WHERE StudentID = 61749685
    }

    // Returns the number of pre-listed queries.
    public int GetTotalQueries()
    {
        return m_queryArray.size();
    }

    // Returns the number of parameters for the query with the given index.
    public int GetParameterAmtForQuery(int queryChoice) {
        if (queryChoice < 0) return 0;
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetParmAmount();
    }

    // Returns the parameter name for the query and the param with the given indices.
    public String GetParamText(int queryChoice, int parmnum) {
       QueryData e = m_queryArray.get(queryChoice);
       return e.GetParamText(parmnum); 
    }

    // Returns the query statement for the query with the given index.
    public String GetQueryText(int queryChoice) {
        QueryData e=m_queryArray.get(queryChoice);
        return e.GetQueryString();        
    }
    
    // Returns how many rows were updated as a result of the update query.
    public int GetUpdateAmount()
    {
        return m_updateAmount;
    }
    
    // Returns ALL of the Column Headers from the query.
    public String [] GetQueryHeaders()
    {
        return m_jdbcData.GetHeaders();
    }
    
    // After the query has been run, returns all of the data has been captured into
    // a multi-dimensional string array which contains all the row's. For each
    // row it also has all the column data. It is in string format.
    public String[][] GetQueryData() {
        return m_jdbcData.GetData();
    }

    // Returns the project team application name.
    public String GetProjectTeamApplication() {
        return m_projectTeamApplication;        
    }

    // Returns whether the query with the given index is an action (INSERT or UPDATE) query.
    public boolean  isActionQuery (int queryChoice) {
        if (queryChoice < 0) return false;
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryAction();
    }

    // Returns whether the query with the given index is a parameterized query.
    public boolean isParameterQuery(int queryChoice) {
        if (queryChoice < 0) return false;
        QueryData e=m_queryArray.get(queryChoice);
        return e.IsQueryParm();
    }

    // Executes a non-action (SELECT) query.
    public boolean ExecuteQuery(int queryChoice, String [] parms) {
        QueryData e=m_queryArray.get(queryChoice);
        boolean bOK = m_jdbcData.ExecuteQuery(e.GetQueryString(), parms, e.GetAllLikeParams());
        // BUGFIX : error was not getting updated before
        if (!bOK) {
            m_error = m_jdbcData.GetError();
        }
        return bOK;
    }

    // Executes an action (INSERT or UPDATE) query.
    public boolean ExecuteUpdate(int queryChoice, String [] parms) {
        QueryData e=m_queryArray.get(queryChoice);
        boolean bOK = m_jdbcData.ExecuteUpdate(e.GetQueryString(), parms);
        m_updateAmount = m_jdbcData.GetUpdateCount();
        // BUGFIX : error was not getting updated before
        if (!bOK) {
            m_error = m_jdbcData.GetError();
        }
        return bOK;
    }

    // Executes a "Write your own" query.
    public boolean Execute(String query) {
        boolean bOK = true;
        String[] parms = new String[0];
        if (query.trim().toLowerCase().startsWith("select ")) {
            bOK = m_jdbcData.ExecuteQuery(query, parms, null);
        } else {
            bOK = m_jdbcData.ExecuteUpdate(query, parms);
            m_updateAmount = m_jdbcData.GetUpdateCount();
        }

        if (!bOK) {
            m_error = m_jdbcData.GetError();
        }

        return bOK;
    }

    // Establishes a connection to the SQL database.
    public boolean Connect(String szHost, String szUser, String szPass, String szDatabase) {
        boolean bConnect = m_jdbcData.ConnectToDatabase(szHost, szUser, szPass, szDatabase);
        if (bConnect == false)
            m_error = m_jdbcData.GetError();        
        return bConnect;
    }

    // Disconnects from the SQL database.
    public boolean Disconnect() {
        // Disconnect the JDBCData Object
        boolean bConnect = m_jdbcData.CloseDatabase();
        if (bConnect == false)
            m_error = m_jdbcData.GetError();
        return true;
    }

    // Returns the error if there was any.
    public String GetError() {
        return m_error;
    }

    /**
     * Entry point to our program.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        final QueryRunner queryrunner = new QueryRunner();
        
        if (args.length == 0) {
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new QueryFrame(queryrunner).setVisible(true);
                }            
            });
        } else if (args[0].equals ("-console")) {
            QueryRunner runner = new QueryRunner();
            Scanner scan = new Scanner(new InputStreamReader(System.in));
            // Connect
            boolean ok = runner.Connect(runner.host, runner.user, runner.pass, runner.database);
            if (!ok) {
                System.out.println(runner.m_error);
                return;
            }

            while (true) {
                System.out.println("\nPlease select one of the following options:");
                // Show all the pre-listed queries and "write your own" option.
                for (int i = 0; i < runner.GetTotalQueries(); i++) {
                    System.out.println("Query [" + (i + 1) + "]:" + runner.GetQueryText(i) + "\n");
                }
                System.out.println("[W]rite your own");
                System.out.println("[Q]uit");
                String userSelect = scan.nextLine();
                if (userSelect.toLowerCase().startsWith("q")) break;
                int queryChoice = 0;
                if (userSelect.toLowerCase().startsWith("w"))
                    queryChoice = -1;
                else
                    queryChoice = Integer.parseInt(userSelect) - 1;

                String[] params = new String[0];
                // Is it a query that has parameters
                if (runner.isParameterQuery(queryChoice)) {
                    // Find out how many parameters it has
                    int amt = runner.GetParameterAmtForQuery(queryChoice);
                    // Create a parameter array of strings for that amount
                    params = new String[amt];
                    for (int j = 0; j < amt; j++) {
                        // Get the parameter label for query
                        String paramLabel = runner.GetParamText(queryChoice, j);
                        // Print it to console and ask the user to enter a value
                        System.out.println("Enter the value for parameter " + paramLabel + ": ");
                        // Take the value you got and put it into your parameter array
                        params[j] = scan.nextLine();
                    }
                }

                if (queryChoice < 0) {
                    System.out.println("Enter your query:");
                    String query = scan.nextLine();
                    ok = runner.Execute(query);
                    if (ok) {
                        if (query.toLowerCase().startsWith("select")) {
                            // Get the headers
                            String[] headers = runner.GetQueryHeaders();
                            for (int h = 0; h < headers.length; h++) {
                                System.out.print(headers[h] + "\t");
                            }
                            System.out.println();

                            // Get the results of the query data
                            String[][] queryData = runner.GetQueryData();
                            // Print all the results of query data
                            for (int a = 0; a < queryData.length; a++) {
                                for (int b = 0; b < queryData[a].length; b++) {
                                    System.out.print(queryData[a][b] + "\t");
                                }
                                System.out.println();
                            }
                        } else {
                            // Find out how many rows were affected
                            int updateAmount = runner.GetUpdateAmount();
                            // Print the value
                            System.out.println(updateAmount + " rows affected.");
                        }
                    } else {
                        System.out.println(runner.m_error);
                    }
                } else if (runner.isActionQuery(queryChoice)) {
                    // Run the action query
                    boolean updated = runner.ExecuteUpdate(queryChoice, params);
                    // Check if updated
                    if (updated) {
                        // Find out how many rows were affected
                        int updateAmount = runner.GetUpdateAmount();
                        // Print the value
                        System.out.println(updateAmount + " rows affected.");
                    } else {
                        // Update failed
                        System.out.println(runner.m_error);
                    }
                } else {
                    // It is not an action query
                    boolean success = runner.ExecuteQuery(queryChoice, params);
                    // Check if query is executed
                    if (success) {
                        // Get the headers
                        String[] headers = runner.GetQueryHeaders();
                        for (int h = 0; h < headers.length; h++) {
                            System.out.print(headers[h] + "\t");
                        }
                        System.out.println();

                        // Get the results of the query data
                        String[][] queryData = runner.GetQueryData();
                        // Print all the results of query data
                        for (int a = 0; a < queryData.length; a++) {
                            for (int b = 0; b < queryData[a].length; b++) {
                                System.out.print(queryData[a][b] + "\t");
                            }
                            System.out.println();
                        }
                    } else {
                        // If query failed
                        System.out.println(runner.m_error);
                    }
                }
                System.out.println("\nHit ENTER.");
                scan.nextLine();
            }

            // Disconnect from the database
            ok = runner.Disconnect();
            if (!ok) {
                System.out.println(runner.m_error);
            }
        }
    }    
}
