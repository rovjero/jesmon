package es.jesmon.controller.empresa;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jesmon.controller.JesmonController;
import es.jesmon.controller.forms.EmpresaForm;
import es.jesmon.controller.forms.ResponsableForm;
import es.jesmon.controller.forms.SedeForm;
import es.jesmon.controller.incidencia.IncidenciasController;
import es.jesmon.entities.Direccion;
import es.jesmon.entities.Empresa;
import es.jesmon.entities.Responsable;
import es.jesmon.entities.Sede;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.empresa.EmpresaService;
import es.jesmon.services.estadoIncidencia.EstadoIncidenciaService;
import es.jesmon.services.estados.EstadosService;
import es.jesmon.services.incidencias.IncidenciasService;
import es.jesmon.services.mail.MailSender;
import es.jesmon.services.mensaje.MensajesService;
import es.jesmon.services.provincia.ProvinciaService;
import es.jesmon.services.responsable.ResponsableServices;
import es.jesmon.services.sedes.SedesServices;
import es.jesmon.services.tramitador.TramitadorServices;

@Controller
public class EmpresaController extends JesmonController {
	
	private static Logger logger = LoggerFactory.getLogger(IncidenciasController.class);

	@Autowired
	EstadoIncidenciaService estadoIncidenciaService;
	
	@Autowired
	EstadosService estadosService;
	
	@Autowired
	JesmonServices jesmonService;
	
	@Autowired
	IncidenciasService incidenciasService;
	
	@Autowired
	SedesServices sedesServices;
	
	@Autowired
	TramitadorServices tramitadorServices;
	
	@Autowired
	ResponsableServices responsableServices;
	
	@Autowired
	MensajesService mensajesService;
	
	@Autowired
	EmpresaService empresaServices;
	
	@Autowired
	ProvinciaService provinciaService;
	
	@Autowired
    @Qualifier("javasampleapproachMailSender")
	public MailSender mailSender;
	
	@GetMapping("/admin/empresas")
    public String getEmpresas(HttpServletRequest request) {
		try {
			//model.addAttribute("incidenciaForm", new IncidenciaForm());
	    	return procesarViewResolver("empresas", request);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/empresas")
	public String postEmpresa(HttpServletRequest request, Model model,
		@RequestParam(value = "idEmpresa", required = true) String idEmpresaStr) {
		try {
			if(StringUtils.isNotBlank(idEmpresaStr)) {
				Integer idEmpresa = new Integer(idEmpresaStr);
				CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
				criteriosBusqueda.addCriterio("idEmpresa", idEmpresa);
				criteriosBusqueda.addCriterio("sedes", "sedes", AliasBean.LEFT_JOIN);
				criteriosBusqueda.addCriterio("responsables", "responsables", AliasBean.LEFT_JOIN);
				Empresa empresa = (Empresa)jesmonService.buscarByPK(Empresa.class, "idEmpresa", idEmpresa, criteriosBusqueda);
				model.addAttribute("listaProvincias", provinciaService.getListaProvincias());
				model.addAttribute("empresa", empresa);
			}
	    	return procesarViewResolver("empresas", request);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/modificarSedesResponsables")
	public String postModificarSedesResponsable(HttpServletRequest request, Model model,
			@RequestParam(value = "idEmpresa", required = true) String idEmpresaStr) {
		try {
			Integer idEmpresa = new Integer(idEmpresaStr);
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.addCriterio("empresa", new Empresa(idEmpresa));
			List<Responsable> listaResponsables = (List<Responsable>)(List)jesmonService.getLista(criteriosBusqueda, null, Responsable.class);
			for(Responsable responsable : listaResponsables) {
				responsable.getSedes().clear();
				String[] sedesResponsable = request.getParameterValues("sedes_responsable_" + responsable.getIdResponsable());
				if(sedesResponsable != null && sedesResponsable.length > 0)
					for (String sede : sedesResponsable)
						responsable.getSedes().add(new Sede(new Integer(sede)));
			}
			
			jesmonService.modificarLista((List)listaResponsables);
			request.setAttribute("mensaje", "Sedes asignadas a los usurarios de forma correcta");
				
	    	return postEmpresa(request, model, idEmpresaStr);
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
	}
	
	@PostMapping("/admin/insertarEmpresa")
	public String insertarEmpresa(HttpServletRequest request, Model model, EmpresaForm empresaForm) {
		try {
			Empresa empresa = new Empresa();
			empresa.setDenominacion(empresaForm.getDenominacion());
			empresa.setEmail(empresaForm.getEmail());
			empresa.setTelefono(empresaForm.getTelefono());
			empresa.setNif(empresaForm.getNif());
			jesmonService.insertar(empresa);
			empresaServices.setEmpresasUsuario(getUsuarioSesion(request));
			request.setAttribute("mensaje", "Empresa insertada de forma correcta");
	    	return postEmpresa(request, model, empresa.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/modificarEmpresa")
	public String modificarEmpresa(@RequestParam(value = "idEmpresa", required = true) Integer idEmpresa,
			HttpServletRequest request, Model model, EmpresaForm empresaForm) {
		try {
			Empresa empresa = (Empresa)jesmonService.buscarByPK(Empresa.class, "idEmpresa", idEmpresa);
			empresa.setDenominacion(empresaForm.getDenominacion());
			empresa.setEmail(empresaForm.getEmail());
			empresa.setTelefono(empresaForm.getTelefono());
			empresa.setNif(empresaForm.getNif());
			jesmonService.modificar(empresa);
			empresaServices.setEmpresasUsuario(getUsuarioSesion(request));
			request.setAttribute("mensaje", "Empresa modificada de forma correcta");
	    	return postEmpresa(request, model, empresa.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/insertarSede")
	public String insertarSede(HttpServletRequest request, Model model, SedeForm sedeForm) {
		try {
			Sede sede = new Sede();
			sede.setDenominacion(sedeForm.getDenominacion());
			sede.setEmpresa(new Empresa(sedeForm.getIdEmpresa()));
			sede.setTelefono(sedeForm.getTelefono());
			if(StringUtils.isNotBlank(sedeForm.getDireccion())) {
				Direccion direccion = new Direccion();
				direccion.setDireccion(sedeForm.getDireccion());
				direccion.setCodigoPostal(sedeForm.getCodigoPostal());
				direccion.setMunicipio(sedeForm.getMunicipio());
				if(StringUtils.isNotBlank(sedeForm.getCdProvincia()))
					direccion.setProvincia(provinciaService.getProvincia(sedeForm.getCdProvincia()));
				sede.setDireccion(direccion);
			}
			
			jesmonService.insertar(sede);
			empresaServices.setEmpresasUsuario(getUsuarioSesion(request));
			request.setAttribute("mensaje", "Sede insertada de forma correcta");
	    	return postEmpresa(request, model, sedeForm.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/modificarSede")
	public String modificarSede(@RequestParam(value = "idSede", required = true) Integer idSede,
			HttpServletRequest request, Model model, SedeForm sedeForm) {
		try {
			Sede sede = (Sede)jesmonService.buscarByPK(Sede.class, "idSede", idSede);
			sede.setDenominacion(sedeForm.getDenominacion());
			sede.setEmpresa(new Empresa(sedeForm.getIdEmpresa()));
			sede.setTelefono(sedeForm.getTelefono());
			if(StringUtils.isNotBlank(sedeForm.getDireccion())) {
				Direccion direccion = new Direccion();
				direccion.setDireccion(sedeForm.getDireccion());
				direccion.setCodigoPostal(sedeForm.getCodigoPostal());
				direccion.setMunicipio(sedeForm.getMunicipio());
				if(StringUtils.isNotBlank(sedeForm.getCdProvincia()))
					direccion.setProvincia(provinciaService.getProvincia(sedeForm.getCdProvincia()));
				sede.setDireccion(direccion);
			}
			jesmonService.modificar(sede);
			empresaServices.setEmpresasUsuario(getUsuarioSesion(request));
			request.setAttribute("mensaje", "Sede modificada de forma correcta");
	    	return postEmpresa(request, model, sedeForm.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/insertarResponsable")
	public String insertarResponsable(HttpServletRequest request, Model model, ResponsableForm responsableForm) {
		try {
			Responsable responsable = new Responsable(responsableForm.getIdEmpresa());
			responsable.setActivo(1);
			responsable.setNif(responsableForm.getNif());
			responsable.setNombre(responsableForm.getNombre());
			responsable.setApellido1(responsableForm.getApellido1());
			responsable.setApellido2(responsableForm.getApellido2());
			responsable.setEmail(responsableForm.getEmail());
			responsable.setTelefono(responsableForm.getTelefono());
			responsable.setCargo(responsableForm.getCargo());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			responsable.setPassword(passwordEncoder.encode(responsableForm.getPassword()).getBytes(StandardCharsets.UTF_8));
			jesmonService.insertar(responsable);
			request.setAttribute("mensaje", "Usuario insertado de forma correcta");
	    	return postEmpresa(request, model, responsableForm.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/modificarResponsable")
	public String modificarResponsable(HttpServletRequest request, Model model, ResponsableForm responsableForm) {
		try {
			Responsable responsable = (Responsable)jesmonService.buscarByPK(Responsable.class, "idResponsable", responsableForm.getIdResponsable());
			responsable.setNif(responsableForm.getNif());
			responsable.setNombre(responsableForm.getNombre());
			responsable.setApellido1(responsableForm.getApellido1());
			responsable.setApellido2(responsableForm.getApellido2());
			responsable.setEmail(responsableForm.getEmail());
			responsable.setTelefono(responsableForm.getTelefono());
			responsable.setCargo(responsableForm.getCargo());
			BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
			responsable.setPassword(passwordEncoder.encode(responsableForm.getPassword()).getBytes(StandardCharsets.UTF_8));
			jesmonService.modificar(responsable);
			request.setAttribute("mensaje", "Usuario modificado de forma correcta");
	    	return postEmpresa(request, model, responsableForm.getIdEmpresa().toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@PostMapping("/admin/asignarSedeCentral")
	public String asignarSedeCentral(HttpServletRequest request, Model model, @RequestParam(value = "idEmpresa", required = true) Integer idEmpresa,
			@RequestParam(value = "idSedeCentral", required = false) Integer idSedeCentral) {
		try {
			Empresa empresa = (Empresa)jesmonService.buscarByPK(Empresa.class, "idEmpresa", idEmpresa);
			if(idSedeCentral == null)
				empresa.setSede(null);
			else
				empresa.setSede(new Sede(idSedeCentral));
			jesmonService.modificar(empresa);
			request.setAttribute("mensaje", "Sede central asignada de forma correcta");
	    	return postEmpresa(request, model, idEmpresa.toString());
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	
	
	
}
