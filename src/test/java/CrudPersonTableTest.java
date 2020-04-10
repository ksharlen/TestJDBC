import org.h2.tools.DeleteDbFiles;
import org.h2.tools.Server;
import org.junit.*;

import java.sql.*;

/**
 * Я пока не знаю до конца как работать с тестовыми наборами и нужно ли ловить исключения SQLException,
 * поэтому я их просто выбрасывал выше, но в самом классе crudPersonTable конечно обрабатывал
 */

public class CrudPersonTableTest {
	private static final String URL = "jdbc:h2:~/test;AUTO_SERVER=TRUE;Mode=Oracle";
	private static final String USER = "sa";
	private static final String PASS = "";

	private static Connection connect;
	private static CrudPersonTable crudPerson;


	@BeforeClass
	public static void init() throws SQLException {
		crudPerson = new CrudPersonTable();
		DeleteDbFiles.execute("~", "test", true);
		Server server = Server.createWebServer();
		server.start();
		connect = DriverManager.getConnection(URL, USER, PASS);
		Statement statement = connect.createStatement();
		statement.executeUpdate("create table persons(id serial, name varchar(255), lastName varchar(255))");
		statement.close();
	}

	@Before
	public void deleteRecordsFromTable() throws SQLException {
		Statement statement = connect.createStatement();

		statement.executeUpdate("truncate table persons");
		statement.close();
	}

	@Test
	public void createShouldBeCreateNewRecordInTable() throws SQLException {
		String[] testStrings = {"Alexandr", "Akinin"};

		Statement statement = connect.createStatement();
		crudPerson.createRecord(connect, testStrings[0], testStrings[1]);
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
		statement.close();
	}

	@Test
	public void readRecordByIdShouldBeReadRecord() throws SQLException {
		Statement statement = connect.createStatement();
		int id;

		statement.executeUpdate("insert into persons(name, lastName) values ('Alexandr', 'Akinin')");
		ResultSet resultSet = statement.executeQuery("select * from persons");
		resultSet.next();
		id = resultSet.getInt("id");
		String testingString = crudPerson.readRecordById(connect, id);
		Assert.assertEquals("Alexandr:Akinin", testingString);
		statement.close();
		resultSet.close();
	}

	@Test
	public void updateRecordShouldBeUpdateRecord() throws SQLException {
		int id;
		Statement statement = connect.createStatement();

		statement.executeUpdate("insert into persons(name, lastName) values('Alexandr', 'Akinin')");
		ResultSet resultSet = statement.executeQuery("select * from persons where name='Alexandr' AND lastName='Akinin'");
		resultSet.next();
		id = resultSet.getInt("id");
		resultSet.close();
		crudPerson.updateRecord(connect, id, "Sergey", "Akinin");
		resultSet = statement.executeQuery("select * from persons where id=" + id);
		Assert.assertTrue(resultSet.next());
		Assert.assertEquals("Sergey", resultSet.getString("name"));
		Assert.assertEquals("Akinin", resultSet.getString("lastName"));
		statement.close();
		resultSet.close();
	}

	@AfterClass
	public static void finishTests() {
		try {
			connect.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
