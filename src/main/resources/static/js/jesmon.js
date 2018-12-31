function FnBuscarSedes(){
	var idEmpresa = jQuery("#idEmpresa").val();
	//alert(idEmpresa);
	document.getElementById("idSede").options.length = 0;
	if(idEmpresa == ""){		
		jQuery("#celdaSede").hide();
	}
	else {
		var parametros = {};
		parametros["idEmpresa"] = idEmpresa;
		
		jQuery.ajax({
			url: "listaSedes",
			dataType: "json",
			data: parametros,
			type: "post",
			async: false,
			success: function(data) {
				jQuery("#celdaSede").show();
				var selectSede = jQuery("#idSede")[0];
				selectSede.options[0] = new Option ("", "");
				jQuery(data).each(function(indice, sede){
					selectSede.options[indice + 1] = new Option (sede.denominacion, sede.idSede);
				});
			},
			error: function(data) {
				alert("Error: no se ha podido cargar la lista de sedes.")
			}				
		});
	}
}

function FnBuscarResponsables(){
	var idEmpresa = jQuery("#idEmpresa").val();
	//alert(idEmpresa);
	
	document.getElementById("idResponsable").options.length = 0;
	if(idEmpresa != "") {
		var parametros = {};
		parametros["idEmpresa"] = idEmpresa;
		
		jQuery.ajax({
			url: "listaResponsables",
			dataType: "json",
			data: parametros,
			type: "post",
			async: false,
			success: function(data) {
				var select = jQuery("#idResponsable")[0];
				select.options[0] = new Option ("", "");	
				jQuery(data).each(function(indice, responsable){
					select.options[indice + 1] = new Option (responsable.nombreCompleto, responsable.idResponsable);
				});
			},
			error: function(data) {
				alert("Error: no se ha podido cargar la lista de responsables.")
			}				
		});
	}
}
	


function getValidationObject(rules, submitHandler) {
    var mySubmitHandler;
    
    if (typeof submitHandler !== 'undefined') {
        mySubmitHandler = submitHandler;
    } else {
        mySubmitHandler = function(form) {
            $(form).find('.btn').attr('disabled','disabled');
            //alert($(form).find('input[type=submit]').length);
            form.submit();
        }
    }
    
    return {
        'submitHandler': mySubmitHandler,
        'rules': rules,
        'errorClass': 'text-danger control-label-msg',
        'invalidHandler': function(form, validator) {
            if (!validator.numberOfInvalids()) {
                return;
            }
            scrollToElement($(validator.errorList[0].element).attr('id'));
        },
        'highlight': function(element) {
            $(element).closest('.form-group').addClass('has-error');
        },            
        'unhighlight': function(element) {
            $(element).closest('.form-group').removeClass('has-error');
        }
    };
}

/*
var loginFormRules = {
	    usuario: {
	        required:true,
	        maxlength:64
	    },
	    password: {
	        required:true,
	        maxlength:64
	    }
	};
*/


var solicitarCambioPasswordFormRules = {
    email: {
      required: true,
      email: true
    },
	repeatEmail: {
  		equalTo: "#email"
     }
};

var loginFormRules = {
	username: {
		required: true
	},
	password: {
		required: true
	}
};

var insertarIncidenciaFormRules = {
	idSede: {
		required: true
	},
	asunto: {
		required: true
	},
	texto: {
		required: true
	}
};


var estadoFormRules = {
	idEstado: {
		required: true
	},
	idIncidenciaEstado: {
		required: true
	}
};

var mensajeFormRules = {
	idIncidenciaMensaje: {
		required: true
	},
	texto: {
		required: true
	}
};

var cambiarPasswordFomRules = {
	passwordActual: {
      required: true,
      minlength: 6
    },
    nuevoPassword: {
      required: true,
      minlength: 6
    },
    repitaNuevoPassword: {
    	required: true,
    	minlength: 6,
  		equalTo: "#nuevoPassword"
    }
};

var empresaFomRules = {
	denominacion: {
		required: true,
		minlength: 3
	},
	nif: {
      minlength: 9
    },
    telefono: {
    	number: true,
    	minlength: 9
    },
    email: {
      required: function(element){
          return $("#emailEmpresa").val()!="";
      },
      email: true
    }
};

var sedeFomRules = {
	denominacion: {
		required: true,
		minlength: 3
	}
};

var usuarioFomRules = {
	login: {
		required: true
	},
	nombre: {
		required: true
	},
	telefono: {
    	minlength: 9,
    	number: true
	},
	password: {
		required: true,
		minlength: 6
    },
    repitaPassword: {
      required: true,
      equalTo: "#passwordResponsable",
      minlength: 6
    },
	nif: {
      minlength: 9
    },
    email: {
    	required: true,
    	email: true
    }
};


var tramitadorFomRules = {
	login: {
		required: true,
		minlength: 6
	},
	nombre: {
		required: true
	},
	password: {
      required: true,
      minlength: 6
    },
    repitaPassword: {
      required: true,
      equalTo: "#passwordTramitador",
      minlength: 6
    },
    telefono: {
    	minlength: 9,
    	number: true
	},
	nif: {
      minlength: 9
    },
    email: {
    	required: true,
    	email: true
    }
};


if (!String.prototype.startsWith) {
	  String.prototype.startsWith = function(stringBuscada, posicion) {
	    posicion = posicion || 0;
	    return this.indexOf(stringBuscada, posicion) === posicion;
	  };
	}


if (!String.prototype.trim) {
  (function() {
    // Make sure we trim BOM and NBSP
    var rtrim = /^[\s\uFEFF\xA0]+|[\s\uFEFF\xA0]+$/g;
    String.prototype.trim = function() {
      return this.replace(rtrim, '');
    };
  })();
}

if (!String.prototype.endsWith) {
  String.prototype.endsWith = function(searchString, position) {
      var subjectString = this.toString();
      if (typeof position !== 'number' || !isFinite(position) || Math.floor(position) !== position || position > subjectString.length) {
        position = subjectString.length;
      }
      position -= searchString.length;
      var lastIndex = subjectString.indexOf(searchString, position);
      return lastIndex !== -1 && lastIndex === position;
  };
}

function validarEmail(valor) {
	if ( /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,4})+$/.test(valor))
		return true;
	else
		return false;
}


function redondeo2decimales(numero) {
	if(numero == null || numero == 0)
		return "0,00";

	numero += 0.0000000000003;
	return +(Math.round(numero + "e+2") + "e-2");
}


function floatToString(numero) {
	if(numero == null || numero == undefined || numero == 0)
		return "0,00";
	
	numero = numero + "";
	numero = numero.replace(".", ",");
	
	if(numero.indexOf(",") == -1)
		numero += ",00"; 
	else if(numero.endsWith(",0"))
		numero += "0";
	return numero;
}

function toFloat(numero){
	if(numero == null || numero == undefined || numero == "")
		return 0;
	return parseFloat(numero.replace(',','.'));
}


function FnAddElementosIncendio(){
	var numElementosIncendioAnt = numElementosIncendio;
	numElementosIncendio ++;
	if(numElementosIncendio == 1)
		$('#filaElementoIncendio_1').show();
	else {
		var html = $('#filaElementoIncendio_1').html();
		html = "<tr id='filaElementoIncendio_" + numElementosIncendio + "'>" + html + "</tr>";
			//"<td><a id='enlaceEliminarElemento_" + numElementosIncendio + "' href='#' onclick='FnEliminarElemento()' ><img src='../img/icon_eliminar.gif' alt='Eliminar elemento' title='Eliminar elemento'></a></td>" +
			//"</tr>";
		html = html.replace (/_1/g , '_' + numElementosIncendio);
		$('#filaElementoIncendio_' + numElementosIncendioAnt).after(html);
	}
	$('#enlaceEliminarElemento_' + numElementosIncendioAnt).hide();
	$("#filaElementoIncendio_" + numElementosIncendio + " :text").val("");
	$("#filaElementoIncendio_" + numElementosIncendio + " select").val("");
	$("#unidades_" + numElementosIncendio)[0].focus();
	$("#numElementosIncendio").val(numElementosIncendio);
}


function FnEliminarElemento(){
	var numElementosIncendioAnt = numElementosIncendio;
	numElementosIncendio --;
	if(numElementosIncendio == 0)
		$('#filaElementoIncendio_1').hide();
	else {
		$('#filaElementoIncendio_' + numElementosIncendioAnt).remove();
		$('#enlaceEliminarElemento_' + numElementosIncendio).show();
	}
	$("#numElementosIncendio").val(numElementosIncendio);
}