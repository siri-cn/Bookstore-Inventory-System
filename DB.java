package bookstore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private static final String URL = "jdbc:mysql://localhost:3306/bookstore_db";
    private static final String USER = "root";
    private static final String PASS = "12345";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
    
    public static boolean add(Book b) {
        try (Connection c = connect(); 
             PreparedStatement ps = c.prepareStatement("INSERT INTO books (title,author,price,quantity) VALUES (?,?,?,?)")) {
            ps.setString(1, b.getTitle());
            ps.setString(2, b.getAuthor());
            ps.setDouble(3, b.getPrice());
            ps.setInt(4, b.getQuantity());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public static List<Book> getAll() {
        List<Book> list = new ArrayList<>();
        try (Connection c = connect(); 
             Statement s = c.createStatement(); 
             ResultSet rs = s.executeQuery("SELECT * FROM books")) {
            while (rs.next()) {
                Book b = new Book();
                b.setId(rs.getInt("id"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setPrice(rs.getDouble("price"));
                b.setQuantity(rs.getInt("quantity"));
                list.add(b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static boolean delete(int id) {
        try (Connection c = connect(); 
             PreparedStatement ps = c.prepareStatement("DELETE FROM books WHERE id=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}