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
				System.out.println("Jetzt kommt Donald");
				s.execute("INSERT INTO Konto(Kontonummer, Name, Saldo______) VALUES (4, 'Donald the great', 0)");
				
				System.out.notify();
				System.out.wait();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
