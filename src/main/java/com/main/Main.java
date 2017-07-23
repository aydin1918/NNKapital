package com.main;

import org.opencv.core.Core;

import com.recognize.Opencv;
import com.train.CNN;


public class Main {

	   public static void main(String[] args)
	   {
		   System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		   CNN objCNN = new CNN();
		   Opencv objOpen = new Opencv();
		   System.out.println("Hello, Aydin!");
		   try {
			    objCNN.TrainNN();
			 //  objOpen.SetPath("\\train\\0");
			   
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   }
}
