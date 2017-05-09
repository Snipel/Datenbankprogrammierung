import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class B implements Runnable {

	@Override
	public void run() {
		try {
			Connection connect;

			// Setup the connection with the DB
			connect = DriverManager
					.getConnection("jdbc:sqlserver://localhost\\Supermarkt:1433;user=auser;password=123456");
			// jdbc:sqlserver://[serverName[\instanceName][:portNumber]][;property=value[;property=value]]

			connect.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
			connect.setAutoCommit(false);

			synchronized (System.out) {
				Statement s = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
				System.out.println("Jetzt ziehen wir Enking die Kohle ab!");
				s.execute("UPDATE Konto SET Saldo______ = 40 WHERE Name = 'Enking'");
				s.getConnection().commit();
				System.out.println("Jetzt ist Enking pleite");
				System.out.notify();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
