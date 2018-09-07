'use strict';

app.factory('borradorService', function ($http, $q) {

    return {
      obtenerCantidadBorradores: function (foja,numero,ano) {
        var paramsObj = {metodo: 'obtenerCantidadBorradores', foja:foja, numero:numero, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/borradores',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },obtenerBorradores: function (foja,numero,ano) {
        var paramsObj = {metodo: 'obtenerBorradores', foja:foja, numero:numero, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/borradores',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },obtenerTitulosAnteriores: function (foja,numero,ano,bis) {
        var paramsObj = {metodo: 'obtenerTitulosAnteriores', foja:foja, numero:numero, ano:ano, bis:bis};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/borradores',
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
