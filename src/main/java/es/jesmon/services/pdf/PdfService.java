package es.jesmon.services.pdf;

import java.util.List;

import es.jesmon.repository.util.ParBean;
import es.jesmon.services.exception.ServicesExpception;

public interface PdfService {

	public byte[] generarPDF (byte[] plantilla, List<ParBean> listaValores) throws ServicesExpception;
}
