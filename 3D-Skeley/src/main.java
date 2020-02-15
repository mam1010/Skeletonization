import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.awt.Color;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.Math;
import java.util.Arrays;
import javax.imageio.ImageIO;
import java.util.concurrent.TimeUnit;
//package org.trifort.rootbeer.runtime;

public class main {
	//file path
	static String path = "C:\\Users\\mam1010\\Documents\\Skeleys\\New_Data_Grey_Scale";
	static String output = "C:\\Users\\mam1010\\Documents\\output\\SpecialSet\\Conv\\";
	// File representing the folder that you select using a FileChooser
    static final File dir = new File(path);
	// array of supported extensions (use a List if you prefer)
    static final String[] EXTENSIONS = new String[]{
        "gif", "png", "bmp" , "jpg", "tif" // and other formats you need
    };
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };
    //determine dimensions of data
    static double mag = 0.25;
    static int z_ = dir.listFiles(IMAGE_FILTER).length;
    static int x_ = getWidth(dir);
    static int y_ = getLength(dir);
    static int xs = 0;
    static int xf = x_;
    static int ys = 0;
    static int yf = y_;
	static int xmax = xf-xs;// maximum values for any
	static int ymax = yf-ys;
	static int zmax = z_;
	static float[][][] picdata = new float[zmax][xmax][ymax];// the data from the picture, where the output is stored int ok = pause();
	static float[][][] temp = new float[zmax][xmax][ymax];
	static int [][][] skel = new int[zmax][xmax][ymax];// where the skeleton is
	// stored
	static double[][][] impulse; 

	public static void main(String[] args) {// this rotates it 90 deg, watch
		int i = 0;
		for (final File f : dir.listFiles(IMAGE_FILTER)) {
			picdata[i] = initMap(f);
			i++;
		}
		System.out.println("Thresholded");
		//Write intermediate picture
		/*
		for (int z = 0; z < z_; z++) {
			BufferedImage image1 = new BufferedImage(xf-xs, yf-ys,
					BufferedImage.TYPE_INT_RGB);// creates image
			for (int b = 0; b < yf-ys; b++) {
				for (int j = 0; j < xf-xs; j++) {
					try {
						
						image1.setRGB(j, b, (int) picdata[z][j][b]);
					} catch (IndexOutOfBoundsException e) {

					}
				}
			}
			String s1 = output + "Inter\\150\\" + z + ".png";
			File ouptut = new File(s1);
			try {
				ImageIO.write(image1, "png", ouptut);// writes the slices
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/ 
		//Makes impulse function
		
		
		//convolves impulse with picdata
		int imp = 1;
		System.out.println("Making Impulse");
		makeImpulse(((2*imp)+1));
		System.out.println("Convolving Image");
		convolution(imp, 1, 0.5);
		imp = 10;
		System.out.println("Making Impulse");
		makeImpulse(((2*imp)+1));
		System.out.println("Convolving Image");
		convolution(imp, 1, 50);
		
		finisher();
		 
		System.out.println("Writing Pictures");
		//Writes Pictures
		for (int z = 0; z < zmax; z++) {
			BufferedImage image = new BufferedImage(xf-xs, yf-ys,
					BufferedImage.TYPE_INT_RGB);// creates image
			for (int b = 0; b < ymax; b++) {
				for (int j = 0; j < xmax; j++) {
					try {
						image.setRGB(j, b, skel[z][j][b]);
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
			String s1 = output + z + ".png";
			File ouptut = new File(s1);
			try {
				ImageIO.write(image, "png", ouptut);// writes the slices
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("Finished");
	}
	
	public interface Kernel {
		  void gpuMethod();
	}
	
	public static float[][] initMap(File url) {// function to load the map
		try {
			BufferedImage b = ImageIO.read((url));// reads the image, from old
			int width = xmax;
			int height = ymax;
			float[][] generic = new float[width][height];
			for (int x = xs; x <= xf - 1; x++) {// -1 because it is the
															// last valid
															// Coordinate
															// because 0 is used
				for (int y = ys; y <= yf - 1; y++) {// loads the image
					
					if (150 < (0xff & (b.getRGB(x, y)>>16))) {
						generic[x-xs][y-ys] = 0;
					} else {
						generic[x-xs][y-ys] = 255;
					}
				}

			}
			return generic;// returns the image, this is called at the top of
							// the code
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	public static void convolution(int imp, int conv, double thresh) {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for (int z = 0; z < zmax; z++) {
					skel[z][x][y] = 0;
				}
			}
		}
		long startTime = System.currentTimeMillis();
		for (int x = imp; x < (xmax-imp); x++) {
			for (int y = imp; y < (ymax-imp); y++) {
				for (int z = imp; z < (zmax-imp); z++) {
					//multiplies data
					for (int a = (-1*imp); a < (imp+1); a++) {
						for (int b = (-1*imp); b < (imp+1); b++) {
							for (int c = (-1*imp); c < (imp+1); c++) {
								temp[z+c][x+a][y+b] += (float) (picdata[z][x][y]*impulse[c+imp][a+imp][b+imp]);
								//System.out.println("x:" + x + "/y:" + y + "/z:" + z + "/val:" + temp[z][x][y]);
							}
						}
					}
				}
			}
		}
		long endTime = System.currentTimeMillis();

		long timeElapsed = endTime - startTime;
		System.out.println("Finished Convolution " + conv);
		System.out.println("Time: " + timeElapsed);
		int count=0;
		//threshold
		for (int x = 1; x < xmax-1; x++) {
			for (int y = 1; y < ymax-1; y++) {
				for (int z = 1; z < zmax-1; z++) {
					//System.out.println("x:" + x + "/y:" + y + "/z:" + z + "/val:" + temp[z][x][y]);
					count = 0;
					count += Math.abs(temp[z][x][y+1] - temp[z][x][y-1]);
					count += Math.abs(temp[z][x+1][y+1] - temp[z][x-1][y-1]);
					count += Math.abs(temp[z][x+1][y] - temp[z][x-1][y]);
					count += Math.abs(temp[z][x+1][y-1] - temp[z][x-1][y+1]);
					
					count += Math.abs(temp[z+1][x][y+1] - temp[z-1][x][y-1]);
					count += Math.abs(temp[z+1][x+1][y+1] - temp[z-1][x-1][y-1]);
					count += Math.abs(temp[z+1][x+1][y] - temp[z-1][x-1][y]);
					count += Math.abs(temp[z+1][x+1][y-1] - temp[z-1][x-1][y+1]);
					
					count += Math.abs(temp[z+1][x][y-1] - temp[z-1][x][y+1]);
					count += Math.abs(temp[z+1][x-1][y-1] - temp[z-1][x+1][y+1]);
					count += Math.abs(temp[z+1][x-1][y] - temp[z-1][x+1][y]);
					count += Math.abs(temp[z+1][x-1][y+1] - temp[z-1][x+1][y-1]);
					
					count += Math.abs(temp[z+1][x][y] - temp[z-1][x][y]);
					
					count = count/26;
					//System.out.println("Count: " + count);
					if(count>thresh) {
						skel[z][x][y] = 0;
					}
					else {
						skel[z][x][y] = (short) (temp[z][x][y] + 0);
					}
				}
			}
		}
		//Exclude extra data
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for (int z = 0; z < zmax; z++) {
					skel[z][x][y] = (int) (skel[z][x][y] * picdata[z][x][y]);
				}
			}
		}
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for (int z = 0; z < zmax; z++) {
					if(skel[z][x][y]>0) {
						skel[z][x][y] = 1;
						picdata[z][x][y] = 1;
					}
					else {
						picdata[z][x][y] = 0;
					}
				}
			}
		}
		//resets temp
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for (int z = 0; z < zmax; z++) {
					temp[z][x][y] = 0;
				}
			}
		}
		
	}
	/*
	public static void writer(int num) {
		for (int z = 0; z < zmax; z++) {
			BufferedImage image = new BufferedImage(xf-xs, yf-ys,
					BufferedImage.TYPE_INT_RGB);// creates image
			for (int b = 0; b < ymax; b++) {
				for (int j = 0; j < xmax; j++) {
					try {
						Color myWhite = null;
						if(skel[j][b]>=255) {
							skel[j][b] = (short) (skel[j][b] - 255);
							//skel[j][b] = (short) (skel[j][b] - 255);
							if(skel[j][b]>=255) {
								skel[j][b] = (short) (skel[j][b] - 255);
								if(skel[j][b]>=255) {
									myWhite = new Color(255, 255, 255);
								}
								else {
									myWhite = new Color(255, (short) skel[j][b], 255);
								}
							}
							else {
								myWhite = new Color(255, 0, (short) skel[j][b]);
							}
						}
						else {
							myWhite = new Color((short) skel[j][b], 0, 0); // Color white
						}
						int rgb = myWhite.getRGB();
						//System.out.println(rgb);
						image.setRGB(j, b, rgb);
					} catch (IndexOutOfBoundsException e) {
					}
				}
			}
			String s1 = output + "SpinodalRange\\trial" + num + "conv" + 4 + ".bmp";
			File output = new File(s1);
			try {
				ImageIO.write(image, "bmp", output);// writes the slices
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	*/
	public static void finisher() {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for(int z = 0; z < zmax; z++) {
					if(skel[z][x][y]>765) {
						skel[z][x][y] = 765;
					
					}
					if(skel[z][x][y]>0 && skel[z][x][y]<255) {
						skel[z][x][y] = 255;
					}
				}
			}
		}
	}
	public static void recurse(int size, int val) {
		for (int x = val; x < size ; x++) {
			for (int y = val; y < size; y++) {
				for (int z = val; z < size; z++) {
					impulse[x][y][z] = (double) (mag*(val+1));
				}
			}
		}
		if(size == (val+1))
			return;
		recurse(size-1, val+1);
	}
	public static void makeImpulse(int size) {
		impulse = new double[size][size][size];
		recurse(size, 0);
	}
	public static int getWidth(File dir){
		int width = 0;
		try {
			width = ImageIO.read(dir.listFiles(IMAGE_FILTER)[0]).getWidth();
		}catch(final IOException e) {

		}
		System.out.println(width);
		return width;
	}
	
	public static int getLength(File dir){
		int length = 0;
		try {
			length = ImageIO.read(dir.listFiles(IMAGE_FILTER)[0]).getHeight();
		}catch(final IOException e) {

		}
		System.out.println(length);
		return length;
	}
}
