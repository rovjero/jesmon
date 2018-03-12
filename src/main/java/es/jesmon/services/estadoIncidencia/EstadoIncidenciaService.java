package es.jesmon.services.estadoIncidencia;

import es.jesmon.entities.EstadoIncidencia;
import es.jesmon.services.exception.ServicesExpception;

public interface EstadoIncidenciaService {

	public void insertarEstadoIncidencia(EstadoIncidencia estadoIncidencia) throws ServicesExpception;
}
