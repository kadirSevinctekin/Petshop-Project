import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    DBConnect database = new DBConnect() ;
    Connection c = database.connectToDb("Petshop", "postgres" , "1234") ;
    JFrame frame;

    public Login() {


        frame = new JFrame("Login Screen");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());

        // Kullanıcı adı ve şifre için JLabel ve JTextField
        JLabel userLabel = new JLabel("Username:");
        JTextField userField = new JTextField(15);

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        // "Şifreyi Göster" seçeneği için JCheckBox
        JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
        showPasswordCheckBox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                showPasswordStateChanged(passwordField, showPasswordCheckBox.isSelected());
            }
        });

        // Buton paneli için JPanel
        JPanel buttonPanel = new JPanel();

        JButton loginButton = new JButton("Login");
        JButton backButton = new JButton("Back");

        // Buton boyutları ve fontu ayarla
        Dimension buttonSize = new Dimension(150, 50);
        Font buttonFont = new Font("Arial", Font.PLAIN, 18);

        loginButton.setPreferredSize(buttonSize);
        loginButton.setFont(buttonFont);

        backButton.setPreferredSize(buttonSize);
        backButton.setFont(buttonFont);

        // Kullanıcı adı, şifre ve butonları ekleyin
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 0, 10, 0);
        frame.add(userLabel, gbc);

        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Yatay büyümeyi etkinleştir
        frame.add(userField, gbc);

        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE; // Yatay büyümeyi kapat
        frame.add(passwordLabel, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Yatay büyümeyi etkinleştir
        frame.add(passwordField, gbc);

        gbc.gridy = 4;
        frame.add(showPasswordCheckBox, gbc);

        gbc.gridy = 5;
        frame.add(buttonPanel, gbc);

        buttonPanel.add(loginButton);
        buttonPanel.add(backButton);

        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Login butonuna tıklandığında yapılacak işlemler
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                char[] passwordChars = passwordField.getPassword();
                String password = new String(passwordChars);

                // Giriş bilgilerini kontrol etme
                if (validateLogin(username, password)) {
                    JOptionPane.showMessageDialog(frame, "Login successful!");
                    frame.dispose();
                    OperationPage op = new OperationPage(username);
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose(); // Bu pencereyi kapat
                new firstPage(); // Giriş ekranını aç
            }
        });
    }

    private boolean validateLogin(String username, String password) {
        String query = "SELECT username, user_password FROM users WHERE username=? AND user_password=?";
        try (PreparedStatement preparedStatement = c.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Giriş Başarılı");
                    return true;
                } else {
                    System.out.println("Geçersiz Kullanıcı Adı veya Şifre");
                    return false;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showPasswordStateChanged(JPasswordField passwordField, boolean showPassword) {
        // Şifreyi göster/gizle durumuna göre şifre alanının görünürlüğünü ayarla
        if (showPassword) {
            passwordField.setEchoChar((char) 0); // Şifreyi göster
        } else {
            passwordField.setEchoChar('\u2022'); // Şifreyi gizle (nokta karakteri kullanarak)
        }
    }
}
