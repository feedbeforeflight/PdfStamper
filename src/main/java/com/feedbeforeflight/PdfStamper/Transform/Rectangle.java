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

    public Rectangle(PDRectangle rectangle) {
        top = rectangle.getUpperRightY();
        bottom = rectangle.getLowerLeftY();
        left = rectangle.getLowerLeftX();
        right = rectangle.getUpperRightX();
    }

    public float getWidth() {
        return abs(right - left);
    }

    public float getHeight() {
        return abs(top - bottom);
    }
}
