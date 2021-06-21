package queryrunner;

/**
 * An object that contains a pre-listed, potentially parameterized query.
 * Improved for by Group 5.
 * Improvements we did are:
 * - Reorganized fields and methods.
 * - Added code comments for readability.
 */
public class QueryData {
    private String m_queryString;
    private String [] m_arrayParms;
    private boolean m_isAction;
    private boolean m_isParms;
    private boolean [] m_arrayLikeParms;

    // Constructor that initializes all the class members.
    QueryData(String query, String[] parms, boolean [] likeparms, boolean isAction, boolean isParm) {
        m_queryString = query;
        m_arrayParms = parms;
        m_arrayLikeParms = likeparms;
        m_isAction = isAction;
        m_isParms = isParm;
    }

    // Returns the SQL query string.
    String GetQueryString() {
        return m_queryString;
    }

    // Returns the number of parameters for the query.
    int GetParmAmount() {
        if (m_arrayParms == null)
            return 0;
        else
            return m_arrayParms.length;
    }

    // Returns the name of the parameter with the input index.
    String GetParamText(int index) {
        return m_arrayParms[index];
    }

    // Returns whether the parameter with the input index is a like parameter or not.
    boolean GetLikeParam(int index) {
        return m_arrayLikeParms[index];
    }

    // Returns all LIKE parameters for the query.
    boolean [] GetAllLikeParams() {
        return m_arrayLikeParms;
    }

    // Return whether this is an action query or not.
    // UPDATE and INSERT statements are action queries. SELECT statements are not.
    boolean IsQueryAction() {
        return m_isAction;
    }

    // Returns whether the query is parameterized or not.
    boolean IsQueryParm() {
        return m_isParms;
    }
}
