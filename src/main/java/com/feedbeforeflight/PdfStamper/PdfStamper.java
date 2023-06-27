package com.feedbeforeflight.PdfStamper;

import com.feedbeforeflight.PdfStamper.Transform.Point;
import com.feedbeforeflight.PdfStamper.Transform.Rectangle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.util.Matrix;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PdfStamper {

    @Getter @Setter
    private int coarseDPI = 100;
    @Getter @Setter
    private int fineDPI = 300;
    @Getter @Setter
    private int bottomThreshold = 100; // пропускаем колонтитул внизу (номер строки, исполнитель, etc)
    @Getter @Setter
    private float stampInsertionMarginWidth = 20.0F;
    @Getter @Setter
    private float stampInsertionContentSpacing = 10.0F;

    public PdfStamper() {}

    public void applyStamp(String sourceDocumentFileName, String stampDocumentFileName, String destinationDocumentFileName) throws IOException {
        if (sourceDocumentFileName == null || stampDocumentFileName == null || destinationDocumentFileName == null) {
            return;
        }

        PDDocument sourceDocument = PDDocument.load(new File(sourceDocumentFileName));
        PDDocument stampDocument = PDDocument.load(new File(stampDocumentFileName));

        doApplyStamp(sourceDocument, stampDocument).save(destinationDocumentFileName);

        stampDocument.close();
        sourceDocument.close();
    }

    public void applyStamp(InputStream sourceDocumentStream, InputStream stampDocumentStream, OutputStream destinationDocumentStream) throws IOException {
        if (sourceDocumentStream == null || stampDocumentStream == null || destinationDocumentStream == null) {
            return;
        }

        PDDocument sourceDocument = PDDocument.load(sourceDocumentStream);
        PDDocument stampDocument = PDDocument.load(stampDocumentStream);

        doApplyStamp(sourceDocument, stampDocument).save(destinationDocumentStream);

        stampDocument.close();
        sourceDocument.close();
    }

    private PDDocument doApplyStamp(PDDocument sourceDocument, PDDocument stampDocument) throws IOException {
        if (sourceDocument == null || stampDocument == null) {
            return null;
        }

        int sourceDocumentLastPageIndex = sourceDocument.getPages().getCount() - 1;
        PDPage sourceDocumentLastPage = sourceDocument.getPage(sourceDocumentLastPageIndex);

        Rectangle borders = new Rectangle(sourceDocumentLastPage.getBBox(), sourceDocumentLastPage.getRotation());

        PDFRenderer pdfRenderer = new PDFRenderer(sourceDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(sourceDocumentLastPageIndex, coarseDPI, ImageType.BINARY);

        int renderedLastPageHeight = bufferedImage.getHeight();
        int renderedLastPageContentBottom = bottomMargin(bufferedImage, bottomThreshold);

        PDImageXObject stampXImage = makeStampImageFromPdf(sourceDocument, stampDocument, sourceDocumentLastPage);

        float scale = stampXImage.getWidth() / (borders.getRight() - stampInsertionMarginWidth * 2);
        float stampImageScaledWidth = stampXImage.getWidth() / scale;
        float stampImageScaledHeight = stampXImage.getHeight() / scale;

        Point stampInsertionPoint = new Point();
        stampInsertionPoint.setX(stampInsertionMarginWidth);

        stampInsertionPoint.setY(borders.getTop()
                * (renderedLastPageHeight - renderedLastPageContentBottom) / renderedLastPageHeight
                - stampImageScaledHeight - stampInsertionContentSpacing);

        if (stampInsertionPoint.getY() < 0) {
            PDPage blankPage = new PDPage(sourceDocumentLastPage.getBBox());
            blankPage.setRotation(sourceDocumentLastPage.getRotation());
            sourceDocument.addPage(blankPage);

            borders = new Rectangle(blankPage.getBBox(), blankPage.getRotation());
            stampInsertionPoint.setY(borders.getTop()
                    - stampImageScaledHeight - stampInsertionContentSpacing);
            sourceDocumentLastPage = blankPage;
        }

        try (PDPageContentStream contentStream = new PDPageContentStream(sourceDocument, sourceDocumentLastPage,
                PDPageContentStream.AppendMode.APPEND, true)) {

            System.out.println(sourceDocumentLastPage.getRotation());

            if (sourceDocumentLastPage.getRotation() > 0) {
                contentStream.transform(getMatrix(sourceDocumentLastPage.getRotation(), borders));
            }

            contentStream.drawImage(stampXImage, stampInsertionPoint.getX(), stampInsertionPoint.getY(),
                    stampImageScaledWidth, stampImageScaledHeight);
        }

        return sourceDocument;
    }

    private Matrix getMatrix(int rotation, Rectangle borders) {
        switch (rotation) {
            case 90 -> {
                return new Matrix(0, 1, -1, 0, borders.getHeight(), 0);
            }
            case 180 -> {
                return new Matrix(-1, 0, 0, -1, borders.getWidth(), borders.getHeight());
            }
            case 270 -> {
                return new Matrix(0, -1, 1, 0, 0, borders.getWidth());
            }
        }
        return new Matrix(1, 0, 0, 1, 0, 0);
    }

    private PDImageXObject makeStampImageFromPdf(PDDocument sourceDocument, PDDocument stampDocument, PDPage sourceDocumentLastPage) throws IOException {
        PDFRenderer stampRenderer = new PDFRenderer(stampDocument);
        BufferedImage stampImage = stampRenderer.renderImageWithDPI(0, fineDPI, ImageType.RGB);

        BufferedImageMarginCoordinates stampMargins = marginCoordinates(stampImage);
        BufferedImage trimmedStamp = stampImage.getSubimage(stampMargins.left, stampMargins.top,
                stampMargins.right - stampMargins.left, stampMargins.bottom - stampMargins.top);

        return JPEGFactory.createFromImage(sourceDocument, trimmedStamp);
    }

    private static int bottomMargin(BufferedImage bufferedImage, int bottomThreshold) {
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int bottom = 0;

        int referenceColour = bufferedImage.getRGB(0, height - 1);
        for (int i = height - 1 - bottomThreshold; i >= 0; i--) {
            for (int j = 0; j < width; j++){
                int currentRGB = bufferedImage.getRGB(j, i);
                if (currentRGB != referenceColour) {
                    bottom = i;
                    break;
                }
            }
            if (bottom > 0 ) {
                break;
            }
        }

        return bottom;
    }

    private static BufferedImageMarginCoordinates marginCoordinates(BufferedImage bufferedImage) {
        BufferedImageMarginCoordinates margins = new BufferedImageMarginCoordinates();
        margins.bottom = bottomMargin(bufferedImage, 0);

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int referenceColour = bufferedImage.getRGB(0, height - 1);

        for (int i = 0; i < margins.bottom; i++) {
            for (int j = 0; j < width; j++){
                int currentRGB = bufferedImage.getRGB(j, i);
                if (currentRGB != referenceColour) {
                    margins.top = i;
                    break;
                }
            }
            if (margins.top > 0 ) {
                break;
            }
        }
        for (int j = 0; j < width; j++){
            for (int i = margins.top; i <= margins.bottom; i++) {
                int currentRGB = bufferedImage.getRGB(j, i);
                if (currentRGB != referenceColour) {
                    margins.left = j;
                    break;
                }
            }
            if (margins.left > 0 ) {
                break;
            }
        }
        for (int j = width - 1; j > margins.left; j--){
            for (int i = margins.top; i <= margins.bottom; i++) {
                int currentRGB = bufferedImage.getRGB(j, i);
                if (currentRGB != referenceColour) {
                    margins.right = j;
                    break;
                }
            }
            if (margins.right > 0 ) {
                break;
            }
        }

        return margins;
    }

    @Getter @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class BufferedImageMarginCoordinates {
        private int left;
        private int right;
        private int top;
        private int bottom;
    }

}
