package game;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.swing.*;

import java.util.Date;

public class ScoreMinesweeper {
    private String playerName;
    private static TileMinesweeper[][] matrix;
    private int score;
    private TimerMinesweeper gameTime;
    private String sysTime;
    private String difficulty;
    private int ROWS;
    private int COLS;

    ScoreMinesweeper(String playerName, int score,TimerMinesweeper gameTime, String difficulty, int ROWS, int COLS,TileMinesweeper matrix[][]) {
        this.playerName = playerName;
        this.score = score;
        this.gameTime = gameTime;
        this.difficulty = difficulty;
        this.ROWS = ROWS;
        this.COLS = COLS;
        this.matrix = matrix;
        Date currentTime = new Date();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        String formattedTime = dateFormat.format(currentTime);
        this.sysTime = formattedTime;

    }

    private void calculate_score() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++)
                if (matrix[i][j].isBomb() && matrix[i][j].isFlag()) { score++; }
        }
    }


    private String getFormattedTime() {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(gameTime.getTimer());
    }

    public void getScore() {
        calculate_score();
    }

    public String[] getScoreObjectValues(){
        String[] values = {playerName, String.valueOf(score)};
        return values;
    }

    public void scoreReset(){ score = 0; }

    public void writeScoreToFile() {
        try {
            if (score != 0) {
                FileWriter writer = new FileWriter("scores.txt", true);
                //String formattedTime = getFormattedTime();
                String line = String.format("%s,%d,%s,%s,%d,%d", playerName, score, getFormattedTime(), difficulty, ROWS, COLS);
                writer.write(line + System.lineSeparator());
                writer.close();
                System.out.println("Score saved to scores.txt: " + line);
            } else {
                JOptionPane.showMessageDialog(null, "You found zero mines, Better Luck Next Time","Failed",JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException e) {
            System.out.println("Error writing score to file: " + e.getMessage());
        }
    }

  /*  private static String readScoresFromFile() {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader("scores.txt"))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                String playerName = parts[0];
                int score = Integer.parseInt(parts[1]);
                String gameTime = parts[2];
                String difficulty = parts[3];
                int rows = Integer.parseInt(parts[4]);
                int cols = Integer.parseInt(parts[5]);

                sb.append(String.format("%s: %d (%s) - %s (%d x %d)\n", playerName, score, gameTime, difficulty, rows, cols));
            }
        } catch (IOException e) {
            System.out.println("Error reading scores from file: " + e.getMessage());
        }

        return sb.toString();
    } */

    /*public static void showScores(){
        String rawScore = readScoresFromFile();
        JOptionPane.showMessageDialog(null, rawScore, "Minesweeper High Scores", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void sortScores(String inputFile) throws IOException {

        // Read the input file into an array of strings
        String[] scores = readScores(inputFile);

        // Sort the array based on the third value using a custom comparator
        Arrays.sort(scores, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                double v1 = Double.parseDouble(s1.split(",")[2]);
                double v2 = Double.parseDouble(s2.split(",")[2]);
                return Double.compare(v1, v2);
            }
        });
        writeScores(inputFile, scores);
    } */

    private static String[] readScores(String inputFile) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            return br.lines().toArray(String[]::new);
        }
    }

    private static void writeScores(String inputFile, String[] scores) throws IOException {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(inputFile))) {
            for (String score : scores) {
                bw.write(score);
                bw.newLine();
            }
        }
    }


}
