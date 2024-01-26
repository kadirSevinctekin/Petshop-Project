import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class addPet {

    private DBConnect database = new DBConnect();
    private Connection c = database.connectToDb("Petshop", "postgres", "1234");
    private JFrame frame;
    private JLabel petNameLabel = new JLabel("Pet Name:");
    private JTextField petNameField = new JTextField(15);

    private JLabel petTypeLabel = new JLabel("Pet Type:");
    private JTextField petTypeField = new JTextField(15);

    private JLabel ageLabel = new JLabel("Age:");
    private JTextField ageField = new JTextField(3);

    private JButton addButton = new JButton("Add Pet");

    public addPet(String username) {
        frame = new JFrame("Add Pet");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);

        // GridBagLayout kullan
        frame.setLayout(new GridBagLayout());

        // GridBagConstraints oluştur
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Pet Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        frame.add(petNameLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        frame.add(petNameField, gbc);

        // Pet Type
        gbc.gridx = 0;
        gbc.gridy = 1;
        frame.add(petTypeLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(petTypeField, gbc);

        // Age
        gbc.gridx = 0;
        gbc.gridy = 2;
        frame.add(ageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        frame.add(ageField, gbc);

        // Add Pet button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(addButton, gbc);

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    addPET(username);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        // Frame'i boyutlandır, konumlandır ve görünür yap
        frame.setSize(400, 300);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void addPET(String username) throws SQLException {
        String petName = petNameField.getText();
        String petType = petTypeField.getText();
        String ageStr = ageField.getText();
        int userId = 0;

        if (ageStr != null && !petName.isEmpty() && !petType.isEmpty()){
            // Fonksiyon çağrısı için PreparedStatement oluştur
            String sql = "SELECT getUserIdByUsername(?)";
            try (PreparedStatement preparedStatement = c.prepareStatement(sql)) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        userId = resultSet.getInt(1);
                    } else {
                        JOptionPane.showMessageDialog(frame, "User not found!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            // Yaş alanını sayıya dönüştürme
            int age = 0;
            try {
                age = Integer.parseInt(ageStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(frame, "Invalid age format. Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String advertiseQuery = "INSERT INTO pets VALUES (DEFAULT, ?, ?, ?, ?)";
            try (PreparedStatement advertiseStatement = c.prepareStatement(advertiseQuery)) {
                advertiseStatement.setInt(1, userId);
                advertiseStatement.setString(2, petName);
                advertiseStatement.setString(3, petType);
                advertiseStatement.setInt(4, age);

                // Advertise tablosuna veriyi ekle
                int rowsAffected = advertiseStatement.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("Pet advertised successfully.");
                    JOptionPane.showMessageDialog(frame, "Pet added successfully.");

                } else {
                    System.out.println("Failed to advertise pet.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            frame.dispose();
        }
        else{
            JOptionPane.showMessageDialog(frame, "Invalid format!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
