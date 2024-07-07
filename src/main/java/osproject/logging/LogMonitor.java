package osproject.logging;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import osproject.StringObserver;

public class LogMonitor {
    private FileWriter fileWriter;
    private static final String FILE_PATH = "log.txt";
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    StringObserver stringObserver;
    private static String string;

    public LogMonitor() {
        try {
            this.fileWriter = new FileWriter(FILE_PATH, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void log(String message) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String str = dtf.format(now) + " - " + message + "\n";

            String updatedValue = stringObserver.get() + str;
            String[] lines = updatedValue.split("\n");

            if (lines.length > 5) {
                String[] lastFiveLines = Arrays.copyOfRange(lines, lines.length - 5, lines.length);

                updatedValue = String.join("\n", lastFiveLines) + "\n";
            }

            // Step 6: Set the new value to stringObserver
            stringObserver.setValue(updatedValue);
            fileWriter.write(str);
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public StringObserver getObserver() {
        return stringObserver;
    }

    public void setObserver(StringObserver observer) {
        stringObserver = observer;
    }

}
