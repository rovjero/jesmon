function getValidationObject(rules, submitHandler) {
    var mySubmitHandler;
    
    if (typeof submitHandler !== 'undefined') {
        mySubmitHandler = submitHandler;
    } else {
        mySubmitHandler = function(form) {
            $(form).find('button[type=submit]').attr('disabled','disabled');
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
	rules: {
	    email: {
	      required: true,
	      email: true
	    },
		repeatEmail: {
	  		equalTo: "#email"
	     }
	  }
};


var loginFormRules = {
	rules: {
		username: {
			required: true
		},
		password: {
			required: true
		}
	}
};