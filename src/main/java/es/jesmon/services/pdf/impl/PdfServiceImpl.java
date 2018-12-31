package es.jesmon.services.pdf.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import es.jesmon.repository.util.ParBean;
import es.jesmon.services.exception.ServicesExpception;
import es.jesmon.services.pdf.PdfService;
import es.jesmon.util.UtilJesmon;

@Service
public class PdfServiceImpl implements PdfService {

	private static Logger logger = LoggerFactory.getLogger(PdfServiceImpl.class);
	
	@Override
	public byte[] generarPDF(byte[] plantilla, List<ParBean> listaValores) throws ServicesExpception {
		// TODO Auto-generated method stub
		try {
			PdfReader reader = new PdfReader(plantilla);
			ByteArrayOutputStream fout = new ByteArrayOutputStream();
			PdfStamper stamp = new PdfStamper (reader, fout);
			AcroFields form = stamp.getAcroFields();
			for(ParBean valor : listaValores)
				form.setField(valor.getNombre(), valor.getValor());
				
			stamp.setFullCompression();
			stamp.close();
			fout.close();
			byte[] pdf = fout.toByteArray();
			return pdf;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new ServicesExpception(e);
		}		
	}
	
	public static void main(String[] args) {
		try {
			PdfServiceImpl pdfServiceImpl = new PdfServiceImpl();
			byte[] plantilla = UtilJesmon.leerBytesFichero("D:/usuarios/java/proyectos/Tasas/plantillas/Formulario_790052/Transformacion_PDF/original_v4.pdf");
			List<ParBean> listaValores = new ArrayList<ParBean>();
			listaValores.add(new ParBean("Numero_Justificante", "79005211111111"));
			byte[] pdf = pdfServiceImpl.generarPDF(plantilla, listaValores);
			UtilJesmon.grabarFichero("D:/contrato_jesmon2.pdf", pdf);
			
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
		}
	}

}
