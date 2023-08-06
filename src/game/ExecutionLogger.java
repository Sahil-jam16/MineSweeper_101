package game;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ExecutionLogger {
    private final String logFilePath;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public ExecutionLogger(String logFilePath) {
        this.logFilePath = logFilePath;
        startTime = null;
        endTime = null;
    }

    public void logStart() {
        startTime = LocalDateTime.now();
        System.out.println("Application started on " + formatDate(startTime) + " at " + formatTime(startTime));
    }

    public void logEnd() {
        endTime = LocalDateTime.now();
        System.out.println("Application ended on " + formatDate(endTime) + " at " + formatTime(endTime));

        Duration executionTime = Duration.between(startTime, endTime);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFilePath, true))) {
            writer.write("Date: " + formatDate(startTime) + "\n");
            writer.write("Start Time: " + formatTime(startTime) + "\n");
            writer.write("End Time: " + formatTime(endTime) + "\n");
            writer.write("Running Time: " + formatDuration(executionTime) + "\n\n");
        } catch (IOException e) {
            System.err.println("Error logging execution to file: " + e.getMessage());
        }
    }

    private String formatDate(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return time.format(formatter);
    }

    private String formatTime(LocalDateTime time) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return time.format(formatter);
    }

    private String formatDuration(Duration duration) {
        long seconds = duration.getSeconds();
        long absSeconds = Math.abs(seconds);
        String format = String.format("%02d:%02d:%02d", absSeconds / 3600, (absSeconds % 3600) / 60, absSeconds % 60);
        return seconds < 0 ? "-" + format : format;
    }
}

