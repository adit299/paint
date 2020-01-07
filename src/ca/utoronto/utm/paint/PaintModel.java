package ca.utoronto.utm.paint;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javafx.scene.canvas.GraphicsContext;

public class PaintModel extends Observable implements Observer, DrawingVisitable {

	public void save(PrintWriter writer) {
		writer.write("PaintSaveFileVersion1.0" + "\n");
		for(PaintCommand c: this.commands) {
			writer.write(c.getInfo());
		}
		
		writer.write("EndPaintSaveFile");
		writer.close();
	}
	public void reset(){
		for(PaintCommand c: this.commands){
			c.deleteObserver(this);
		}
		this.commands.clear();
		this.setChanged();
		this.notifyObservers();
	}
	
	public void addCommand(PaintCommand command){
		this.commands.add(command);
		command.addObserver(this);
		this.setChanged();
		this.notifyObservers();
	}
	
	private ArrayList<PaintCommand> commands = new ArrayList<PaintCommand>();

	public void executeAll(GraphicsContext g) {
		for(PaintCommand c: this.commands){
			this.accept((DrawingVisitor) c, g);
		}
	}
	
	/**
	 * We Observe our model components, the PaintCommands
	 */

	public void update(Observable o, Object arg) {
		this.setChanged();
		this.notifyObservers();
	}
	
	public void accept(DrawingVisitor dv, GraphicsContext g) {
		dv.visit(this, g);	
	}


}
