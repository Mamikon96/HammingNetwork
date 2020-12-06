package core;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageUtils {

    public static int[] getImageSize(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        return new int[]{image.getHeight(), image.getWidth()};
    }

    public static int[] extractPixels(File file) throws IOException {
        BufferedImage image = ImageIO.read(file);
        int[] pixels = new int[image.getWidth() * image.getHeight()];
        int linesCount = image.getHeight();

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y));

                // Получаем каналы этого цвета
                int blue = color.getBlue();
                int red = color.getRed();
                int green = color.getGreen();

                // Применяем стандартный алгоритм для получения черно-белого изображения
                double grey = (double) (red * 0.299 + green * 0.587 + blue * 0.114) / 255;

                double t = 0.5;

//                pixels[x * linesCount + y] = grey == 0 ? -1 : 1;
                pixels[x * linesCount + y] = grey > t ? 1 : -1;
            }
        }
        return pixels;
    }
}
