package es.jesmon.services.provincia;

import java.util.List;

import es.jesmon.entities.Provincia;
import es.jesmon.services.exception.ServicesExpception;

public interface ProvinciaService {

	public List<Provincia> getListaProvincias() throws ServicesExpception;
	public Provincia getProvincia(String cdProvincia) throws ServicesExpception;
}
