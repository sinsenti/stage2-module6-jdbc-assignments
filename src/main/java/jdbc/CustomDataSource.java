// CustomDataSource.java
package jdbc;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class CustomDataSource implements DataSource {
  private static volatile CustomDataSource instance;
  private final String driver;
  private final String url;
  private final String user;
  private final String password;

  private CustomDataSource(String driver, String url, String user, String password) {
    this.driver = driver;
    this.url = url;
    this.user = user;
    this.password = password;
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Driver class not found", e);
    }
  }

  public static CustomDataSource getInstance() {
    if (instance == null) {
      synchronized (CustomDataSource.class) {
        if (instance == null) {
          Properties props = new Properties();
          try (InputStream input = CustomDataSource.class.getClassLoader().getResourceAsStream("app.properties")) {
            if (input == null) {
              throw new RuntimeException("app.properties not found");
            }
            props.load(input);
          } catch (IOException e) {
            throw new RuntimeException("Error loading app.properties", e);
          }
          String driver = props.getProperty("postgres.driver");
          String url = props.getProperty("postgres.url");
          String user = props.getProperty("postgres.user");
          String password = props.getProperty("postgres.password");
          instance = new CustomDataSource(driver, url, user, password);
        }
      }
    }
    return instance;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return new CustomConnector().getConnection(url, user, password);
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return new CustomConnector().getConnection(url, username, password);
  }

  // Other DataSource methods with default implementations
  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public java.io.PrintWriter getLogWriter() throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public void setLogWriter(java.io.PrintWriter out) throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    throw new SQLException("Not supported");
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException("Not supported");
  }
}
