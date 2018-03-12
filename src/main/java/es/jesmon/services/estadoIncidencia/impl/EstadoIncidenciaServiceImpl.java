package es.jesmon.services.estadoIncidencia.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.jesmon.entities.EstadoIncidencia;
import es.jesmon.repository.EstadoIncidenciaRepository;
import es.jesmon.services.estadoIncidencia.EstadoIncidenciaService;
import es.jesmon.services.exception.ServicesExpception;

@Service
public class EstadoIncidenciaServiceImpl implements EstadoIncidenciaService{

	@Autowired
	EstadoIncidenciaRepository estadoIncidenciaRepository;
	
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = ServicesExpception.class)
	public void insertarEstadoIncidencia(EstadoIncidencia estadoIncidencia) throws ServicesExpception {
		try {
			estadoIncidenciaRepository.save(estadoIncidencia);
		}
		catch (Exception e) {
			e.printStackTrace();
			throw new ServicesExpception(e);
		}
		
	}
}
