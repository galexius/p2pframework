package de.testalgo;

import junit.framework.Assert;

import org.junit.Test;

import de.bachelor.graphgame.GraphCalculator;

public class GraphCalculatorTest {

	@Test
	public void testIntersectionOnEndPoints() {
		double x1 = 0.09d;
		double y1 = 0.1d;
		double x2 = 0.9d;
		double y2 = 0.1d;
		double x3 = 0.1d;
		double y3 = 0.09d;
		double x4 = 0.9d;
		double y4 = 0.9d;		
		Assert.assertTrue(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testIntersectionInTheMiddle() {
		double x1 = 0.1d;
		double y1 = 0.5d;
		double x2 = 0.9d;
		double y2 = 0.5d;
		double x3 = 0.5d;
		double y3 = 0.1d;
		double x4 = 0.5d;
		double y4 = 0.9d;		
		Assert.assertTrue(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testNoIntersectionParallel() {
		double x1 = 0.1d;
		double y1 = 0.1d;
		double x2 = 0.9d;
		double y2 = 0.1d;
		double x3 = 0.1d;
		double y3 = 0.9d;
		double x4 = 0.9d;
		double y4 = 0.9d;		
		Assert.assertFalse(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testNoIntersectionOnEndPoints() {
		double x1 = 0.2d;
		double y1 = 0.1d;
		double x2 = 0.9d;
		double y2 = 0.1d;
		double x3 = 0.1d;
		double y3 = 0.1d;
		double x4 = 0.9d;
		double y4 = 0.9d;
		
		Assert.assertFalse(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testNoIntersectionAlmostOnEachOther() {
		double x1 = 0.1d;
		double y1 = 0.1d;
		double x2 = 0.1d;
		double y2 = 0.9d;
		double x3 = 0.1d;
		double y3 = 0.1d;
		double x4 = 0.11d;
		double y4 = 0.9d;
		
		Assert.assertFalse(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testNoIntersectionOnEndPoints2() {
		double x1 = 0.1d;
		double y1 = 0.2d;
		double x2 = 0.1d;
		double y2 = 0.9d;
		double x3 = 0.2d;
		double y3 = 0.1d;
		double x4 = 0.9d;
		double y4 = 0.1d;
		
		Assert.assertFalse(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}
	
	@Test
	public void testNoIntersectionAlmostCross() {
		double x1 = 0.1d;
		double y1 = 0.1d;
		double x2 = 0.9d;
		double y2 = 0.6d;
		double x3 = 0.5d;
		double y3 = 0.5d;
		double x4 = 0.1d;
		double y4 = 0.9d;
		
		Assert.assertFalse(GraphCalculator.linesIntersect(x1, y1, x2, y2, x3, y3, x4, y4));
	}

}
