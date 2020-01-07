package ca.utoronto.utm.paint;
import javafx.scene.canvas.GraphicsContext;

public class CircleCommand extends PaintCommand implements DrawingVisitor {
	private Point centre;
	private int radius;
	
	public CircleCommand(Point centre, int radius){
		this.centre = centre;
		this.radius = radius;
	}
	
	public CircleCommand() {}
	
	
	public Point getCentre() { return centre; }
	public void setCentre(Point centre) { 
		this.centre = centre; 
		this.setChanged();
		this.notifyObservers();
	}
	public int getRadius() { return radius; }
	public void setRadius(int radius) { 
		this.radius = radius; 
		this.setChanged();
		this.notifyObservers();
	}
	
	public String getInfo() {
		int r = (int) (this.getColor().getRed() * 255); 
		int g = (int) (this.getColor().getGreen() * 255); 
		int b = (int) (this.getColor().getBlue() * 255); 
		
		
		String s = "";
		s += "Circle\n";
		s += "	color:" + r  + "," + g  + "," + b + "\n";
		s += "	filled:" + this.isFill() + "\n";
		s += "	center:" + "(" + this.getCentre().x + "," + this.getCentre().y + ")" + "\n";
		s += "	radius:" + this.radius + "\n";
		s += "End Circle\n";
		return s;
		
		
	}
	
	
	
	public void execute(GraphicsContext g){
		int x = this.getCentre().x;
		int y = this.getCentre().y;
		int radius = this.getRadius();
		if(this.isFill()){
			g.setFill(this.getColor());
			g.fillOval(x-radius, y-radius, 2*radius, 2*radius);
		} else {
			g.setStroke(this.getColor());
			g.strokeOval(x-radius, y-radius, 2*radius, 2*radius);
		}
	}

	@Override
	public void visit(PaintModel p, GraphicsContext g) {
		this.execute(g);
		
	}

}
