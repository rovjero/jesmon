package es.jesmon.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.jesmon.config.JesmonConstantes;
import es.jesmon.controller.forms.CambiarPasswordForm;
import es.jesmon.entities.JesmonEntity;
import es.jesmon.entities.Responsable;
import es.jesmon.entities.Tramitador;
import es.jesmon.repository.ResponsableRepository;
import es.jesmon.repository.TramitadorRepository;
import es.jesmon.repository.util.CriteriosBusqueda;
import es.jesmon.services.JesmonServices;
import es.jesmon.services.mail.MailSender;

@Controller
public class DefaultController extends JesmonController{

	@Autowired
	JesmonServices jesmonServices;
	
	@Autowired
	ResponsableRepository responsableRepository;
	
	@Autowired
	TramitadorRepository tramitadorRepository;
	
	@Autowired
    @Qualifier("javasampleapproachMailSender")
	public MailSender mailSender;
	
	@GetMapping("/login/login")
	public String login(HttpServletRequest request) {
	    return procesarViewResolver("login", request);
	}
	    
    @GetMapping("/")
    public String raiz(HttpServletRequest request) {
    	if(request.isUserInRole(JesmonConstantes.ROLE_TRAMITADOR))
    		return "redirect:/tramitador/incidencias.html";
    	else if(request.isUserInRole(JesmonConstantes.ROLE_CLIENTE))
    		return "redirect:/cliente/incidencias.html";
    	return "redirect:/login/login.html";
    }
    
    @GetMapping("/error")
    public String error(HttpServletRequest request) {
    	return "redirect:/error/error.html";
	}
    
    @GetMapping("/error/error")
    public String errorError(HttpServletRequest request) {
	    return procesarViewResolver("error", request);
	}
    
    @GetMapping("/error/403")
    public String error403(HttpServletRequest request) {
        return procesarViewResolver("/error/403", request);
    }

    @RequestMapping(value = "/login/solicitarCambioPassword")
    public String solicitarCambioPassword(@RequestParam (value="email", required=true)String email, 
    		@RequestParam (value="repeatEmail", required=true)String repeatEmail,
    		HttpServletRequest request)
    {
    	try {
    		if(StringUtils.isBlank(email) || !StringUtils.equals(email, repeatEmail)) {
    			return "redirect:/login/login?errorLogin=" + URLEncoder.encode("Error. El campo email es vacio o no coinciden los valores de los campos email", "UTF-8") + "&";
    		}
    		
	    	CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
	    	criteriosBusqueda.addCriterio("email", email);
	    	criteriosBusqueda.addCriterio("activo", 1);
	    	
	    	Long numResultados = jesmonServices.getNumResultados(criteriosBusqueda, Responsable.class);
	    	if(numResultados.equals(0L)) {
	    		numResultados = jesmonServices.getNumResultados(criteriosBusqueda, Tramitador.class);
	    	}
	    	
	    	if(numResultados > 0) {
	    		//String from = "rovejero@gmail.com";
	    		String subject = "Reestablecer contraseña en Jesmon Seguridad";
	    		String token = DigestUtils.sha512Hex(email + JesmonConstantes.CLAVE_FIRMA) + "|" + email;
	    		
	    		String enlace = getUrlAplicacion(request) + "/login/resetearPassword?token=" + new String(Base64.getEncoder().encode(token.getBytes()));
	    		String body = "<div>Puede reestablecer su contraseña pinchando <a href='" + enlace + "'>aquí</a></div>";
	    		
	    		mailSender.sendMail(email, subject, body);
	    		return "redirect:/login/login.html?mensaje=" + URLEncoder.encode("Correo para reestablecer la contraseña enviado de forma correcta", "UTF-8") + "&";
	    	}
	    	return "redirect:/login/login?errorLogin=" + URLEncoder.encode("Error. No se ha encontrado ningún usuario con el email indicado: " + email, "UTF-8") + "&";
    	}
    	catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
    }
    
    @GetMapping("/login/resetearPassword")
    public String getResetearPassword(HttpServletRequest request) {
    	String token = request.getParameter("token");
    	token = new String(Base64.getDecoder().decode(token.getBytes()));

    	String[] partes = StringUtils.split(token, "|");
    	String email = partes[1];
    	String firmaRequest = partes[0];
    	
    	String firma = DigestUtils.sha512Hex(email + JesmonConstantes.CLAVE_FIRMA);
    	if(StringUtils.equals(firmaRequest, firma)){
    		request.getSession().setAttribute("email", email);
    		return procesarViewResolver("resetearPassword", request);
    	}
    	return procesarViewResolver("error", request);
    }
    
    @PostMapping("/login/resetearPassword")
    public String postResetearPassword(
    	@RequestParam (value="password", required=true)String password, 
		@RequestParam (value="repeatPassword", required=true)String repeatPassword,
		HttpServletRequest request) {
		try {
			if(StringUtils.isBlank(password) || !StringUtils.equals(password, repeatPassword)) {
				request.setAttribute("error", "El campo contraseña es vacio o no coinciden las dos contraseñas introducidas");
				return procesarViewResolver("resetearPassword", request);
    		}
			
			String email = request.getSession().getAttribute("email").toString();
	    	CriteriosBusqueda criteriosBusqueda = new CriteriosBusqueda();
	    	criteriosBusqueda.addCriterio("email", email);
	    	criteriosBusqueda.addCriterio("activo", 1);
	    	
	    	BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    	
	    	List<Responsable> listaResponsables = (List<Responsable>)(List) jesmonServices.getLista(criteriosBusqueda, null, Responsable.class);
	    	if(!listaResponsables.isEmpty()) {
	    		Responsable responsable = listaResponsables.get(0);
	    		responsable.setPassword(passwordEncoder.encode(password).getBytes(StandardCharsets.UTF_8));
	    		responsableRepository.save(responsable);
	    		//return procesarViewResolver("login", request);
	    		return "redirect:/login/login.html?mensaje=" + URLEncoder.encode("Contraseña reestablecida de forma correcta", "UTF-8") + "&";
	    	}
	    	else {
	    		List<Tramitador> listaTramitadores = (List<Tramitador>)(List) jesmonServices.getLista(criteriosBusqueda, null, Tramitador.class);
		    	if(!listaTramitadores.isEmpty()) {
		    		Tramitador tramitador = listaTramitadores.get(0);
		    		tramitador.setPassword(passwordEncoder.encode(password).getBytes(StandardCharsets.UTF_8));
		    		tramitadorRepository.save(tramitador);
		    		//return procesarViewResolver("login", request);
		    		return "redirect:/login/login.html?mensaje=" + URLEncoder.encode("Contraseña reestablecida de forma correcta", "UTF-8") + "&";
		    	}
	    	}
	    	return procesarViewResolver("error", request);
	    }
		catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
    }
    
    @PostMapping("/login/cambiarPassword")
    public String cambiarPassword(@Valid CambiarPasswordForm cambiarPasswordForm, BindingResult bindingResult, HttpServletRequest request) {
    	try {
	        if (bindingResult.hasErrors()) {
	            return procesarViewResolver("consultarPerfil", request);
	        }
	        
	        String error = null;
	        if(StringUtils.isBlank(cambiarPasswordForm.getNuevoPassword()))
	        	error = "El campo nuevo password no pu";
	        	
	        	
	        JesmonEntity usuarioSesion = getUsuarioSesion(request);
	        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	        JesmonEntity usuario = (JesmonEntity)jesmonServices.buscarByPK(usuarioSesion.getClass(), usuarioSesion.getNombreCampoId(), usuarioSesion.getId());
	        usuario.setPassword(passwordEncoder.encode(cambiarPasswordForm.getNuevoPassword()).getBytes(StandardCharsets.UTF_8));
	        //responsable.setPassword(responsable.setPassword(passwordEncoder.encode(password).getBytes(StandardCharsets.UTF_8));/
	        jesmonServices.modificar(usuario);
	        request.setAttribute("mensaje", "Contraseña cambiada de forma correcta");
	        return procesarViewResolver("consultarPerfil", request);
    	}
		catch (Exception e) {
			e.printStackTrace();
			return procesarViewResolver("error", request);
		}
    }
    
    
    public static void main(String[] args) {
    	try {
    		String email = "rovejero@gmail.com"; 
    		String token = DigestUtils.sha512Hex(email + JesmonConstantes.CLAVE_FIRMA) + "|" + email;
    		token =  new String(Base64.getEncoder().encode(token.getBytes()));
    		System.out.println(token);
    		token =  new String(Base64.getDecoder().decode(token.getBytes()));
    		System.out.println(token);
    	}
    	catch (Exception e) {
			e.printStackTrace();
		}
    }
}
