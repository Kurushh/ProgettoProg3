package hoppin.employee;

import hoppin.util.factory.CookieFactory;
import hoppin.util.sql.MySQLConnect;
import jakarta.servlet.http.HttpServletRequest;

/**
 * 
 * Implementa {@link hoppin.util.factory.CookieFactory} da cui eredita
 * il metodo {@link hoppin.util.factory.CookieFactory#makeCookieGetter(HttpServletRequest)}
 * ampiamente utilizzato, anche all'interno dello stesso metodo {@link #makeDatabaseConnect(HttpServletRequest)} 
 * che costruisce e restituisce una connessione al database
 *
 */

public class EmployeeFactory implements CookieFactory {
	
	public MySQLConnect makeDatabaseConnect(HttpServletRequest request) {
		int i = this.makeCookieGetter(request).getIdbyCookies();
		MySQLConnect db = (MySQLConnect) new MySQLEmployee(i);
		
		return db;
		
	}
}
