package com.recognize;


import java.awt.Dimension;
import java.awt.Image;

import javax.swing.JFrame;

class ImageFrame extends JFrame {
	
	public void displayImageInFrame(Image img,String title) {
		ImagePanel panel = new ImagePanel(img);
		add(panel);
		
		setVisible(true);
		//setSize(panel.getSize());
		setSize(new Dimension(612,295));
		setPreferredSize(panel.getSize());
		//setPreferredSize(new Dimension(400,800));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle(title);
		
	}
}