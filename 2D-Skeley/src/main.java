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
import java.io.File;
import java.io.FileOutputStream;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class main {
	//file path
	static String path = "C:\\Users\\mam1010\\Documents\\Skeleys";
	static String output = "C:\\\\Users\\\\mam1010\\\\Documents\\output\\";
	
	//Opens all files in directory
    static final File dir = new File(path);
    static final String[] EXTENSIONS = new String[]{
            "gif", "png", "bmp" , "jpg" // and other formats you need
    };
    
    // filter to identify images based on their extensions
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

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
    static int spc = 1;
    static int z_ = 1;
    static int x_ = getWidth(dir);
    static int y_ = getLength(dir);
    static int xs = 0;
    static int xf = x_;
    static int ys = 0;
    static int yf = y_;
	static int xmax = xf-xs;// maximum values for any
	static int ymax = yf-ys;
	static int zmax = z_;
	static int freq = x_ + y_;
	static short[] omega = new short[freq];
	static float[][] picdata = new float[xmax][ymax];// the data from the picture, where the output is stored
	static float[][] temp = new float[xmax][ymax];
	static float[][] skel = new float[xmax][ymax];// where the skeleton is stored
	static float[][] impulse; 
	final static int[][] nbrs = {{-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
	        {-1, 1}};
    //width of picture
	public static int getWidth(File dir){
		int width = 0;
		try {
			width = ImageIO.read(dir.listFiles(IMAGE_FILTER)[7]).getWidth();
		}catch(final IOException e) {

		}
		System.out.println(width);
		return width;
	}
	
	//length of pictures
	public static int getLength(File dir){
		int length = 0;
		try {
			length = ImageIO.read(dir.listFiles(IMAGE_FILTER)[7]).getHeight();
		}catch(final IOException e) {

		}
		System.out.println(length);
		return length;
	}
	//makes pictures binary
	public static float[][] initMap(File url, int thresh) {// function to load the map
		try {
			BufferedImage b = ImageIO.read((url));// reads the image, from old
			int width = xmax;
			int height = ymax;
			float[][] generic = new float[width][height];
			for (int x = xs; x <= xf - 1; x++) {
				for (int y = ys; y <= yf - 1; y++) {// loads the image
					if (thresh < (0xff & (b.getRGB(x, y)>>16))) {
						generic[x-xs][y-ys] = 1;
					} else {
						generic[x-xs][y-ys] = 0;
					}
				}

			}
			return generic;// returns the image, this is called at the top of the code
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	//makes impulse function
	public static void recurse(int size, int val, int mag) {
		for (int x = val; x < size ; x++) {
			for (int y = val; y < size; y++) {
				impulse[x][y] = (short) (mag*(val+1));
			}
		}
		if(size == (val+1)) {
			return;
		}
		recurse(size-1, val+1, mag);
	}
	//makes impulse
	public static void makeImpulse(int size, int mag) {
		impulse = new float[size][size];
		float midx = ((size-1)/2), midy = ((size-1)/2);
		float dist = size;
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				impulse[i][j] = (float) (dist - (float)(Math.sqrt((Math.pow(Math.abs(i-midx),2) + Math.pow(Math.abs(j-midy),2)))));
			}
		}
		//recurse(size, 0, mag);
	}
	public static void makeImpulse2(int size, int mag) {
		impulse = new float[size][size];
		for(int x=0; x<size; x++) {
			for(int y=0; y<size; y++) {
				impulse[x][y] = (short) (mag);
			}
		}
	}
	public static void fft(int imp, int conv, int thresh) {
		
	}
	//convolution
	public static void convolution(int imp, int conv, int thresh) {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				skel[x][y] = 0;
			}
		}
		//convolution
		for(int j=0; j<conv; j++) {
			for (int x = imp; x < (xmax-imp); x+=spc) {
				for (int y = imp; y < (ymax-imp); y+=spc) {
					//multiplies data
					for (int a = (-1*imp); a < (imp+1); a++) {
						for (int b = (-1*imp); b < (imp+1); b++) {
							temp[x+a][y+b] += (float) (picdata[x][y]*impulse[a+imp][b+imp]);
						}
					}
				}
			}
		}
		/*
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				temp[x][y] = (float) (temp[x][y] * picdata[x][y]);
			}
		}
		*/
		System.out.println("Finished Convolution " + conv);
		float count=0;
		for (int x = 1; x < xmax-1; x++) {
			for (int y = 1; y < ymax-1; y++) {
				int nn = numNeighbors(x, y);
				count = 0;
				
				count += Math.abs(temp[x][y+1] - temp[x][y-1]);
				count += Math.abs(temp[x+1][y+1] - temp[x-1][y-1]);
				count += Math.abs(temp[x+1][y] - temp[x-1][y]);
				count += Math.abs(temp[x+1][y-1] - temp[x-1][y+1]);
				count = count/4;
				/*
				int sum = temp[x][y+1] + temp[x][y-1] + temp[x+1][y+1] + temp[x-1][y-1] + temp[x+1][y] + temp[x-1][y] + temp[x+1][y-1] + temp[x-1][y+1];
				count = Math.abs(temp[x][y] - (sum/8));
				*/
				if(x==52 && y==31)
					System.out.println("Here:" + count);
				if(x==52 && y==32)
					System.out.println("Here:" + count);
				if(x==53 && y==32)
					System.out.println("Here:" + count);
				if(count>thresh) {
					if(x==53 && y==32)
						System.out.println("Here:" + count);
					//System.out.println(x + "," + y + ":" + count);
					if(numTransitions(x, y)>1 && (nn>=2 && nn<=6)) {
						skel[x][y] = 1;
						//if(x==51 && y==26)
							//System.out.println("Here");
					}
					else {
						skel[x][y] = 0;
					}
				}
				else {
					
					if(temp[x][y]>0) {
						skel[x][y] = 1;
					}
					else {
						skel[x][y] = 0;
						//skel[x][y] = (short) (temp[x][y] + 0);
					}
					
					skel[x][y] = (short) (temp[x][y] + 0);
				}
				
				if(temp[x][y]>=temp[x-1][y-1] && temp[x][y]>=temp[x-1][y] && temp[x][y]>=temp[x-1][y+1] && temp[x][y]>=temp[x][y-1] && temp[x][y]>=temp[x][y+1] && temp[x][y]>=temp[x+1][y-1] && temp[x][y]>=temp[x+1][y] && temp[x][y]>=temp[x+1][y+1]) {
					skel[x][y] = temp[x][y];
				}
				if(temp[x][y]<=temp[x-1][y-1] && temp[x][y]<=temp[x-1][y] && temp[x][y]<=temp[x-1][y+1] && temp[x][y]<=temp[x][y-1] && temp[x][y]<=temp[x][y+1] && temp[x][y]<=temp[x+1][y-1] && temp[x][y]<=temp[x+1][y] && temp[x][y]<=temp[x+1][y+1]) {
					skel[x][y] = temp[x][y];
				}
				/*
				if(temp[x][y]>0) {
					if(numTransitions(x, y)>1 && (nn>=2 && nn<=6)) {
						skel[x][y] = 1;
					}
				}
				*/
			}
		}
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				skel[x][y] = (float) (skel[x][y] * picdata[x][y]);
			}
		}
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				if(skel[x][y]>0) {
					skel[x][y] = 1;
					picdata[x][y] = 1;					
				}
				else {
					picdata[x][y] = 0;
				}
			}
		}
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				temp[x][y] = 0;
			}
		}

	}
	static int numTransitions(int x, int y) {
    	int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (picdata[x + nbrs[i][1]][y + nbrs[i][0]] == 0) {
                if (picdata[x + nbrs[i + 1][1]][y + nbrs[i + 1][0]] > 0)
                    count++;
            }
        return count;
    }
	public static void finisher() {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				if(skel[x][y]>765) {
					skel[x][y] = 765;
					
				}
				if(skel[x][y]>0 && skel[x][y]<255) {
					skel[x][y] = 255;
				}
			}
		}
	}
	
	public static void excelinator() {
		/*
		for(int x=1; x<x_-1; x++) {
    		for(int y=1; y<y_-1; y++) {
    			picdata[x][y] = skel[x][y];
    		}
    	}
    	*/
		//Blank workbook
        @SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Path Data");
          
        //This data needs to be written (Object[])
        int arr[] = {0, 0, 0, 0, 0, 0, 0, 0};
        int a = 2, size = 8;
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"X", "Y"});
        	for(int x=1; x<x_-1; x++) {
        		for(int y=1; y<y_-1; y++) {
        			if(numNeighbors( x, y)<=1 && picdata[x][y]==1) {
        				//System.out.println(x + "," + y + ":Cond. 1");
        				skel[x][y] = 765;
        				data.put(String.valueOf(a), new Object[] {x, y});
    					a++;
        			}
        			else if(numTransitions(x, y)==1 && numNeighbors( x, y)>=2 && numNeighbors( x, y)<=3 && picdata[x][y]==1) {
        				//System.out.println(x + "," + y + ":Cond. 2");
        				skel[x][y] = 765;
    					data.put(String.valueOf(a), new Object[] {x, y});
    					a++;
    				}
        			else if(numTransitions(x, y)>2 && picdata[x][y]==1) {
        				//System.out.println(x + "," + y + ":Cond. 3");
        				skel[x][y] = 765;
    					data.put(String.valueOf(a), new Object[] {x, y});
    					a++;
    				}
        		}
        	}
        //Iterate over data and write to sheet
        Set<String> keyset = data.keySet();
        int rownum = 0;
        for (String key : keyset)
        {
            XSSFRow row = sheet.createRow(rownum++);
            Object [] objArr = data.get(key);
            int cellnum = 0;
            for (Object obj : objArr)
            {
               Cell cell = row.createCell(cellnum++);
               if(obj instanceof String)
                    cell.setCellValue((String)obj);
                else if(obj instanceof Integer)
                    cell.setCellValue((Integer)obj);
            }
        }
        try
        {
            //Write the workbook in file system
            FileOutputStream out = new FileOutputStream(new File("spinal.xlsx"));
            workbook.write(out);
            out.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
		
	}
        
	static int numNeighbors(int r, int c) {
		int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (picdata[r + nbrs[i][1]][c + nbrs[i][0]] > 0)
                count++;
        return count;
    }
	//euclidian distance
	public static void Eucl() {
		int Nfinished = 1, j=1;
		int min=1000;
		while(Nfinished==1) {
			//for(int = )
		}
		return;
		
	}
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
	//main func
	public static void main(String[] args){
		int V = 4;  // Number of vertices in graph 
        int E = 5;  // Number of edges in graph 
        Graph graph = new Graph(V, E);
		int imp = 1;
		int mag = 1;
		int i = 0, thresh1 = 260, thresh2 = 280;
		System.out.println("Thresholding Images");
		File f = new File("C:\\Users\\mam1010\\Documents\\Skeleys\\spinodal_GMU_Math.jpg");
		for(int l=0; l<255; l++) {
		picdata = initMap(f, l);
		//skel = picdata;
		//Makes impulse function
		System.out.println("Making Impulse");
		makeImpulse(((2*imp)+1), mag);
		//run convolution
		
		convolution(imp, 1, 1);
		
		imp = 3;
		//mag = 1;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 50);
		
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 35);
		
		imp = 1;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 5);
		/*
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 40);
		
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 10);
		
		imp = 1;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 1);
		*/
		/*
		imp = 1;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 5);
		
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 7);		
		
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 7);
		
		imp = 15;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 500);
		
		imp = 9;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 130);
		
		imp = 3;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 7);
		
		imp = 1;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 3);
		
		imp = 2;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 6);
		
		imp = 15;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 300);
		
		imp = 14;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 260);
		imp = 10;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 80);
		
		imp = 10;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 80);
		
		imp = 10;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 80);
		
		imp = 5;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 10);
		
		imp = 10;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 80);
		
		imp = 20;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 500);
		
		imp = 5;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 10);
		
		imp = 10;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 80);
		
		imp = 7;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 50);
		
		imp = 4;
		makeImpulse(((2*imp)+1), mag);
		convolution(imp, 1, 8);*/
		//excelinator();
		finisher();
		//Writes Pictures
		writer(l);
		}
		System.out.println("Finished");
	}
}
