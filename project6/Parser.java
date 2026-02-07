import java.io.*;
import java.util.*;

public class Parser {

    private List<String> lines = new ArrayList<>();
    private int currentIndex = -1;
    private String currentCommand;

    public Parser(String fileName) throws IOException {

        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line;

        while ((line = reader.readLine()) != null) {
            line = line.split("//")[0].trim();
            if (!line.isEmpty()) {
                lines.add(line);
            }
        }
        reader.close();
    }

    public boolean hasMoreCommands() {
        return currentIndex + 1 < lines.size();
    }

    public void advance() {
        currentIndex++;
        currentCommand = lines.get(currentIndex);
    }

    public String commandType() {
        if (currentCommand.startsWith("@")) return "A_COMMAND";
        if (currentCommand.startsWith("(")) return "L_COMMAND";
        return "C_COMMAND";
    }

    public String symbol() {
        if (commandType().equals("A_COMMAND"))
            return currentCommand.substring(1);
        if (commandType().equals("L_COMMAND"))
            return currentCommand.substring(1, currentCommand.length()-1);
        return null;
    }

    public String dest() {
        if (!currentCommand.contains("=")) return null;
        return currentCommand.split("=")[0];
    }

    public String comp() {
        String temp = currentCommand;
        if (temp.contains("=")) temp = temp.split("=")[1];
        if (temp.contains(";")) temp = temp.split(";")[0];
        return temp;
    }

    public String jump() {
        if (!currentCommand.contains(";")) return null;
        return currentCommand.split(";")[1];
    }
}
