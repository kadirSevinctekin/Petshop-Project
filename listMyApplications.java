import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class listMyApplications {

    private DBConnect database = new DBConnect();
    private Connection c = database.connectToDb("Petshop", "postgres", "1234");
    private JFrame frame;
    private DefaultTableModel tableModel;

    public listMyApplications(String username) {
        frame = new JFrame("My Applications");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Tablo modelini oluştur
        String[] columnNames = {"Pet Name", "Pet Type", "Pet Age", "Application Price", "Pet Address"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // JTable oluştur
        JTable table = new JTable(tableModel);

        // Tablo panelini oluştur
        JScrollPane scrollPane = new JScrollPane(table);

        // Tabloyu doldur
        listMyApplications(username);

        // Panel oluştur
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        // Frame'e paneli ekle
        frame.add(panel);

        // Frame'i görünür yap
        frame.setVisible(true);
    }

    private void listMyApplications(String username) {
        int userID = 0;
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
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            String query = "SELECT pet_name, pet_type, pets.age AS pet_age, applications.price, address " +
                    "FROM users, pets, applications, advertisements " +
                    "WHERE applications.user_id = ? " +
                    "AND applications.advertisement_id = advertisements.advertisement_id " +
                    "AND advertisements.user_id = users.user_id " +
                    "AND owner_id = advertisements.user_id " +
                    "AND advertisements.pet_id=pets.pet_id";

            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setInt(1, userID);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // JTable içinde kullanılacak modeli temizle
                    tableModel.setRowCount(0);

                    while (resultSet.next()) {
                        String petName = resultSet.getString("pet_name");
                        String petType = resultSet.getString("pet_type");
                        int petAge = resultSet.getInt("pet_age");
                        double applicationPrice = resultSet.getDouble("price");
                        String userAddress = resultSet.getString("address");

                        // Model üzerine yeni satır ekle
                        tableModel.addRow(new Object[]{petName, petType, petAge, applicationPrice, userAddress});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
