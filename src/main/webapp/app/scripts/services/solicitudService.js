'use strict';

app.factory('solicitudService', function ($http, $q) {

  return {
    save: function (solicitudes, rut, nombre) {
      var paramsObj = 'metodo=save&rut='+rut+'&nombre='+encodeURIComponent(nombre)+'&data='+solicitudes;

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/solicitud',
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
    },
    getByRut: function (rut) {
      var paramsObj = {metodo: 'getByRut', rut: rut};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    getByUserInSession: function () {
      var paramsObj = {metodo: 'getByUserInSession'};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    saveSingleConValidacion: function (foja, numero, ano, bis ) {
      var paramsObj = {metodo: 'saveSingleConValidacion', fojaSol: foja, numeroSol: numero, anoSol: ano, bisSol: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    saveSingle: function (foja, numero, ano, bis ) {
      var paramsObj = {metodo: 'saveSingle', fojaSol: foja, numeroSol: numero, anoSol: ano, bisSol: bis};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    actualizar: function (idSolicitud, accion) {
      var paramsObj = {metodo: 'actualizar', idSolicitud: idSolicitud, accion: accion};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    getByFojaNumeroAno: function (foja,numero,ano,bis) {
      var paramsObj = {metodo: 'getByFojaNumeroAno', foja: foja, numero:numero, ano:ano, bis:bis };

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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
    urgenciaSolicitud: function (foja,numero,ano,bis) {
      var paramsObj = {metodo: 'urgenciaSolicitud', foja: foja, numero:numero, ano:ano, bis:bis };

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/solicitud',
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

