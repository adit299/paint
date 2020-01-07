package ca.utoronto.utm.paint;

import javafx.scene.canvas.GraphicsContext;

public interface DrawingVisitor {


	public void visit(PaintModel p, GraphicsContext g);


}
