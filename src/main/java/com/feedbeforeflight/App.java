package com.feedbeforeflight;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App
{
    public static void main( String[] args ) {

        int coarseDPI = 100;
        int fineDPI = 300;
        int bottomThreshold = 100; // на последней строке есть нечто, привязанное к нижнему краю (номер строки, исполнитель, etc)
        float stampInsertionMarginWidth = 50.0F;
        float stampInsertionContentSpacing = 10.0F;
        String sourceDocumentFileName = "d:\\Data\\pdf\\document3.pdf";
        String stampDocumentFileName = "d:\\Data\\pdf\\stamp2.pdf";
        String destinationDocumentFileName = "d:\\Data\\pdf\\result.pdf";

        File destinationFile = new File(destinationDocumentFileName);
        destinationFile.delete();

        PdfStamper pdfStamper = new PdfStamper();
        pdfStamper.setCoarseDPI(coarseDPI);
        pdfStamper.setFineDPI(fineDPI);
        pdfStamper.setBottomThreshold(bottomThreshold);
        pdfStamper.setStampInsertionMarginWidth(stampInsertionMarginWidth);
        pdfStamper.setStampInsertionContentSpacing(stampInsertionContentSpacing);
        try {
            pdfStamper.applyStamp(Files.newInputStream(Paths.get(sourceDocumentFileName)),
                    Files.newInputStream(Paths.get(stampDocumentFileName)),
                    Files.newOutputStream(Paths.get(destinationDocumentFileName)));
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }

}
