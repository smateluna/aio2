'use strict';

app.factory('urgenciaModel', function () {
  
  //titulo
  var busquedaTitulo = {
    foja: null,
    numero: null,
    ano: null,
    bis: false,
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
    getBusquedaTitulo: function () {
      return busquedaTitulo;
    },
    setBusquedaTitulo: function (newBusquedaTitulo){
      busquedaTitulo = newBusquedaTitulo;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
