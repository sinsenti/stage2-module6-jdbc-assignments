package jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomDataSource implements DataSource {

  private static volatile CustomDataSource instance;

  private final String driver;
  private final String url;
  private final String name;
  private final String password;

  private CustomDataSource(String driver, String url, String password, String name) {
    this.driver = driver;
    this.url = url;
    this.password = password;
    this.name = name;

    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  public static CustomDataSource getInstance() {
    if (instance == null) {
      synchronized (CustomDataSource.class) {
        if (instance == null) {
          // Load properties from app.properties (not shown here, assume loaded)
          String driver = "org.postgresql.Driver";
          String url = "jdbc:postgresql://localhost:5432/myfirstdb";
          String name = "your_db_username";
          String password = "your_db_password";

          instance = new CustomDataSource(driver, url, password, name);
        }
      }
    }
    return instance;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return java.sql.DriverManager.getConnection(url, name, password);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return java.sql.DriverManager.getConnection(url, username, password);
  }

  @Override
  public java.io.PrintWriter getLogWriter() throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLogWriter(java.io.PrintWriter out) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public java.util.logging.Logger getParentLogger() throws java.util.logging.LoggingException {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException();
  }
}
