import java.awt.Color;
import java.awt.List;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Zhang_Thinner {
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
    Map<String, Object[]> data1 = new TreeMap<String, Object[]>();
    static int startx, starty, maximizer, nodeNum, row = 0;
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
	static float[][] picdata = new float[xmax][ymax];// the data from the picture, where the output is stored
	static float[][] temp = new float[xmax][ymax];
	static float[][] skel = new float[xmax][ymax];// where the skeleton is stored
	static short[][] impulse; 
	final static int[][] nbrs = {{0, -1}, {1, -1}, {1, 0}, {1, 1}, {0, 1},
	        {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}};
	 
	    final static int[][][] nbrGroups = {{{0, 2, 4}, {2, 4, 6}}, {{0, 2, 6},
	        {0, 4, 6}}};
	 
	    static ArrayList toWhite = new ArrayList();
	    static char[][] grid;
    //width of picture
	public static int getWidth(File dir){
		int width = 0;
		try {
			width = ImageIO.read(dir.listFiles(IMAGE_FILTER)[7]).getWidth();
		}catch(final IOException e) {

		}
		System.out.println("width:" + width);
		return width;
	}
	
	//length of pictures
	public static int getLength(File dir){
		int length = 0;
		try {
			length = ImageIO.read(dir.listFiles(IMAGE_FILTER)[7]).getHeight();
		}catch(final IOException e) {

		}
		System.out.println("length:" + length);
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
	public static void recurse(int size, int val) {
		for (int x = val; x < size ; x++) {
			for (int y = val; y < size; y++) {
				impulse[x][y] = (short) (mag*(val+1));
			}
		}
		if(size == (val+1)) {
			//impulse[val][val] = 100; 
			return;
		}
		recurse(size-1, val+1);
	}
	//makes impulse
	public static void makeImpulse(int size) {
		impulse = new short[size][size];
		recurse(size, 0);
	}
	public static void makeImpulse2(int size) {
		impulse = new short[size][size];
		for(int x=0; x<size; x++) {
			for(int y=0; y<size; y++) {
				impulse[x][y] = (short) (mag);
			}
		}
	}
	//convolution
	public static void convolution(int imp, int conv, int thresh) {
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				skel[x][y] = 0;
			}
		}
		//convolution
		for (int x = imp; x < (xmax-imp); x+=spc) {
			for (int y = imp; y < (ymax-imp); y+=spc) {
				//multiplies data
				for (int a = (-1*imp); a < (imp+1); a++) {
					for (int b = (-1*imp); b < (imp+1); b++) {
						temp[x+a][y+b] += (short) (picdata[x][y]*impulse[a+imp][b+imp]);
					}
				}
			}
		}
		
		for (int x = 0; x < xmax; x++) {
			for (int y = 0; y < ymax; y++) {
				temp[x][y] = (short) (temp[x][y] * picdata[x][y]);
			}
		}
		
		System.out.println("Finished Convolution " + conv);
		int count=0;
		for (int x = 1; x < xmax-1; x++) {
			for (int y = 1; y < ymax-1; y++) {

				count = 0;
				count += Math.abs(temp[x][y+1] - temp[x][y-1]);
				count += Math.abs(temp[x+1][y+1] - temp[x-1][y-1]);
				count += Math.abs(temp[x+1][y] - temp[x-1][y]);
				count += Math.abs(temp[x+1][y-1] - temp[x-1][y+1]);
				count = count/4;
				if(count>thresh) {
					skel[x][y] = 0;
				}
				else {
					skel[x][y] = (short) (temp[x][y] + 0);
				}
				if(temp[x][y]>=temp[x-1][y-1] && temp[x][y]>=temp[x-1][y] && temp[x][y]>=temp[x-1][y+1] && temp[x][y]>=temp[x][y-1] && temp[x][y]>=temp[x][y+1] && temp[x][y]>=temp[x+1][y-1] && temp[x][y]>=temp[x+1][y] && temp[x][y]>=temp[x+1][y+1]) {
					picdata[x][y] = temp[x][y];
				}
				if(temp[x][y]<=temp[x-1][y-1] && temp[x][y]<=temp[x-1][y] && temp[x][y]<=temp[x-1][y+1] && temp[x][y]<=temp[x][y-1] && temp[x][y]<=temp[x][y+1] && temp[x][y]<=temp[x+1][y-1] && temp[x][y]<=temp[x+1][y] && temp[x][y]<=temp[x+1][y+1]) {
					picdata[x][y] = temp[x][y];
				}
				if(skel[x][y]>0) {
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
	public static void thinner() {
		boolean firstStep = false;
        boolean hasChanged;
 
        do {
            hasChanged = false;
            firstStep = !firstStep;
 
            for (int x = xs+1; x<xf-1; x++) {
            	for(int y = ys+1; y<yf-1; y++) {
 
                    if (picdata[x][y] != 1)
                        continue;
                    
                    int nn = numNeighbors(x, y);
                    
                    if (nn < 2 || nn > 6)
                        continue;
                    if (numTransitions(x, y) != 1)
                        continue;
                    if (!atLeastOneIsWhite(x, y, firstStep ? 0 : 1))
                        continue;
 
                    skel[x][y] = 1;
                    hasChanged = true;
                }
            }
 
            for (int x = xs; x<xf; x++) {
            	for(int y=ys; y<yf; y++) {
            		if(skel[x][y]==1)
            			picdata[x][y] = 0;
            		skel[x][y] = 0;
            	}
            }
            
            //toWhite.clear();
 
        } while (firstStep || hasChanged);
        for (int x = xs; x<xf; x++) {
        	for(int y=ys; y<yf; y++) {
        		skel[x][y] = picdata[x][y];
        	}
        }
        //printResult();
	}
	static int numNeighbors(int r, int c) {
		int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (picdata[r + nbrs[i][1]][c + nbrs[i][0]] == 1)
                count++;
        return count;
    }
 
    static int numTransitions(int x, int y) {
    	int count = 0;
        for (int i = 0; i < nbrs.length - 1; i++)
            if (picdata[x + nbrs[i][1]][y + nbrs[i][0]] == 0) {
                if (picdata[x + nbrs[i + 1][1]][y + nbrs[i + 1][0]] == 1)
                    count++;
            }
        return count;
    }
 
    static boolean atLeastOneIsWhite(int r, int c, int step) {
        int count = 0;
        int[][] group = nbrGroups[step];
        for (int i = 0; i < 2; i++)
            for (int j = 0; j < group[i].length; j++) {
                int[] nbr = nbrs[group[i][j]];
                if (picdata[r + nbr[1]][c + nbr[0]] == 0) {
                    count++;
                    break;
                }
            }
        return count > 1;
    }
 
    static void printResult() {
        for (char[] row : grid)
            System.out.println(row);
    }
	public static void relMax(int d) {
		/*
		skel = picdata;
		int count = 0, adj = 1, res=0;
		int arr[] = {0,0,0,0,0,0,0,0};
		do {
			res = 0;
			for(int x= xs+1; x<xf-1 ; x++) {
				for(int y= ys+1; y<yf-1 ; y++) {
					arr[0] = picdata[x-1][y-1];
					arr[1] = picdata[x][y-1];
					arr[2] = picdata[x+1][y-1];
					arr[3] = picdata[x+1][y];
					arr[4] = picdata[x+1][y+1];
					arr[5] = picdata[x][y+1];
					arr[6] = picdata[x-1][y+1];
					arr[7] = picdata[x-1][y];
					if(picdata[x][y]==d) {
						adj = 1;
						count = 0;
						int j=0, k=0;
						while(j<8 && arr[j]>=1) {
							j++;
							j = j%8;
						}
						while(j<8 && arr[j]==0) {
							j++;
							j = j%8;
						}
						while(k<8 && arr[j]>=1) {
							count++;
							j++;
							j = j%8;
							k++;
						}
						while(k<8 && arr[j]==0) {
							j++;
							j = j%8;
							k++;
						}
						if(k<8) {
							adj++;
						}
						
						for(int j=-1; j<2; j+=2) {
							for(int k=-1; k<2; k+=1) {
								if(picdata[j+x][k+y]==d) {
									continue;
								}
								count++;
							}
						}
					
						if(picdata[x][y-1]<=1 || skel[x][y-1]==1) {
							count++;
						}
					
						if(picdata[x][y+1]<=1 || skel[x][y+1]==1) {
							count++;
						}
						if(count>1 && adj==1) {
							picdata[x][y] = 0;
							res = 1;
						}
					}
				}
			}
			System.out.println("Here");
		}while(res==1);
		*/
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
        				System.out.println(x + "," + y + ":Cond. 1");
        				skel[x][y] = 765;
        				data.put(String.valueOf(a), new Object[] {x, y});
        				nodeNum++;
    					a++;
        			}
        			else if(numTransitions(x, y)==1 && numNeighbors( x, y)>=2 && numNeighbors( x, y)<=3 && picdata[x][y]==1) {
        				System.out.println(x + "," + y + ":Cond. 2");
        				skel[x][y] = 765;
    					data.put(String.valueOf(a), new Object[] {x, y});
    					nodeNum++;
    					a++;
    				}
        			else if(numTransitions(x, y)>2 && picdata[x][y]==1) {
        				System.out.println(x + "," + y + ":Cond. 3");
        				skel[x][y] = 765;
    					data.put(String.valueOf(a), new Object[] {x, y});
    					nodeNum++;
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
            FileOutputStream out = new FileOutputStream(new File("nodeList.xlsx"));
            workbook.write(out);
            out.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
	}
	public static void excelinator1() {
		for(int i=0; i<x_; i++) {
			for(int j=0; j<y_; j++) {
				temp[i][j] = 0;
			}
		}
		//Blank workbook
        @SuppressWarnings("resource")
		XSSFWorkbook workbook = new XSSFWorkbook();
         
        //Create a blank sheet
        XSSFSheet sheet = workbook.createSheet("Path Data");
          
        //This data needs to be written (Object[])
        int a = 2;
        Map<String, Object[]> data = new TreeMap<String, Object[]>();
        data.put("1", new Object[] {"X", "Y"});
        
        //Reads node list
        //Read sheet inside the workbook by its name

        XSSFWorkbook guru99Workbook = null;
		Sheet guru99Sheet = (Sheet) guru99Workbook.getSheet("nodeList.xlsx");

        //Find number of rows in excel file

        int rowCount = ((XSSFSheet) guru99Sheet).getLastRowNum()-((XSSFSheet) guru99Sheet).getFirstRowNum();

        //Create a loop over all the rows of excel file to read it

        for (int i = 0; i < rowCount+1; i++) {

            Row row = ((XSSFSheet) guru99Sheet).getRow(i);

            //Create a loop to print cell values in a row

            for (int j = 0; j < row.getLastCellNum(); j++) {

                //Print Excel data in console

                System.out.print(row.getCell(j).getStringCellValue()+"|| ");

            }

            //System.out.println();
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
            FileOutputStream out = new FileOutputStream(new File("Zhang_adjacency.xlsx"));
            workbook.write(out);
            out.close();
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
       
	}
	String findAdj(int x, int y) {
		//int num = numTransitions(x, y);
		String temper = "";
	    for (int i = 0; i < nbrs.length - 1; i++) {
	        if (picdata[x + nbrs[i][1]][y + nbrs[i][0]] == 0) {
	            if (picdata[x + nbrs[i + 1][1]][y + nbrs[i + 1][0]] == 1) {
	            		temp[x][y] = 1;
	                    temper = finder(x + nbrs[i + 1][1], (y + nbrs[i + 1][0]));
	                    data1.put(String.valueOf(row), new Object[] {temper});
	            }
	        }
	    }
		return null;
	}
	String finder(int x, int y) {
		temp[x][y] = 1;
		String tem = "";
		if(skel[x][y]==765) {
			tem = x + "," + y;
			return tem;
		}
	    for (int i = 0; i < nbrs.length - 1; i++) {
	        if (picdata[x + nbrs[i + 1][1]][y + nbrs[i + 1][0]] == 1 && temp[x][y] == 0) {
	        	tem = finder(x + nbrs[i + 1][1], (y + nbrs[i + 1][0]));
	        }
	    }
		return null;
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
			String s1 = output + "SpinodalRange\\Thinner\\trial" + num + "thinner.bmp";
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
		int imp = 7;
		int i = 0, thresh1 = 260, thresh2 = 280;
		System.out.println("Thresholding Images");
		File f = new File("C:\\Users\\mam1010\\Documents\\Skeleys\\spinodal_GMU_Math.jpg");
		for(int l=0; l<255; l++) {
		picdata = initMap(f, l);
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
		//excelinator();
		finisher(); 
		//Writes Pictures
		writer(l);
		}
		System.out.println("Finished");
	}
}
