'use strict';

app.factory('gponlineModel', function () {

  var tab = {
    parentActive: 1
  };
  
  var mostrar = {
    mostrarInactivosProp: false,
    mostrarInactivosProh: false,
    mostrarInactivosHipo: false
  };

  var busquedaGponline = {
    borrador: null,
    folio: null,
    fecha: null,
    anos: null,
    fechaUltimoGP: null,
    codigoAlpha: null,
    gp: [],
    eventos: [],
    numeroEventos: null,
    datosPropiedad: null,
    duenyos: [],
    hipoteca: [],
    prohibicion: [],
    roles: [],
    folios: [],
    data: null,
    res: null,
    urls: [],
    tieneNoVigenteProp: false,
    tieneNoVigenteProh: false,
    tieneNoVigenteHipo: false,
    listaCaratulaPorPropiedad: []
  };

  var busquedaCaratula = {
    caratula: null,
    data: null
  };

  var busquedaInscripcion = {
	fojas: null,
	numero: null,
	ano: null,
	registro: "PROP",
    data: null
  };
  
  var busquedaPlanos = {
	tienePlanos: null,  
    data: null
  };

  var states = {
    borrador : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    caratula: {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    inscripcion: {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };

  return {
    getMostrar: function () {
      return mostrar;
    },
    setMostrar: function (newMostrar){
      mostrar = newMostrar;
    },
    getTab: function () {
      return tab;
    },
    setTab: function (newTab){
      tab = newTab;
    },
    getBusquedaGponline: function () {
      return busquedaGponline;
    },
    setBusquedaGponline: function (newBusquedaGponline){
      busquedaGponline = newBusquedaGponline;
    },
    getBusquedaCaratula: function () {
      return busquedaCaratula;
    },
    setBusquedaCaratula: function (newBusquedaCaratula){
      busquedaCaratula = newBusquedaCaratula;
    },
    getBusquedaInscripcion: function () {
      return busquedaInscripcion;
    },
    setBusquedaInscripcion: function (newBusquedaInscripcion){
      busquedaInscripcion = newBusquedaInscripcion;
    },
    getBusquedaPlanos: function () {
      return busquedaPlanos;
    },
    setBusquedaPlanos: function (newBusquedaPlanos){
      busquedaPlanos = newBusquedaPlanos;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
