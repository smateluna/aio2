'use strict';

app.factory('estadoModel', function () {
  
  var caratulasPorRut = {
    rut: null,
    dv: null,
	resultado : 10
  };
  
  var requirente = {
		  nombre: null,
		  apep: null,
		  apem: null,
		  correo: null,
		  telefono: null,
		  direccion: null
  }
  
  var listaCaratulas = [];
  
  
  return {
	    getCaratulasPorRut: function () {
	      return caratulasPorRut;
	    },setCaratulasPorRut: function (req) {
	      caratulasPorRut = req;
	    },
	    getDatosRequirente: function() {
	    	return requirente;
	    },
	    setDatosRequirente: function(req) {
	    	requirente = req;
	    },
	    getListaCaratulas: function() {
	    	return listaCaratulas;
	    },
	    setListaCaratulas: function(lista) {
	    	listaCaratulas = lista;
	    }
  };

});
