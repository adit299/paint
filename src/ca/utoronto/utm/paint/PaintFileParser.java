package ca.utoronto.utm.paint;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
/**
 * Parse a file in Version 1.0 PaintSaveFile format. An instance of this class
 * understands the paint save file format, storing information about
 * its effort to parse a file. After a successful parse, an instance
 * will have an ArrayList of PaintCommand suitable for rendering.
 * If there is an error in the parse, the instance stores information
 * about the error. For more on the format of Version 1.0 of the paint 
 * save file format, see the associated documentation.
 * 
 * @author 
 *
 */
public class PaintFileParser {
	private int lineNumber = 0; // the current line being parsed
	private String errorMessage =""; // error encountered during parse
	private PaintModel paintModel; 
	public ArrayList<PaintCommand> PaintCommandArrayList = new ArrayList<PaintCommand>(); 
	
	
	
	/**
	 * Below are Patterns used in parsing 
	 */
	private Pattern pFileStart=Pattern.compile("^PaintSaveFileVersion1.0$");
	private Pattern pFileEnd=Pattern.compile("^EndPaintSaveFile$");
	


	private Pattern pCircleStart=Pattern.compile("^Circle$");
	private Pattern pColor = Pattern.compile("^color:([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]),([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5]),([0-9]|[0-9][0-9]|1[0-9][0-9]|2[0-4][0-9]|25[0-5])$");
	private Pattern pFilled = Pattern.compile("^filled:(true|false)$");
	private Pattern pCircleCenter = Pattern.compile("^center:\\((\\d+|-\\d+),(\\d+|-\\d+)\\)$");
	private Pattern pCircleRadius =  Pattern.compile("^radius:(\\d+)$");
	private Pattern pCircleEnd=Pattern.compile("^EndCircle$");
	
	private Pattern pRectangleStart = Pattern.compile("^Rectangle$");
	private Pattern pRectangleP1 = Pattern.compile("^p1:\\((\\d+|-\\d+),(\\d+|-\\d+)\\)$");
	private Pattern pRectangleP2 = Pattern.compile("^p2:\\((\\d+|-\\d+),(\\d+|-\\d+)\\)$");
	private Pattern pRectangleEnd = Pattern.compile("^EndRectangle$");
	
	private Pattern pSquiggleStart = Pattern.compile("^Squiggle$");
	private Pattern pPoints = Pattern.compile("^points$");
	private Pattern pPoint = Pattern.compile("^point:\\((\\d+|-\\d+),(\\d+|-\\d+)\\)$");
	private Pattern pPointsEnd = Pattern.compile("^endpoints$");
	private Pattern pSquiggleEnd = Pattern.compile("^EndSquiggle$");
	
	private Pattern pPolylineStart = Pattern.compile("^Polyline$");
	private Pattern pPolylineEnd = Pattern.compile("^EndPolyline$");
	
	
	/**
	 * Store an appropriate error message in this, including 
	 * lineNumber where the error occurred.
	 * @param mesg
	 * @throws Exception 
	 */
	private void error(String mesg) throws Exception{
		this.errorMessage = "Error in line "+lineNumber+" "+mesg;
		throw new Exception(this.errorMessage);
	}
	
	/**
	 * 
	 * @return the error message resulting from an unsuccessful parse
	 */
	public String getErrorMessage(){
		return this.errorMessage;
		
	}
	
	/**
	 * Parse the inputStream as a Paint Save File Format file.
	 * The result of the parse is stored as an ArrayList of Paint command.
	 * If the parse was not successful, this.errorMessage is appropriately
	 * set, with a useful error message.
	 * 
	 * @param inputStream the open file to parse
	 * @param paintModel the paint model to add the commands to
	 * @return whether the complete file was successfully parsed
	 */
	public boolean parse(BufferedReader inputStream, PaintModel paintModel) {
		this.paintModel = paintModel;
		this.errorMessage="";
		
		// During the parse, we will be building one of the 
		// following commands. As we parse the file, we modify 
		// the appropriate command.
		
		CircleCommand circleCommand = new CircleCommand(); 
		RectangleCommand rectangleCommand = new RectangleCommand();
		SquiggleCommand squiggleCommand = new SquiggleCommand();
		PolylineCommand polylineCommand = new PolylineCommand();
		
	
		try {	
			int state=0; Matcher m; String l;
			
			this.lineNumber=0;
			while ((l = inputStream.readLine()) != null) {
				l = l.replaceAll("\\s+","");
				if (l.trim().length() == 0 ) continue;  
				this.lineNumber++;
				System.out.println(lineNumber+" "+l+" "+state);
				switch(state){
					case 0:
						m=pFileStart.matcher(l);
						if(m.matches()){
							state=1;
							break;
						}
						error("Expected Start of Paint Save File");
						return false;
					case 1: // Looking for the start of a new object or end of the save file
						m=pCircleStart.matcher(l);
						if(m.matches()){
							circleCommand = new CircleCommand();
							state = 6;
							break;
						}			
					case 2:
						m=pRectangleStart.matcher(l);
						
						if(m.matches()) {
							rectangleCommand = new RectangleCommand();	
							state = 11;
							break;
						}
					
					case 3:
						m = pSquiggleStart.matcher(l);
						
						if(m.matches()) {
							squiggleCommand = new SquiggleCommand();
							state = 16;
							break;
						}
						
					case 4:
						m = pPolylineStart.matcher(l);
						
						if(m.matches()) {
							polylineCommand = new PolylineCommand();
							state = 21;
							break;
						}
						
					case 5:
						m = pFileEnd.matcher(l);
						
						if(m.matches()) {
							break;
						}
						
						else {
							error("Expected start of a new object/end of project");
						}
					
					case 6:
						m = pColor.matcher(l);
						
						if(m.matches()) {
							circleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
							state = 7;
							break;
						}
						else {
							error("Issue detected with circle color");
							return false;
						}
						
					case 7:
						m = pFilled.matcher(l);
						
						if(m.matches()) {
							circleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 8;
							break;
						}
						else {
							error("Issue detected with circle fill");
							return false;
						}
					
					case 8:
						m = pCircleCenter.matcher(l);
						
						if(m.matches()) {
							Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							circleCommand.setCentre(p);
							state = 9;
							break;
						}
						else {
							error("Issue detected with circle center");
							return false;
						}
					
					case 9:
						m = pCircleRadius.matcher(l);
						
						if(m.matches()) {
							circleCommand.setRadius(Integer.parseInt(m.group(1)));
							state = 10;
							break;
						}
						else {
							error("Issue detected with circle radius");
							return false;
						}
					
					case 10:
						m = pCircleEnd.matcher(l);
							
						if(m.matches()) {
							this.PaintCommandArrayList.add(circleCommand);
							state = 1;
							break;
						}
						else {
							error("Issue detected with circle end statement");
							return false;
						}
					case 11:
						m = pColor.matcher(l);
						
						if(m.matches()) {
							rectangleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
							state = 12;
							break;
						}
						else {
							error("Issue detected with rectangle color");
							return false;
						}
						
					case 12:
						m = pFilled.matcher(l);
						if(m.matches()) {
							rectangleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 13;
							break;
						}
						else {
							error("Issue detected with rectangle fill");
							return false;
						}
					
					case 13:
						m = pRectangleP1.matcher(l);
						
						if(m.matches()) {
							Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							rectangleCommand.setP1(p);
							state = 14;
							break;
						}
						else {
							error("Issue detected with rectangle point P1");
							return false;
						}
					
					case 14:
						m = pRectangleP2.matcher(l);
						
						if(m.matches()) {
							Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							rectangleCommand.setP2(p);
							state = 15;
							break;
						}
						else {
							error("Issue detected with rectangle point P2");
							return false;
						}
					
					case 15:
						m = pRectangleEnd.matcher(l);
						
						if(m.matches()) {
							this.PaintCommandArrayList.add(rectangleCommand);
							state = 1;
							break;
						}
						else {
							error("Issue detected with rectangle end statement");
							return false;
						}
					
					case 16:
						m = pColor.matcher(l);
						
						if(m.matches()) {
							squiggleCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
							state = 17;
							break;
						}
						else {
							error("Issue detected with Squiggle Color");
							return false;
						}
					
					case 17:
						m = pFilled.matcher(l);
							
						if(m.matches()) {
							squiggleCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 18;
							break;
						}
						else {
							error("Issue detected with Squiggle fill");
							return false;
						}
					
					case 18:
						m = pPoints.matcher(l);
						
						if(m.matches()) {
							state = 19;
							break;
						}
						else {
							error("Issue detected with beginning squiggle points declaration");
							return false;
						}
					
					case 19:
						m = pPoint.matcher(l);
						Matcher n = pPointsEnd.matcher(l);
						
						if(m.matches()) {
							Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							squiggleCommand.add(p);
							state = 19;
							break;
						}
						else if (n.matches()) {
							state = 20;
							break;
						}
						else {
							error("Issue detected with squiggle points and/or points ending declaration");
							return false;
						}
					
					case 20:
						m = pSquiggleEnd.matcher(l);
						
						if(m.matches()) {
							this.PaintCommandArrayList.add(squiggleCommand);
							state = 1;
							break;
						}
						else {
							error("Issue detected with squiggle points ending declaration");
							return false;
						}
					
					case 21:
						m = pColor.matcher(l);
						
						if(m.matches()) {
							polylineCommand.setColor(Color.rgb(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3))));
							state = 22;
							break;
						}
						else {
							error("Issue detected with Polyline Color");
							return false;
						}
					
					case 22:
						m = pFilled.matcher(l);
						if(m.matches()) {
							polylineCommand.setFill(Boolean.parseBoolean(m.group(1)));
							state = 23;
							break;
						}
						else {
							error("Issue detected with Polyline fill");
							return false;
						}
					
					case 23:
						m = pPoints.matcher(l);
						
						if(m.matches()) {
							state = 24;
							break;
						}
						else {
							error("Issue detected with beginning Polyline points declaration");
							return false;
						}
					
					case 24:
						m = pPoint.matcher(l);
						Matcher o = pPointsEnd.matcher(l);
						
						if(m.matches()) {
							Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
							polylineCommand.addPoint(p);
							state = 24;
							break;
						}
						else if (o.matches()) {
							state = 25;
							break;
						}
						else {
							error("Issue detected with squiggle points and/or points ending declaration");
							return false;
						}
					
					case 25:
						m = pPolylineEnd.matcher(l);
						
						if(m.matches()) {
							this.PaintCommandArrayList.add(polylineCommand);
							state = 1;
							break;
						}
						else {
							error("Issue detected with polyline points ending declaration");
							return false;
						}
				}
			}
		}  catch (Exception e){
			System.out.println(e.getMessage());
			return false;
			
		}
		for(PaintCommand c: this.PaintCommandArrayList) {
			this.paintModel.addCommand(c);
		}
		return true;
	}
}
