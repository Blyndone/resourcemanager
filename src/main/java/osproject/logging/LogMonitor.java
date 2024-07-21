package osproject.logging;

import java.io.File;
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
    private static StringBuilder log = new StringBuilder();

    public LogMonitor() {

    }

    public void log(String message) {

        LocalDateTime now = LocalDateTime.now();
        String str = dtf.format(now) + " - " + message + "\n";

        log.append(str);

        String updatedValue = stringObserver.get() + str;
        String[] lines = updatedValue.split("\n");

        if (lines.length > 6) {
            String[] lastFiveLines = Arrays.copyOfRange(lines, lines.length - 6, lines.length);

            updatedValue = String.join("\n", lastFiveLines) + "\n";
        }

        // Step 6: Set the new value to stringObserver
        stringObserver.setValue(updatedValue);

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

    public void saveLog() throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDateTime now = LocalDateTime.now();
        String fileName = dtf.format(now) + "_" + FILE_PATH;

        File file = new File(fileName);
        int counter = 1;

        // Check if the file exists and generate a new file name if it does
        while (file.exists()) {
            fileName = dtf.format(now) + "_" + counter + "_" + FILE_PATH;
            file = new File(fileName);
            counter++;
        }

        this.fileWriter = new FileWriter(fileName, true);
        fileWriter.write(log.toString());
        fileWriter.flush();
    }

}
