/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.pi;

import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 *
 * @author Marcus Becker
 */
public class PiCore {

    private int orientation;
    protected ImageIcon[] images;
    protected final Point pieces = new Point(1, 1);

    public File[] getDirFromDir(File f) {
        File[] dirs = f.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });

        return dirs;
    }

    public int[] extractNumbers(String str) {
        if (str == null || str.trim().isEmpty()) {
            return new int[0];
        }

        short count = 0;
        boolean startAdd = false;
        String[] strArray = new String[str.length()];
        for (char c : str.toCharArray()) {
            if (Character.isDigit(c)) {
                startAdd = true;

                if (strArray[count] == null) {
                    strArray[count] = String.valueOf(c);
                } else {
                    strArray[count] += c;
                }

            } else if (startAdd) {
                startAdd = false;
                count++;
            }
        }

        if (startAdd) {
            count++;
        }

        int[] temp = new int[count];
        for (int i = 0; i < temp.length; i++) {
            temp[i] = Integer.parseInt(strArray[i]);
        }

        return temp;
    }

    public ImageIcon[] getImagesFromDir(File f) {
        File[] files = f.listFiles(new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        });

        ImageIcon[] arr = new ImageIcon[files.length];

        for (int i = 0; i < arr.length; i++) {
            ImageIcon temp = new ImageIcon(files[i].getAbsolutePath());
            try {
                temp.getImage();
                arr[i] = temp;
            } catch (Exception e) {
                info("no image " + files[i].getName());
            }
        }

        sortByNumericName(arr);

        return arr;
    }

    public void sortByNumericName(ImageIcon[] images) {
        Arrays.sort(images, new Comparator<ImageIcon>() {
            //private final String regex = "[^\\d]";

            @Override
            public int compare(ImageIcon a, ImageIcon b) {
                if (a == null || b == null) {
                    return 0;
                }
                int[] temp = extractNumbers(removeExtension(a.getDescription()));
                final int nA = temp[temp.length - 1];

                temp = extractNumbers(removeExtension(b.getDescription()));
                final int nB = temp[temp.length - 1];

                return Integer.compare(nA, nB);
            }
        });
    }

    public String removeExtension(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public String getExtension(String name) {
        return name.substring(name.lastIndexOf(".") + 1).toLowerCase();
    }

    public ImageIcon[] getImages() {
        return images;
    }

    public void setImages(ImageIcon[] images) {
        this.images = images;
    }

    public void setX(int x) {
        pieces.x = x;
    }

    public int getX() {
        return pieces.y;
    }

    public void setY(int y) {
        pieces.y = y;
    }

    public int getY() {
        return pieces.y;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    protected void info(String str) {
        Logger.getLogger(getClass().getName()).log(Level.INFO, str);
    }

}
