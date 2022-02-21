package hoppin;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MySQLConnect { //Forse questo dovrebbe diventare Singleton
	Connection conn = null;
	private String passw = "anoncorno";
	private String user ="kurush";

	public MySQLConnect(){
		
		try {
			this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/hoppin", this.user, this.passw); //Establishing connection
		} catch (SQLException e) {
			System.out.println(e);
			System.out.println("Error while connecting to the database");
		}
	}
	
	public void disconnect() {
		
		try {
			this.conn.close();
		} catch (SQLException e) {
			System.out.println("Error when closing DB");
		}
	}
	
	public boolean login(String user, String passw) { 
		try {
			// ps = PreparedStatement
			// rs = ResultSet
			PreparedStatement ps = conn.prepareStatement("select id from User where email = ? and passw_hash = ?");
			ps.setString(1, user);
			ps.setString(2, passw);
			ResultSet rs = ps.executeQuery();
		
			if ( rs.next() == false) {
				return false;
			}else {
				return true;
			}
			
		} catch (SQLException e) { //e = Exception
			System.out.println(e);
			return false;
		}
	}
	
	public boolean register(String name, String email, String passw) {
		
		try {
			PreparedStatement pss = conn.prepareStatement("select max(id) as id from User");
			ResultSet rs = pss.executeQuery();
			int i=2;
			if ( rs != null) {
				rs.next();
				i = rs.getInt("id") + 1;
			}
			rs.close();
			PreparedStatement psi = conn.prepareStatement("insert into User (id, completeName, email, passw_hash, accType ) values (?,?,?,?,?)");
			psi.setInt(1, i);
			psi.setString(2, name);
			psi.setString(3, email);
			psi.setString(4, passw);
			psi.setString(5, "Customer");
			boolean res = psi.execute();
			System.out.println("res: " + res);
			return true;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public int getId(String email) {
		int i = 0;
		
		try {
			PreparedStatement pss = conn.prepareStatement("select id from User where email = ?");
			pss.setString(1, email);
			ResultSet rs = pss.executeQuery();
			rs.next();
			i = rs.getInt("id");
		}catch (SQLException e) {
			System.out.println(e);
		}
		
		return i;
	}
	
	public String getNamebyId(int i) {
		String completeName = null;
		try {
			PreparedStatement pss = conn.prepareStatement("select completeName from User where id = ?");
			pss.setInt(1, i);
			ResultSet rs = pss.executeQuery();
			rs.next();
			completeName = rs.getString("completeName");
			
		}catch (SQLException e) {
			System.out.println(e);
		}
		return completeName;
	}
	
	public List<String> getNEmployeeList(int i){ //Funzione da cancellare, utile solo come riferimento
		List<String> list = new ArrayList<String>();
		try {
			PreparedStatement pss = conn.prepareStatement("select completeName from User where sid = ?");
			pss.setInt(1, i);
			ResultSet rs = pss.executeQuery();
			
			while ( rs.next()) {
				list.add(rs.getString("completeName"));
			}
			
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		return list;
	}
	
	public boolean addEmployee(String name, String email, String passw, int id) {
		try {
			PreparedStatement pss = conn.prepareStatement("select max(id) as id from User");
			ResultSet rs = pss.executeQuery();
			int i=2;
			if ( rs != null) {
				rs.next();
				i = rs.getInt("id") + 1;
			}
			rs.close();
			PreparedStatement psi = conn.prepareStatement("insert into User (id, completeName, email, passw_hash, accType, sid ) values (?,?,?,?,?,?)");
			psi.setInt(1, i);
			psi.setString(2, name);
			psi.setString(3, email);
			psi.setString(4, passw);
			psi.setString(5, "Customer");
			psi.setInt(6, id);
			boolean res = psi.execute();
			return true;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public ArrayList<Employee> getEmployeeList(int i){
		//ArrayList<String>[] al = null;
		
		ArrayList<Employee> al = null;
		
		try {
			PreparedStatement pss = conn.prepareStatement("select count(id) as max from User where sid = ?");
			pss.setInt(1, i);
			ResultSet rs = pss.executeQuery();
			rs.next();
			int max = rs.getInt("max");
			al = new ArrayList<Employee>();
			al.ensureCapacity(max);
			
			
			pss = conn.prepareStatement("select id,completeName,email from User where sid = ?");
			pss.setInt(1, i);
			rs = pss.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String email = rs.getString("email");
				String completeName = rs.getString("completeName");
				Employee pnt = new Employee(id,email,completeName);
				al.add(pnt);
			}
			
			
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		return al;
	}
	
	
	public boolean deleteEmployee(List<Integer> ids) {
		try {
			int size = ids.size();
			System.out.println("Size: " + size);
			if ( size < 1) {
				System.out.println("Lista vuota");
				return false;
			}
			
			
			StringBuilder sb = new StringBuilder();
			String qistart = "( "; //Query Incognites Start
			String qiend = " )"; //QUery Incognites End
			String strep =" ?"; //String To Repeat 
			sb.append(qistart);
			for (int i=0; i<size; i++) {
				sb.append(strep);
				if ( i+1 < size)
					sb.append(", ");
			}
			sb.append(qiend);
			
			String idsToDelete = sb.toString();
			System.out.println("Stringa costruita: " + idsToDelete);
			PreparedStatement ps = conn.prepareStatement("delete from User where id IN " + idsToDelete);
			
			for (int i=0; i<size;i++) {
				ps.setInt(i+1, ids.get(i));
			}
			boolean res = ps.execute(); //ps return true or false
			System.out.println("Query ritorna: " + res);
			return res;
			
		} catch( SQLException e) {
			System.out.println(e);
		}
		
		return false;
		
	}

	public ArrayList<Reservation> getReservationList(int OwnerId){
		//ArrayList<String>[] al = null;
		
		ArrayList<Reservation> al = null;
		
		try {
			PreparedStatement pss = conn.prepareStatement("select Name from Hotel where OwnerId = ?");
			pss.setInt(1, OwnerId);
			ResultSet rs = pss.executeQuery();
			rs.next();
			String HotelName = rs.getString("Name");
			al = new ArrayList<Reservation>();
			
			//^--Questo dovrebbe diventare un altro metodo
			
			
			pss = conn.prepareStatement("select id,CustomerName,RoomNum, CheckIn, CheckOut, Package from Reservation where HotelName = ?");
			pss.setString(1, HotelName);
			rs = pss.executeQuery();
			while (rs.next()) {
				int id = rs.getInt("id");
				String CustomerName = rs.getString("CustomerName");
				String RoomNum = rs.getString("RoomNum");
				String CheckIn = rs.getString("CheckIn");
				String CheckOut = rs.getString("CheckOut");
				String pckg = rs.getString("Package");
				Reservation pnt = new Reservation(CustomerName, id, RoomNum, CheckIn, CheckOut, pckg );
				al.add(pnt);
			}
			
			
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		return al;
	}
	
	public boolean addReservation(String name, String room, String checkin, String checkout, String pckg) {
		try {
			PreparedStatement pss = conn.prepareStatement("select max(id) as id from Reservation");
			ResultSet rs = pss.executeQuery();
			int id = -1;
			if ( rs != null) {
				rs.next();
				id = rs.getInt("id") + 1;
			}
			rs.close();
			
			//get Hotel Name
			pss = conn.prepareStatement("select HotelName from Room where Num= ?;");
			pss.setString(1, room);
			rs = pss.executeQuery();
			rs.next();
			String HotelName = rs.getString("HotelName");
			
			//Conversione del formato HTML:
			//HTML ha come formato yyyy-mm-dd, bisogna convertirlo prima di inserire
			//i dati nella query in dd-mm-yyyy
			System.out.print("Checkin: " + checkin + " | Checkout: " + checkout + "\n");
			System.out.println("");
			
			DateTimeFormatter oformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			DateTimeFormatter nformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate Datecheckin = LocalDate.parse(checkin,oformat);
			LocalDate Datecheckout = LocalDate.parse(checkout,oformat);
			
			checkin = Datecheckin.format(nformat).toString();
			checkout = Datecheckout.format(nformat).toString();
			System.out.print("Checkin: " + checkin + " | Checkout: " + checkout + "\n");
			
			PreparedStatement psi = conn.prepareStatement("INSERT INTO Reservation (CustomerName, id, HotelName, RoomNum, CheckIn, CheckOut, Package) "
					+ " VALUES (?, ?, ?, ?, STR_TO_DATE( ?,  '%d-%m-%Y'), STR_TO_DATE( ?, '%d-%m-%Y' ) , ? )");
			psi.setString(1, name);
			psi.setInt(2, id);
			psi.setString(3, HotelName);
			psi.setString(4, room);
			psi.setString(5, checkin);
			psi.setString(6, checkout);
			psi.setString(7, pckg);
			boolean res = psi.execute();
			return true;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public boolean deleteReservation(int id) {
		try {
		PreparedStatement pss = conn.prepareStatement("delete from Reservation where id = ? ");
		pss.setInt(1, id);
		boolean res = pss.execute();
		return true;
		} catch ( SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public boolean editReservation(Reservation res) {
		DateTimeFormatter oformat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter nformat = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate Datecheckin = null;
		LocalDate Datecheckout = null;
		
		try {
			StringBuilder sb = new StringBuilder();
	
			sb.append("UPDATE Reservation SET ");
			if ( res.getRoomNum() != "") {
				System.out.println("passato res.getRoomNum(): " + res.getRoomNum());
				sb.append("roomNum = ?, ");
			}
			if ( res.getCheckIn() != "") {
				sb.append("checkIn = STR_TO_DATE(? , '%d-%m-%Y'), ");
				Datecheckin = LocalDate.parse(res.getCheckIn(),oformat);
			}
			
			if ( res.getCheckOut() != "") {
				sb.append("checkOut = STR_TO_DATE(? , '%d-%m-%Y')");
				Datecheckout = LocalDate.parse(res.getCheckOut(),oformat);
			}
			
			if ( res.getPckg() != "") {
				sb.append("Package = ?");
			}
			sb.append(" WHERE id = ?");
			String psQuery = sb.toString(); //ps = PreparedStatement
			System.out.println("Stringa costruita: " + psQuery);
			
			PreparedStatement pss = conn.prepareStatement(psQuery);
			
			int pstack = 1;
			if ( res.getRoomNum() != "") {
				pss.setString(pstack, res.getRoomNum());
				pstack++;
			}
			if ( res.getCheckIn() != "") {
				String checkin = Datecheckin.format(nformat).toString();
				pss.setString(pstack, checkin);
				pstack++;
			}
			
			if ( res.getCheckOut() != "") {
				String checkout = Datecheckout.format(nformat).toString();
				pss.setString(pstack, checkout);
				pstack++;
			}
			if ( res.getPckg() != "") {
				pss.setString(pstack, res.getPckg());
				pstack++;
			}
			
			pss.setInt(pstack, res.getId());
			if ( pstack == 1)
				return false;
			
			int queryResult = pss.executeUpdate();
			
			return true;
		} catch ( SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	
}


