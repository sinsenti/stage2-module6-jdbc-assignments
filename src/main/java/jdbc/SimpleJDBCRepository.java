package jdbc;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {
  private Connection connection = null;
  private PreparedStatement ps = null;
  private Statement st = null;

  private static final String createUserSQL = "INSERT INTO myusers (firstname, lastname, age) VALUES (?, ?, ?) RETURNING id";
  private static final String updateUserSQL = "UPDATE myusers SET firstname = ?, lastname = ?, age = ? WHERE id = ?";
  private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
  private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
  private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
  private static final String findAllUserSQL = "SELECT * FROM myusers";

  // Create user and return generated id
  public Long createUser(User user) {
    Long id = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(createUserSQL, Statement.RETURN_GENERATED_KEYS)) {

      ps.setString(1, user.getFirstName());
      ps.setString(2, user.getLastName());
      ps.setInt(3, user.getAge());

      int affectedRows = ps.executeUpdate();
      if (affectedRows == 0) {
        throw new RuntimeException("Creating user failed, no rows affected.");
      }

      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          id = rs.getLong(1);
        } else {
          throw new RuntimeException("Creating user failed, no ID obtained.");
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return id;
  }

  // Find user by Id
  public User findUserById(Long userId) {
    User user = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByIdSQL)) {
      ps.setLong(1, userId);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        user = User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build();
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return user;
  }

  // Find user by first name
  public User findUserByName(String userName) {
    User user = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByNameSQL)) {
      ps.setString(1, userName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        user = User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build();
      }
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return user;
  }

  // Find all users
  public List<User> findAllUser() {
    List<User> users = new ArrayList<>();
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findAllUserSQL);
        ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        User user = User.builder()
            .id(rs.getLong("id"))
            .firstName(rs.getString("firstname"))
            .lastName(rs.getString("lastname"))
            .age(rs.getInt("age"))
            .build();
        users.add(user);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return users;
  }

  // Update user and return updated user, or null if failed
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
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  // Delete user by id
  private void deleteUser(Long userId) {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(deleteUser)) {
      ps.setLong(1, userId);
      ps.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
