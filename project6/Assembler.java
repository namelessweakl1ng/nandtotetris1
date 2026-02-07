import java.io.*;

public class Assembler {

    public static void main(String[] args) throws Exception {

        if (args.length == 0) {
            System.out.println("Usage: java Assembler Prog.asm");
            return;
        }

        String inputFile = args[0];
        String outputFile = inputFile.replace(".asm", ".hack");

        SymbolTable symbolTable = new SymbolTable();
        Code code = new Code();

        // PASS 1 — record labels
        Parser parser1 = new Parser(inputFile);
        int romAddress = 0;

        while (parser1.hasMoreCommands()) {
            parser1.advance();
            String type = parser1.commandType();

            if (type.equals("L_COMMAND")) {
                symbolTable.addEntry(parser1.symbol(), romAddress);
            } else {
                romAddress++;   // Only real instructions count
            }
        }

        // PASS 2 — translate
        Parser parser2 = new Parser(inputFile);
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));

        int ramAddress = 16;

        while (parser2.hasMoreCommands()) {
            parser2.advance();
            String type = parser2.commandType();

            if (type.equals("A_COMMAND")) {

                String symbol = parser2.symbol();
                int address;

                if (symbol.matches("\\d+")) {
                    address = Integer.parseInt(symbol);
                } else {
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, ramAddress++);
                    }
                    address = symbolTable.getAddress(symbol);
                }

                String binary = String.format("%16s",
                        Integer.toBinaryString(address)).replace(' ', '0');

                writer.write(binary);
                writer.newLine();
            }

            else if (type.equals("C_COMMAND")) {
                String comp = code.comp(parser2.comp());
                String dest = code.dest(parser2.dest());
                String jump = code.jump(parser2.jump());

                writer.write("111" + comp + dest + jump);
                writer.newLine();
            }
        }

        writer.close();
        System.out.println("Generated: " + outputFile);
    }
}
