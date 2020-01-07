package ca.utoronto.utm.paint;

import javafx.scene.canvas.GraphicsContext;

public interface DrawingVisitable {
	
	public void accept(DrawingVisitor dv, GraphicsContext g);
	

}
