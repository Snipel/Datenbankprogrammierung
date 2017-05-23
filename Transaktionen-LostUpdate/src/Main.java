import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

	public static void main(String[] args) throws Exception {
		Connection connect;

		// Setup the connection with the DB
		connect = DriverManager.getConnection("jdbc:sqlserver://localhost\\Supermarkt:1433;user=auser;password=123456");
		// jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]

		// Transaction Isolation Level (sonst heult er rum)
		// Serializable nie in der Realität (schlechte Performance)
		connect.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
		connect.setAutoCommit(false);

		setUpDatabase(connect);
		synchronized (System.out) {
			new Thread(new B()).start();

			Statement s = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			ausgabe(s, "SELECT 'A' as Thread, * " + "FROM Kunden Kd, Konto Kt " + "WHERE Kd.KundenID = Kt.KundenID");

			int ueberweisungsbetrag = 3000;

			ResultSet rs = s.executeQuery("SELECT * FROM Konto WHERE KundenID = 1");
			rs.next();
			int saldo = rs.getInt("Saldo______");
			System.out.wait();
			System.out.println("A ist wieder am Start");
			saldo = saldo + ueberweisungsbetrag;
			rs.updateInt("Saldo______", saldo);
			rs.updateRow();
			connect.commit();

			ausgabe(s, "SELECT 'A' as Thread, * " + "FROM Kunden Kd, Konto Kt " + "WHERE Kd.KundenID = Kt.KundenID");
			System.out.notify();
			System.out.wait();
		}
	}

	public static void setUpDatabase(Connection c) throws Exception {
		Statement s = c.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

		try {
			s.execute("DROP TABLE Konto");
		} catch (Exception e) {
			System.out.println("dicker Patzer!");
			e.printStackTrace();
		}
		try {
			s.execute("DROP TABLE Produkte");

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			s.execute("DROP TABLE Kunden");
		} catch (Exception e) {
			e.printStackTrace();
		}

		s.execute("CREATE TABLE Kunden (KundenID int primary key, Bezeichnung VARCHAR(50))");
		s.execute("CREATE TABLE Produkte(ProduktID int primary key, Bezeichnung varchar(50))");
		s.execute(
				"CREATE TABLE Konto(Kontonummer int primary key, ProduktID int foreign key references Produkte(ProduktID), KundenID int foreign key references Kunden(KundenID), Saldo______ decimal(10,2), Dispo______ decimal(10,2))");

		s.execute(
				"INSERT INTO Kunden (KundenID, Bezeichnung) VALUES (1, 'Lord Jens'), (2, 'Sir Hans Peter'), (3, 'Herzogin Dumpfbacke'), (4, 'Sultan Dieter')");
		s.execute(
				"INSERT INTO Produkte(ProduktID, Bezeichnung) VALUES (1, 'GiroSparPremiumPlus'), (2, 'CreditPremium'), (3, 'CardPlus'), (4, 'Gewinnsparen')");
		s.execute(
				"INSERT INTO Konto(Kontonummer, ProduktID, KundenID, Saldo______, Dispo______) VALUES (1, 1, 1, 100.0, 200.0), (2, 2, 2, 300, 10), (3, 3, 3, 500.12, 1.40), (4, 4, 4, 1000.26, 600)");
		s.getConnection().commit();
	}

	public static void ausgabe(Statement s, String query) throws SQLException {
		synchronized (System.out) {
			ResultSet rs = s.executeQuery(query);

			for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
				System.out.print(rs.getMetaData().getColumnName(i) + "   ");
			}

			System.out.println();

			while (rs.next()) {
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					int le = rs.getMetaData().getColumnName(i).length();
					String string;
					if (rs.getString(i).length() > le) {
						string = rs.getString(i).substring(0, le - 3) + "..." + "   ";
					} else {
						int lp = le - rs.getString(i).length();
						string = rs.getString(i) + padRight(" ", lp + 3);
					}
					System.out.print(string);
				}
				System.out.println();
			}
		}
	}

	public static String padRight(String s, int n) {
		return String.format("%1$-" + n + "s", s);
	}

}
