package es.jesmon.entities;
// Generated 29-ene-2018 22:17:55 by Hibernate Tools 5.2.6.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;

/**
 * Tramitador generated by hbm2java
 */
@Entity
@Table(name = "tramitador", catalog = "jesmon")
public class Tramitador extends JesmonEntity implements java.io.Serializable {

	private static final long serialVersionUID = 6933726978664517149L;
	private Integer idTramitador;
	private String nif;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String email;
	private String telefono;
	private Integer activo;
	private byte[] password;
	private String login;
	private Set<Mensaje> mensajes = new HashSet<Mensaje>(0);
	private Set<Incidencia> incidencias = new HashSet<Incidencia>(0);
	private Set<EstadoIncidencia> estadoIncidencias = new HashSet<EstadoIncidencia>(0);
	private Set<Sede> sedes = new HashSet<Sede>(0);
	
	public Tramitador() {
	}

	public Tramitador(String nif, String nombre) {
		this.nif = nif;
		this.nombre = nombre;
	}

	public Tramitador(String nif, String nombre, String apellido1, String apellido2, String email, String telefono,
			Integer activo, byte[] password, Set<Mensaje> mensajes, Set<Incidencia> incidencias,
			Set<EstadoIncidencia> estadoIncidencias, Set<Sede> sedes) {
		this.nif = nif;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.email = email;
		this.telefono = telefono;
		this.activo = activo;
		this.password = password;
		this.mensajes = mensajes;
		this.incidencias = incidencias;
		this.estadoIncidencias = estadoIncidencias;
		this.sedes = sedes;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id_tramitador", unique = true, nullable = false)
	public Integer getIdTramitador() {
		return this.idTramitador;
	}

	public void setIdTramitador(Integer idTramitador) {
		this.idTramitador = idTramitador;
	}

	@Column(name = "NIF", nullable = false, length = 9)
	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	@Column(name = "nombre", nullable = false, length = 100)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "apellido1", length = 100)
	public String getApellido1() {
		return this.apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	@Column(name = "apellido2", length = 100)
	public String getApellido2() {
		return this.apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	@Column(name = "email", length = 50)
	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Column(name = "telefono", length = 9)
	public String getTelefono() {
		return this.telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	@Column(name = "activo")
	public Integer getActivo() {
		return this.activo;
	}

	public void setActivo(Integer activo) {
		this.activo = activo;
	}

	@Column(name = "password")
	public byte[] getPassword() {
		return this.password;
	}

	public void setPassword(byte[] password) {
		this.password = password;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tramitador")
	public Set<Mensaje> getMensajes() {
		return this.mensajes;
	}

	public void setMensajes(Set<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tramitador")
	public Set<Incidencia> getIncidencias() {
		return this.incidencias;
	}

	public void setIncidencias(Set<Incidencia> incidencias) {
		this.incidencias = incidencias;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tramitador")
	public Set<EstadoIncidencia> getEstadoIncidencias() {
		return this.estadoIncidencias;
	}

	public void setEstadoIncidencias(Set<EstadoIncidencia> estadoIncidencias) {
		this.estadoIncidencias = estadoIncidencias;
	}

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "tramitadors")
	public Set<Sede> getSedes() {
		return this.sedes;
	}

	public void setSedes(Set<Sede> sedes) {
		this.sedes = sedes;
	}
	
	@Transient
	public String getNombreCompleto() {
		String nombreCompleto = "";
		if(StringUtils.isNotBlank(nombre))
			nombreCompleto = nombre;
		if(StringUtils.isNotBlank(apellido1))
			nombreCompleto += " " + apellido1;
		if(StringUtils.isNotBlank(apellido2))
			nombreCompleto += " " + apellido2;
		return nombreCompleto.trim();
	}
	
	@Transient
	public Integer getId() {
		return idTramitador;
	}
	
	@Transient
	public String getNombreCampoId() {
		return "idTramitador";
	}
	
	@Column(name = "login", length = 50)
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public boolean equals(Object o) {
		if(o == null || !o.getClass().equals(Tramitador.class))
			return false;

		Tramitador tramitador = (Tramitador)o;
		return idTramitador.equals(tramitador.getIdTramitador());
	}
}
