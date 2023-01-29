package com.feedbeforeflight;

import com.feedbeforeflight.PdfStamper.PdfStamper;
import com.feedbeforeflight.PdfStamper.PdfStamperConfigManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfStamperApp
{
    public static void main( String[] args ) {

        if (args.length == 0 || args[0].equals("-help") || args[0].equals("-?")) {
            System.out.println(helpString());
            return;
        }

        if (args[0].equals("-makeconfig")) {
            System.out.println(PdfStamperConfigManager.makeDefaultConfigString());
            return;
        }

        if (args.length != 3) {
            System.out.println("Wrong number of arguments");
            System.out.println("Expected 3, provided " + args.length);
            return;
        }

        Path sourceDocumentPath = Paths.get(args[0]);
        Path stampDocumentPath = Paths.get(args[1]);
        Path resultDocumentPath = Paths.get(args[2]);

        if (!Files.exists(sourceDocumentPath)) {
            System.out.println("Source file does not exists");
            return;
        }
        if (!Files.exists(stampDocumentPath)) {
            System.out.println("Stamp file does not exists");
            return;
        }

        try {
            Files.deleteIfExists(resultDocumentPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        PdfStamper pdfStamper = new PdfStamper();
        Path configFilePath = PdfStamperConfigManager.findConfigFilePath();
        if (configFilePath != null) {
            PdfStamperConfigManager.applyPropertiesFile(configFilePath, pdfStamper);
        }

        try {
            pdfStamper.applyStamp(Files.newInputStream(sourceDocumentPath),
                    Files.newInputStream(stampDocumentPath),
                    Files.newOutputStream(resultDocumentPath));
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

    private static String helpString() {
        return """
                Usage:

                1. Add stamp to pdf file:
                java -jar PdfStamper.jar <source pdf path> <stamp pdf path> <destination pdf path>

                2. Make default configuration:
                java -jar PdfStamper.jar -makeconfig >>PdfStamper.properties

                3. Help:run without parameters or pass -help or -? parameter""";
    }


}
