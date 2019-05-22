'use strict';

app.factory('plantilleroModel', function () {
  
	var plantillero = {
			tipocertificado : null,
			cuerpocertificado : null,
			copiatemplate : null,
			tiposcertificados : null,
			caratula : null,
			valor : null,
			mostrarcampotexto : false,
			mostrarboton : 0
	};    
	
  return {
	    getPlantillero: function () {
	      return plantillero;
	    },setPlantillero: function (req) {
	    	plantillero = req;
	    },resetPlantillero: function(){
	    	plantillero.tipocertificado = null;
	    	plantillero.cuerpocertificado = null;
	    	plantillero.caratula = null;
	    	plantillero.copiatemplate = null;
	    	plantillero.valor = null;
	    }
  };

});
