import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class listPets {

    private static DBConnect database = new DBConnect();
    private static Connection c = database.connectToDb("Petshop", "postgres", "1234");
    private static DefaultTableModel tableModel;
    private JButton advertiseButton;
    private JButton showApplicationsButton;
    private JFrame frame = new JFrame("List My Pets");
    private JTable table;

    public listPets(String username) {
        // Frame oluştur
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);

        // Tablo modelini oluştur
        String[] columnNames = {"Pet Name", "Pet Type", "Age", "Pet ID"};
        tableModel = new DefaultTableModel(columnNames, 0);

        // JTable oluştur
        table = new JTable(tableModel);

        // Tabloyu doldur
        listUserPets(username);

        // Tablo panelini oluştur
        JScrollPane scrollPane = new JScrollPane(table);

        // Advertise butonunu oluştur
        advertiseButton = new JButton("Advertise");
        advertiseButton.setEnabled(false);
        advertiseButton.setPreferredSize(new Dimension(300, 100));

        // Show Applications butonunu oluştur
        showApplicationsButton = new JButton("Show Applications");
        showApplicationsButton.setEnabled(false);
        showApplicationsButton.setPreferredSize(new Dimension(300, 100));

        // Tablodan seçim dinleyicisi ekle
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // Seçili satır varsa advertiseButton'ı ve showApplicationsButton'ı etkinleştir, yoksa devre dışı bırak
                boolean rowSelected = table.getSelectedRow() != -1;
                advertiseButton.setEnabled(rowSelected);
                showApplicationsButton.setEnabled(rowSelected);
            }
        });

        // Advertise butonuna tıklanınca yapılacak işlemler
        advertiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Seçili satırın bilgilerini al
                int selectedRow = table.getSelectedRow();
                String petName = table.getValueAt(selectedRow, 0).toString();
                String petType = table.getValueAt(selectedRow, 1).toString();
                int age = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
                int petID = Integer.parseInt(table.getValueAt(selectedRow, 3).toString());
                boolean flag = validatePet(petID);

                // Pet ilanda değilse
                if (flag) {
                    // Fiyatı kullanıcıdan al
                    String priceStr = JOptionPane.showInputDialog(frame, "Enter the price:", "Advertise Pet", JOptionPane.PLAIN_MESSAGE);

                    if (priceStr != null && !priceStr.isEmpty()) {
                        try {
                            double price = Double.parseDouble(priceStr);

                            if (price > 10000) {
                                JOptionPane.showMessageDialog(frame, "Price cannot be more than 10000!", "Error", JOptionPane.ERROR_MESSAGE);
                            } else {
                                // Advertise işlemleri burada gerçekleştirilir
                                advertisePet(username, petID, price);
                            }

                            System.out.println("Advertise button clicked for pet: " + petName + ", " + petType);
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(frame, "Invalid price format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
                // Pet zaten ilandaysa
                else {
                    JOptionPane.showMessageDialog(frame, "This pet is already in ad!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Show Applications butonuna tıklanınca yapılacak işlemler
        showApplicationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Seçili satırın bilgilerini al
                int selectedRow = table.getSelectedRow();
                String petName = table.getValueAt(selectedRow, 0).toString();
                String petType = table.getValueAt(selectedRow, 1).toString();
                int age = Integer.parseInt(table.getValueAt(selectedRow, 2).toString());
                int petID = Integer.parseInt(table.getValueAt(selectedRow, 3).toString());

                // Show Applications işlemleri burada gerçekleştirilir
                showApplications(username, petID);
            }
        });

        // Panel oluştur
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2));
        buttonPanel.add(advertiseButton);
        buttonPanel.add(showApplicationsButton);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Frame'e paneli ekle
        frame.add(panel);

        // Frame'i görünür yap
        frame.setVisible(true);
    }

    public static void listUserPets(String username) {
        try {
            String query = "SELECT pet_name, pet_type, age, pet_id FROM pets WHERE owner_id IN (SELECT user_id FROM users WHERE username = ?)";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // JTable içinde kullanılacak modeli temizle
                    tableModel.setRowCount(0);

                    while (resultSet.next()) {
                        String petName = resultSet.getString("pet_name");
                        String petType = resultSet.getString("pet_type");
                        int age = resultSet.getInt("age");
                        int petID = resultSet.getInt("pet_id");

                        // Model üzerine yeni satır ekle
                        tableModel.addRow(new Object[]{petName, petType, age, petID});
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean validatePet(int petID) {
        String query = "SELECT * FROM advertisements WHERE pet_id=?";
        try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setInt(1, petID);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return !resultSet.next(); // Eğer sonuç kümesi boşsa (pet zaten ilanda değilse), true döner
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void advertisePet(String username, int petID, double price) {
        // Örneğin, advertise tablosuna veri eklemek
        int ownerID = getUserIdByUsername(username);
        if (ownerID == -1) {
            JOptionPane.showMessageDialog(frame, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            String advertiseQuery = "INSERT INTO advertisements VALUES (DEFAULT, ?, ?, ?)";
            try (PreparedStatement advertiseStatement = c.prepareStatement(advertiseQuery)) {
                advertiseStatement.setInt(1, ownerID);
                advertiseStatement.setInt(2, petID);
                advertiseStatement.setDouble(3, price);

                int rowsAffected = advertiseStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Pet advertised successfully.");
                } else {
                    System.out.println("Failed to advertise pet.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void showApplications(String username, int petID) {
        new listApplications(username, petID);
    }

    public int getUserIdByUsername(String username) {
        int userId = -1;

        String query = "SELECT user_id FROM users WHERE username = ?";
        try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    userId = resultSet.getInt("user_id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return userId;
    }
}
