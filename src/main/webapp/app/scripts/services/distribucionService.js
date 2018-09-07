/**
 * Created by jaguileram on 18/12/2014.
 */

'use strict';

app.factory('distribucionService', function ($q, $http) {

  // Public API here
  return {
    getCaratula: function (caratula) {
      var paramsObj = {metodo: 'getCaratula', numeroCaratula: caratula};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/distribucion',
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
    getSecciones: function () {
      var paramsObj = {metodo: 'getSecciones'};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/distribucion',
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
    getResponsables: function (codSeccion) {
      var paramsObj = {metodo: 'getResponsables', codSeccion: codSeccion};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/distribucion',
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
    distribuir: function (caratulas, codSeccion, responsable) {
      var paramsObj = {metodo: 'distribuir'},
        dataObj = {caratulas: caratulas, responsable: responsable, codSeccion: codSeccion},
        deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/distribucion',
        params: paramsObj,
        data: dataObj
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getCaratulaSesion: function () {
        var paramsObj = {metodo: 'getCaratulaSesion'},
          deferred = $q.defer();

        $http({
          method: 'POST',
          url: '../do/service/distribucion',
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
