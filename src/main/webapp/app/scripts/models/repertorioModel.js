'use strict';

app.factory('RepertorioModel', function () {
  
  //titulo
  var busquedaRepertorio = {
    caratula: null,
	repertorio: null,
    anoRepertorio: null,
    resultados: [],
    data: null,
    predicate: null,
    reverse: null,
    anosRepertorios: [],
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
    getBusquedaRepertorio: function () {
      return busquedaRepertorio;
    },
    setBusquedaRepertorio: function (newBusquedaRepertorio){
      busquedaRepertorio = newBusquedaRepertorio;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
