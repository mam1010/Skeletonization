import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Thinner {
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
    LinkedList neigh = new LinkedList();
    static int[] voxel = new int[27]; 
    static int n = 0;
    static int background = 0;
    //voxel = {};
    static int startx, starty, maximizer;
    static int mag = 2;
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
	static short[][][] picdata = new short[zmax][xmax][ymax];// the data from the picture, where the output is stored
	static short[][][] temp = new short[zmax][xmax][ymax];
	static short[][][] skel = new short[zmax][xmax][ymax];// where the skeleton is stored
	static short[][][] impulse; 
	final static int[][] nbrs = {{-1, -1, -1}, {-1, 0, -1}, {-1, 1, -1}, {-1, -1, 0}, {-1, 0, 0}, {-1, 1, 0}, {-1, -1, 1}, {-1, 0, 1}, {-1, 1, 1}, 
			{0, -1, -1}, {0, 0, -1}, {0, 1, -1}, {0, -1, 0}, {0, 0, 0}, {0, 1, 0}, {0, -1, 1}, {0, 0, 1}, {0, 1, 1}, 
			{1, -1, -1}, {1, 0, -1}, {1, 1, -1}, {1, -1, 0}, {1, 0, 0}, {1, 1, 0}, {1, -1, 1}, {1, 0, 1}, {1, 1, 1}};
	 
	 
	    static ArrayList toWhite = new ArrayList();
	    static char[][] grid;
    //width of picture
	public static int getWidth(File dir){
		int width = 0;
		try {
			width = ImageIO.read(dir.listFiles(IMAGE_FILTER)[3]).getWidth();
		}catch(final IOException e) {

		}
		return width;
	}
	
	//length of pictures
	public static int getLength(File dir){
		int length = 0;
		try {
			length = ImageIO.read(dir.listFiles(IMAGE_FILTER)[3]).getHeight();
		}catch(final IOException e) {

		}
		return length;
	}
	//makes pictures binary
	public static short[][] initMap(File url) {// function to load the map
		try {
			BufferedImage b = ImageIO.read((url));// reads the image, from old
			int width = xmax;
			int height = ymax;
			short[][] generic = new short[width][height];
			for (int x = xs; x <= xf - 1; x++) {
				for (int y = ys; y <= yf - 1; y++) {// loads the image
					if (40 < (0xff & (b.getRGB(x, y)>>16))) {
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
	
	public static void thinner() {
		
	}
	public static void connectedness(int x, int y, int z) {
		voxel[13] = 1;
		recurse(x, y, z);
	}
	public static void recurse(int x, int y, int z) {
		int curr = -1;
		for(int i=0; i<26; i++) {
			if(voxel[i]==1) {
				curr = i;
			}
		}
		if(curr == -1) {
			return;
		}
		int state = curr % 9;
		int level = curr / 9;
		int index = (level * 9) + state;
		int index1, index2, index3, index4, index5, index6;
		//int val = index % 27;
		switch(index)
		{
		   case 0 :
			  index1 = index + 1; index2 = index - 1; index3 = index + 9;
			  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
				  voxel[index1] = 1;
			  }
			  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
				  voxel[index2] = 1;
			  }
			  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
				  voxel[index3] = 1;
			  }
		      break; 
		   case 1 :
			  index1 = index + 1; index2 = index - 1; index3 = index + 3; index4 = index + 9;
			  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
				  voxel[index1] = 1;
			  }
			  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
				  voxel[index2] = 1;
			  }
			  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
				  voxel[index3] = 1;
			  }
			  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
				  voxel[index4] = 1;
			  }
			  break; 
		   case 2 :
			  index1 = index + 3; index2 = index - 1; index3 = index + 9;
			  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
				  voxel[index1] = 1;
			  }
			  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
				  voxel[index2] = 1;
			  }
			  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
				  voxel[index3] = 1;
			  }
			  break; 
		   case 3 :
				  index1 = index + 1; index2 = index - 3; index3 = index + 3; index4 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break;  
		   case 4 :
				  index1 = index + 3; index2 = index - 3; index3 = index + 1; index4 = index - 1; index5 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break;    
		   case 5 :
				  index1 = index - 3; index2 = index - 1; index3 = index + 3; index4 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break;  
		   case 6 :
				  index1 = index + 1; index2 = index - 1; index3 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break; 
		   case 7 :
				  index1 = index + 1; index2 = index - 1; index3 = index - 3; index4 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break; 
		   case 8 :
				  index1 = index - 3; index2 = index - 1; index3 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break;
		   case 9 :
				  index1 = index + 1; index2 = index + 3; index3 = index + 9; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }				  
				  break;
		   case 10 :
				  index1 = index - 1; index2 = index + 3; index3 = index + 1; index4 = index - 9; index5 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break;
		   case 11 :
				  index1 = index - 1; index2 = index + 3; index3 = index + 9; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }				  
				  break;
		   case 12 :
				  index1 = index + 1; index2 = index - 3; index3 = index + 3; index4 = index - 9; index5 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break;  
		   case 13 :
				  index1 = index - 1; index2 = index + 1; index3 = index + 3; index4 = index - 3; index5 = index + 9; index6 = index -9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  if(picdata[z+nbrs[index6][0]][x+nbrs[index6][1]][y+nbrs[index6][2]]==1) {
					  voxel[index6] = 1;
				  }	
				  break;  
		   case 14 :
				  index1 = index - 1; index2 = index - 3; index3 = index + 3; index4 = index - 9; index5 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break;
		   case 15 :
				  index1 = index + 1; index2 = index - 3; index3 = index + 9; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }				  
				  break;  
		   case 16 :
				  index1 = index - 1; index2 = index - 3; index3 = index + 1; index4 = index - 9; index5 = index + 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break; 
		   case 17 :
				  index1 = index - 1; index2 = index - 3; index3 = index + 9; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }				  
				  break;
		   case 18 :
				  index1 = index + 1; index2 = index + 3; index3 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break;
		   case 19 :
				  index1 = index + 1; index2 = index - 1; index3 = index + 3; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break; 
		   case 20 :
				  index1 = index + 3; index2 = index - 1; index3 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break;
		   case 21 :
				  index1 = index + 1; index2 = index + 3; index3 = index - 3; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break;  
		   case 22 :
				  index1 = index + 1; index2 = index - 1; index3 = index + 3; index4 = index - 3; index5 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }		
				  if(picdata[z+nbrs[index5][0]][x+nbrs[index5][1]][y+nbrs[index5][2]]==1) {
					  voxel[index5] = 1;
				  }	
				  background = 1;
				  break;    
		   case 23 :
				  index1 = index + 3; index2 = index - 1; index3 = index - 3; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break; 
		   case 24 :
				  index1 = index + 1; index2 = index - 3; index3 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break; 
		   case 25 :
				  index1 = index + 1; index2 = index - 1; index3 = index - 3; index4 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  if(picdata[z+nbrs[index4][0]][x+nbrs[index4][1]][y+nbrs[index4][2]]==1) {
					  voxel[index4] = 1;
				  }
				  break;  
		   case 26 :
				  index1 = index - 3; index2 = index - 1; index3 = index - 9;
				  if(picdata[z+nbrs[index1][0]][x+nbrs[index1][1]][y+nbrs[index1][2]]==1) {
					  voxel[index1] = 1;
				  }
				  if(picdata[z+nbrs[index2][0]][x+nbrs[index2][1]][y+nbrs[index2][2]]==1) {
					  voxel[index2] = 1;
				  }
				  if(picdata[z+nbrs[index3][0]][x+nbrs[index3][1]][y+nbrs[index3][2]]==1) {
					  voxel[index3] = 1;
				  }
				  break;
		   default : 
		      return;
		}
		voxel[curr] = 2;
		n++;
	}
	public static void finisher() {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				for (int z = 0; z < zmax; z++) {
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
	public static void excelinator() {
			
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
            FileOutputStream out = new FileOutputStream(new File("howtodoinjava_demo1.xlsx"));
            workbook.write(out);
            out.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
	}
        
	//main func
	public static void main(String[] args){
		int imp = 7;
		int i = 0, thresh1 = 260, thresh2 = 280;
		System.out.println("Thresholding Images");
		//File f = new File("C:\\Users\\mam1010\\Documents\\Skeleys\\spinodal_GMU_Math.jpg");
		for (final File f : dir.listFiles(IMAGE_FILTER)) {
			picdata[i] = initMap(f);
			i++;
		}
		//Makes impulse function
		thinner();
		//relMax(1);
		//relMax(2);
		//relMax(3);
		//relMax(4);
		//relMax(5);
		//relMax(6);
		//relMax(7);
		//relMax(8);
		//relMax(9);
		excelinator();
		finisher();
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
	}
}

