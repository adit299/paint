package ca.utoronto.utm.paint;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PolylineCommand extends PaintCommand implements DrawingVisitor {
	
	private ArrayList<Point> points=new ArrayList<Point>();
	private ArrayList<Point> currPair = new ArrayList<Point>();
	
	private Point tracePoint;
	private boolean isShapeFinised = false;
	
	public void addTrace(Point p) {
		this.tracePoint = p;
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addPoint(Point p) {
		this.points.add(p);
		if (this.currPair.size() == 2) {
			currPair.remove(0);
			currPair.add(p);
		}
		else {
			currPair.add(p);
		}
		
		this.setChanged();
		this.notifyObservers();
	}
	
	public Point tracePoint() { return this.tracePoint; }
	
	public String getInfo() {
		
		int r = (int) (this.getColor().getRed() * 255); 
		int g = (int) (this.getColor().getGreen() * 255); 
		int b = (int) (this.getColor().getBlue() * 255); 
		
		String s = "";
		s += "Polyline\n";
		s += "	color:" + r + "," + g  + "," + b + "\n";
		s += "	filled:" + this.isFill() + "\n";
		s += "	points" + "\n";
		for(Point p: this.points) {
			s += "		" + "point:(" + p.x + "," + p.y + ")" + "\n";
		}
		s += "	end points\n";
		s += "End Polyline\n";
		return s;
	}

	@Override
	public void execute(GraphicsContext g) {
		g.setStroke(this.getColor());
		
		if (points.size() == 1) {
			if(!(tracePoint == null))currPair.add(tracePoint);
			if (currPair.size() == 2) {
				g.strokeLine(currPair.get(0).x, currPair.get(0).y, currPair.get(1).x, currPair.get(1).y);
				currPair.remove(1);
			}
		}
		
		for(int i=0;i<points.size()-1;i++) {
			if(!(tracePoint == null))currPair.add(tracePoint);
			if (currPair.size() == 3) currPair.remove(0);
			if (currPair.size() == 2) {
				g.strokeLine(currPair.get(0).x, currPair.get(0).y, currPair.get(1).x, currPair.get(1).y);
				currPair.remove(1);
			}
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
			
		}
	}
	
	public void visit(PaintModel p, GraphicsContext g) {
		this.execute(g);
	}
}
	

		
