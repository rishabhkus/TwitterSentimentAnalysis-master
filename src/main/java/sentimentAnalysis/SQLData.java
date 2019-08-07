/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentimentAnalysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rogue
 */
public class SQLData {
     Connection con;
     Statement stmt;
     ResultSet rs;
    public SQLData() throws ClassNotFoundException, SQLException
    {
        Class.forName("org.sqlite.JDBC");
            con=DriverManager.getConnection("jdbc:sqlite:hi.db");
            stmt=con.createStatement();
    }
   public void reset(){
         try {
             stmt.executeUpdate("delete from tweeter;");
             stmt.executeUpdate("delete from user;");
                     
                     } catch (SQLException ex) {
             Logger.getLogger(SQLData.class.getName()).log(Level.SEVERE, null, ex);
         }
        
   }
    public void input(long id,String text,String usr)throws Exception{
        
        PreparedStatement statement=con.prepareStatement("insert into tweeter(id,text,name,analysis) values(?,?,?,'0')"); 
        statement.setLong(1, id);
        statement.setString(2, text);
        statement.setString(3, usr);
        statement.executeUpdate();
        
    
}
public Long latestId(){
    Long id=0L;
    ResultSet rs;
         try {
             rs = stmt.executeQuery("SELECT Max(id) FROM tweeter;");
              while(rs.next())
        {
           id=rs.getLong(1);
        }
         } catch (SQLException ex) {
             System.out.println(ex.getMessage()+"lol");
         }
       
    
return id;
}
public void update(Long id,String[] arr){
         try {
             stmt.executeUpdate("UPDATE tweeter set analysis='"+arr[6]+"',vp="+arr[0]+",p="+arr[1]+",nu="+arr[2]+",n="+arr[3]+",vn="+arr[4]+" WHERE id="+id);
         } catch (SQLException ex) {
             Logger.getLogger(SQLData.class.getName()).log(Level.SEVERE, null, ex);
         }
    }
public void close()
{
         try {
             con.close();
         } catch (SQLException ex) {
             Logger.getLogger(SQLData.class.getName()).log(Level.SEVERE, null, ex);
         }
}
public ResultSet tableData() throws SQLException{
    rs=stmt.executeQuery("Select * from tweeter;");
    
return rs;
}
public Boolean isPresent(Long id){ Boolean state=false;
         try {
            
             ResultSet rs=stmt.executeQuery("select count(*) from tweeter where id="+id);
             while(rs.next())
                 if(rs.getInt(1)==1)
                     state=true;
             
         } catch (SQLException ex) {
             Logger.getLogger(SQLData.class.getName()).log(Level.SEVERE, null, ex);
         }
return state;
}}
