'use strict';

app.factory('oficioModel', function () {
  
  //titulo
  var busquedaOficio = {
    caratula: null,
    estado: "",
    institucion: null,
    requirente: null,
    materia: null,
    rol: null,
    oficio: null,
    identificador: null,
    fechaEntrega: null,
    fechaCreacion: null,
    button_clicked:false,
    resultados: [],
    data: null,
    predicate: null,
    reverse: null,
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
  
  var listaOficio = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
  
  return {
    getBusquedaOficio: function () {
      return busquedaOficio;
    },
    setBusquedaOficio: function (newBusquedaOficio){
      busquedaOficio = newBusquedaOficio;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    },
    getListaOficio: function () {
      return listaOficio;
    },
    setListaOficio: function (newlistaOficio){
      listaOficio = newlistaOficio;
    }
  };
});
