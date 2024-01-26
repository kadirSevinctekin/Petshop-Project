import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OperationPage {

    private DBConnect database = new DBConnect();
    private Connection c = database.connectToDb("Petshop", "postgres", "1234");

    public OperationPage(String username) {

        // Ana frame oluştur
        JFrame frame = new JFrame("Operation Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(5, 1)); // Buton eklediğimiz için 5 satır

        // Butonlar oluştur
        JButton listPetsButton = new JButton("List My Pets");
        JButton advertisementsButton = new JButton("Look at Advertisements");
        JButton listApplicationsButton = new JButton("List My Applications");
        JButton addPetButton = new JButton("Add Pet"); // Yeni buton
        JButton backButton = new JButton("Back");

        // Butonlara ActionListener ekleyerek tıklama olaylarını dinle
        listPetsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new listPets(username);
            }
        });

        advertisementsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new lookAdvertisements(username);
            }
        });

        listApplicationsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new listMyApplications(username);
            }
        });

        addPetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new addPet(username);
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Back butonuna tıklandığında yapılacak işlemler
                frame.dispose(); // Bu pencereyi kapat
                new Login();
            }
        });

        // Butonları frame'e ekle
        frame.add(listPetsButton);
        frame.add(advertisementsButton);
        frame.add(listApplicationsButton);
        frame.add(addPetButton);
        frame.add(backButton);

        // Frame'i boyutlandır, konumlandır ve görünür yap
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
