/*
 * Copyright (c) 2007, Romain Guy
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   * Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above
 *     copyright notice, this list of conditions and the following
 *     disclaimer in the documentation and/or other materials provided
 *     with the distribution.
 *   * Neither the name of the TimingFramework project nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package sneer.bricks.snapps.games.go.impl.gui.game.painters;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Romain Guy
 */
 public class PiecePainter{
	
    private int _width;
	private int _height;
	
	Color bottomOvalHighlightOutterCollor;
	Color centerGlow;
	Color overallColor;
	Color edges;
	protected void paintPiece(BufferedImage image, final boolean black) {
		paintPiece(image,black, false);
	}
	
	protected void paintPiece(BufferedImage image, final boolean black, final boolean stroke) {
        _width = image.getWidth();
        _height = image.getHeight();
        Graphics2D g2 = (Graphics2D)image.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    	
        defaultColorSettings();
        if(black){
        	blackPiecesColorSettings();
        }else{
        	whitePiecesColorSettings();
        }
        
        
        fillCircle(g2);
        paintTopShadow(g2); 
        paintBottomHighlights(g2);
        paintDarkEdges(g2);
        paintBottomOvalHighlight(g2);
        paintTopLeftOvalSpecularHighlight(g2);
        if(stroke)
        	drawStroke(black, g2);
    }

	private void drawStroke(final boolean black, Graphics2D g2) {
		final Color strokeColor;
        if(black)
        	strokeColor = Color.WHITE;
        else
        	strokeColor = Color.BLACK;
        
        g2.setStroke(new BasicStroke(getWidth()*0.03f));
		g2.setColor(strokeColor);
        g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
	}

	private void defaultColorSettings() {
		bottomOvalHighlightOutterCollor = new Color(64, 142, 203, 255);
		centerGlow = 						 new Color(6, 76, 160, 127);
		overallColor =                       new Color(1,83,204,255);
		edges = new Color(0.0f, 0.0f, 0.0f, 0.8f);
	}

	private void blackPiecesColorSettings() {
		bottomOvalHighlightOutterCollor = getSaturatedColor(bottomOvalHighlightOutterCollor);
        centerGlow = getSaturatedColor(centerGlow);
        overallColor = getSaturatedColor(overallColor);
	}
	
	private void whitePiecesColorSettings() {
		bottomOvalHighlightOutterCollor = getSaturatedColor(bottomOvalHighlightOutterCollor).brighter().brighter();
        centerGlow = getSaturatedColor(centerGlow);
        centerGlow = Color.WHITE;
        overallColor = getSaturatedColor(overallColor);
        edges = new Color(255,255,255,180);
	}

	private Color getSaturatedColor( Color color ) {
		int saturated = (color.getRed() + color.getGreen() + color.getBlue()) / 3;
        return new Color(saturated,saturated,saturated,255);
	}

	private int getWidth() {
		return _width;
	}

	private int getHeight() {
		return _height;
	}

	private void paintTopLeftOvalSpecularHighlight(Graphics2D g2) {
		Color color = new Color(1.0f, 1.0f, 1.0f, 0.4f);
		Color color2 = new Color(1.0f, 1.0f, 1.0f, 0.0f);
		
		
		Point2D.Double center = new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0);
		float radius = getWidth() / 1.4f;
		Point2D.Double focus = new Point2D.Double(45.0, 25.0);
		float[] fractions = new float[] { 0.0f, 0.5f };
		Color[] colors = new Color[] { color,color2 };
		Paint paint = new RadialGradientPaint(center, radius,focus,fractions,colors,RadialGradientPaint.CycleMethod.NO_CYCLE);
        g2.setPaint(paint);
        
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	}

	private void paintBottomOvalHighlight(Graphics2D g2) {
		Color bottomOvalHighlightInnerCollor = new Color(bottomOvalHighlightOutterCollor.getRed()
				, bottomOvalHighlightOutterCollor.getGreen(), bottomOvalHighlightOutterCollor.getBlue(), 0);
		
		Point2D.Double center = new Point2D.Double(getWidth() / 2.0, getHeight() * 1.5);
		float radius = getWidth() / 2.3f;
		Point2D.Double focus = new Point2D.Double(getWidth() / 2.0, getHeight() * 1.75 + 6);
		float[] fractions = new float[] { 0.0f, 0.8f };
		Color[] colors = new Color[] { bottomOvalHighlightOutterCollor,bottomOvalHighlightInnerCollor };
		AffineTransform scaleInstance = AffineTransform.getScaleInstance(1.0, 0.5);
		Paint paint = new RadialGradientPaint(center, radius,focus,fractions,colors,RadialGradientPaint.CycleMethod.NO_CYCLE,
				RadialGradientPaint.ColorSpaceType.SRGB, scaleInstance);
        g2.setPaint(paint);
        
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	}

	private void paintDarkEdges(Graphics2D g2) {
		
		Point2D.Double center = new Point2D.Double(getWidth() / 2.0, getHeight() / 2.0);
		float radius = getWidth() / 2.0f;
		float[] fractions = new float[] { 0.0f, 1.0f };
		Color[] colors = new Color[] { centerGlow, edges };
		Paint paint = new RadialGradientPaint(center, radius,fractions,colors);
        g2.setPaint(paint);
        
        int x = 0;
        int y = 0;
		int width = getWidth() - 1;
		int height = getHeight() - 1;
		g2.fillOval(x,y, width, height);
	}

	private void paintBottomHighlights(Graphics2D g2) {
		Color color1 = new Color(1.0f, 1.0f, 1.0f, 0.0f);
		Color color2 = new Color(1.0f, 1.0f, 1.0f, 0.4f);
		
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = getHeight();
		
		Paint paint = new GradientPaint(x1, y1, color1, x2, y2, color2);
        g2.setPaint(paint);
        
        int width = getWidth() - 1;
		int height = y2 - 1;
		g2.fillOval(x1, x1, width, height);
	}

	private void paintTopShadow(Graphics2D g2) {
		Color color1 = new Color(0.0f, 0.0f, 0.0f, 0.4f);
		Color color2 = new Color(0.0f, 0.0f, 0.0f, 0.0f);
		
		int x1 = 0;
		int y1 = 0;
		int x2 = 0;
		int y2 = getHeight();
		
		Paint paint= new GradientPaint(x1, y1, color1, x2, y2, color2);
        g2.setPaint(paint);
        
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	}

	private void fillCircle(Graphics2D g2) {
		g2.setColor(overallColor);
        g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
	}
}