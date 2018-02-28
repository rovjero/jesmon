package es.jesmon.controller.cliente;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jesmon.controller.JesmonController;
import es.jesmon.controller.forms.BuscadorIncidenciasForm;
import es.jesmon.controller.forms.IncidenciaForm;
import es.jesmon.entities.Estado;
import es.jesmon.entities.Incidencia;
import es.jesmon.entities.Responsable;
import es.jesmon.entities.Sede;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.estados.EstadosService;
import es.jesmon.services.incidencias.IncidenciasService;
import es.jesmon.services.sedes.SedesServices;
import es.jesmon.services.util.UtilDate;

@Controller
public class ResponsableIncidenciasController extends JesmonController {

	@Autowired
	JesmonServices jesmonService;
	
	@Autowired
	EstadosService estadosService;
	
	@Autowired
	SedesServices sedesServices;
	
	@Autowired
	IncidenciasService incidenciasService;
	
	@RequestMapping(value = "/cliente/incidencias")
	public String incidencias(Map<String, Object> model,
			@ModelAttribute("buscadorIncidenciasForm") BuscadorIncidenciasForm buscadorIncidenciasForm, HttpServletRequest request) {
		try {
			Responsable responsable = (Responsable)getUsuarioSesion(request);
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.agregarAlias("sede", "sede", AliasBean.INNER_JOIN);
			//criteriosBusqueda.agregarAlias("sede.empresa", "empresa", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("responsable", "responsable", AliasBean.INNER_JOIN);
			//criteriosBusqueda.agregarAlias("estadoIncidencia", "estadoIncidencia", AliasBean.INNER_JOIN);
			//criteriosBusqueda.agregarAlias("estadoIncidencia.estado", "estado", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoActual", "estadoActual", AliasBean.INNER_JOIN);
			criteriosBusqueda.addCriterio("sede.idSede", responsable.getListaIdsSedes().toArray(), CriteriosBusqueda.IN);
			
			if(buscadorIncidenciasForm.getIdSede() != null)
				criteriosBusqueda.addCriterio("sede", new Sede(buscadorIncidenciasForm.getIdSede()));
			
			if(buscadorIncidenciasForm.getIdEstado() != null)
				criteriosBusqueda.addCriterio("estadoActual.estado", new Estado(buscadorIncidenciasForm.getIdEstado()));

			Date fechaAltaDesde = UtilDate.stringToDate(buscadorIncidenciasForm.getFechaAltaDesde());
			if(fechaAltaDesde != null)
				criteriosBusqueda.addCriterio("fechaAlta", fechaAltaDesde, CriteriosBusqueda.MAYOR_IGUAL);
				
			Date fechaAltaHasta = UtilDate.stringToDate235959(buscadorIncidenciasForm.getFechaAltaHasta());
			if(fechaAltaHasta != null)
				criteriosBusqueda.addCriterio("fechaAlta", fechaAltaHasta, CriteriosBusqueda.MENOR_IGUAL);
			
			if(buscadorIncidenciasForm.getIdResponsable() != null)
				criteriosBusqueda.addCriterio("responsable", new Responsable(buscadorIncidenciasForm.getIdResponsable()));
				
			List<ParBean> criteriosOrdenacion = new ArrayList<>();
			criteriosOrdenacion.add(new ParBean("fechaAlta", CriteriosBusqueda.DESC));
			List<Incidencia> listaIncidencias = (List<Incidencia>)(List)jesmonService.getLista(criteriosBusqueda, criteriosOrdenacion, Incidencia.class);
			model.put("listaEstados", estadosService.getListaEstados());
			
			model.put("listaIncidencias", listaIncidencias);
			return procesarViewResolver("incidenciasResponsable", request);
		}
		catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
	}
	
	@RequestMapping(value = "/cliente/consultarIncidencia")
	public String consultarIncidencia(Map<String, Object> model,
			@RequestParam("idIndicencia") String idIndicenciaStr, HttpServletRequest request) {
		try {
			Responsable responsable = (Responsable)getUsuarioSesion(request);
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.agregarAlias("sede", "sede", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("sede.empresa", "empresa", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("responsable", "responsable", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoIncidencia", "estadoIncidencia", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoIncidencia.estado", "estado", AliasBean.INNER_JOIN);
			criteriosBusqueda.addCriterio("sede.idSede", responsable.getListaIdsSedes().toArray(), CriteriosBusqueda.IN);
			Integer idIncidencia = new Integer(new String(Base64.getDecoder().decode(idIndicenciaStr.getBytes())));
			
			Incidencia incidencia = (Incidencia)jesmonService.buscarByPK(Incidencia.class, "idIncidencia", idIncidencia, criteriosBusqueda);
			if(incidencia == null) {
				return procesarViewResolver("error", request);
			}
			
			model.put("incidencia", incidencia);
			return procesarViewResolver("consultarIncidencia", request);
		}
		catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
	}

	@GetMapping("/cliente/insertarIncidencia")
    public String getMappingInsertarIncidencia(HttpServletRequest request) {
    	return procesarViewResolver("insertarIncidencia", request);
    }
	
	@PostMapping("/cliente/insertarIncidencia")
    public String postMappingInsertarIncidencia(@Valid IncidenciaForm incidenciaFrom, HttpServletRequest request) {
    	try {
    		Incidencia incidencia = new Incidencia();
    		incidencia.setAsunto(incidenciaFrom.getAsunto());
    		incidencia.setTexto(incidenciaFrom.getTexto());
    		incidencia.setFechaAlta(new Date());
    		incidencia.setResponsable((Responsable)getUsuarioSesion(request));
    		incidencia.setSede(new Sede(incidenciaFrom.getIdSede()));
    		incidenciasService.insertar(incidencia);
    		return "redirect:/cliente/incidencias.html?resultado=Incidencia insertarda de forma correcta&";
    	}
    	catch (Exception e) {
    		e.printStackTrace();
			return procesarViewResolver("error", request);
		}
    }
	
}
