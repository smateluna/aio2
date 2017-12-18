'use strict';

app.factory('masInfoindiceModel', function () {
  
  //titulo
  var busquedaBorrador = {
    borradores: [],
    caratulasEnProceso: [],
    caratulasTerminadas: [],
    planos: [],
    titulosAnteriores: [],
    data: null,
  };

  var states = {
    buscar : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };
  
  return {
    getBusquedaBorrador: function () {
      return busquedaBorrador;
    },
    setBusquedaBorrador: function (newBusquedaBorrador){
      busquedaBorrador = newBusquedaBorrador;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    }
  };
});
