package com.feedbeforeflight;

import com.feedbeforeflight.PdfStamper.SampleMaker;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SampleMakeApp {
    public static void main( String[] args ) {

        String destinationFolder = args[0];

        try {
            SampleMaker.makeDocument90(Paths.get(destinationFolder, "BadSample90.pdf").toString());
            SampleMaker.makeDocument180(Paths.get(destinationFolder, "BadSample180.pdf").toString());
            SampleMaker.makeDocument270(Paths.get(destinationFolder, "BadSample270.pdf").toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

}
