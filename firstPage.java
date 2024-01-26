import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class firstPage {

    firstPage() {
            JFrame frame = new JFrame("Welcome Screen");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridBagLayout());

            // Welcome metni için JLabel
            JLabel welcomeLabel = new JLabel("Welcome");
            welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

            // Buton paneli için JPanel
            JPanel buttonPanel = new JPanel();

            JButton loginButton = new JButton("Login");
            JButton registerButton = new JButton("Register");

            // Buton boyutları ve fontu ayarla
            Dimension buttonSize = new Dimension(150, 50);
            Font buttonFont = new Font("Arial", Font.PLAIN, 18);

            loginButton.setPreferredSize(buttonSize);
            loginButton.setFont(buttonFont);

            registerButton.setPreferredSize(buttonSize);
            registerButton.setFont(buttonFont);



            // Welcome metni ve butonları ekleyin
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(10, 0, 10, 0);
            frame.add(welcomeLabel, gbc);

            gbc.gridy = 1;
            frame.add(buttonPanel, gbc);

            buttonPanel.add(loginButton);
            buttonPanel.add(registerButton);

            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);


        // Login butonuna tıklandığında yapılacak işlemler
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Login lp = new Login() ;
                frame.dispose();
            }
        });

        // Register butonuna tıklandığında yapılacak işlemler
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Register rg = new Register() ;
                frame.dispose();
            }
        });
    }
}
