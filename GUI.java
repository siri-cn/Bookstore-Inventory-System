package bookstore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class GUI extends JFrame {
    JTextField t1, t2, t3, t4;
    JTable table;
    DefaultTableModel model;
    
    public GUI() {
        setTitle("Bookstore System");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Input Panel
        JPanel input = new JPanel(new GridLayout(5, 2, 5, 5));
        input.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        input.add(new JLabel("Title:"));
        t1 = new JTextField();
        input.add(t1);
        
        input.add(new JLabel("Author:"));
        t2 = new JTextField();
        input.add(t2);
        
        input.add(new JLabel("Price:"));
        t3 = new JTextField();
        input.add(t3);
        
        input.add(new JLabel("Quantity:"));
        t4 = new JTextField();
        input.add(t4);
        
        JButton add = new JButton("Add");
        add.addActionListener(e -> add());
        input.add(add);
        
        JButton view = new JButton("View");
        view.addActionListener(e -> view());
        input.add(view);
        
        add(input, BorderLayout.WEST);
        
        // Table
        model = new DefaultTableModel(new String[]{"ID", "Title", "Author", "Price", "Qty"}, 0);
        table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Delete Button
        JButton del = new JButton("Delete Selected");
        del.addActionListener(e -> delete());
        add(del, BorderLayout.SOUTH);
        
        setLocationRelativeTo(null);
        view();
    }
    
    void add() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            
            out.println("ADD|" + t1.getText() + "|" + t2.getText() + "|" + t3.getText() + "|" + t4.getText());
            if (in.readLine().equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "Added!");
                t1.setText(""); t2.setText(""); t3.setText(""); t4.setText("");
                view();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Server not running!");
        }
    }
    
    void view() {
        try (Socket s = new Socket("localhost", 5000);
             PrintWriter out = new PrintWriter(s.getOutputStream(), true);
             BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
            
            out.println("VIEW");
            String resp = in.readLine();
            model.setRowCount(0);
            
            String[] parts = resp.split("\\|");
            if (parts.length > 1) {
                String[] books = parts[1].split(";");
                for (String book : books) {
                    if (!book.isEmpty()) {
                        model.addRow(book.split(","));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    void delete() {
        int row = table.getSelectedRow();
        if (row >= 0) {
            String id = model.getValueAt(row, 0).toString();
            try (Socket s = new Socket("localhost", 5000);
                 PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                 BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()))) {
                
                out.println("DELETE|" + id);
                if (in.readLine().equals("SUCCESS")) {
                    JOptionPane.showMessageDialog(this, "Deleted!");
                    view();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
    }
}
