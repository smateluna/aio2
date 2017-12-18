'use strict';

app.factory('certificacionHipotecasModel', function () {
  
  //titulo
  var busquedaCertificacion = {
    caratula: null,
	foja: null,
    numero: null,
    ano: null,
    bis: false,
	fechaCreacion: null,
    resultados: [],
    data: null,
    predicate: null,
    reverse: null 
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
  
  var listaresumida = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
	
  return {
    getBusquedaCertificacion: function () {
      return busquedaCertificacion;
    },
    setBusquedaCertificacion: function (newBusquedaCertificacion){
      busquedaCertificacion = newBusquedaCertificacion;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    },
    getListaResumida: function () {
      return listaresumida;
    },
    setListaResumida: function (newlistaResumida){
      listaresumida = newlistaResumida;
    }
  };
});
