package es.jesmon.services.empresa.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import es.jesmon.entities.Empresa;
import es.jesmon.entities.JesmonEntity;
import es.jesmon.repository.JesmonRepository;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.empresa.EmpresaService;
import es.jesmon.services.exception.ServicesExpception;
import es.jesmon.services.mensaje.impl.MensajesServiceImpl;

@Service
public class EmpresaServiceImpl implements EmpresaService{

	private static Logger logger = LoggerFactory.getLogger(EmpresaServiceImpl.class);
	
	@Autowired
	private JesmonRepository jesmonRepository;
	
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED, rollbackFor = ServicesExpception.class)
	public void setEmpresasUsuario(JesmonEntity entity) throws ServicesExpception {
		try {
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.addCriterio("sedes", "sedes", AliasBean.LEFT_JOIN);
			criteriosBusqueda.addCriterio("responsables", "responsables", AliasBean.LEFT_JOIN);
			List<ParBean> criteriosOrdenacion = new ArrayList<ParBean>();
			List<Empresa> listaEmpresas = (List<Empresa>) (List)jesmonRepository.getLista(criteriosBusqueda, criteriosOrdenacion, Empresa.class);
			entity.setListaEmpresas(listaEmpresas);
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			throw new ServicesExpception(e);
		}
	}
}
