package es.jesmon.controller.tramitador;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jesmon.controller.JesmonController;
import es.jesmon.controller.forms.TramitadorForm;
import es.jesmon.entities.Empresa;
import es.jesmon.entities.Sede;
import es.jesmon.entities.Tramitador;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.tramitador.TramitadorServices;

@Controller
public class TramitadorController extends JesmonController {
	
	private static Logger logger = LoggerFactory.getLogger(TramitadorController.class);
	
	@Autowired
	JesmonServices jesmonService;
	
	@Autowired
	TramitadorServices tramitadorServices;
	
	@GetMapping("/admin/tramitadores")
    public String getTramitadores(HttpServletRequest request, Model model) {
		try {
			//model.addAttribute("incidenciaForm", new IncidenciaForm());
			List<Tramitador> listaTramitadores = tramitadorServices.getListaTramitadoresEmpresa(null);
			model.addAttribute("listaTramitadores", listaTramitadores);
	    	return procesarViewResolver("tramitadores", request);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/tramitadores")
	public String postTramitadores(HttpServletRequest request, Model model,
		@RequestParam(value = "idTramitador", required = true) Integer idTramitador) {
		try {
			if(idTramitador != null) {
				CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
				criteriosBusqueda.addCriterio("idTramitador", idTramitador);
				criteriosBusqueda.addCriterio("sedes", "sedes", AliasBean.LEFT_JOIN);
				Tramitador tramitador = (Tramitador)jesmonService.buscarByPK(Tramitador.class, "idTramitador", idTramitador, criteriosBusqueda);
				model.addAttribute("tramitador", tramitador);
			}
			List<Tramitador> listaTramitadores = tramitadorServices.getListaTramitadoresEmpresa(null);
			model.addAttribute("listaTramitadores", listaTramitadores);
	    	
			CriteriosBusqueda criteriosBusquedaEmpresas = new CriteriosBusqueda();
			criteriosBusquedaEmpresas.agregarAlias("sedes", "sedes", AliasBean.LEFT_JOIN);
			criteriosBusquedaEmpresas.setUnicoResultado(true);
			List<ParBean> criteriosOrdenacionEmpresas = new ArrayList<ParBean>();
			criteriosOrdenacionEmpresas.add(new ParBean("denominacion", CriteriosBusqueda.ASC));
			List<Empresa> listaEmpresas = (List)jesmonService.getLista(criteriosBusquedaEmpresas, criteriosOrdenacionEmpresas, Empresa.class);
			model.addAttribute("listaEmpresas", listaEmpresas);
			
			return procesarViewResolver("tramitadores", request);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	
	@PostMapping("/admin/asignarSedesTramitador")
	public String asignarSedesTramitador(HttpServletRequest request, Model model,
		@RequestParam(value = "idTramitador", required = true) Integer idTramitador) {
		try {
			Tramitador tramitador = (Tramitador)jesmonService.buscarByPK(Tramitador.class, "idTramitador", idTramitador);
	    	/*
			Set<Sede> sedes = new HashSet<Sede>();
			tramitador.setSedes(sedes);
			String[] sedesTramitador = request.getParameterValues("sedes_tramitador");
			if(sedesTramitador != null)
				for(String idSede : sedesTramitador)
					sedes.add(new Sede(new Integer(idSede)));
			*/
			
			List<Sede> listaSedesTramitador = new ArrayList<Sede>();
			String[] sedesTramitador = request.getParameterValues("sedes_tramitador");
			if(sedesTramitador != null)
				for(String idSede : sedesTramitador)
					listaSedesTramitador.add(new Sede(new Integer(idSede)));			
			
			tramitador.setSedes(new HashSet<Sede>(listaSedesTramitador));
			
			CriteriosBusqueda criteriosBusquedaSedes = new CriteriosBusqueda();
			criteriosBusquedaSedes.agregarAlias("tramitadores", "tramitadores", AliasBean.LEFT_JOIN);
			List<Sede> listaSedes = (List)jesmonService.getLista(null, null, Sede.class);
			List<Sede> listaSedesModificar = new ArrayList<Sede>();
			for(Sede sede : listaSedes) {
				if(listaSedesTramitador.contains(sede) && !sede.getTramitadores().contains(tramitador)) {
					sede.getTramitadores().add(tramitador);
					listaSedesModificar.add(sede);
				}
				else if(!listaSedesTramitador.contains(sede) && sede.getTramitadores().contains(tramitador)) {
					sede.getTramitadores().remove(tramitador);
					listaSedesModificar.add(sede);
				}
			}
			
			jesmonService.modificarLista((List)listaSedesModificar);
			return postTramitadores(request, model, idTramitador);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
	}
	
	
	@PostMapping("/admin/insertarTramitador")
	public String insertarTramitador(HttpServletRequest request, Model model, TramitadorForm tramitadorForm) {
		try {
			Tramitador tramitador = new Tramitador();
			tramitador.setLogin(tramitadorForm.getLogin());
			tramitador.setActivo(1);
			tramitador.setNif(tramitadorForm.getNif());
			tramitador.setNombre(tramitadorForm.getNombre());
			tramitador.setApellido1(tramitadorForm.getApellido1());
			tramitador.setApellido2(tramitadorForm.getApellido2());
			tramitador.setEmail(tramitadorForm.getEmail());
			tramitador.setTelefono(tramitadorForm.getTelefono());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			tramitador.setPassword(passwordEncoder.encode(tramitadorForm.getPassword()).getBytes(StandardCharsets.UTF_8));
			jesmonService.insertar(tramitador);
			request.setAttribute("mensaje", "Tramitador insertado de forma correcta");
	    	return postTramitadores(request, model, tramitador.getIdTramitador());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/modificarTramitador")
	public String modificarTramitador(HttpServletRequest request, Model model, TramitadorForm tramitadorForm) {
		try {
			Tramitador tramitador = (Tramitador)jesmonService.buscarByPK(Tramitador.class, "idTramitador", tramitadorForm.getIdTramitador());
			tramitador.setLogin(tramitadorForm.getLogin());
			tramitador.setNif(tramitadorForm.getNif());
			tramitador.setNombre(tramitadorForm.getNombre());
			tramitador.setApellido1(tramitadorForm.getApellido1());
			tramitador.setApellido2(tramitadorForm.getApellido2());
			tramitador.setEmail(tramitadorForm.getEmail());
			tramitador.setTelefono(tramitadorForm.getTelefono());
			if(StringUtils.isNotBlank(tramitadorForm.getPassword())) {
				BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
				tramitador.setPassword(passwordEncoder.encode(tramitadorForm.getPassword()).getBytes(StandardCharsets.UTF_8));
			}
			jesmonService.modificar(tramitador);
			request.setAttribute("mensaje", "Tramitador modificado de forma correcta");
	    	return postTramitadores(request, model, tramitadorForm.getIdTramitador());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
}