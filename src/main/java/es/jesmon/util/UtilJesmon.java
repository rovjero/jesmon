package es.jesmon.util;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import org.apache.commons.lang.StringUtils;

public class UtilJesmon {
	
	private static DecimalFormat df;
	private static DecimalFormat df3;
	
	static {
		df = new DecimalFormat("#,###.##");
		df.setMinimumFractionDigits(2);
		
		df3 = new DecimalFormat("#,###.###");
		df3.setMinimumFractionDigits(3);
	}
	
	public static Double stringToDouble (String numeroStr) {
		try {
			if(StringUtils.isBlank(numeroStr))
				return null;
			
			if(StringUtils.contains(numeroStr, ",") || StringUtils.countMatches(numeroStr, ".") == 2)
				return new Double(df.parse(numeroStr).doubleValue());
				
			if(StringUtils.contains(numeroStr, ".")){
				int posPunto = numeroStr.indexOf(".") + 1;
				if(StringUtils.substring(numeroStr, posPunto, numeroStr.length()).length() == 3)
					return new Double(df.parse(numeroStr).doubleValue());
			}
			numeroStr = StringUtils.replace(numeroStr, ",", ".");
			return new Double (numeroStr);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public static byte[] leerBytesFichero (String rutaFichero) throws FileNotFoundException, IOException {
		File file = new File(rutaFichero);
		int longitud = new Long(file.length()).intValue();
		byte[] bytes = new byte[longitud];
		FileInputStream fiStream = new FileInputStream(file);
		DataInputStream dStream = new DataInputStream(fiStream);
		dStream.read(bytes, 0, longitud);
		dStream.close();
		fiStream.close();
		return bytes;
	}
	
	public static void grabarFichero (String rutaFichero, byte[] os) throws FileNotFoundException,IOException{
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(rutaFichero)));
	    dos.write(os);
	    dos.close();
	}
	
	public static String doubleToString (Double numero, int numDecimales, boolean mostrarPuntoMiles){
		String numeroStr = "0,00";
		if(numero == null)
			return numeroStr;
		if(numDecimales == 2){
			numeroStr = df.format(numero);
		}
		if(numDecimales == 3){
			numeroStr = df3.format(numero);
		}
		else {	
			DecimalFormat dfTmp = new DecimalFormat("#,###.##");
			dfTmp.setMinimumFractionDigits(numDecimales);
			numeroStr = dfTmp.format(numero);
		}
		if(!mostrarPuntoMiles)
			numeroStr = StringUtils.replace(numeroStr, ".", "");
		return numeroStr;
	}
	
	public static String doubleToString (Double numero){
		return doubleToString (numero, 2, true);
	}
}
