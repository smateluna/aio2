'use strict';

app.factory('anotacionTipoModel', function () {

  var data = {
    usuario: '',
    tipos: []
  };

  return {
    getData: function () {
      return data;
    },
    setData: function (newData){
      data = newData;
    }
  };
});
