import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class CrudPerson {
	public void createNewPerson(Connection connection, String name, String lName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("insert into persons(name, lastName) " +
					"values('" + name + "','" + lName + "')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void deleteRecord(Connection connection, String name, String lName) {
		try (Statement statement = connection.createStatement()) {
			statement.executeUpdate("delete from persons where " +
					"name='" + name + "' AND lastName='" + lName + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String readRecordsByLastName(Connection connection, String lName) throws SQLException {
		String resultString = "";
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery("select * from persons where lastName='" + lName + "'");
		while (resultSet.next()) {
			resultString += resultSet.getString("name") + ":";
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
