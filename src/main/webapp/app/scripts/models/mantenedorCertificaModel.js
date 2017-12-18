'use strict';

app.factory('mantenedorCertificaModel', function () {
  
  //titulo
  var busquedaUsuario = {
	seccion:null,
	perfil:null,
    resultados: [],
    data: null,
  };

  var busquedaMis = {
    resultados: [],
    data: null,
    fecha: null
  };

  var states = {
    buscar : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    urgencia : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };
    
  return {
    getBusquedaUsuario: function () {
      return busquedaUsuario;
    },
    setBusquedaUsuario: function (newBusquedaUsuario){
      busquedaUsuario = newBusquedaUsuario;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
