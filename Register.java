import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Register {

    DBConnect database = new DBConnect();
    Connection c = database.connectToDb("Petshop", "postgres", "1234");
    JFrame frame;

    public Register() {
        frame = new JFrame("Register Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        // İsim, soyisim, kullanıcı adı, şifre, yaş ve adres için JLabel ve JTextField/JPasswordField
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(15);

        JLabel surnameLabel = new JLabel("Surname:");
        JTextField surnameField = new JTextField(15);

        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JLabel ageLabel = new JLabel("Age:");
        JTextField ageField = new JTextField(3);

        JLabel addressLabel = new JLabel("Address:");
        JTextField addressField = new JTextField(15);

        // Buton paneli için JPanel
        JPanel buttonPanel = new JPanel();

        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        // Buton boyutları ve fontu ayarla
        Dimension buttonSize = new Dimension(150, 50);
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        registerButton.setPreferredSize(buttonSize);
        registerButton.setFont(buttonFont);

        backButton.setPreferredSize(buttonSize);
        backButton.setFont(buttonFont);

        // İsim, soyisim, kullanıcı adı, şifre, yaş ve adres alanlarını ekleyin
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER; // Yazıları ortala
        frame.add(nameLabel, gbc);

        gbc.gridy = 1;
        frame.add(nameField, gbc);

        gbc.gridy = 2;
        frame.add(surnameLabel, gbc);

        gbc.gridy = 3;
        frame.add(surnameField, gbc);

        gbc.gridy = 4;
        frame.add(userLabel, gbc);

        gbc.gridy = 5;
        frame.add(userField, gbc);

        gbc.gridy = 6;
        frame.add(passwordLabel, gbc);

        gbc.gridy = 7;
        frame.add(passwordField, gbc);

        gbc.gridy = 8;
        frame.add(ageLabel, gbc);

        gbc.gridy = 9;
        frame.add(ageField, gbc);

        gbc.gridy = 10;
        frame.add(addressLabel, gbc);

        gbc.gridy = 11;
        frame.add(addressField, gbc);

        gbc.gridy = 12;
        frame.add(buttonPanel, gbc);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Register butonuna tıklandığında yapılacak işlemler
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String surname = surnameField.getText();
                String username = userField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);
                String address = addressField.getText();
                String ageText = ageField.getText();
                int flag = validateRegistration(name, surname, username, password, ageText);

                // Kayıt bilgilerini kontrol etme
                if (flag == 0) {
                    JOptionPane.showMessageDialog(frame, "Invalid registration information", "Error", JOptionPane.ERROR_MESSAGE);
                } else if (flag == 1) {
                    JOptionPane.showMessageDialog(frame, "This username is already used!");
                } else {
                    int age = Integer.parseInt(ageText);
                    if(age<18){
                        JOptionPane.showMessageDialog(frame, "Those under the age of 18 cannot register!(TRIGGER)");
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "Registration successful!");
                        String q = "INSERT INTO users VALUES (DEFAULT, ?, ?, ?, ?, ?, ?)";
                        try {
                            PreparedStatement pt = c.prepareStatement(q);
                            pt.setString(1, username);
                            pt.setString(2, password);
                            pt.setString(3, name);
                            pt.setString(4, surname);
                            pt.setInt(5, age);
                            pt.setString(6, address);
                            pt.executeUpdate();
                        } catch (SQLException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        // Back butonuna tıklandığında yapılacak işlemler
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Bu pencereyi kapat
                new firstPage(); // Giriş ekranını aç
            }
        });
    }

    private int validateRegistration(String name, String surname, String username, String password, String age) {
        if (!name.isEmpty() && !surname.isEmpty() && !username.isEmpty() && !password.isEmpty() && !age.isEmpty()) {
            String query = "SELECT username FROM users WHERE username=?";
            try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
                preparedStatement.setString(1, username);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        System.out.println("Bu kullanici adi sistemde kayitli");
                        return 1;
                    } else {
                        System.out.println("Kayit yapilabilir");
                        return 2;
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } else {
            System.out.println("Eksik girdiler");
            return 0;
        }
    }
}
