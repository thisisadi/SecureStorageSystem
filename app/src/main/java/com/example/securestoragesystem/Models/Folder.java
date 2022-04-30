package com.example.securestoragesystem.Models;

import android.graphics.Color;

import java.util.Random;

public class Folder {
    String FolderName, CreationDate;
    int colorTop, colorBottom, colorBackground, numFiles, identifier;

    public Folder(){}

    public Folder(String folderName, String creationDate) {
        FolderName = folderName;
        CreationDate = creationDate;
        numFiles = 0;
        Random rnd = new Random();
        colorBottom = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
        colorTop = ColorDarker(colorBottom,0.7f);
        colorBackground = ColorLighter(colorBottom,0.8f);
    }

    public String getFolderName() {
        return FolderName;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setFolderName(String folderName) {
        FolderName = folderName;
    }

    public void setCreationDate(String creationDate) {
        CreationDate = creationDate;
    }

    public int ColorDarker (int color, float factor) {
        int a = Color.alpha( color );
        int r = Color.red( color );
        int g = Color.green( color );
        int b = Color.blue( color );

        return Color.argb( a,
                Math.max( (int)(r * factor), 0 ),
                Math.max( (int)(g * factor), 0 ),
                Math.max( (int)(b * factor), 0 ) );
    }
    public int ColorLighter(int color, float factor) {
        int red = (int) ((Color.red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((Color.green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((Color.blue(color) * (1 - factor) / 255 + factor) * 255);
        return Color.argb(Color.alpha(color), red, green, blue);
    }

    public int getColorTop() {
        return colorTop;
    }

    public int getColorBottom() {
        return colorBottom;
    }

    public int getColorBackground() {
        return colorBackground;
    }

    public int getNumFiles() {
        return numFiles;
    }

    public void setNumFiles(int numFiles) {
        this.numFiles = numFiles;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }
}
