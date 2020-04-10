import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CrudPersonTable {
	public void createRecord(Connection connection, String name, String lastName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("insert into persons(name, lastName) " +
					"values('" + name + "','" + lastName + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteRecord(Connection connection, String name, String lastName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("delete from persons where " +
					"name='" + name + "' AND lastName='" + lastName + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String readRecordsByLastName(Connection connection, String lastName) throws SQLException {
		String resultString = "";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from persons where lastName='" + lastName + "'");
		while (resultSet.next()) {
			resultString += resultSet.getString("name") + ":";
		}
		return (resultString);
	}

	public String readRecordById(Connection connection, int id) {
		String resultString = "";

		try (Statement statement = connection.createStatement()) {
			ResultSet resultSet = statement.executeQuery("select * from persons where id=" + id);
			resultSet.next();
			resultString = resultSet.getString("name") + ":" + resultSet.getString("lastName");
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return (resultString);
	}

	public void updateRecord(Connection connection, int id, String newName, String newLastName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("update persons set name='" + newName + "', lastName='" + newLastName + "' where id=" + id);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
