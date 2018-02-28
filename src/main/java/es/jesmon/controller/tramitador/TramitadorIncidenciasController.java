package es.jesmon.controller.tramitador;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.jesmon.controller.JesmonController;
import es.jesmon.controller.forms.BuscadorIncidenciasForm;
import es.jesmon.entities.Empresa;
import es.jesmon.entities.Estado;
import es.jesmon.entities.Incidencia;
import es.jesmon.entities.Responsable;
import es.jesmon.entities.Sede;
import es.jesmon.entities.Tramitador;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.estados.EstadosService;
import es.jesmon.services.responsable.ResponsableServices;
import es.jesmon.services.sedes.SedesServices;
import es.jesmon.services.util.Pager;
import es.jesmon.services.util.UtilDate;

@Controller
public class TramitadorIncidenciasController extends JesmonController {

	@Autowired
	JesmonServices jesmonService;
	
	@Autowired
	EstadosService estadosService;
	
	@Autowired
	SedesServices sedesServices;
	
	@Autowired
	ResponsableServices responsableServices;
	
	@RequestMapping(value = "/tramitador/incidencias")
	public String incidencias(Map<String, Object> model,
			@ModelAttribute("buscadorIncidenciasForm") BuscadorIncidenciasForm buscadorIncidenciasForm, 
			HttpServletRequest request,
			@RequestParam("pageSize") Optional<Integer> pageSize,
            @RequestParam("page") Optional<Integer> page,
            @RequestParam("orden") Optional<String> orden) {
		try {
			
			if(page.isPresent() || orden.isPresent()) {
				if(request.getSession().getAttribute("buscadorIncidenciasFormSesion") != null) {
					BuscadorIncidenciasForm buscadorIncidenciasFormSesion = (BuscadorIncidenciasForm)request.getSession().getAttribute("buscadorIncidenciasFormSesion");
					buscadorIncidenciasForm.setIdEstado(buscadorIncidenciasFormSesion.getIdEstado());
					BeanUtils.copyProperties(buscadorIncidenciasForm, buscadorIncidenciasFormSesion);
				}
			}
			
			
			int evalPageSize = pageSize.orElse(INITIAL_PAGE_SIZE);
	        // Evaluate page. If requested parameter is null or less than 0 (to
	        // prevent exception), return initial size. Otherwise, return value of
	        // param. decreased by 1.
			int evalPage = (page.orElse(0) < 1) ? INITIAL_PAGE : page.get() - 1;
			
			Tramitador tramitador = (Tramitador)getUsuarioSesion(request);
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.agregarAlias("sede", "sede", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("sede.empresa", "empresa", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("responsable", "responsable", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoActual", "estadoActual", AliasBean.INNER_JOIN);
			//criteriosBusqueda.agregarAlias("estadoIncidencia.estado", "estado", AliasBean.INNER_JOIN);
			criteriosBusqueda.addCriterio("sede.idSede", tramitador.getListaIdsSedes().toArray(), CriteriosBusqueda.IN);
			
			criteriosBusqueda.setNumPaginacion(evalPage + 1);
			criteriosBusqueda.setNumRegistrosPagina(evalPageSize);
			
			if(buscadorIncidenciasForm.getIdSede() != null)
				criteriosBusqueda.addCriterio("sede", new Sede(buscadorIncidenciasForm.getIdSede()));
			
			if(buscadorIncidenciasForm.getIdEmpresa() != null) {
				Empresa empresa = new Empresa(buscadorIncidenciasForm.getIdEmpresa());
				criteriosBusqueda.addCriterio("sede.empresa", empresa);
				request.setAttribute("listaSedes", sedesServices.getListaSedes(buscadorIncidenciasForm.getIdEmpresa()));
				request.setAttribute("listaResponsables", responsableServices.getListaResponsables(buscadorIncidenciasForm.getIdEmpresa()));
			}
			
			if(buscadorIncidenciasForm.getIdEstado() != null)
				criteriosBusqueda.addCriterio("estadoActual", new Estado(buscadorIncidenciasForm.getIdEstado()));

			Date fechaAltaDesde = UtilDate.stringToDate(buscadorIncidenciasForm.getFechaAltaDesde());
			if(fechaAltaDesde != null)
				criteriosBusqueda.addCriterio("fechaAlta", fechaAltaDesde, CriteriosBusqueda.MAYOR_IGUAL);
				
			Date fechaAltaHasta = UtilDate.stringToDate235959(buscadorIncidenciasForm.getFechaAltaHasta());
			if(fechaAltaHasta != null)
				criteriosBusqueda.addCriterio("fechaAlta", fechaAltaHasta, CriteriosBusqueda.MENOR_IGUAL);
			
			if(buscadorIncidenciasForm.getIdResponsable() != null)
				criteriosBusqueda.addCriterio("responsable", new Responsable(buscadorIncidenciasForm.getIdResponsable()));
				
			List<ParBean> criteriosOrdenacion = new ArrayList<>();
			if(orden.isPresent()) {
				String[] datosOrden = StringUtils.split(orden.get(), ";");
				criteriosOrdenacion.add(new ParBean(datosOrden[0], datosOrden[1]));
			}else
				criteriosOrdenacion.add(new ParBean("fechaAlta", CriteriosBusqueda.DESC));
			List<Incidencia> listaIncidencias = new ArrayList<>();
			Long numResultados = jesmonService.getNumResultados(criteriosBusqueda, Incidencia.class);
			if(numResultados > 0L)
				listaIncidencias = (List<Incidencia>)(List)jesmonService.getLista(criteriosBusqueda, criteriosOrdenacion, Incidencia.class);
			
			model.put("listaEstados", estadosService.getListaEstados());
			model.put("listaEmpresas", tramitador.getListaEmpresas());
			model.put("listaIncidencias", listaIncidencias);
			
			model.put("selectedPageSize", evalPageSize);
			if(orden.isPresent())
				model.put("orden", orden.get());
			model.put("pageSizes", PAGE_SIZES);
			model.put("numResultados", numResultados.intValue());
			Integer totalPages = (numResultados.intValue() / evalPageSize) + 1;
			Pager pager = new Pager(totalPages, evalPage, BUTTONS_TO_SHOW);
			model.put("pager", pager);
			model.put("totalPages", totalPages);
			model.put("currentPage", evalPage);
			
			request.getSession().setAttribute("buscadorIncidenciasFormSesion", buscadorIncidenciasForm);
			return procesarViewResolver("incidenciasTramitador", request);
		}
		catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
	}
	
	@RequestMapping(value = "/tramitador/consultarIncidencia")
	public String consultarIncidencia(Map<String, Object> model,
			@RequestParam("idIndicencia") String idIndicenciaStr, HttpServletRequest request) {
		try {
			Tramitador tramitador = (Tramitador)getUsuarioSesion(request);
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.agregarAlias("sede", "sede", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("sede.empresa", "empresa", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("responsable", "responsable", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoIncidencia", "estadoIncidencia", AliasBean.INNER_JOIN);
			criteriosBusqueda.agregarAlias("estadoIncidencia.estado", "estado", AliasBean.INNER_JOIN);
			criteriosBusqueda.addCriterio("sede.idSede", tramitador.getListaIdsSedes().toArray(), CriteriosBusqueda.IN);
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

	@RequestMapping(value = "/tramitador/listaSedes")
	public @ResponseBody
	List<Sede> getListaSedes(@RequestParam("idEmpresa") Integer idEmpresa) {  
		try {
			return sedesServices.getListaSedes(idEmpresa);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	@RequestMapping(value = "/tramitador/listaResponsables")
	public @ResponseBody  
	List<Responsable> getListaResponsables(@RequestParam("idEmpresa") Integer idEmpresa) {  
		try {
			return responsableServices.getListaResponsables(idEmpresa);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
