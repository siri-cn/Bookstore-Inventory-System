package bookstore;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    public static void main(String[] args) {
        try (ServerSocket ss = new ServerSocket(5000)) {
            System.out.println("Server ON - Port 5000");
            while (true) {
                Socket s = ss.accept();
                new Thread(() -> handle(s)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    static void handle(Socket s) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
             PrintWriter out = new PrintWriter(s.getOutputStream(), true)) {
            
            String req = in.readLine();
            String[] p = req.split("\\|");
            
            if (p[0].equals("ADD")) {
                Book b = new Book(p[1], p[2], Double.parseDouble(p[3]), Integer.parseInt(p[4]));
                out.println(DB.add(b) ? "SUCCESS" : "ERROR");
            } else if (p[0].equals("VIEW")) {
                List<Book> books = DB.getAll();
                StringBuilder sb = new StringBuilder("SUCCESS|");
                for (Book b : books) {
                    sb.append(b.getId()).append(",").append(b.getTitle()).append(",")
                      .append(b.getAuthor()).append(",").append(b.getPrice()).append(",")
                      .append(b.getQuantity()).append(";");
                }
                out.println(sb);
            } else if (p[0].equals("DELETE")) {
                out.println(DB.delete(Integer.parseInt(p[1])) ? "SUCCESS" : "ERROR");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}