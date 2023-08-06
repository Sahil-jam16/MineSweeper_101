package game;

import java.sql.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class JDBCMineSweeper extends JFrame implements ActionListener {
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/jdbc_minesweeper";
    static final String USER = "root";
    static final String PASS = "root";

    private JTextField nameInput;
    private JButton calculateButton;
    private JLabel resultLabel;

    public JDBCMineSweeper() {
        setTitle("Average Score");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 150);
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.add(new JLabel("Enter player name:"), BorderLayout.WEST);
        nameInput = new JTextField();
        inputPanel.add(nameInput, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(this);
        buttonPanel.add(calculateButton);

        JPanel resultPanel = new JPanel(new BorderLayout(10, 10));
        resultLabel = new JLabel();
        resultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        resultPanel.add(resultLabel, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        mainPanel.add(resultPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    public static void addScoresDatabase(ScoreMinesweeper score) {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER,  PASS);

            java.util.Date currentTime = new Date();
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            String formattedTime = dateFormat.format(currentTime);

            String[] values = score.getScoreObjectValues();

            String sql = "INSERT INTO score_table (name, score, time) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, values[0] );
            stmt.setInt(2, Integer.parseInt(values[1]) );
            stmt.setTime(3, Time.valueOf(formattedTime) );

            stmt.executeUpdate();

            stmt.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void calculateAverages() {
        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);


            String playerName = JOptionPane.showInputDialog(null, "Please enter the player name:");
            ImageIcon icon = new ImageIcon(".//res//info.png");

            String sqlCheck = "SELECT COUNT(*) AS count FROM Score_table WHERE name = ?";
            PreparedStatement statementCheck = conn.prepareStatement(sqlCheck);
            statementCheck.setString(1, playerName);
            ResultSet rsCheck = statementCheck.executeQuery();

            int count = 0;
            if (rsCheck.next()) {
                count = rsCheck.getInt("count");
            }

            if (count == 0) {
                JOptionPane.showMessageDialog(null, "Player name not found in database", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String sql1 = "SELECT AVG(score) AS avg_score FROM Score_table WHERE name = ? AND time BETWEEN '06:00:00' AND '13:59:59'";
            PreparedStatement statement1 = conn.prepareStatement(sql1);
            statement1.setString(1, playerName);
            ResultSet rs1 = statement1.executeQuery();

            String sql2 = "SELECT AVG(score) AS avg_score FROM Score_table WHERE name = ? AND time BETWEEN '14:00:00' AND '21:59:59'";
            PreparedStatement statement2 = conn.prepareStatement(sql2);
            statement2.setString(1, playerName);
            ResultSet rs2 = statement2.executeQuery();

            double avgScore1 = 0.0;
            double avgScore2 = 0.0;

            if (rs1.next()) {
                avgScore1 = rs1.getDouble("avg_score");
            }

            if (rs2.next()) {
                avgScore2 = rs2.getDouble("avg_score");
            }

            String message;

            if (avgScore1 > avgScore2) {
                message = String.format("Average score for %s between 6 AM - 2 PM is %.2f, which is greater than the average score between 2 PM - 10 PM (%.2f)", playerName, avgScore1, avgScore2);
            } else if (avgScore1 < avgScore2) {
                message = String.format("Average score for %s between 2 PM - 10 PM is %.2f, which is greater than the average score between 6 AM - 2 PM (%.2f)", playerName, avgScore2, avgScore1);
            } else {
                message = String.format("Average score for %s between 6 AM - 2 PM and 2 PM - 10 PM is %.2f, which are equal", playerName, avgScore1);
            }

            JOptionPane.showMessageDialog(null, message, "Result", JOptionPane.INFORMATION_MESSAGE,icon);

            rs1.close();
            statement1.close();
            rs2.close();
            statement2.close();
            conn.close();

        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        String playerName = nameInput.getText().trim();
        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the player name", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Class.forName(JDBC_DRIVER);
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);

            String sql = "SELECT AVG(score) AS avg_score FROM Score_table WHERE name = ?";
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, playerName);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                double avgScore = rs.getDouble("avg_score");
                resultLabel.setText(String.format("Average score for %s: %.2f", playerName, avgScore));
            } else {
                JOptionPane.showMessageDialog(this, "No scores found for player " + playerName, "Result",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
