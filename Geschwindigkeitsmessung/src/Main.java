import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Random;

public class Main {
	public final static int ZAEHLER = 5000;
	public final static int VERBINDUNGSZAEHLER = 200;
	public final static int QUERYZAEHLER = 10;

	public static void main(String[] args) throws Exception {
		Connection connect;

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:sqlserver://localhost\\Supermarkt:1433;user=auser;password=123456");
		// jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]

		setUpDataBase(connect);
		System.out.println("Normal: " + lesenNormal(connect));

		setUpDataBase(connect);
		System.out.println("Normal: " + lesenNormal(connect));

		connect.close();
		connect = DriverManager.getConnection("jdbc:sqlserver://localhost\\Supermarkt:1433;user=auser;password=123456");
		setUpDataBase(connect);
		System.out.println("Prepared: " + lesenPrepared(connect));

		connect.close();
		connect = DriverManager.getConnection("jdbc:sqlserver://localhost\\Supermarkt:1433;user=auser;password=123456");
		setUpDataBase(connect);
		System.out.println("Prepared: " + lesenPrepared(connect));
	}

	public static void setUpDataBase(Connection c) throws Exception {
		Random random = new Random(System.currentTimeMillis());
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		try {
			s.execute("DROP TABLE Artikel");
			s.execute("DROP TABLE Verkaeufer");
			s.execute("DROP TABLE Kaeufer");
			s.execute("DROP TABLE VA");
			s.execute("DROP TABLE KA");
		} catch (Exception e) {
			System.out.println("fehler beim droppen");
		}
		s.execute(
				"CREATE TABLE Artikel (ArtikelID int primary key, Bezeichnung VarChar(25), Preis Decimal(5,2), Anzahl int)");
		s.execute("CREATE TABLE Verkaeufer (VerkaeuferID int primary key, Bezeichnung VarChar(25))");
		s.execute("CREATE TABLE VA (ID int primary key identity, VerkaeuferID int, ArtikelID int)");
		s.execute("CREATE TABLE Kaeufer (KaeuferID int primary key, Bezeichnung VarChar(25))");
		s.execute("CREATE TABLE KA (ID int primary key identity, KaeuferID int, ArtikelID int)");

		for (int i = 0; i < ZAEHLER; i++) {
			s.execute("INSERT INTO Artikel(ArtikelID, Bezeichnung, Preis, anzahl) " + "VALUES (" + i + ", 'Artikel"
					+ random.nextInt(20) + "', " + random.nextInt(20) + ", " + random.nextInt(200) + ")");

			s.execute("INSERT INTO Verkaeufer(VerkaeuferID, Bezeichnung) " + "VALUES (" + i + ", 'Verkaeufer"
					+ random.nextInt(20) + "')");

			s.execute("INSERT INTO Kaeufer(KaeuferID, Bezeichnung) " + "VALUES (" + i + ", 'Artikel"
					+ random.nextInt(20) + "')");
		}

		for (int i = 0; i < VERBINDUNGSZAEHLER; i++) {
			s.execute("INSERT INTO VA(VerkaeuferID, ArtikelID) " + "VALUES (" + random.nextInt(ZAEHLER) + ", "
					+ random.nextInt(ZAEHLER) + ")");

			s.execute("INSERT INTO KA(KaeuferID, ArtikelID) " + "VALUES (" + random.nextInt(ZAEHLER) + ", "
					+ random.nextInt(ZAEHLER) + ")");
		}
	}

	public static long lesenNormal(Connection c) throws Exception {
		Random r = new Random(System.currentTimeMillis());
		long time = System.currentTimeMillis();
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
		for (int i = 0; i < QUERYZAEHLER; i++) {
			s.executeQuery("SELECT * " + "FROM Artikel A, Verkaeufer V, Kaeufer K, VA, KA "
					+ "WHERE A.ArtikelID = VA.ArtikelID " + "AND V.VerkaeuferID = VA.VerkaeuferID "
					+ "AND A.ArtikelID = KA.ArtikelID " + "AND K.KaeuferID = KA.KaeuferID " + "AND anzahl < "
					+ r.nextInt(200));
		}

		return System.currentTimeMillis() - time;
	}

	public static long lesenPrepared(Connection c) throws Exception {
		Random r = new Random(System.currentTimeMillis());
		long time = System.currentTimeMillis();

		PreparedStatement ps = c.prepareStatement("SELECT * " + "FROM Artikel A, Verkaeufer V, Kaeufer K, VA, KA "
				+ "WHERE A.ArtikelID = VA.ArtikelID " + "AND V.VerkaeuferID = VA.VerkaeuferID "
				+ "AND A.ArtikelID = KA.ArtikelID " + "AND K.KaeuferID = KA.KaeuferID " + "AND anzahl < ?");

		for (int i = 0; i < QUERYZAEHLER; i++) {
			ps.setInt(1, r.nextInt(200));
			ps.executeQuery();
		}

		return System.currentTimeMillis() - time;

	}

}
