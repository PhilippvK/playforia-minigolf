package com.aapeli.client;

import java.awt.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public final class ImageManager {
    private final boolean isDebug;
    private Hashtable<String, Image> gameImages;
    private Hashtable<String, Image> sharedImages;

    public ImageManager(boolean isDebug) {
        this.isDebug = isDebug;
        this.gameImages = new Hashtable<>();
        this.sharedImages = new Hashtable<>();
    }

    public String defineGameImage(String fileName) {
        return this.defineGameImage(this.removeExtension(fileName), fileName);
    }

    public String defineGameImage(String name, String imageFileName) {
        if (this.isDebug) {
            System.out.println("ImageManager.defineGameImage(\"" + name + "\",\"" + imageFileName + "\")");
        }

        try {
            Image image = ImageIO.read(this.getClass().getResource("/picture/agolf/" + imageFileName));
            this.gameImages.put(name, image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return name;
    }

    public String defineSharedImage(String fileName) {
        return defineSharedImage(removeExtension(fileName), fileName);
    }

    public String defineSharedImage(String name, String imageFileName) {
        if (this.isDebug) {
            System.out.println("ImageManager.defineSharedImage(\"" + name + "\",\"" + imageFileName + "\")");
        }

        try {
            Image image = ImageIO.read(this.getClass().getResource("/picture/shared/" + imageFileName));
            this.sharedImages.put(name, image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return name;
    }

    public void unloadGameImage(String name) {
        this.gameImages.remove(name);
    }

    public double getImageLoadProgress() {
        return 1.0D;
    }

    public Image getGameImage(String name) {
        return this.gameImages.get(name);
    }

    public boolean isGameImageDefined(String name) {
        return this.gameImages.contains(name);
    }

    public Image getShared(String name) {
        String extensionlessName = this.removeExtension(name);
        return this.sharedImages.get(extensionlessName);
    }

    public int getWidth(Image image) {
        return image.getWidth(null);
    }

    public int getHeight(Image image) {
        return image.getHeight(null);
    }

    public int[] getPixels(Image image, int width, int height) {
        return this.getPixels(image, 0, 0, width, height);
    }

    public int[] getPixels(Image image, int x, int y, int width, int height) {
        int[] pixels = new int[width * height];
        PixelGrabber pixelGrabber = new PixelGrabber(image, x, y, width, height, pixels, 0, width);

        try {
            pixelGrabber.grabPixels();
        } catch (InterruptedException e) {
        }

        return pixels;
    }

    public Image createImage(int[] pixels, int width, int height) {
        return Toolkit.getDefaultToolkit().createImage(new MemoryImageSource(width, height, pixels, 0, width));
    }

    public Image[] separateImages(Image image, int length) {
        return this.separateImages(image, length, 1)[0];
    }

    public Image[][] separateImages(Image image, int length, int rows) {
        if (this.isDebug) {
            System.out.println("ImageManager.separateImages(...," + length + "," + rows + ")");
        }

        int width = this.getWidth(image);
        int height = this.getHeight(image);
        int imageWidth = width / length;
        int imageHeight = height / rows;
        if (this.isDebug && (width % length > 0 || height % rows > 0)) {
            System.out.println("ImageManager.separateImages(...,"
                    + length
                    + ","
                    + rows
                    + "): Warning! Source image can not be divided to "
                    + length
                    + "*"
                    + rows
                    + " blocks");
            Thread.dumpStack();
        }

        int[] allPixels = this.getPixels(image, width, height);
        Image[][] images = new Image[rows][length];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < length; ++col) {
                int[] pixels = new int[imageWidth * imageHeight];

                for (int y = 0; y < imageHeight; ++y) {
                    for (int x = 0; x < imageWidth; ++x) {
                        pixels[y * imageWidth + x] =
                                allPixels[row * width * imageHeight + y * width + col * imageWidth + x];
                    }
                }

                images[row][col] = this.createImage(pixels, imageWidth, imageHeight);
            }
        }

        return images;
    }

    public void destroy() {}

    private String removeExtension(String fileName) {
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
