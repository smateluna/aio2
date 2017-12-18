'use strict';

app.factory('cartelesModel', function () {

  var tab = {
    parentActive: 1
  };

  var busquedaCartel = {
    numero: '',
    mes: '',
    ano: '',
    registro: '',
    bis: false
  };

  var busquedaMis = {
    resultados: [],
    data: null,
    fecha: null
  };

  var states = {
    busquedaCartel : {
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

  var estadoDocumento = {
    numero: '',
    mes: '',
    ano: '',
    registro: '',
    bis: '',
    cantidadPaginas: 0,
    hayArchivo: false,
    tipoArchivoDTO: {}
  };

  return {
    getTab: function () {
      return tab;
    },
    setTab: function (newTab){
      tab = newTab;
    },
    getEstadoDocumento: function () {
      return estadoDocumento;
    },
    setEstadoDocumento: function (newEstadoDocumento){
      estadoDocumento = newEstadoDocumento;
    },
    resetEstadoDocumento: function (){
      estadoDocumento.numero = '';
      estadoDocumento.mes = '';
      estadoDocumento.ano = '';
      estadoDocumento.registro = '';
      estadoDocumento.bis = '';
      estadoDocumento.cantidadPaginas = 0;
      estadoDocumento.hayArchivo = false;
      estadoDocumento.tipoArchivoDTO = {};
    },
    getBusquedaCartel: function () {
      return busquedaCartel;
    },
    setBusquedaCartel: function (newBusquedaCartel){
      busquedaCartel = newBusquedaCartel;
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