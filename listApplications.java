import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class listApplications {

    private DBConnect database = new DBConnect();
    private Connection c = database.connectToDb("Petshop", "postgres", "1234");
    private DefaultTableModel tableModel;
    private JButton sellButton;
    private int selectedApplicationID;
    private JFrame frame;
    String name;
    String surname;
    int age;
    String address;

    public listApplications(String username, int petID) {
        // Frame oluştur
        frame = new JFrame("Show Applications");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLocationRelativeTo(null);

        // Tablo modelini oluştur
        String[] columnNames = {"Name", "Surname", "Age", "Address", "Price", "Application ID"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // JTable oluştur
        JTable table = new JTable(tableModel);

        // Tabloyu doldur
        listApplications(username, petID);

        // Tablo panelini oluştur
        JScrollPane scrollPane = new JScrollPane(table);

        // Sell butonunu oluştur
        sellButton = new JButton("Sell");
        sellButton.setEnabled(false);
        sellButton.setPreferredSize(new Dimension(200, 100));

        // Tablodan seçim dinleyicisi ekle
        table.getSelectionModel().addListSelectionListener(e -> {
            // Seçili satır varsa sellButton'ı etkinleştir, yoksa devre dışı bırak
            boolean rowSelected = table.getSelectedRow() != -1;
            sellButton.setEnabled(rowSelected);

            // Seçili satır varsa, ilgili başvurunun ID'sini sakla
            if (rowSelected) {
                int selectedRow = table.getSelectedRow();
                selectedApplicationID = Integer.parseInt(table.getValueAt(selectedRow, 5).toString());

                // Seçili satırın bilgilerini değişkenlere al
                name = table.getValueAt(selectedRow, 0).toString();
                surname = table.getValueAt(selectedRow, 1).toString();
                age = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
                address = table.getValueAt(selectedRow, 3).toString();
                double price = Double.parseDouble(table.getValueAt(selectedRow, 4).toString());
            }
        });


        // Sell butonuna tıklanınca yapılacak işlemler
        sellButton.addActionListener(e -> sellPet(username, petID, selectedApplicationID, name, surname, age, address));

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(sellButton, BorderLayout.SOUTH);

        // Frame'e paneli ekle
        frame.add(panel);

        // Frame'i görünür yap
        frame.setVisible(true);
    }

    private void listApplications(String username, int petID) {
        try {
            String query = "select user_name, user_surname, users.age, address, applications.price, application_id from applications, users, advertisements where applications.user_id=users.user_id and applications.advertisement_id=advertisements.advertisement_id and advertisements.pet_id=?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setInt(1, petID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // JTable içinde kullanılacak modeli temizle
                    tableModel.setRowCount(0);

                    while (resultSet.next()) {
                        String name = resultSet.getString("user_name");
                        String surname = resultSet.getString("user_surname");
                        int age = resultSet.getInt("age");
                        String address = resultSet.getString("address");
                        double price = resultSet.getDouble("price");
                        int applicationID = resultSet.getInt("application_id");

                        // Model üzerine yeni satır ekle
                        tableModel.addRow(new Object[]{name, surname, age, address, price, applicationID});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String findUsername(String name, String surname, int age, String address) {
        String username = null;

        try {
            // Veritabanındaki tablo ve sütun isimlerine göre sorguyu güncelle
            String query = "SELECT username FROM users WHERE user_name = ? AND user_surname = ? AND age = ? AND address = ?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setString(1, name);
                preparedStatement.setString(2, surname);
                preparedStatement.setInt(3, age);
                preparedStatement.setString(4, address);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        username = resultSet.getString("username");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return username;
    }
    private void sellPet(String username, int petID, int selectedApplicationID, String name, String surname, int age, String address) {
        int advertisementID = -1;

        try {
            // İlgili application_id'nin advertisement_id'sini sorgula
            String query = "SELECT advertisement_id FROM applications WHERE application_id = ?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setInt(1, selectedApplicationID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        advertisementID = resultSet.getInt("advertisement_id");
                    }
                }
            }

            // Sell işlemleri burada gerçekleştirilir
            String sellQuery = "DELETE FROM applications WHERE advertisement_id = ?";
            try (PreparedStatement sellStatement = c.prepareStatement(sellQuery)) {
                sellStatement.setInt(1, advertisementID);

                int rowsAffected = sellStatement.executeUpdate();

                if (rowsAffected > 0) {
                    listApplications(username, petID); // Satıştan sonra başvuruları güncelle
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to sell pet.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            query = "DELETE FROM advertisements WHERE advertisement_id = ?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setInt(1, advertisementID);

                int rowsAffected = preparedStatement.executeUpdate();

            }
            //update
            String newUser = findUsername(name, surname, age, address);
            int newuserID = 0;
            String sql = "SELECT getUserIdByUsername(?)";
            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {
                preparedStatement.setString(1, newUser);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        newuserID = resultSet.getInt(1);
                    } else {
                        JOptionPane.showMessageDialog(frame, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            query = "UPDATE pets SET owner_id=? where pet_id=?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setInt(1, newuserID);
                preparedStatement.setInt(2, petID);

                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(frame, "Pet sold successfully.");
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to sell pet.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                listPets.listUserPets(username);
                frame.dispose();
            }


        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
