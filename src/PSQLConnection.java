import java.sql.*;

public class PSQLConnection {
  private Connection conn;

  public PSQLConnection(String db) {
    try {
      conn = DriverManager.getConnection(
        String.format("jdbc:postgresql:%s", db),
        "postgres", // CHANGE TO YOUR POSTGRESQL USERNAME
        "password" // CHANGE TO YOUR POSTGRESQL PASSWORD
      );
    } catch(SQLException e) { System.err.println(e.getMessage()); }
  }

  public boolean execute(String query) throws SQLException {
    Statement st = conn.createStatement();
    return st.execute(query);
  }
  public ResultSet executeQuery(String query) throws SQLException {
    Statement st = conn.createStatement();
    return st.executeQuery(query);
  }
  public int executeUpdate(String query) throws SQLException {
    Statement st = conn.createStatement();
    return st.executeUpdate(query);
  }
}
