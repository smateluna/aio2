'use strict';

app.factory('digitalHipotecasModel', function () {

  var tab = {
    parentActive: 1
  };

  //titulo
  var busquedaTitulo = {
    foja: null,
    numero: null,
    ano: null,
    bis: false
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
    mis: {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };

  var dataState = {};

  return {
    getDataState: function () {
      return dataState;
    },
    setDataState: function (datap) {
      dataState = datap;
    },
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
