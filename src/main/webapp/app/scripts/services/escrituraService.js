'use strict';

app.factory('escrituraService', function ($http, $q) {

    return {
      obtenerEscrituraPorInscripcion: function (caratula) {
        var paramsObj = {metodo: 'obtenerEscrituraPorInscripcion', caratula:caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/escritura',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },verDocumento: function (url,type) {
        var paramsObj = {metodo: 'verDocumento', url:url,type:type};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/escritura',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },eliminarDocumento: function (idDocumento,caratula,version) {
        var paramsObj = {metodo: 'eliminarDocumento', idDocumento:idDocumento, caratula:caratula, version:version};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/escritura',
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


