package es.jesmon.services.incidencias;

import es.jesmon.entities.Incidencia;
import es.jesmon.services.exception.ServicesExpception;

public interface IncidenciasService {

	public void insertar(Incidencia incidencia)  throws ServicesExpception;
}
