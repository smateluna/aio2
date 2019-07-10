'use strict';

app.factory('certificacionModel', function () {
  
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
    reverse: null ,
    tiemporefresco:10
  };
  
  var paginacionMaster = {
	currentPage: 1,
	numPerPage: 25,
	maxSize: 10,
	filteredTodos: [],
	todos: []
  } 

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
    getPaginacionMaster: function () {
    	return paginacionMaster;
	},
	setPaginacionMaster: function (newPaginacionMaster){
		paginacionMaster = newPaginacionMaster;
	},     
    getListaResumida: function () {
      return listaresumida;
    },
    setListaResumida: function (newlistaResumida){
      listaresumida = newlistaResumida;
    }
  };
});
