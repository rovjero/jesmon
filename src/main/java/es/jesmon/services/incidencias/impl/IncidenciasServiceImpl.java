package es.jesmon.services.incidencias.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.jesmon.entities.Estado;
import es.jesmon.entities.EstadoIncidencia;
import es.jesmon.entities.Incidencia;
import es.jesmon.repository.EstadoIncidenciaRepository;
import es.jesmon.repository.IncidenciaRepository;
import es.jesmon.services.exception.ServicesExpception;
import es.jesmon.services.incidencias.IncidenciasService;

@Service
public class IncidenciasServiceImpl implements IncidenciasService {

	@Autowired
	IncidenciaRepository incidenciaRepository;
	
	@Autowired
	EstadoIncidenciaRepository estadoIncidenciaRepository;
	
	
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = ServicesExpception.class)
	public void insertar(Incidencia incidencia) throws ServicesExpception {
		try {
			incidencia = incidenciaRepository.save(incidencia);
			EstadoIncidencia estadoIncidencia = new EstadoIncidencia();
			estadoIncidencia.setEstado(new Estado(Estado.ESTADO_PENDIENTE));
			estadoIncidencia.setFechaEstado(new Date());
			estadoIncidencia.setIncidencia(incidencia);
			estadoIncidencia.setResponsable(incidencia.getResponsable());
			estadoIncidenciaRepository.save(estadoIncidencia);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ServicesExpception(e);
		}
		
	}

}
