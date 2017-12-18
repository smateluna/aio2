'use strict';

app.factory('revisionNotasModel', function () {
  
  //titulo
  var busquedaRevisionNotas = {
	cuadernillo: null,
	resultado_cuadernillo: null,
    fojaini: null,
    fojafin: null,
    ano: null,
    anos: [],
    button_clicked:false,
    resultados: [],
    tieneDespacho: false,
    data: null,
    selectedRow: null,
    indicebusqueda: false
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
  
  var listaPropiedad = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
  
  return {
    getBusquedaRevisionNotas: function () {
      return busquedaRevisionNotas;
    },
    setBusquedaRevisionNotas: function (newBusquedaRevisionNotas){
      busquedaRevisionNotas = newBusquedaRevisionNotas;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    },
    getListaPropiedad: function () {
      return listaPropiedad;
    },
    setListaPropiedad: function (newlistaPropiedad){
      listaPropiedad = newlistaPropiedad;
    }
  };
});
