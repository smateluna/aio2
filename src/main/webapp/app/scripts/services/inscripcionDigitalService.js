'use strict';

app.factory('inscripcionDigitalService', function ($http, $q) {

  return {
    getInscripcionParaDigital: function (foja, numero, ano, bis) {
      var d = new Date();
      var dateNumber = d.getTime();

      var paramsObj = {v: dateNumber,metodo: 'getInscripcionParaDigital', foja: foja, numero: numero, ano: ano, bis: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getBusquedaTitulo: function (foja, numero, ano, bis, anteriores) {
      var d = new Date(),
        dateNumber = d.getTime(),
        paramsObj = {v: dateNumber, metodo: 'getBusquedaTitulo', foja: foja, numero: numero, ano: ano, bis: bis, anteriores: anteriores};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },getInscripcion: function (foja, numero, ano, bis) {
      var d = new Date();
      var dateNumber = d.getTime();


      var paramsObj = {v: dateNumber,metodo: 'getInscripcion', foja: foja, numero: numero, ano: ano, bis: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getInscripcionSimple: function (foja, numero, ano, bis) {
      var d = new Date();
      var dateNumber = d.getTime();


      var paramsObj = {v: dateNumber,metodo: 'getInscripcionSimple', foja: foja, numero: numero, ano: ano, bis: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    obtenerBorradores: function (foja, numero, ano, bis) {
      var d = new Date();
      var dateNumber = d.getTime();

      var paramsObj = {v: dateNumber,metodo: 'obtenerBorradores', foja: foja, numero: numero, ano: ano, bis: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    obtenerCaratulasPorEstado: function (foja, numero, ano, bis, estado) {
      var d = new Date();
      var dateNumber = d.getTime();

      var paramsObj = {v: dateNumber,metodo: 'obtenerCaratulasPorEstado', foja: foja, numero: numero, ano: ano, bis: bis, estado: estado};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/inscripcionDigital',
        params: paramsObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getTiposAnotacion: function () {

      var paramsObj = 'metodo=getTiposAnotacion';

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/inscripcionDigitalService',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },getInscripcionJPG: function (foja, numero, ano, bis) {
        var paramsObj = {metodo: 'getInscripcionJPG', foja: foja, numero: numero, ano: ano, bis: bis};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/inscripcionDigital',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },getAnotacionParaRevision: function (fojaini, fojafin, ano, cuadernillo) {
        var paramsObj = {metodo: 'getAnotacionParaRevision', fojaini: fojaini, fojafin: fojafin, ano: ano, cuadernillo:cuadernillo};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/inscripcionDigital',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },actualizaEstadoAnotacion:function (anotacion) {
        var paramsObj = {metodo: 'actualizaEstadoAnotacion', anotacion: anotacion};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/inscripcionDigital',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },getResumenNotas:function (caratula,borrador,folio) {
        var paramsObj = {metodo: 'getResumenNotas', caratula:caratula,borrador:borrador,folio:folio};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/inscripcionDigital',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },existeDocumentoReg:function (foja, numero, ano, registro) {
          var paramsObj = {metodo: 'existeDocumentoReg', foja: foja, numero: numero, ano: ano, registro: registro};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/inscripcionDigital',
            params: paramsObj
          }).
            success(function(data, status, headers, config){
              deferred.resolve(data);
            }).
            error(function(data, status, headers, config){
              deferred.reject(status);
            });

          return deferred.promise;
      },getNotas:function (foja,numero,ano,bis) {
    	  var paramsObj = {metodo: 'getNotas', foja:foja,numero:numero,ano:ano,bis:bis};

    	  var deferred = $q.defer();

    	  $http({
    		  method: 'GET',
    		  url: '../do/service/inscripcionDigital',
    		  params: paramsObj,
    		  timeout: 60000
    	  }).
    	  success(function(data, status, headers, config){
    		  deferred.resolve(data);
    	  }).
    	  error(function(data, status, headers, config){
    		  deferred.reject(status);
    	  });

    	  return deferred.promise;
      },getFojaIniFojaFinCuadernillo:function (cuadernillo,ano) {
    	  var paramsObj = {metodo: 'getFojaIniFojaFinCuadernillo', cuadernillo:cuadernillo,ano:ano};

    	  var deferred = $q.defer();

    	  $http({
    		  method: 'GET',
    		  url: '../do/service/inscripcionDigital',
    		  params: paramsObj
    	  }).
    	  success(function(data, status, headers, config){
    		  deferred.resolve(data);
    	  }).
    	  error(function(data, status, headers, config){
    		  deferred.reject(status);
    	  });

    	  return deferred.promise;
      },despacharCuadernillo:function (cuadernillo, ano) {
    	  var paramsObj = {metodo: 'despacharCuadernillo', cuadernillo:cuadernillo, ano:ano};

    	  var deferred = $q.defer();

    	  $http({
    		  method: 'GET',
    		  url: '../do/service/inscripcionDigital',
    		  params: paramsObj
    	  }).
    	  success(function(data, status, headers, config){
    		  deferred.resolve(data);
    	  }).
    	  error(function(data, status, headers, config){
    		  deferred.reject(status);
    	  });

    	  return deferred.promise;
      },getDespachoCuadernillos:function (fechaIni, fechaFin) {
    	  var paramsObj = {metodo: 'getDespachoCuadernillos', fechaIni:fechaIni, fechaFin:fechaFin};

    	  var deferred = $q.defer();

    	  $http({
    		  method: 'GET',
    		  url: '../do/service/inscripcionDigital',
    		  params: paramsObj
    	  }).
    	  success(function(data, status, headers, config){
    		  deferred.resolve(data);
    	  }).
    	  error(function(data, status, headers, config){
    		  deferred.reject(status);
    	  });

    	  return deferred.promise;
      },reasignarCuadernillos:function (cuadernillos, usuario) {
    	  var paramsObj = {metodo: 'reasignarCuadernillos', cuadernillos:cuadernillos, usuario:usuario};

    	  var deferred = $q.defer();

    	  $http({
    		  method: 'GET',
    		  url: '../do/service/inscripcionDigital',
    		  params: paramsObj
    	  }).
    	  success(function(data, status, headers, config){
    		  deferred.resolve(data);
    	  }).
    	  error(function(data, status, headers, config){
    		  deferred.reject(status);
    	  });

    	  return deferred.promise;
      }
      
  };
});
