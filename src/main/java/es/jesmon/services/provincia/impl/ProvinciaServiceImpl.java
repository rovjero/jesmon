package es.jesmon.services.provincia.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.jesmon.controller.incidencia.IncidenciasController;
import es.jesmon.entities.Estado;
import es.jesmon.entities.Provincia;
import es.jesmon.repository.JesmonRepository;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.exception.ServicesExpception;
import es.jesmon.services.provincia.ProvinciaService;
import es.jesmon.services.util.SingletonJesmon;

@Service
public class ProvinciaServiceImpl implements ProvinciaService{

	private static Logger logger = LoggerFactory.getLogger(ProvinciaServiceImpl.class);
	
	@Autowired
	JesmonRepository jesmonRepository;
	
	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = ServicesExpception.class)
	public List<Provincia> getListaProvincias() throws ServicesExpception {
		try {
			List<Provincia> listaProvincias = SingletonJesmon.getLista(SingletonJesmon.LISTA_PROVINCIAS);
			if(listaProvincias == null) {
				List<ParBean> criteriosOrdenacion = new ArrayList<ParBean>();
				criteriosOrdenacion.add(new ParBean("denominacion", CriteriosBusqueda.ASC));
				listaProvincias = jesmonRepository.getLista(null, criteriosOrdenacion, Provincia.class);
				SingletonJesmon.addLista(SingletonJesmon.LISTA_PROVINCIAS, listaProvincias);
			}
			return listaProvincias;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new ServicesExpception(e);
		}
	}

	@Override
	public Provincia getProvincia(String cdProvincia) throws ServicesExpception {
		try {
			Provincia provincia = (Provincia)SingletonJesmon.getObjetoHashMap(SingletonJesmon.HASH_MAP_PROVINCIAS, cdProvincia);
			if(provincia == null) {
				provincia = (Provincia)jesmonRepository.findById(Provincia.class, cdProvincia);
				SingletonJesmon.putHashMap(SingletonJesmon.HASH_MAP_PROVINCIAS, cdProvincia, provincia);
			}
			return provincia;
		}
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			throw new ServicesExpception(e);
		}
	}
}
