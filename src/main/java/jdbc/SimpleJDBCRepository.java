// SimpleJDBCRepository.java
package jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleJDBCRepository {
  private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?)";
  private static final String updateUserSQL = "UPDATE myusers SET firstname=?, lastname=?, age=? WHERE id=?";
  private static final String deleteUserSQL = "DELETE FROM myusers WHERE id=?";
  private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id=?";
  private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname=?";
  private static final String findAllUserSQL = "SELECT * FROM myusers";

  public Long createUser(User user) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setInt(3, user.getAge());
      ps.executeUpdate();
      ResultSet rs = ps.getGeneratedKeys();
      if (rs.next()) {
        return rs.getLong(1);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public User findUserById(Long userId) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByIdSQL)) {
      ps.setLong(1, userId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public User findUserByName(String userName) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByNameSQL)) {
      ps.setString(1, userName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        return User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public List<User> findAllUser() {
    List<User> users = new ArrayList<>();
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(findAllUserSQL)) {
      while (rs.next()) {
        users.add(User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return users;
  }

  public User updateUser(User user) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(updateUserSQL)) {
      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setInt(3, user.getAge());
      ps.setLong(4, user.getId());
      int affectedRows = ps.executeUpdate();
      if (affectedRows > 0) {
        return findUserById(user.getId());
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return null;
  }

  public void deleteUser(Long userId) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(deleteUserSQL)) {
      ps.setLong(1, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
