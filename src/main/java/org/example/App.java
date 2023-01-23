package org.example;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDFontFactory;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.interactive.measurement.PDViewportDictionary;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import javax.swing.text.StyleConstants;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException {
        Path resultPath = Paths.get("d:\\Data\\pdf\\result.pdf");
        try {
            Files.deleteIfExists(resultPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Preved medved!");

        PDDocument sourceDocument = PDDocument.load(new File("d:\\Data\\pdf\\document.pdf"));
        PDDocument stampDocument = PDDocument.load(new File("d:\\Data\\pdf\\stamp.pdf"));

        System.out.println("Source document pages count " + sourceDocument.getPages().getCount());
        System.out.println("Stamp document pages count " + stampDocument.getPages().getCount());

        int sourceDocumentLastPageIndex = sourceDocument.getPages().getCount() - 1;
        PDPage sourceDocumentLastPage = sourceDocument.getPage(sourceDocumentLastPageIndex);

        PDRectangle rectangle = sourceDocumentLastPage.getBBox();

        PDFRenderer pdfRenderer = new PDFRenderer(sourceDocument);
        BufferedImage bufferedImage = pdfRenderer.renderImageWithDPI(sourceDocumentLastPageIndex, 100, ImageType.BINARY);
        ImageIO.write(bufferedImage, "jpg", new File("d:\\Data\\pdf\\last-page.jpg"));

        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();
        int bottom = 0;

        int referenceColour = bufferedImage.getRGB(0, height - 1);
        for (int i = height - 1; i >= 0; i--) {
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

        System.out.println();
        System.out.printf("Bottom of content is %d, total height is %d", bottom, height);
        System.out.println();
        System.out.println();

//        PDImageXObject stampImage = PDImageXObject.createFromFile("d:\\Data\\pdf\\pallas_cat.jpg", sourceDocument);

        PDFRenderer stampRenderer = new PDFRenderer(stampDocument);
        BufferedImage stampImage = stampRenderer.renderImageWithDPI(0, 300, ImageType.RGB);
        PDImageXObject stampXImage = JPEGFactory.createFromImage(sourceDocument, stampImage);

        float x = 10;
        float scale = stampXImage.getWidth() / (rectangle.getUpperRightX() - x * 2);
        float stampImageHeight = stampXImage.getHeight() / scale;
        float stampImageWidth = stampXImage.getWidth() / scale;

        float y = rectangle.getUpperRightY() * (height - bottom) / height - stampImageHeight - 20;
        System.out.printf("Placing picture at position %f %f", x, y);
        System.out.println();

        PDPageContentStream contentStream = new PDPageContentStream(sourceDocument, sourceDocumentLastPage, PDPageContentStream.AppendMode.APPEND, true);
        contentStream.drawImage(stampXImage, x, y, stampImageWidth, stampImageHeight);
        contentStream.close();

        sourceDocument.save("d:\\Data\\pdf\\result.pdf");

    }

}
