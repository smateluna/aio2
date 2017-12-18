'use strict';

app.factory('tareasModel', function () {
  
  var busquedaTareas = {
    caratula: null,
    data: null,
    predicate: null,
    reverse: null,
    tiemporefresco:2
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
    getBusquedaTareas: function () {
      return busquedaTareas;
    },
    setBusquedaTareas: function (newBusquedaTareas){
      busquedaTareas = newBusquedaTareas;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
