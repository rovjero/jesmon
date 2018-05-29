package es.jesmon.controller.forms;

import java.io.Serializable;

public class TramitadorForm implements Serializable {
	
	private static final long serialVersionUID = -3345912440715722860L;
	private String nif;
	private String nombre;
	private String apellido1;
	private String apellido2;
	private String email;
	private String telefono;
	private Integer activo;
	private byte[] password;
	private String login;
	private String repitaPassword;
	
	public String getNif() {
		return nif;
	}
	public void setNif(String nif) {
		this.nif = nif;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getApellido1() {
		return apellido1;
	}
	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}
	public String getApellido2() {
		return apellido2;
	}
	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public Integer getActivo() {
		return activo;
	}
	public void setActivo(Integer activo) {
		this.activo = activo;
	}
	public byte[] getPassword() {
		return password;
	}
	public void setPassword(byte[] password) {
		this.password = password;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getRepitaPassword() {
		return repitaPassword;
	}
	public void setRepitaPassword(String repitaPassword) {
		this.repitaPassword = repitaPassword;
	}
}
