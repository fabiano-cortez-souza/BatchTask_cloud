package com.is.utility;
import java.sql.*;
import java.util.Base64;

public class DBQuery {
	// JDBC driver name and database URL
	public static String JDBC_DRIVER;  
	public static String DB_URL;

	//  Database credentials
	public static String USER;
	public static String PASS;
	
	public static String QUERY;
	public static ResultSet rs;
	public static String[][][] aryList = null;
	public static int total = 0;
	public static String response;
	
	public static String setPass(String PASS) throws Exception {
        byte[] base64decodedBytes = Base64.getDecoder().decode(PASS);
        PASS = new String(base64decodedBytes, "utf-8");
        return PASS;
    }
	
	public static void consult() {
		Connection conn = null;
		Statement stmt = null;
		try{
			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//STEP 3: Open a connection
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			
			//STEP 4: Execute a query
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);       
	
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			aryList = new String[5000][columnCount+1][2];
			
			
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				for (int i = 1; i <= columnCount; i++ ) {

					aryList[rs.getRow()][i][1] = rs.getString(rsmd.getColumnName(i));
					aryList[rs.getRow()][i][0] = rsmd.getColumnName(i).toString();
					//System.out.println("[" + rs.getRow() + "][" + i + "][0] = #"  + rsmd.getColumnName(i).toString());
					//System.out.println("[" + rs.getRow() + "][" + i + "][1] = "  + rs.getString(rsmd.getColumnName(i)));
					//System.out.println("[" + rs.getRow() + "][ " + i + "][" + rs.getString(rsmd.getColumnName(i)) + " = " + rsmd.getColumnName(i).toString());
				}
			}
			

			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
	}//end main
	
	public static int update() {

		Connection conn = null;
		Statement stmt = null;
		total = 0;
		try{
			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);
			//STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			//STEP 4: Execute a query
			stmt = conn.createStatement();
		    total = stmt.executeUpdate(QUERY);
		    conn.commit();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}
		
		return total;
	}
	
	public static String select() {
		Connection conn = null;
		Statement stmt = null;
		response = "";
		try{
			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			
			//STEP 4: Execute a query
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(QUERY);       
	
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			
			
			//STEP 5: Extract data from result set
			while(rs.next()){
				//Retrieve by column name
				for (int i = 1; i <= columnCount; i++ ) {

					if (columnCount > 1) {
						response = response.concat("{" + rs.getString(rsmd.getColumnName(i)) + "}");
					}else{
						response = response.concat(rs.getString(rsmd.getColumnName(i)));
					}
				}
			}
			

			rs.close();
			stmt.close();
			conn.close();
		}catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();
			}//end finally try
		}//end try
		
		return response;
	}//end main
	
	
	
	public    static void callProcedure(String office) throws ClassNotFoundException {
		Connection conn = null;
		CallableStatement cs = null;
		response = "";
		try{
			//STEP 2: Register JDBC driver
			Class.forName(JDBC_DRIVER);

			//STEP 3: Open a connection
			conn = DriverManager.getConnection(DB_URL,USER,PASS);

			
			//STEP 4: Execute a query

            cs = conn.prepareCall("{call WhoAreThey(?,?)}");
            cs.registerOutParameter(1, Types.VARCHAR);
            cs.execute();
            String str = cs.getString(1);
            if (str != null) {
                System.out.println(str);
            }
        } catch (SQLException e) {
            System.err.println("SQLException: " + e.getMessage());
        }
        finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException e) {
                    System.err.println("SQLException: " + e.getMessage());
                }
            }
            if (conn != null) {
                try {
                	conn.close();
                } catch (SQLException e) {
                    System.err.println("SQLException: " + e.getMessage());
                }
            }
        }
    }
	
}
