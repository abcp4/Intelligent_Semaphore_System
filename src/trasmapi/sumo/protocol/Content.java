package trasmapi.sumo.protocol;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import trasmapi.genAPI.exceptions.WrongCommand;
import trasmapi.sumo.ControlledLinks;
import trasmapi.sumo.Pair;
import trasmapi.sumo.SumoPolygon;

public class Content {

	private boolean debug = false;;
	
	public byte variable;
	public String id;
	public byte varType;
	public ArrayList<Byte> varValue;
	
	
	public Content()
	{
		varValue = new ArrayList<Byte>();
	}
	
	public Content(int idList, String id) {

		varValue = new ArrayList<Byte>();
		
		writeByte(idList);
		
		writeString(id);
		
	}

	//used in simulationStep
	public Content(int i) {
		
		varValue = new ArrayList<Byte>();
		
		writeInt(i);
		
	}

	public Content(Buffer buf) {

		variable = buf.readByte();
		if(debug) System.out.println("Var : "+Integer.toString( variable & 0xFF, 16));
		
		id = buf.readString();
		if(debug) System.out.println("Id : "+id);
		
		varType = buf.readByte();
		if(debug) System.out.println("varType : " + Integer.toString(varType & 0xFF, 16) + " ");
		
		varValue = buf.readValue(varType);
		if(debug) 	System.out.println("varValue : "+varValue);
	}
	
	public Content(byte[] cont) {

		if(debug) 	System.out.println("");
		int pointer = 0;
		variable = cont[pointer];
		if(debug) 	System.out.println("Var : "+Integer.toString( variable & 0xFF, 16));
		
		int idLength = cont[pointer+1] << 24 | cont[pointer+2] << 16 | cont[pointer+3] << 8 | cont[pointer+4];
		if(debug) 	System.out.println("idLength : "+idLength);
		
		pointer += 5;
		id = "";
		for(int j=0; j<idLength; j++)
			id += (char)cont[pointer++];

		if(debug) 	System.out.println("Id : "+id);
		
		varType = cont[pointer++];
		if(debug) 	System.out.println("varType : "+varType);
		
		if(debug) 	System.out.println("p : " + pointer);
		
		varValue = new ArrayList<Byte>();
		
		for(int i = 0; i<cont.length-pointer;i++)
			varValue.add(cont[pointer+i]);
		
		if(debug) 	System.out.println("varValue : "+varValue.toString());
		
	}

	public Content(int cmd, String id, int type) {

		varValue = new ArrayList<Byte>();
		
		writeByte(cmd);
		writeString(id);
		writeByte(type);
		
	}

	/**
	 * Command Subscribe Variable
	 * @param beginTime - time (ms)
	 * @param endTime - time (ms)
	 * @param objectId - string
	 * @param variableList - variables to subscribe
	 */
	public Content(int beginTime, int endTime, String objectId, ArrayList<Integer> variableList) {

		varValue = new ArrayList<Byte>();
		
		writeInt(beginTime);
		writeInt(endTime);
		writeString(objectId);
		writeByte(variableList.size());
		
		for(Integer b: variableList)
			writeByte(b);
		
		
	}


	/**
	 * Command Subscribe Variable
	 * @param beginTime - time (ms)
	 * @param endTime - time (ms)
	 * @param objectId - string
	 * @param contextDomain - int
	 * @param sensingRadius - double
	 * @param variableList - variables to subscribe
	 */
	public Content(int beginTime, int endTime, String objectId, int contextDomain, double sensingRadius, ArrayList<Integer> variableList) {

		varValue = new ArrayList<Byte>();
		
		writeInt(beginTime);
		writeInt(endTime);
		writeString(objectId);
		writeByte(contextDomain);
		writeDouble(sensingRadius);
		
		writeByte(variableList.size());
		
		for(Integer b: variableList)
			writeByte(b);
		
	}

	private void writeString(String value) {
		
		byte bytes[];

		try {
			bytes = value.getBytes("ASCII");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return;
		}

		writeInt(value.length());

		for (int i=0; i<bytes.length; i++)
			writeByte(bytes[i]);	
	}

	private void writeInt(int value) {

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream(4);
		DataOutputStream dataOut = new DataOutputStream(byteOut);
		
		byte bytes[] = new byte[4]; 
		
		try {
			dataOut.writeInt(value);
			dataOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		bytes = byteOut.toByteArray();
		
		for (int i=0; i<4; i++)
			writeByte(bytes[i]);
		
	}

	private void writeByte(int value) {
		
	//	if (value < -128 || value > 127)
		//	throw new IllegalArgumentException("only range from -128 to 127.");

		varValue.add(new Byte( (byte)(value) ));
	}

	public int length() {
		return varValue.size();
	}

	public synchronized void print(String prefix) {

		System.out.println(prefix + " 创创 Content 创创");
		System.out.print(prefix);
		for(Byte b : varValue)
			System.out.print(Integer.toString(b & 0xFF, 16) + " ");
		System.out.println();

	}

	public void sendContent(DataOutputStream out) throws IOException {
		
		for(Byte b: varValue)
			out.writeByte(b);
		
	}

	public void validate(int idList, int typeStringlist) throws WrongCommand {

		if(idList != variable)
			throw new WrongCommand("VARIABLE check: Was expecting " + Integer.toString(idList & 0xFF, 16) +
									" and got " +  Integer.toString(variable & 0xFF, 16));
		
		if(typeStringlist != varType)
			throw new WrongCommand("VAR_TYPE check: Was expecting " + Integer.toString(typeStringlist & 0xFF, 16) +
									" and got " +  Integer.toString(varType & 0xFF, 16));
		
	}

	public double getDouble() {

		byte content[] = new byte[8];
		double result = 0.0;
		
		for (int i=0; i<8; i++)
			content[i] = varValue.get(i);

		ByteArrayInputStream byteIn;
		DataInputStream dataIn;
		
		byteIn =  new ByteArrayInputStream(content);
		dataIn = new DataInputStream(byteIn);
		try {
			result = dataIn.readDouble();
			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(debug) System.out.println("double - "+result);

		return result;
	}

	public int getInteger() {

		ByteArrayInputStream byteIn;
		DataInputStream dataIn;
		byte content[] = new byte[4];
		int result = 0;
		
		for (int i=0; i<4; i++)
		{
			content[i] = varValue.get(i);
		}
		byteIn =  new ByteArrayInputStream(content);
		dataIn = new DataInputStream(byteIn);
		try {
			result = dataIn.readInt();
			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public void setDouble(double d) {

		writeDouble(d);
	}

	private void writeDouble(double d) {

		ByteArrayOutputStream byteOut =  new ByteArrayOutputStream();
		
		DataOutputStream os = new DataOutputStream(byteOut);
		
		try {
			
			os.writeDouble(d);
			
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte content[] = byteOut.toByteArray();
		
		for (int i=0; i<8; i++)
		{
			writeByte(content[i]);
		}

	}

	@SuppressWarnings("unchecked")
	public void setCompound(ArrayList<Pair<Integer, Object>> items) {

		int itemNum = items.size();
		
		writeInt(itemNum);

		for(Pair<Integer,Object> p : items){
			
			switch(p.first()){
			
			case Constants.TYPE_DOUBLE:
				writeByte(p.first());
				writeDouble((Double) p.second());
				break;
			case Constants.TYPE_STRING:
				writeByte(p.first());
				writeString((String) p.second());
				break;
			case Constants.TYPE_INTEGER:
				writeByte(p.first());
				writeInt((Integer) p.second());
				break;
			case Constants.TYPE_BYTE:
				writeByte(p.first());
				writeByte((Byte) p.second());
				break;
			case Constants.TYPE_UBYTE:
				writeByte(p.first());
				writeByte( ((Integer) p.second()));
				break;
			case Constants.TYPE_COLOR:
				writeByte(p.first());
				writeByte((byte) ((Color) p.second()).getRed());
				writeByte((byte) ((Color) p.second()).getGreen());
				writeByte((byte) ((Color) p.second()).getBlue());
				writeByte((byte) ((Color) p.second()).getAlpha());
				break;
			case Constants.TYPE_POLYGON:
				writeByte(p.first());
				writePolygon((ArrayList<Pair<Double,Double>>) p.second());
				break;
			}

		}

	}

	private void writePolygon(ArrayList<Pair<Double, Double>> arrayList) {

		writeByte((byte) arrayList.size());
		
		for(Pair<Double,Double> p: arrayList){
			writeDouble(p.first());
			writeDouble(p.second());
		}
	}

	public void setString(String str) {

		writeString(str);
		
	}

	public void setColor(Color color) {

		writeByte((byte) color.getRed());
		writeByte((byte) color.getGreen());
		writeByte((byte) color.getBlue());
		writeByte((byte) color.getAlpha());

	}

	public String getString() {

		//read string array
		int length = varValue.get(0) << 24 | varValue.get(1) << 16 | 
				varValue.get(2) << 8 | varValue.get(3);

		if(debug) System.out.println("StrLength : "+length);
		
		String str = new String();

		str = "";
		
		byte[] value = new byte[length];
		
		for(int i = 0; i< length;i++)
			value[i] = varValue.get(i+4);
		
		for(int j=0; j<length; j++)
			str += (char) value[j];
		
		return str;


	}

	public ArrayList<String> getStringList() {

		//read string array
		int numStrings = readInt(0);

		int pointer = 4;
		
		ArrayList<String> stringList = new ArrayList<String>();
		
		for(int i=0 ; i<numStrings ; i++){

			int strLength = readInt(pointer);

			String str = new String();
			byte[] value = new byte[strLength];

			pointer += 4;
			str = "";
			
			for(int j = 0; j < strLength; j++)
				value[j] = varValue.get(j+pointer);
			
			for(int j = 0; j < strLength; j++)
				str += (char) value[j];
			
			pointer += strLength;

			stringList.add(str);
		}
		
		return stringList;
	}

	public void setStringList(ArrayList<String> stringList) {

		writeInt(stringList.size());
		
		for(String s:stringList)
			writeString(s);
		
	}

	public int readInt(int pointer) {
		
		ByteArrayInputStream byteIn;
		DataInputStream dataIn;
		byte content[] = new byte[4];
		int result = 0;
		
		for (int i=0; i<4; i++)
		{
			content[i] = varValue.get(i+pointer);
		}
		byteIn =  new ByteArrayInputStream(content);
		dataIn = new DataInputStream(byteIn);
		try {
			result = dataIn.readInt();
			dataIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	public void setInteger(int time) {

		writeInt(time);
	}

}
