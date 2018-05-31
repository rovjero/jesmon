function FnBuscarSedes(){
	var idEmpresa = jQuery("#idEmpresa").val();
	//alert(idEmpresa);
	if(idEmpresa == ""){
		document.getElementById("idSede").options.length = 0;
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
	if(idEmpresa == "")
		document.getElementById("idResponsable").options.length = 0;
	else {
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