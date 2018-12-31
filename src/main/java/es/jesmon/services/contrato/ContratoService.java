package es.jesmon.services.contrato;

import es.jesmon.services.exception.ServicesExpception;

public interface ContratoService {

	public byte[] generarContrato(Long idContrato, String tipoContrato) throws ServicesExpception;
}
