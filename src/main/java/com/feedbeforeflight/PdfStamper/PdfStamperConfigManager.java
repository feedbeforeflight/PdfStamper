package com.feedbeforeflight.PdfStamper;

import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class PdfStamperConfigManager {

    public static void applyPropertiesFile(Path propertiesFilePath, PdfStamper pdfStamper) {

        if (propertiesFilePath == null) {return;}

        Properties properties = new Properties();
        try(final FileChannel channel = FileChannel.open(propertiesFilePath, StandardOpenOption.READ);
            final FileLock lock = channel.lock(0L, Long.MAX_VALUE, true)) {
                properties.load(Channels.newInputStream(channel));

                pdfStamper.setCoarseDPI(Integer.parseInt(properties.getProperty("CoarseDPI")));
                pdfStamper.setFineDPI(Integer.parseInt(properties.getProperty("FineDPI")));
                pdfStamper.setBottomThreshold(Integer.parseInt(properties.getProperty("BottomThreshold")));
                pdfStamper.setStampInsertionMarginWidth(Float.parseFloat(properties.getProperty("StampInsertionMarginWidth")));
                pdfStamper.setStampInsertionContentSpacing(Float.parseFloat(properties.getProperty("StampInsertionContentSpacing")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String makeDefaultConfigString() {

        PdfStamper template = new PdfStamper();

        return "CoarseDPI = " +
                template.getCoarseDPI() +
                "\nFineDPI = " +
                template.getFineDPI() +
                "\nBottomThreshold = " +
                template.getBottomThreshold() +
                "\nStampInsertionMarginWidth = " +
                template.getStampInsertionMarginWidth() +
                "\nStampInsertionContentSpacing = " +
                template.getStampInsertionContentSpacing();
    }

}
