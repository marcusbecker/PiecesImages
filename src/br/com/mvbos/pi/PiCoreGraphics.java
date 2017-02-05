/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.mvbos.pi;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *
 * @author Marcus Becker
 */
public class PiCoreGraphics extends PiCore {

    public void exportImage(File file) {
        try {
            if (images != null) {
                if (images.length == 1) {
                    File dest = new File(file.getParent(), "split");
                    if (!dest.exists()) {
                        dest.mkdir();
                    }

                    split(images[0], file, dest);

                } else {
                    merge(images, file.getName(), file.getParent());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(PiWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void merge(ImageIcon[] images, String baseName, String destination) throws IOException {
        int w = 0, h = 0;
        final String fName = baseName;
        final String ext = getExtension(fName);
        final String name = removeExtension(fName) + "_all." + ext;

        for (ImageIcon i : images) {
            w += i.getIconWidth();
            h += i.getIconHeight();
        }

        w = w / pieces.x;
        h = h / pieces.y;

        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buf.createGraphics();
        drawMultipleImages(g, images);

        ImageIO.write(buf, ext, new File(destination, name));
        g.dispose();

    }

    public void deepMerge(File parent, File outputFolder, File[] dirs, MergeCount count) {
        for (int i = 0; i < dirs.length; i++) {
            File f = dirs[i];
            File[] subdirs = getDirFromDir(f);
            ImageIcon[] arr = getImagesFromDir(f);

            count.folders += subdirs.length;

            if (arr != null && arr.length > 0) {
                count.totalImages += arr.length;
                try {
                    merge(arr, parent.getName() + "_" + f.getName() + ".jpg", outputFolder.getAbsolutePath());
                    count.images++;
                } catch (IOException ex) {
                    count.errors++;
                    Logger.getLogger(PiWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            deepMerge(f, outputFolder, subdirs, count);
        }
    }

    private void split(ImageIcon ico, File file, File destiny) throws IOException {
        //w = w / pieces.x;
        //h = h / pieces.y;
        String name = file.getName();
        final int w = Math.round(ico.getIconWidth() / (float) pieces.x);
        final int h = Math.round(ico.getIconHeight() / (float) pieces.y);

        final String fName = removeExtension(name);
        final String ext = getExtension(name);

        BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < pieces.x; i++) {
            for (int j = 0; j < pieces.y; j++) {
                int dx = w * i;
                int dy = h * j;
                Graphics2D g = buf.createGraphics();
                g.drawImage(ico.getImage(), 0, 0, w, h, dx, dy, w + dx, h + dy, null);

                String n;
//cbOrientation.getSelectedIndex()
                if (getOrientation() == 0) {
                    n = String.format("%s_%d%d.%s", fName, i, j, ext);
                } else {
                    n = String.format("%s_%d%d.%s", fName, j, i, ext);
                }

                ImageIO.write(buf, ext, new File(destiny, n));
                g.dispose();
            }
        }
    }

    public void drawSingleImage(Graphics2D g, ImageIcon ico) {
        int w = 1;
        int h = 1;

        w = ico.getIconWidth();
        h = ico.getIconHeight();

        g.drawImage(ico.getImage(), 0, 0, null);

        g.setColor(Color.RED);
        w = Math.round(w / (float) pieces.x);
        h = Math.round(h / (float) pieces.y);

        for (int i = 0; i < pieces.x; i++) {
            for (int j = 0; j < pieces.y; j++) {
                g.drawRect(i * w, j * h, w, h);
            }
        }
    }

    public void drawMultipleImages(final Graphics2D g, final ImageIcon[] images) {
        if (images == null || images.length == 0) {
            return;
        }

        int ctrl = 0;
        final int w = images[0].getIconWidth();
        final int h = images[0].getIconHeight();

        //g.setColor(Color.RED);
        //Font font = g.getFont();
        //g.setFont(new Font(font.getName(), font.getStyle(), 15));
        for (int i = 0; i < pieces.x; i++) {
            for (int j = 0; j < pieces.y; j++) {
                ImageIcon ico = images[ctrl];

                //info(ico.getDescription());
                int ii = i, jj = j;
//cbOrientation.getSelectedIndex()
                switch (getOrientation()) {
                    case 0:
                        break;
                    case 1:
                        ii = j;
                        jj = i;
                        break;
                    case 2:
                        ii = pieces.x - i - 1;
                        jj = pieces.y - j - 1;
                        break;
                    case 3:
                        ii = pieces.x - j - 1;
                        jj = pieces.y - i - 1;
                        break;
                }

                g.drawImage(ico.getImage(), ii * w, jj * h, null);
                //g.drawString(String.valueOf(ctrl), ii * w + 10, jj * h + 10);

                ctrl++;
                if (ctrl == images.length) {
                    ctrl = 0;
                }
            }
        }
    }

}
