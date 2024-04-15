import java.awt.*;
import java.awt.event.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GenerateBill extends JFrame {

    JTextField food, quantity;
    String[] columnNames = { "Food Name",
            "Quantity",
            "Price"
    };
    JTable cart;

    JLabel totalP = new JLabel();
    Object data[][] = new Object[100][3];
    int i = 0;
    double totalprice = 0;
    ArrayList<foodCart> foodList = new ArrayList<>();

    GenerateBill() {
        JPanel jp1 = new JPanel(new GridLayout(2, 1));
        JPanel jp2 = new JPanel(new BorderLayout());

        JLabel a = new JLabel("Food Name : ");
        jp1.add(a);
        food = new JTextField(50);
        jp1.add(food);

        JLabel b = new JLabel("Quantity : ");
        jp1.add(b);
        quantity = new JTextField(50);
        jp1.add(quantity);

        JButton ok = new JButton("OK");
        jp1.add(ok);

        ok.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PreparedStatement pst;
                DBConnection con = new DBConnection();
                ResultSet rs;
                try {
                    pst = con.mkDataBase()
                            .prepareStatement("select f_prize from canteenmanagement.food where f_name = ?");
                    pst.setString(1, food.getText());
                    rs = pst.executeQuery();

                    while (rs.next()) {
                        foodCart f = new foodCart();
                        f.name = food.getText();
                        f.quantity = Integer.parseInt(quantity.getText());
                        f.totalPer = f.quantity * rs.getDouble("f_prize");
                        totalprice += f.quantity * rs.getDouble("f_prize");

                        foodList.add(f);
                        data[i][0] = f.name;
                        data[i][1] = f.quantity;
                        data[i][2] = f.totalPer;
                        i++;

                        // Update table model
                        DefaultTableModel model = (DefaultTableModel) cart.getModel();
                        model.addRow(new Object[] { f.name, f.quantity, f.totalPer });

                        // Update total price label
                        totalP.setText("TOTAL Price : " + totalprice + "tk");
                    }
                } catch (Exception ex) {
                    System.out.println(ex);
                }
            }
        });

        cart = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(cart);
        jp2.add(scrollPane, BorderLayout.CENTER);
        jp2.add(totalP, BorderLayout.SOUTH);

        JButton checkOut = new JButton("CheckOut");
        checkOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int count = 1;
                for (foodCart fc : foodList) {
                    System.out.println(count + ": Food Name : " + fc.name + " Quantity : " + fc.quantity + " Price : "
                            + fc.totalPer + "tk");

                }
                double vat = 15;
                System.out.println("Total Cost : " + (totalprice + totalprice * vat / 100) + "tk");

                JOptionPane.showMessageDialog(null,
                        "Total Cost : " + (totalprice + totalprice * vat / 100) + "tk (with vat " + vat
                                + "% included)");
                dispose();
            }
        });
        jp2.add(checkOut, BorderLayout.NORTH);

        add(jp1, BorderLayout.NORTH);
        add(jp2, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 550);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    class foodCart {
        String name;
        Double totalPer;
        int quantity;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GenerateBill();
        });
    }
}
