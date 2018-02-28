package es.jesmon.controller.forms;

import javax.validation.constraints.NotNull;

public class IncidenciaForm {

	@NotNull(message = "El campo asunto es obligatorio")
	private String asunto;
	@NotNull(message = "El campo descripción es obligatorio")
	private String texto;
	@NotNull(message = "El campo sede es obligatorio")
	private Integer idSede;
	
	public Integer getIdSede() {
		return idSede;
	}
	public void setIdSede(Integer idSede) {
		this.idSede = idSede;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getTexto() {
		return texto;
	}
	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	
}
