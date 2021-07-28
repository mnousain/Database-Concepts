package application;
import java.io.IOException;
import java.sql.*;


public class partOne 
{
	
	static final String DBINFO = "jdbc:oracle:thin:@artemis.vsnet.gmu.edu:1521/vse18c.vsnet.gmu.edu";
	static final String DBUSERNAME = "mnousain";
	static final String DBPASSW = "leebupho";
	
	static Connection conn = null;
	
	public static void main(String[] args) throws SQLException, IOException
	{
			initiateConnection();
			researchDepartment(conn); //1
			houstonProductZ(conn); //2

	}
	
	//initiates connection to database
	public static void initiateConnection() throws SQLException, IOException
	{
		try
		{
			Class.forName("oracle.jdbc.driver.OracleDriver");
		}
		catch(ClassNotFoundException e)
		{
			System.err.println("Error: Driver could not be loaded");
			System.exit(1);
		}
		
		try 
		{
			conn = DriverManager.getConnection(DBINFO, DBUSERNAME, DBPASSW);
		}
		catch(SQLException e)
		{
			System.err.println("Error: Failure to connect to database");
			System.exit(1);
		}
		
			System.out.println("Connection Successful\n");
		
	}
	
	//gets all the employees who work in the research department and displays the output
	public static void researchDepartment(Connection conn)
	{
		
		try
		{
			Statement query = conn.createStatement();
			
			//sql query
			String sql = "select lname, ssn from employee e, department d where d.dname = 'Research' and e.dno = d.dnumber";
			//result set to store the results from the query
			ResultSet results = query.executeQuery(sql);
			
			System.out.println("Employees in Research Department\n");
			System.out.println("Lname" + "\t" + "SSN");
			System.out.println("----------------------");
			
			//prints employee info from the result set
			while(results.next())
			{
				System.out.println(results.getString("lname") + "\t" + results.getInt("ssn"));
			}
			
			results.close();
            query.close();
		}
		catch(SQLException e)
		{
			System.err.println("Error: Failure to complete query one");
			System.exit(1);
		}
		
		
		
	}
	//gets all the employees who work in departments located in Houston and work on the project 'ProductZ' and displays the output
	public static void houstonProductZ(Connection conn)
	{
		try
		{
			Statement query = conn.createStatement();
			
			//sql query
			String sql = "select lname, ssn, hours from employee, works_on, project, dept_locations where ssn = essn and pno = pnumber and dno = dnumber and dlocation = 'Houston' and pname = 'ProductZ'";
			//result set to store the results from the query
			ResultSet results = query.executeQuery(sql);
			
			System.out.println("\nEmployees who work in departments located in Houston and work on the project ‘ProductZ’\n");
			System.out.println("Lname" + "\t" + "SSN" + "\t\t" + "Hours");
			System.out.println("---------------------------------");
			
			//prints employee information from result set
			while(results.next())
			{
				System.out.println(results.getString("lname") + "\t" + results.getInt("ssn") + "\t" + results.getInt("hours"));
			}
			
			results.close();
			query.close();
			
		}
		catch(SQLException e)
		{
			System.err.println("Error: Failure to complete query two");
			System.exit(1);
		}
	}
}
	
	
