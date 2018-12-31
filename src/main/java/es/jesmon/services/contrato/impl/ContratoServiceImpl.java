package es.jesmon.services.contrato.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.jesmon.services.contrato.ContratoService;
import es.jesmon.services.empresa.impl.EmpresaServiceImpl;
import es.jesmon.services.exception.ServicesExpception;
import es.jesmon.services.pdf.PdfService;

@Service
public class ContratoServiceImpl implements ContratoService {
	
	private static Logger logger = LoggerFactory.getLogger(ContratoServiceImpl.class);
	
	@Autowired
	private PdfService pdfService;
	
	public byte[] generarContrato(Long idContrato, String tipoContrato) throws ServicesExpception {
		try {
			return null;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new ServicesExpception(e);
		}
	}
}
