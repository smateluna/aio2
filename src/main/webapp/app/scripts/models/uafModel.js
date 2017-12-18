'use strict';

app.factory('UAFModel', function () {
  
  var busquedaUAF = {
	anos: [],
	ano: null,
	naturaleza: [],
	naturalezas: []
  };
  
  var tab = {
    parentActive: 1
  };  
 	
  return {
    getBusquedaUAF: function () {
      return busquedaUAF;
    },getTab: function () {
      return tab;
    },
    setTab: function (newTab){
      tab = newTab;
    }
  };
});
