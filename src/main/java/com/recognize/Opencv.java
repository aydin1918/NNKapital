package com.recognize;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

public class Opencv {

	
	    public void SetPath(String path)
	    {
	    	  File folder = new File(System.getProperty("user.dir")+path);
			  File[] listOfFiles = folder.listFiles();
			  BufferedImage dest = null;
			  BufferedImage input = null;
			  
			  for (int i = 0; i < listOfFiles.length; i++) {
			        if (listOfFiles[i].isFile()) 
			        {
			        	//input = ImageIO.read(System.getProperty("user.dir")+path +listOfFiles[i]);
			        	ChangeSizeImage(28,28,""+listOfFiles[i]);
			        	//Convert2Black(""+listOfFiles[i]);
			        }
			  }
	    }
	    
	    public void Convert2Black(String path)
		 {
	    	System.out.println(path);
				//read the RGB image
				 Mat rgbImage = Highgui.imread(path);
			    
				//mat gray image holder
				 Mat imageGray = new Mat();
				 Mat imageCny = new Mat();
				 
				 Imgproc.cvtColor(rgbImage, imageGray, Imgproc.COLOR_RGB2GRAY);
				 
				 Imgproc.Canny(imageGray, imageCny, 15, 5, 3 , true);
				 
				// Imgproc.GaussianBlur(imageGray, imageCny, new Size(3,3), 0);
				 
			    //  Imgproc.adaptiveThreshold(imageCny, imageCny, 10, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 1);

		         // Imgproc.threshold(imageCny, imageCny, 10, 3, Imgproc.THRESH_BINARY);
				 
				 Core.bitwise_not(imageCny, imageCny);
				 
		  		// ImageUtils.displayImage(ImageUtils.toBufferedImage(imageCny), "imageCny Image - " );

				 Highgui.imwrite(path , imageCny);	
				 
		 }
	    
	   private void ChangeSizeImage(int WIDTH, int HEIGHT, String filename)
	   {
		   BufferedImage image = null;
			 try {
			        ImageIcon ii = new ImageIcon(filename);//path to image
			        image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
			        Graphics2D g2d = (Graphics2D) image.createGraphics();
			        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING,RenderingHints.VALUE_RENDER_QUALITY));
			        g2d.drawImage(ii.getImage(), 0, 0, WIDTH, HEIGHT, null);
			        ImageIO.write(image, "png", new File(filename));
			    } catch (Exception e) {
			        e.printStackTrace();
			        //return null;
			    }
			 finally 
			 {
				 
			 }
	   }
	    
}
