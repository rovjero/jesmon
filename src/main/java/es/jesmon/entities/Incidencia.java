package es.jesmon.entities;
// Generated 29-ene-2018 22:17:55 by Hibernate Tools 5.2.6.Final

import static javax.persistence.GenerationType.IDENTITY;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

/**
 * Incidencia generated by hbm2java
 */
@Entity
@Table(name = "incidencia", catalog = "jesmon")
@DynamicUpdate(value=true)
@DynamicInsert(value=true)

public class Incidencia implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2414712195286046546L;
	private Integer idIncidencia;
	private Estado estadoActual;
	private Responsable responsable;
	private Sede sede;
	private Tramitador tramitador;
	private Date fechaAlta;
	private String asunto;
	private String texto;
	private Set<Mensaje> mensajes = new HashSet<Mensaje>(0);
	private Set<EstadoIncidencia> estadosIncidencia = new HashSet<EstadoIncidencia>(0);
	private TipoIncidencia tipoIncidencia;
	private PrioridadIncidencia prioridadIncidencia;
	
	public Incidencia() {
	}
	
	public Incidencia(Integer idIncidencia) {
		this.idIncidencia = idIncidencia;
	}

	public Incidencia(Responsable responsable, Sede sede, Date fechaAlta, String asunto) {
		this.responsable = responsable;
		this.sede = sede;
		this.fechaAlta = fechaAlta;
		this.asunto = asunto;
	}

	public Incidencia(Estado estadoActual, Responsable responsable, Sede sede, Tramitador tramitador,
			Date fechaAlta, String asunto, String texto, Set<Mensaje> mensajes) {
		this.estadoActual = estadoActual;
		this.responsable = responsable;
		this.sede = sede;
		this.tramitador = tramitador;
		this.fechaAlta = fechaAlta;
		this.asunto = asunto;
		this.texto = texto;
		this.mensajes = mensajes;
	}

	@Id
	@GeneratedValue(strategy = IDENTITY)

	@Column(name = "id_incidencia", unique = true, nullable = false)
	public Integer getIdIncidencia() {
		return this.idIncidencia;
	}

	public void setIdIncidencia(Integer idIncidencia) {
		this.idIncidencia = idIncidencia;
	}
	
	@Transient
	public String getIdIncidenciaB64() {
		return Base64.getEncoder().encodeToString(idIncidencia.toString().getBytes());
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_estado_actual")
	public Estado getEstadoActual() {
		return this.estadoActual;
	}

	public void setEstadoActual(Estado estadoActual) {
		this.estadoActual = estadoActual;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_reponsable_alta", nullable = false)
	public Responsable getResponsable() {
		return this.responsable;
	}

	public void setResponsable(Responsable responsable) {
		this.responsable = responsable;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_sede", nullable = false)
	public Sede getSede() {
		return this.sede;
	}

	public void setSede(Sede sede) {
		this.sede = sede;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_tramitador")
	public Tramitador getTramitador() {
		return this.tramitador;
	}

	public void setTramitador(Tramitador tramitador) {
		this.tramitador = tramitador;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "fecha_alta", nullable = false, length = 19)
	public Date getFechaAlta() {
		return this.fechaAlta;
	}

	public void setFechaAlta(Date fechaAlta) {
		this.fechaAlta = fechaAlta;
	}

	@Column(name = "asunto", nullable = false, length = 200)
	public String getAsunto() {
		return this.asunto;
	}

	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}

	@Column(name = "texto", length = 4000)
	public String getTexto() {
		return this.texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "incidencia")
	public Set<Mensaje> getMensajes() {
		return this.mensajes;
	}

	public void setMensajes(Set<Mensaje> mensajes) {
		this.mensajes = mensajes;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "incidencia")
	public Set<EstadoIncidencia> getEstadosIncidencia() {
		return this.estadosIncidencia;
	}

	public void setEstadosIncidencia(Set<EstadoIncidencia> estadosIncidencia) {
		this.estadosIncidencia = estadosIncidencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtipo_incidencia")
	public TipoIncidencia getTipoIncidencia() {
		return tipoIncidencia;
	}

	public void setTipoIncidencia(TipoIncidencia tipoIncidencia) {
		this.tipoIncidencia = tipoIncidencia;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idprioridad_incidencia")
	public PrioridadIncidencia getPrioridadIncidencia() {
		return prioridadIncidencia;
	}

	public void setPrioridadIncidencia(PrioridadIncidencia prioridadIncidencia) {
		this.prioridadIncidencia = prioridadIncidencia;
	}
	
	@Transient
	public List<Mensaje> getListaMensajes (){
		List<Mensaje> listaMensajes = new ArrayList<Mensaje>(mensajes);
		listaMensajes.sort(Comparator.comparing(Mensaje::getFecha).reversed());
		return listaMensajes;
	}
 	
	@Transient
	public List<EstadoIncidencia> getListaEstados (){
		List<EstadoIncidencia> listaEstados = new ArrayList<EstadoIncidencia>(estadosIncidencia);
		listaEstados.sort(Comparator.comparing(EstadoIncidencia::getFechaEstado).reversed());
		return listaEstados;
	}
	
}
