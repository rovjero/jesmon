package es.jesmon.controller.contrato;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import es.jesmon.controller.JesmonController;
import es.jesmon.controller.forms.ContratoIncendioForm;
import es.jesmon.entities.ContratoIncendios;
import es.jesmon.entities.ContratoSistemaSeguridad;
import es.jesmon.entities.Direccion;
import es.jesmon.entities.Empresa;
import es.jesmon.entities.EquipoIncendios;
import es.jesmon.entities.Fichero;
import es.jesmon.entities.Sede;
import es.jesmon.repository.util.AliasBean;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.repository.util.ParBean;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.contrato.ContratoService;
import es.jesmon.services.pdf.PdfService;
import es.jesmon.services.util.UtilDate;
import es.jesmon.util.UtilJesmon;

@Controller
public class ContratoController extends JesmonController {
	
	private static Logger logger = LoggerFactory.getLogger(ContratoController.class);
	
	@Autowired
	private JesmonServices jesmonService;
	
	@Autowired
	private ContratoService contratoService;
	
	@Autowired
	private PdfService pdfService;
	
	@PostMapping("/admin/insertarContratoIncendios")
	public String insertarContratoIncendios(HttpServletRequest request, Model model, ContratoIncendioForm contratoIncendioForm) {
		try {
			ContratoIncendios contrato = new ContratoIncendios();
			contrato.setEmpresa(new Empresa(contratoIncendioForm.getIdEmpresa()));
			contrato.setNumeroContrato(contratoIncendioForm.getNumeroContrato());
			contrato.setUnidadesSistemaDeteccion(new Integer(contratoIncendioForm.getUnidadesSistemaDeteccion()));
			contrato.setPrecioUnitarioSistemaDeteccion(UtilJesmon.stringToDouble(contratoIncendioForm.getPrecioUnitarioSistemaDeteccion()));
			contrato.setUnidadesBocaIncendio(contratoIncendioForm.getUnidadesBocaIncendio());
			contrato.setPrecioUnitarioBocaIncendio(UtilJesmon.stringToDouble(contratoIncendioForm.getPrecioUnitarioBocaIncendio()));
			contrato.setUnidadesGrupoPresion(contratoIncendioForm.getUnidadesGrupoPresion());
			contrato.setPrecioUnitarioGrupoPresion(UtilJesmon.stringToDouble(contratoIncendioForm.getPrecioUnitarioGrupoPresion()));
			contrato.setUnidadesRevisionExtintor(contratoIncendioForm.getUnidadesRevisionExtintor());
			contrato.setPrecioUnitarioRevisionExtintor(UtilJesmon.stringToDouble(contratoIncendioForm.getPrecioUnitarioRevisionExtintor()));
			contrato.setUnidadesAlumbradoEmergencia(contratoIncendioForm.getUnidadesAlumbradoEmergencia());
			contrato.setPrecioUnitarioAlumbradoEmergencia(UtilJesmon.stringToDouble(contratoIncendioForm.getPrecioUnitarioAlumbradoEmergencia()));
			contrato.setTotal(UtilJesmon.stringToDouble(contratoIncendioForm.getTotal()));
			contrato.setTipoContrato(contratoIncendioForm.getTipoContrato());
			contrato.setFormaPago(contratoIncendioForm.getFormaPago());
			contrato.setFechaContrato(UtilDate.stringToDate(contratoIncendioForm.getFechaContrato()));
			
			for(int i=1; i <= contratoIncendioForm.getNumElementosIncendio(); i++) {
				EquipoIncendios equipoIncendio = new EquipoIncendios();
				String elemento = request.getParameter("elemento_" + i);
				if(StringUtils.isNotBlank(elemento)) {
					equipoIncendio.setUnidades(new Integer(request.getParameter("unidades_" + i)));
					equipoIncendio.setElemento(elemento);
					equipoIncendio.setPeso(UtilJesmon.stringToDouble(request.getParameter("peso_" + i)));
					equipoIncendio.setUbicacion(request.getParameter("ubicacion"));
					equipoIncendio.setFechaFabricacion(new Integer(request.getParameter("fechaFabricacion_" + i)));
					equipoIncendio.setFechaRetimbrado(new Integer(request.getParameter("fechaRetimbrado_" + i)));
					equipoIncendio.setNumeroSerie(request.getParameter("numeroSerie_" + i));
					contrato.getEquiposIncendios().add(equipoIncendio);
					equipoIncendio.setContratoIncendios(contrato);
				}
			}
			
			jesmonService.insertar(contrato);
			request.setAttribute("mensaje", "Contrato insertado de forma correcta");
			return "redirect:/admin/empresas?idEmpresa=" + contratoIncendioForm.getIdEmpresa() + "&";
	    }
		catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return procesarViewResolver("error", request);
			// TODO: handle exception
		}
    }
	
	@RequestMapping(value = "/admin/generarContrato", method = RequestMethod.GET)
	public void consultarFichero(
			@RequestParam(value = "idContratoIncendio", required = false) Integer idContratoIncendio,
			@RequestParam(value = "idContratoSeguridad", required = false) Integer idContratoSeguridad,
			HttpServletResponse response) {
		try {
			String numeroContrato = "";
			byte[] plantilla= ((Fichero)jesmonService.buscarByPK(Fichero.class, "idFichero", 1)).getContenidoFichero();
			List<ParBean> listaValores = new ArrayList<ParBean>();
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			if(idContratoIncendio != null) {
				criteriosBusqueda.agregarAlias("equiposIncendios", "equiposIncendios", AliasBean.LEFT_JOIN);
				criteriosBusqueda.agregarAlias("empresa", "empresa", AliasBean.INNER_JOIN);
				criteriosBusqueda.agregarAlias("empresa.sede", "sede", AliasBean.LEFT_JOIN);
				criteriosBusqueda.agregarAlias("empresa.responsable", "responsable", AliasBean.LEFT_JOIN);
				ContratoIncendios contrato = (ContratoIncendios)jesmonService.buscarByPK(ContratoIncendios.class, "idContrato", idContratoIncendio, criteriosBusqueda);
				numeroContrato = contrato.getNumeroContrato();
				listaValores.add(new ParBean("CONTRATO_NUMERO", numeroContrato));
				Empresa empresa = contrato.getEmpresa();
				if(empresa.getResponsable() != null) {
					listaValores.add(new ParBean("Nombre_Representante", empresa.getResponsable().getNombreCompleto()));
					listaValores.add(new ParBean("NIF_Representante", empresa.getResponsable().getNif()));
				}
				
				listaValores.add(new ParBean("NombreEmpresa", empresa.getDenominacion()));
				listaValores.add(new ParBean("NIFEmpresa", empresa.getNif()));
				if(empresa.getSede() != null && empresa.getSede().getDireccion() != null) {
					Direccion direccion = empresa.getSede().getDireccion();
					listaValores.add(new ParBean("Domicilio_cliente", direccion.getDireccion()));
					if(direccion.getProvincia() != null)
						listaValores.add(new ParBean("ProvinciaDomicilio", direccion.getProvincia().getDenominacion()));
					listaValores.add(new ParBean("Codigo_Postal", direccion.getCodigoPostal()));
				}
				listaValores.add(new ParBean("Unidades1", contrato.getUnidadesSistemaDeteccion().toString()));
				listaValores.add(new ParBean("PRECIO1", UtilJesmon.doubleToString(contrato.getPrecioUnitarioSistemaDeteccion())));
				listaValores.add(new ParBean("Unidades2", contrato.getUnidadesBocaIncendio().toString()));
				listaValores.add(new ParBean("PRECIO2", UtilJesmon.doubleToString(contrato.getPrecioUnitarioBocaIncendio())));
				listaValores.add(new ParBean("Unidades3", contrato.getUnidadesGrupoPresion().toString()));
				listaValores.add(new ParBean("PRECIO3", UtilJesmon.doubleToString(contrato.getPrecioUnitarioGrupoPresion())));
				listaValores.add(new ParBean("Unidades4", contrato.getUnidadesRevisionExtintor().toString()));
				listaValores.add(new ParBean("PRECIO4", UtilJesmon.doubleToString(contrato.getPrecioUnitarioRevisionExtintor())));
				listaValores.add(new ParBean("Unidades5", contrato.getUnidadesAlumbradoEmergencia().toString()));
				listaValores.add(new ParBean("PRECIO5", UtilJesmon.doubleToString(contrato.getPrecioUnitarioAlumbradoEmergencia())));
				listaValores.add(new ParBean("TOTAL", UtilJesmon.doubleToString(contrato.getTotal())));
				
				if(contrato.getTipoContrato().equals(ContratoIncendios.TIPO_CONTRATO_INSTALACION))
					listaValores.add(new ParBean("INSTALACION", "SI"));
				else
					listaValores.add(new ParBean("MANTENIMIENTO", "SI"));
				listaValores.add(new ParBean("FORMA_PAGO", contrato.getFormaPago()));
				listaValores.add(new ParBean("IBAN_1", empresa.getIban1()));
				listaValores.add(new ParBean("IBAN_2", empresa.getIban2()));
				listaValores.add(new ParBean("IBAN_3", empresa.getIban3()));
				listaValores.add(new ParBean("IBAN_4", empresa.getIban4()));
				listaValores.add(new ParBean("IBAN_5", empresa.getIban5()));
				listaValores.add(new ParBean("IBAN_6", empresa.getIban6()));
				List<EquipoIncendios> listaEquiposIncendios = contrato.getListaEquiposIncendios();
				for(int i=0; i < listaEquiposIncendios.size() ; i ++) {
					EquipoIncendios equipo = listaEquiposIncendios.get(i);
					listaValores.add(new ParBean("UNIDADES_" + i, equipo.getUnidades().toString()));
					listaValores.add(new ParBean("ELEMENTO_" + i, equipo.getElemento()));
					listaValores.add(new ParBean("PESO_" + i, UtilJesmon.doubleToString(equipo.getPeso())));
					listaValores.add(new ParBean("UBICACION_" + i, equipo.getUbicacion()));
					listaValores.add(new ParBean("FECHA_FABRICACION_" + i, equipo.getFechaFabricacion().toString()));
					listaValores.add(new ParBean("FECHA_RETIMBRADO_" + i, equipo.getFechaRetimbrado().toString()));
					listaValores.add(new ParBean("NUM_SERIE_" + i, equipo.getNumeroSerie()));
				}
			}
			else {
				
			}
			
			byte[] pdf = pdfService.generarPDF(plantilla, listaValores);
			response.setContentLength(pdf.length);
			response.setHeader("Content-Disposition", "attachment; filename=\"contrato" + numeroContrato + ".pdf\"");
			response.setContentType("application/x-document");
			ServletOutputStream servletOutputStream = response.getOutputStream();
			servletOutputStream.write(pdf);
			servletOutputStream.flush();
			servletOutputStream.close();
		}
    	catch (Exception e) {
    		e.printStackTrace();
    		logger.error(e.getMessage(), e);
		}
    }
	
	@RequestMapping(value = "/admin/consultarContratoIncedios")
	public @ResponseBody ContratoIncendios consultarContratoIncedios(@RequestParam(value = "idContrato", required = true) Integer idContrato,  HttpServletResponse response) {  
		try {
			CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
			criteriosBusqueda.agregarAlias("equiposIncendios", "equiposIncendios", AliasBean.LEFT_JOIN);
			ContratoIncendios contratoIncendios = (ContratoIncendios)jesmonService.buscarByPK(ContratoIncendios.class, "idContrato", idContrato, criteriosBusqueda);
			jesmonService.limpiarObjetoSesion(contratoIncendios);
			contratoIncendios.setEmpresa(null);
			return contratoIncendios;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}
	
	@RequestMapping(value = "/admin/consultarContratoSeguridad")
	public @ResponseBody ContratoSistemaSeguridad consultarContratoSeguridad(@RequestParam(value = "idContrato", required = true) Integer idContrato,  HttpServletResponse response) {  
		try {
			ContratoSistemaSeguridad contratoSeguridad = (ContratoSistemaSeguridad)jesmonService.buscarByPK(ContratoSistemaSeguridad.class, "idContrato", idContrato);
			return contratoSeguridad;
		}
		catch (Exception e) {
			logger.error(e.getMessage(), e);
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return null;
		}
	}

}
