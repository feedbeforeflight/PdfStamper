package com.feedbeforeflight.PdfStamper;

import java.nio.file.Path;

public class PdfStamperConfigManager {

    public static Path findConfigFilePath() {

        return null;
    }

    public static void applyPropertiesFile(Path propertiesFilePath, PdfStamper target) {

//        int coarseDPI = 100;
//        int fineDPI = 300;
//        int bottomThreshold = 100; // на последней строке есть нечто, привязанное к нижнему краю (номер строки, исполнитель, etc)
//        float stampInsertionMarginWidth = 50.0F;
//        float stampInsertionContentSpacing = 10.0F;

//        pdfStamper.setCoarseDPI(coarseDPI);
//        pdfStamper.setFineDPI(fineDPI);
//        pdfStamper.setBottomThreshold(bottomThreshold);
//        pdfStamper.setStampInsertionMarginWidth(stampInsertionMarginWidth);
//        pdfStamper.setStampInsertionContentSpacing(stampInsertionContentSpacing);

    }

    public static String makeDefaultConfigString() {
        StringBuilder builder = new StringBuilder();

        PdfStamper template = new PdfStamper();

        builder.append("CoarseDPI: ");
        builder.append(template.getCoarseDPI());
        builder.append("\nFineDPI: ");
        builder.append(template.getFineDPI());
        builder.append("\nBottomThreshold: ");
        builder.append(template.getBottomThreshold());
        builder.append("\nStampInsertionMarginWidth: ");
        builder.append(template.getStampInsertionMarginWidth());
        builder.append("\nStampInsertionContentSpacing: ");
        builder.append(template.getStampInsertionContentSpacing());

        return builder.toString();
    }

}
