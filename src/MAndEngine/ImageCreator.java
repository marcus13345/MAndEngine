package MAndEngine;


import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageCreator {
	public static BufferedImage creatImageColorWithNoise(int width, int height, Color c, int variationIterations) {
		BufferedImage image = (new BufferedImage(width, height, BufferedImage.TRANSLUCENT));
		Graphics graphics = image.getGraphics();
		final int variant = variationIterations;
		for (int i = 0; i < image.getWidth(null); i++) {
			for (int j = 0; j < image.getHeight(null); j++) {

				double r = c.getRed();
				double g = c.getGreen();
				double b = c.getBlue();

				for (int k = 0; k < variant; k++) {
					r *= 0.9d;
					g *= 0.9d;
					b *= 0.9d;

					r += (int) (Math.random() * 25);
					g += (int) (Math.random() * 25);
					b += (int) (Math.random() * 25);
				}

				Color color = new Color((int) r, (int) g, (int) b);
				graphics.setColor(color);
				graphics.fillRect(i, j, 1, 1);
			}
		}
		return image;
	}

	public static BufferedImage creatImageWithStripes(int width, int height, Color c) {
		return creatImageWithStripes(width, height, c, c.darker());
	}
	
	public static BufferedImage creatImageWithStripes(int width, int height, Color c, Color c2) {
		BufferedImage image = (new BufferedImage(width, height, BufferedImage.TRANSLUCENT));
		Graphics graphics = image.getGraphics();
		for (int i = 0; i < image.getWidth(null); i++) {
			for (int j = 0; j < image.getHeight(null); j++) {

				boolean b = Math.floor(((i + j) / 2d)) % 2 != 0;

				graphics.setColor(b ? c : c2);
				graphics.fillRect(i, j, 1, 1);
			}
		}
		return image;
	}

	public static BufferedImage getScaledImage(BufferedImage image, int width, int height) throws IOException {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = (double) width / imageWidth;
		double scaleY = (double) height / imageHeight;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage(width, height, image.getType()));
	}

	public static BufferedImage getScaledImage(BufferedImage image, double scale) throws IOException {
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();

		double scaleX = scale;
		double scaleY = scale;
		AffineTransform scaleTransform = AffineTransform.getScaleInstance(scaleX, scaleY);
		AffineTransformOp bilinearScaleOp = new AffineTransformOp(scaleTransform, AffineTransformOp.TYPE_BILINEAR);

		return bilinearScaleOp.filter(image, new BufferedImage((int) (imageWidth * scale), (int) (imageHeight * scale), image.getType()));
	}
	
	public static BufferedImage createImageCloud(int width, int height, Color color) {
		return createImageCloud(width, height,  0xFF000000 | (color.getRed() << 16) | (color.getGreen() << 8) | (color.getBlue() << 0));
	}
	
	public static BufferedImage createImageCloud(int width, int height, int color) {
		return createImageCloud(width, height, 0x000000FF&(color>>16), 0x000000FF&(color>>8), 0x000000FF&(color));
	}

	public static BufferedImage createImageCloud(int width, int height, int r, int g, int b) {
		return createImageCloud(width, height, r/255d, g/255d, b/255d);
	}

	public static BufferedImage createImageCloud(int width, int height, double Rx, double Gx, double Bx) {
		// two to the \/ power = datasize...
		double power = width > height ? width : height;
		power *= 2;
		power = (int) (log(2, power) + .5);
		//not my code
		final int DATA_SIZE = (int) Math.pow(2, power) + 1;
		final double SEED = 1000.0;
		double[][] data = new double[DATA_SIZE][DATA_SIZE];
		data[0][0] = data[0][DATA_SIZE - 1] = data[DATA_SIZE - 1][0] = data[DATA_SIZE - 1][DATA_SIZE - 1] = SEED;
		double h = 500.0;
		for (int sideLength = DATA_SIZE - 1; sideLength >= 2; sideLength /= 2, h /= 2.0) {
			int halfSide = sideLength / 2;
			for (int x = 0; x < DATA_SIZE - 1; x += sideLength) {
				for (int y = 0; y < DATA_SIZE - 1; y += sideLength) {
					double avg = data[x][y] +
					data[x + sideLength][y] +
					data[x][y + sideLength] +
					data[x + sideLength][y + sideLength];
					avg /= 4.0;
					data[x + halfSide][y + halfSide] = avg + (Math.random() * 2 * h) - h;
				}
			}
			for (int x = 0; x < DATA_SIZE - 1; x += halfSide) {
				for (int y = (x + halfSide) % sideLength; y < DATA_SIZE - 1; y += sideLength) {
					double avg = data[(x - halfSide + DATA_SIZE) % DATA_SIZE][y] +
					data[(x + halfSide) % DATA_SIZE][y] +
					data[x][(y + halfSide) % DATA_SIZE] +
					data[x][(y - halfSide + DATA_SIZE) % DATA_SIZE];
					avg /= 4.0;
					avg = avg + (Math.random() * 2 * h) - h;
					data[x][y] = avg;
					if (x == 0)
						data[DATA_SIZE - 1][y] = avg;
					if (y == 0)
						data[x][DATA_SIZE - 1] = avg;
				}
			}
		}
		//end wtf code
		BufferedImage img = new BufferedImage(DATA_SIZE, DATA_SIZE, BufferedImage.TYPE_4BYTE_ABGR);
		double min = SEED;
		double max = SEED;
		for (int i = 0; i < DATA_SIZE; i++) {
			for (int j = 0; j < DATA_SIZE; j++) {
				if (data[i][j] < min)
					min = data[i][j];
				if (data[i][j] > max)
					max = data[i][j];
			}
		}
		for (int i = 0; i < DATA_SIZE; i++) {
			for (int j = 0; j < DATA_SIZE; j++) {
				img.setRGB(i, j, (0xFF000000) | 
					((int) (((data[i][j] - min) / (max - min)) * 255) << 16) | 
					((int) (((data[i][j] - min) / (max - min)) * 255) << 8) | 
					((int) (((data[i][j] - min) / (max - min)) * 255) << 0));
			}
		}
		for (int i = 0; i < DATA_SIZE; i++) {
			for (int j = 0; j < DATA_SIZE; j++) {
				int r = (int) ((double) ((img.getRGB(i, j) >> 16) & 0x000000FF) * Rx);
				int g = (int) ((double) ((img.getRGB(i, j) >> 8) & 0x000000FF) * Gx);
				int b = (int) ((double) ((img.getRGB(i, j) >> 0) & 0x000000FF) * Bx);
				img.setRGB(i, j, 0xFF000000 | (r << 16) | (g << 8) | (b << 0));
			}
		}

		BufferedImage _return = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
		_return.getGraphics().drawImage(img, 0 - (img.getWidth() / 8), 0 - (img.getHeight() / 8), null);
		return _return;
	}

	private static double log(double b, double x) {
		return Math.log(x) / Math.log(b);
	}
	
	public static BufferedImage colorNoise(int r, int g, int b, double multMin,
			double multMax, int width, int height) {

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = image.getGraphics();

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				double k = Math.random() * (multMax - multMin) + multMin;
				graphics.setColor(new Color((int) (r * k), (int) (g * k),
						(int) (b * k)));
				graphics.fillRect(i, j, 1, 1);
			}
		}

		return image;

	}

	public static Image colorNoise(Color c, double d, double i, int width,
			int height) {
		return colorNoise(c.getRed(), c.getGreen(), c.getBlue(), d, i, width,
				height);
	}
}
