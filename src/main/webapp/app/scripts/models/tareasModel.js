'use strict';

app.factory('tareasModel', function () {
  
  var busquedaTareas = {
    caratula: null,
    data: null,
    predicate: null,
    reverse: null,
    tiemporefresco:10
  };
  
  var paginacionMaster = {
	currentPage: 1,
	numPerPage: 25,
	maxSize: 10,
	filteredTodos: [],
	todos: []
  }  
  
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
    getBusquedaTareas: function () {
    	return busquedaTareas;
    },
    setBusquedaTareas: function (newBusquedaTareas){
    	busquedaTareas = newBusquedaTareas;
    },
    getPaginacionMaster: function () {
    	return paginacionMaster;
	},
	setPaginacionMaster: function (newPaginacionMaster){
		paginacionMaster = newPaginacionMaster;
	},    
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
