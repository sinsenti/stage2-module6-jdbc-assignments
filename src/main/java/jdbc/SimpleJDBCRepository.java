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
  private static final String deleteUser = "DELETE FROM myusers WHERE id =?";
  private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
  private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname = ?";
  private static final String findAllUserSQL = "SELECT * FROM myusers";

  public Long createUser() {
    Long id = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(createUserSQL)) {
      ps.setString(1, "John");
      ps.setString(2, "Doe");
      ps.setInt(3, 30);
      var rs = ps.executeQuery();
      if (rs.next()) {
        id = rs.getLong("id");
      }

      ps.close();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return id;
  }

  public User findUserById(Long userId) {
    User user = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByIdSQL)) {
      ps.setLong(1, userId);
      var rs = ps.executeQuery();
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

  public User findUserByName(String userName) {
    User user = null;
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(findUserByNameSQL)) {
      ps.setString(1, userName);
      var rs = ps.executeQuery();
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

  public User updateUser() {
    try (Connection conn = CustomDataSource.getInstance().getConnection();
        PreparedStatement ps = conn.prepareStatement(updateUserSQL)) {
      // Hardcoded update values for user with id 1
      ps.setString(1, "Jane");
      ps.setString(2, "Smith");
      ps.setInt(3, 28);
      ps.setLong(4, 1);

      int updatedRows = ps.executeUpdate();
      if (updatedRows > 0) {
        return findUserById(1L); // return the updated user
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;

  }

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
