package ca.utoronto.utm.paint;

import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PolylineManipulatorStrategy extends ShapeManipulatorStrategy {
	private PolylineCommand polylineCommand;
	
	PolylineManipulatorStrategy(PaintModel paintModel) {
		super(paintModel);
		this.polylineCommand = new PolylineCommand();
		this.addCommand(polylineCommand);
	}
	
	public void mouseMoved(MouseEvent e) {
		this.polylineCommand.addTrace(new Point((int)e.getX(), (int)e.getY()));
	}
	
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseButton.PRIMARY) {
			this.polylineCommand.addPoint(new Point((int)e.getX(), (int)e.getY()));
		}
		else if (e.getButton() == MouseButton.SECONDARY) {
			// finish the shape
			this.polylineCommand.addPoint(new Point((int)e.getX(), (int)e.getY()));
			this.polylineCommand = new PolylineCommand();
			this.addCommand(polylineCommand);
		}			
	}

}
