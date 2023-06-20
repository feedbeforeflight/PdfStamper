package com.feedbeforeflight.PdfStamper.Transform;

public class Calculator {

    public static Point stampInsertionPoint(PageRotation pageRotation,
                                            Rectangle pageRectangle,
                                            Rectangle stampRectangle,
                                            float sideMarginWidth,
                                            float bottomFreeSpace) {
        Point point = new Point();

        switch (pageRotation) {
            case ROTATION0:
                point.setX(sideMarginWidth);
                point.setY(pageRectangle.getHeight() * bottomFreeSpace - stampRectangle.getHeight());
                break;
            case ROTATION90:
                point.setX(pageRectangle.getWidth() * (1 - bottomFreeSpace) + stampRectangle.getHeight());
                point.setY(sideMarginWidth);
                break;
            case ROTATION180:
                point.setX(pageRectangle.getWidth() - sideMarginWidth);
                point.setY(pageRectangle.getHeight() * (1 - bottomFreeSpace));
                break;
            case ROTATION270:
                point.setX(pageRectangle.getWidth() * bottomFreeSpace - stampRectangle.getHeight());
                point.setY(pageRectangle.getHeight() - sideMarginWidth);
                break;
        }



        return point;
    }



}
