package hoppin;
import java.sql.*;
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
			// TODO Auto-generated catch block
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
	
	public List<String> getNEmployeeList(int i){
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
			System.out.println("res: " + res);
			return true;
			
		} catch (SQLException e) {
			System.out.println(e);
			return false;
		}
	}
	
	public int cache = 0;
	public ArrayList<String>[] getEmployeeList(int i){
		ArrayList<String>[] al = null;
		try {
			PreparedStatement pss = conn.prepareStatement("select count(id) as max from User where sid = ?");
			pss.setInt(1, i);
			ResultSet rs = pss.executeQuery();
			rs.next();
			int max = rs.getInt("max");
			this.cache = max;
			al = new ArrayList[3];
			for (int j=0; j<3; j++)
				al[j] = new ArrayList<String>(max);
			
			
			pss = conn.prepareStatement("select id,completeName,email from User where sid = ?");
			pss.setInt(1, i);
			rs = pss.executeQuery();
			int j = 0;
			while (rs.next()) {
				int tmp = rs.getInt("id");
				String t = Integer.toString(tmp);
				al[j].add(t);
				al[j].add(rs.getString("completeName"));
				al[j].add(rs.getString("email"));
				j++;
			}
			
			
			
		} catch (SQLException e) {
			System.out.println(e);
		}
		return al;
	}
	
}

