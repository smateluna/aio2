'use strict';

app.factory('solicitudesProhibicionesModel', function () {

  var tab = {
    parentActive: 1
  };

  //titulo
  var busquedaTitulo = {
    rut: null,
    nombre: '',
    foja: null,
    numero: null,
    ano: null,
    bis: false,
    anteriores: true,
    checkTodo: false,
    resultados: [],
    titulosSeleccionados: {},
    data: null,
    req: {
      rut: null,
      nombre: ''
    }
  };

  //rut
  var busquedaRut = {
    rut: null,
    nombre: '',
    resultados: [],
    data: null,
    fecha: null
  };

  var busquedaMis = {
    resultados: [],
    data: null,
    fecha: null
  };

  var states = {
    titulo : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    rut: {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    mis: {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };

  return {
    getTab: function () {
      return tab;
    },
    setTab: function (newTab){
      tab = newTab;
    },
    getBusquedaTitulo: function () {
      return busquedaTitulo;
    },
    setBusquedaTitulo: function (newBusquedaTitulo){
      busquedaTitulo = newBusquedaTitulo;
    },
    getBusquedaRut: function () {
      return busquedaRut;
    },
    setBusquedaRut: function (newBusquedaRut){
      busquedaRut = newBusquedaRut;
    },
    getBusquedaMis: function () {
      return busquedaMis;
    },
    setBusquedaMis: function (newBusquedaMis){
      busquedaMis = newBusquedaMis;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
