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

        PDRectangle rectangle = sourceDocumentLastPage.getBBox();

        PDFRenderer pdfRenderer = new PDFRenderer(sourceDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(sourceDocumentLastPageIndex, coarseDPI, ImageType.BINARY);

        int renderedLastPageHeight = bufferedImage.getHeight();
        int renderedLastPageContentBottom = bottomMargin(bufferedImage, bottomThreshold);

        PDFRenderer stampRenderer = new PDFRenderer(stampDocument);
        BufferedImage stampImage = stampRenderer.renderImageWithDPI(0, fineDPI, ImageType.RGB);

        BufferedImageMarginCoordinates stampMargins = marginCoordinates(stampImage);
        BufferedImage trimmedStamp = stampImage.getSubimage(stampMargins.left, stampMargins.top,
                stampMargins.right - stampMargins.left, stampMargins.bottom - stampMargins.top);

        PDImageXObject stampXImage = JPEGFactory.createFromImage(sourceDocument, trimmedStamp);

        float scale = stampXImage.getWidth() / (rectangle.getUpperRightX() - stampInsertionMarginWidth * 2);
        float stampImageScaledWidth = stampXImage.getWidth() / scale;
        float stampImageScaledHeight = stampXImage.getHeight() / scale;

        float stampInsertionYCoordinate = rectangle.getUpperRightY()
                * (renderedLastPageHeight - renderedLastPageContentBottom) / renderedLastPageHeight
                - stampImageScaledHeight - stampInsertionContentSpacing;

        if ((stampInsertionYCoordinate - stampImageScaledHeight) < 0) {
            PDPage blankPage = new PDPage(rectangle);
            sourceDocument.addPage(blankPage);
            rectangle = blankPage.getBBox();
            stampInsertionYCoordinate = rectangle.getUpperRightY()
                    - stampImageScaledHeight - stampInsertionContentSpacing;
            sourceDocumentLastPage = blankPage;
        }

        PDPageContentStream contentStream = new PDPageContentStream(sourceDocument, sourceDocumentLastPage,
                PDPageContentStream.AppendMode.APPEND, true);
        contentStream.drawImage(stampXImage, stampInsertionMarginWidth, stampInsertionYCoordinate,
                stampImageScaledWidth, stampImageScaledHeight);
        contentStream.close();

        return sourceDocument;
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
