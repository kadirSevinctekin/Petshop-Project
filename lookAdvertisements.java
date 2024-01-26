import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class lookAdvertisements {

    private DBConnect database = new DBConnect();
    private Connection c = database.connectToDb("Petshop", "postgres", "1234");
    private JFrame frame;
    private DefaultTableModel tableModel;

    public lookAdvertisements(String username) {
        frame = new JFrame("Advertisements");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        // Tablo modelini oluştur
        String[] columnNames = {"Pet Name", "Pet Type", "Age", "Price", "Advertisement ID"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // JTable oluştur
        JTable table = new JTable(tableModel);

        // Tablo panelini oluştur
        JScrollPane scrollPane = new JScrollPane(table);

        // Tabloyu doldur
        listAdvertisements();

        // Apply butonunu oluştur
        JButton applyButton = new JButton("Apply");
        applyButton.setEnabled(false);
        applyButton.setPreferredSize(new Dimension(200, 100));

        applyButton.addActionListener(e -> {
            int userID = 0;
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String petName = table.getValueAt(selectedRow, 0).toString();
                String petType = table.getValueAt(selectedRow, 1).toString();
                int age = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
                double petPrice = Double.parseDouble(table.getValueAt(selectedRow, 3).toString());
                int advertisementId = Integer.parseInt(table.getValueAt(selectedRow, 4).toString());

                //Bu pete basvuru yapabilir mi kontrolu
                String sql = "SELECT getUserIdByUsername(?)";
                try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {
                    preparedStatement.setString(1, username);
                    try (ResultSet resultSet = preparedStatement.executeQuery()) {
                        if (resultSet.next()) {
                            userID = resultSet.getInt(1);
                        } else {
                            JOptionPane.showMessageDialog(frame, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                if(isValid(userID, advertisementId)){
                    String priceStr = JOptionPane.showInputDialog(frame, "Enter the price:", "Apply Price", JOptionPane.PLAIN_MESSAGE);

                    if (priceStr != null && !priceStr.isEmpty()) {
                        try {
                            double price = Double.parseDouble(priceStr);

                            if(price>10000){
                                JOptionPane.showMessageDialog(frame, "Price cannot be more than 10000!", "Error", JOptionPane.ERROR_MESSAGE);
                            }

                            else{

                                String advertiseQuery = "INSERT INTO applications VALUES (DEFAULT, ?, ?, ?)";
                                try (PreparedStatement advertiseStatement = c.prepareStatement(advertiseQuery)) {
                                    advertiseStatement.setInt(1, userID);
                                    advertiseStatement.setInt(2, advertisementId);
                                    advertiseStatement.setDouble(3, price);

                                    // Advertise tablosuna veriyi ekle
                                    int rowsAffected = advertiseStatement.executeUpdate();

                                    if (rowsAffected > 0) {
                                        System.out.println("Applied successfully.");
                                        JOptionPane.showMessageDialog(frame, "Applied successfully.");

                                    } else {
                                        System.out.println("Failed to advertise pet.");
                                    }
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                }
                            }

                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid price format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                else{
                    JOptionPane.showMessageDialog(frame, "You cannot apply this pet!(Own pet or already applied)", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Tablodan seçim dinleyicisi ekle
        table.getSelectionModel().addListSelectionListener(e -> {
            // Seçili satır varsa applyButton'ı etkinleştir, yoksa devre dışı bırak
            applyButton.setEnabled(table.getSelectedRow() != -1);
        });

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(applyButton, BorderLayout.SOUTH);

        // Frame'e paneli ekle
        frame.add(panel);

        // Frame'i görünür yap
        frame.setVisible(true);
    }

    private void listAdvertisements() {
        try {
            String query = "select pet_name, pet_type, age, price, advertisement_id from pets, advertisements where pets.pet_id=advertisements.pet_id order by pet_type";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // JTable içinde kullanılacak modeli temizle
                    tableModel.setRowCount(0);

                    while (resultSet.next()) {
                        String petName = resultSet.getString("pet_name");
                        String petType = resultSet.getString("pet_type");
                        int age = resultSet.getInt("age");
                        double price = resultSet.getDouble("price");
                        int advertisementID = resultSet.getInt("advertisement_id");

                        // Model üzerine yeni satır ekle
                        tableModel.addRow(new Object[]{petName, petType, age, price, advertisementID});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValid(int userID, int advertisementID){
        String query = "SELECT * FROM advertisements WHERE advertisement_id=? and user_id=?";
        try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setInt(1, advertisementID);
            preparedStatement.setInt(2, userID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Kendi ilanina basvuramazsin!");
                    return false;
                } else {
                    String query2 = "SELECT * FROM applications WHERE advertisement_id=? and user_id=?";
                    try (PreparedStatement preparedStatement2 = c.prepareStatement(query2)) {
                        preparedStatement2.setInt(1, advertisementID);
                        preparedStatement2.setInt(2, userID);

                        try (ResultSet resultSet2 = preparedStatement2.executeQuery()) {
                            if (resultSet2.next()) {
                                System.out.println("Zaten basvurulmus!");
                                return false;
                            } else {
                                return true;
                            }
                        }
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
