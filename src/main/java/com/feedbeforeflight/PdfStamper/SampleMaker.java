package com.feedbeforeflight.PdfStamper;

import com.feedbeforeflight.PdfStamper.Transform.Rectangle;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SampleMaker {

    public static void makeDocument90(String documentFileName) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDRectangle badLandscapeA4 = new PDRectangle();
            badLandscapeA4.setLowerLeftX(0);
            badLandscapeA4.setLowerLeftY(0);
            badLandscapeA4.setUpperRightX(PDRectangle.A4.getUpperRightY());
            badLandscapeA4.setUpperRightY(PDRectangle.A4.getUpperRightX());

            PDPage page = new PDPage(badLandscapeA4);
            page.setRotation(90);
            document.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                Matrix matrix = new Matrix(0, 1, -1, 0, page.getBBox().getUpperRightX(), 0);
                contentStream.beginText();
                contentStream.setTextMatrix(matrix);
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 400);
                contentStream.showText("This is a bad pdf document landscaped with BBox and rotated 90 degrees");
                contentStream.endText();
            }

            document.save(new File(documentFileName));

        }
    }

    public static void makeDocument180(String documentFileName) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            page.setRotation(180);
            document.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                Matrix matrix = new Matrix(-1, 0, 0, -1, page.getBBox().getUpperRightX(), page.getBBox().getUpperRightY());
                contentStream.beginText();
                contentStream.setTextMatrix(matrix);
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 400);
                contentStream.showText("This is a bad pdf document landscaped with BBox and rotated 180 degrees");
                contentStream.endText();
            }

            document.save(new File(documentFileName));

        }
    }

    public static void makeDocument270(String documentFileName) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDRectangle badLandscapeA4 = new PDRectangle();
            badLandscapeA4.setLowerLeftX(0);
            badLandscapeA4.setLowerLeftY(0);
            badLandscapeA4.setUpperRightX(PDRectangle.A4.getUpperRightY());
            badLandscapeA4.setUpperRightY(PDRectangle.A4.getUpperRightX());

            PDPage page = new PDPage(badLandscapeA4);
            page.setRotation(270);
            document.addPage(page);

            PDFont font = PDType1Font.HELVETICA_BOLD;


            Matrix originalMatrix = document.getPage(0).getMatrix();
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
                    PDPageContentStream.AppendMode.APPEND, true)) {
                Matrix matrix = new Matrix(0, -1, 1, 0, 0, page.getBBox().getUpperRightY());
                contentStream.beginText();
                contentStream.setTextMatrix(matrix);
                contentStream.setFont(font, 12);
                contentStream.newLineAtOffset(50, 400);
                contentStream.showText("This is a bad pdf document landscaped with BBox and rotated 270 degrees");
                contentStream.endText();
            }

            document.save(new File(documentFileName));

        }
    }

}
