package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;

public class SquiggleCommand extends PaintCommand implements DrawingVisitor {
	private ArrayList<Point> points=new ArrayList<Point>();
	
	public void add(Point p){ 
		this.points.add(p); 
		this.setChanged();
		this.notifyObservers();
	}
	public ArrayList<Point> getPoints(){ return this.points; }
	
	public String getInfo() {
		int r = (int) (this.getColor().getRed() * 255); 
		int g = (int) (this.getColor().getGreen() * 255); 
		int b = (int) (this.getColor().getBlue() * 255); 
		
		String s = "";
		s += "Squiggle\n";
		s += "	color:" + r + "," + g + "," + b + "\n";
		s += "	filled:" + this.isFill() + "\n";
		s += "	points" + "\n";
		for(Point p: this.points) {
			s += "		" + "point:(" + p.x + "," + p.y + ")" + "\n";
		}
		s += "	end points\n";
		s += "End Squiggle\n";
		return s;
	}
	
	
	@Override
	public void execute(GraphicsContext g) {
		ArrayList<Point> points = this.getPoints();
		g.setStroke(this.getColor());
		for(int i=0;i<points.size()-1;i++){
			Point p1 = points.get(i);
			Point p2 = points.get(i+1);
			g.strokeLine(p1.x, p1.y, p2.x, p2.y);
		}
		
	}
	
	public void visit(PaintModel p, GraphicsContext g) {
		this.execute(g);
	}
}
