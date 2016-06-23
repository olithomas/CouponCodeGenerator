package com.codefarm.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.math.BigInteger;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import com.codefarm.engine.Algorithms;

public class RandomTester extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private MyPanel panel;

	/**
	 * Create the frame.
	 */
	public RandomTester() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		panel = new MyPanel();
		add(panel);
	}

	class MyPanel extends JPanel {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		BigInteger tenCharMaxPerms;
		BigInteger tenCharPrime;
		BigInteger tenCharXorOperand;
		BigInteger seed;
		BigInteger two;

	    public MyPanel() {
	    	seed = BigInteger.ONE;
	    	two = new BigInteger("2");
	    	tenCharMaxPerms = new BigInteger("420707233300201");
			tenCharPrime = new BigInteger("420707233300183");
			tenCharXorOperand = new BigInteger(tenCharMaxPerms.bitLength(),0,new Random());
	    }

	    public Dimension getPreferredSize() {
	        return new Dimension(512,612);
	    }

	    public void paintComponent(Graphics g) {
	        super.paintComponent(g);       

	        Color white = new Color(255,255,255);
	        Color black = new Color(0,0,0);
	        g.setColor(white);
	        g.fillRect(0, 0, 512, 512);
	        g.setColor(black);
	        
	        for (int y = 0; y < 512; y++) {
	        	for (int x = 0; x < 512; x++) {
	        		//if (Algorithms.permute(tenCharPrime, seed, tenCharXorOperand).mod(two).intValue() == 1) {
	        		if (Algorithms.permute(tenCharPrime, seed, tenCharXorOperand).compareTo(tenCharMaxPerms.divide(two)) > 0) {
	        			g.fillRect(x, y, 1, 1);
	        		}
	        		seed = seed.add(BigInteger.ONE);
	        	}
	        }
	        g.drawString("Tested using: odds and evens (mod 2)", 5, 600);
	    }  
	}
}
