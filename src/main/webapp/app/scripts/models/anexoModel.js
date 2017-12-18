'use strict';

app.factory('anexoModel', function () {

  //anexo
  var anexo = {
	id: null,
    nombre: '',
    apPaterno: '',
    apMaterno: '',
    codSeccion: '',
    anexo: '',
    piso: '',
    correo: ''
  };

  return {
    getAnexo: function () {
      return anexo;
    },
    setAnexo: function (newAnexo){
      anexo = newAnexo;
    }
  };
});
