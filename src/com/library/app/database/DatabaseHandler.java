package com.library.app.database;

import com.library.app.listBook.ListBookController.Book;
import com.library.app.listMember.ListMemberController;
import com.library.app.listMember.ListMemberController.Member;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.apache.commons.codec.digest.DigestUtils;

public class DatabaseHandler {
    private static DatabaseHandler handler;
    
    private static final String url = "jdbc:mysql://localhost:3306/library_manegment?useSSL=false";
    private static String uname = "root";
    private static String pass = "";
    private static Connection con = null;
    private static Statement smt = null;
    
    private DatabaseHandler()
    {
        createConnection();
        String qu = "set default_storage_ENGINE = InnoDB;";
        if(execAction(qu))
        {
            System.out.println("set default Engine innoDB"
                    + "\n Successfull");
        }
        else
        {
            System.out.println("set default Engine innoDB"
                    + "\n Failed");
        }
        setUpBookTable();
        setUpMember();
        setUpIssueTable();
        setUpLogin();
    }
    
    public static DatabaseHandler getInstance()
    {
        if(handler == null)
        {
            handler = new DatabaseHandler();
        }
        return handler;
    }
    
    void createConnection()
    {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(url, uname, pass);
        } catch (ClassNotFoundException | SQLException e) {
            JOptionPane.showMessageDialog(null, "Can't load database...", "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
    
    public ResultSet execQuery(String Query)
    {
        ResultSet result;
        try {
            smt = con.createStatement();
            result = smt.executeQuery(Query);
        } catch (SQLException e) {
            System.out.println("Exception at execQuery:databaseHandler" + e.getLocalizedMessage());
            return null;
        }
        return result;
    }
    
    public boolean execAction(String qu)
    {
        try {
            smt = con.createStatement();
            smt.execute(qu);
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error:" + e.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execAction:databaseHandler" + e.getLocalizedMessage());
            return false;
        }
    }

    public int execQueryCount(String qu)
    {
        ResultSet result;
        int count = 0;
        try {
            smt = con.createStatement();
            result = smt.executeQuery(qu);
            while (result.next()) {
                count++;
            }
        } catch (SQLException e) {
            System.out.println("Exception at execQuery:databaseHandler" + e.getLocalizedMessage());
            return count;
        }
        return count;
    }
    
    void setUpBookTable()
    {
        String tableName = "book";
        try {
            smt = con.createStatement();
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if(tables.next())
            {
                System.out.println("Table " + tableName +" already exits. Ready to Go !");
            }
            else
            {
                smt.execute("create table "+tableName +"(\n" +
                        "id varchar(100) primary key,\n" +
                        "title varchar(200),\n" +
                        "author varchar(200),\n" +
                        "publisher varchar(200),\n" +
                        "intcode varchar(200),\n" +
                        "isAvail boolean default true\n" +
                        ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "----setUp Database");
        }
    }
    
    private void setUpMember() {
        String tableName = "member";
        try {
            smt = con.createStatement();
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if(tables.next())
            {
                System.out.println("Table " + tableName +" already exits. Ready to Go !");
            }
            else
            {
                smt.execute("create table "+tableName +"(\n" +
                        "id varchar(100) primary key,\n" +
                        "name varchar(200),\n" +
                        "mobile varchar(20),\n" +
                        "email varchar(200)\n" +
                        ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "----setUp Database");
        }
    }
    
    void setUpIssueTable()
    {
        String tableName = "issue";
        try {
            smt = con.createStatement();
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if(tables.next())
            {
                System.out.println("Table " + tableName +" already exits. Ready to Go !");
            }
            else
            {
                smt.execute("create table "+tableName +"(\n" +
                    "	bookid varchar(100) primary key,\n" +
                    "	memberid varchar(200),\n" +
                    "	issuetime timestamp default CURRENT_TIMESTAMP,\n" +
                    "	renewcount integer default 0,\n" +
                    "	FOREIGN KEY (bookid) REFERENCES book(id),\n" +
                    "   INDEX (memberid),\n" +
                    "   FOREIGN KEY (memberid) REFERENCES member(id)\n" +
                    ")");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "----setUp Database");
        }
    }
    
    public void setUpLogin()
    {
        String tableName = "librarian_login";
        try {
            smt = con.createStatement();
            DatabaseMetaData dbm = con.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if(tables.next())
            {
                System.out.println("Table " + tableName +" already exits. Ready to Go !");
            }
            else
            {
                smt.execute("create table "+tableName +"(\n" +
                        "uname varchar(100) primary key,\n" +
                        "pword varchar(200)\n" +
                        ")");
                
                String pass = DigestUtils.shaHex("default");
                
                String qu = "insert into librarian_login values(\n"
                + "'Librarian',\n"
                + "'" + pass +"'\n"
                + ")";
                execAction(qu);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "----setUp Database");
        }
    }
    
    public boolean deleteBook(Book book)
    {
        try {
            String qu = "delete from book where id=?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, book.getId());
            if(smt.executeUpdate() == 1)
            {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean deleteMember(ListMemberController.Member member)
    {
        try {
            String qu = "delete from member where id=?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, member.getId());
            if(smt.executeUpdate() == 1)
            {
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isBookAlreadyIssued(Book book)
    {
        try {
            int count = 0;
            String qu = "select * from issue where bookid = ?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, book.getId());
            ResultSet rs = smt.executeQuery();
            while(rs.next()){
                count++;
            }
            if(count == 0){
                return false;
            }
            else{
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean isMemberHaveBook(ListMemberController.Member member)
    {
        try {
            int count = 0;
            String qu = "select * from issue where memberid = ?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, member.getId());
            ResultSet rs = smt.executeQuery();
            while(rs.next()){
                count++;
            }
            if(count == 0){
                return false;
            }
            else{
                return true;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public boolean updateBook(Book book)
    {
        try {
            String qu = "update book set title = ?,author = ?,publisher = ?,intcode = ? where id = ?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, book.getTitle());
            smt.setString(2, book.getAuthor());
            smt.setString(3, book.getPublisher());
            smt.setString(4, book.getCode());
            smt.setString(5, book.getId());
            int res = smt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }
    
    public boolean updateMember(Member member)
    {
        try {
            String qu = "update member set name = ?,mobile = ?,email = ? where id = ?";
            PreparedStatement smt = con.prepareStatement(qu);
            smt.setString(1, member.getName());
            smt.setString(2, member.getMobile());
            smt.setString(3, member.getEmail());
            smt.setString(4, member.getId());
            int res = smt.executeUpdate();
            return (res>0);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
        
    }
}
