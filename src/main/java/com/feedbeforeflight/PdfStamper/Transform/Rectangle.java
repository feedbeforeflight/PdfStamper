package com.feedbeforeflight.PdfStamper.Transform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import static java.lang.Math.abs;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
public class Rectangle {
    float top;
    float bottom;
    float left;
    float right;

    public Rectangle(PDRectangle rectangle, int rotation) {
        if (rotation == 0) {
            top = rectangle.getUpperRightY();
            bottom = rectangle.getLowerLeftY();
            left = rectangle.getLowerLeftX();
            right = rectangle.getUpperRightX();
        } else if (rotation == 90) {
            top = rectangle.getUpperRightX();
            bottom = rectangle.getLowerLeftX();
            left = rectangle.getLowerLeftY();
            right = rectangle.getUpperRightY();
        } else if (rotation == 180) {
            top = rectangle.getUpperRightY();
            bottom = rectangle.getLowerLeftY();
            left = rectangle.getLowerLeftX();
            right = rectangle.getUpperRightX();
        } else if (rotation == 270) {
            top = rectangle.getUpperRightX();
            bottom = rectangle.getLowerLeftX();
            left = rectangle.getLowerLeftY();
            right = rectangle.getUpperRightY();
        }
    }

    public float getWidth() {
        return abs(right - left);
    }

    public float getHeight() {
        return abs(top - bottom);
    }
}
