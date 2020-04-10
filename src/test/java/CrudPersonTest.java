import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.*;

public class CrudPersonTest {
	private static final String URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE;Mode=Oracle";
	private static final String USER = "sa";
	private static final String PASS = "";

	private static Connection connect;
	private static CrudPerson crudPerson;


	@BeforeClass
	public static void createDB() throws SQLException {
		crudPerson = new CrudPerson();
		DeleteDbFiles.execute("~", "test", true);
		Server server = Server.createWebServer();
		server.start();
		connect = DriverManager.getConnection(URL, USER, PASS);
		Statement statement = connect.createStatement();
		statement.executeUpdate("create table persons(id serial, name varchar(255), lastName varchar(255))");
		statement.close();
	}

	@Test
	public void createShouldBeCreateNewRecordInTable() throws SQLException {
		String[] testStrings = {"Alexandr", "Akinin"};

		Statement statement = connect.createStatement();
		crudPerson.createNewPerson(connect, testStrings[0], testStrings[1]);
		ResultSet resultSet = statement.executeQuery("select * from persons where name='Alexandr' AND lastName='Akinin'");
		//не уверен, но думаю тут нужно разделить два теста, один проверяет создание, второй корректность вставки
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals(testStrings[0], resultSet.getString("name"));
		Assert.assertEquals(testStrings[1], resultSet.getString("lastName"));
		resultSet.close();
		statement.close();
	}

	@Test
	public void deleteRecordShouldBeDeleteRecord() throws SQLException {
		String[] testStrings = {"Alexandr", "Akinin"};
		Statement statement = connect.createStatement();

		statement.executeUpdate("insert into persons(name, lastName) values ('Alexandr', 'Akinin')");
		crudPerson.deleteRecord(connect, testStrings[0], testStrings[1]);
		ResultSet resultSet = statement.executeQuery("select * from persons where name='Alexandr' AND lastName='Akinin'");
		Assert.assertFalse(resultSet.next());
		resultSet.close();
		statement.close();
	}

	/**Не смог понять как правильно писать read, поэтому написал метод,
	 * который возвращает все имена по фамилии, разделенные ":"
	 */
	@Test
	public void readRecordsByLastNameShouldBeReadRecords() throws SQLException {
		String testString = "Akinin";
		Statement statement = connect.createStatement();

		statement.executeUpdate("insert into persons(name, lastName) values('Alexandr', 'Akinin')");
		statement.executeUpdate("insert into persons(name, lastName) values('Ivan', 'Akinin')");
		statement.executeUpdate("insert into persons(name, lastName) values('Petr', 'Akinin')");

		String result = crudPerson.readRecordsByLastName(connect, "Akinin");
		Assert.assertEquals("Alexandr:Ivan:Petr:", result);
	}

	@Test
	public void updateRecordShouldBeUpdateRecord() throws SQLException {
		Statement statement = connect.createStatement();

		ResultSet resultSet = statement.executeQuery("select * from persons");
		System.out.println("here");
		while (resultSet.next()) {
			System.out.println("id: " + resultSet.getInt("id"));
			System.out.println("name: " + resultSet.getString("name"));
			System.out.println("lastName: " + resultSet.getString("lastName"));
		}

		statement.executeUpdate("insert into persons(name, lastName) values('Alexandr', 'Akinin')");
		crudPerson.updateRecord(connect, 1, "Alexandr", "Akinin");

	}
}
