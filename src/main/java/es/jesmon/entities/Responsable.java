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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Responsable generated by hbm2java
 */
@Entity
@Table(name = "responsable", catalog = "jesmon")
@DynamicUpdate(value=true)
@DynamicInsert(value=true)

public class Responsable extends JesmonEntity implements java.io.Serializable {

	private static final long serialVersionUID = 6380648932882774857L;
	private Integer idResponsable;
	private Empresa empresa;
	private String nif;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String email;
	private String telefono;
	private String cargo;
	private Integer activo;
	private byte[] password;
	private Set<Empresa> empresas = new HashSet<Empresa>(0);
	private Set<Mensaje> mensajes = new HashSet<Mensaje>(0);
	private Set<Incidencia> incidencias = new HashSet<Incidencia>(0);
	private Set<Sede> sedes = new HashSet<Sede>(0);
	private Set<EstadoIncidencia> estadoIncidencias = new HashSet<EstadoIncidencia>(0);

	public Responsable() {
	}
	
	public Responsable(Integer idResponsable) {
		this.idResponsable = idResponsable;
	}

	public Responsable(Empresa empresa, String nif, String nombre, String apellido1, String apellido2, String email,
			String telefono, String cargo, Integer activo, byte[] password, Set<Empresa> empresas,
			Set<Mensaje> mensajes, Set<Incidencia> incidencias, Set<Sede> sedes,
			Set<EstadoIncidencia> estadoIncidencias) {
		this.empresa = empresa;
		this.nif = nif;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
		this.email = email;
		this.telefono = telefono;
		this.cargo = cargo;
		this.activo = activo;
		this.password = password;
		this.empresas = empresas;
		this.mensajes = mensajes;
		this.incidencias = incidencias;
		this.sedes = sedes;
		this.estadoIncidencias = estadoIncidencias;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id_responsable", unique = true, nullable = false)
	public Integer getIdResponsable() {
		return this.idResponsable;
	}

	public void setIdResponsable(Integer idResponsable) {
		this.idResponsable = idResponsable;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_empresa")
	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	@Column(name = "NIF", length = 9)
	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	@Column(name = "nombre", length = 150)
	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Column(name = "apellido1", length = 150)
	public String getApellido1() {
		return this.apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	@Column(name = "apellido2", length = 45)
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

	@Column(name = "cargo", length = 45)
	public String getCargo() {
		return this.cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
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

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "responsable")
	public Set<Empresa> getEmpresas() {
		return this.empresas;
	}

	public void setEmpresas(Set<Empresa> empresas) {
		this.empresas = empresas;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "responsable")
	public Set<Mensaje> getMensajes() {
		return this.mensajes;
	}

	public void setMensajes(Set<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "responsable")
	public Set<Incidencia> getIncidencias() {
		return this.incidencias;
	}

	public void setIncidencias(Set<Incidencia> incidencias) {
		this.incidencias = incidencias;
	}

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rel_responsable_sede", catalog = "jesmon", joinColumns = {
			@JoinColumn(name = "id_responsable", nullable = false, updatable = false) }, inverseJoinColumns = {
					@JoinColumn(name = "id_sede", nullable = false, updatable = false) })
	public Set<Sede> getSedes() {
		return this.sedes;
	}

	public void setSedes(Set<Sede> sedes) {
		this.sedes = sedes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "responsable")
	public Set<EstadoIncidencia> getEstadoIncidencias() {
		return this.estadoIncidencias;
	}

	public void setEstadoIncidencias(Set<EstadoIncidencia> estadoIncidencias) {
		this.estadoIncidencias = estadoIncidencias;
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
		return idResponsable;
	}
	
	@Transient
	public String getNombreCampoId() {
		return "idResponsable";
	}

}
