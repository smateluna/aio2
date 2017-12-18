'use strict';

app.factory('reingresoService', function ($q, $http) {

    return {
      getCaratula: function (caratula) {
        var paramsObj = {metodo: 'buscarCaratula', caratula: caratula};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/reingreso',
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
      reingresarCaratula: function (caratulaDTO, caratulaOriginalDTO, observacion, workflow, codigoExtracto, notario) {
        var paramsObj = {metodo: 'reingresarCaratula', caratulaDTO: caratulaDTO, caratulaOriginalDTO: caratulaOriginalDTO, 
        		observacion: observacion, workflow: workflow, codigoExtracto: codigoExtracto, notario: notario};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/reingreso',
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
      getCaratulaSesion: function () {
        var paramsObj = {metodo: 'getCaratulaSesion'};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/reingreso',
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
      getListas: function (caratulaDTO) {
        var paramsObj = {metodo: 'getListas', caratulaDTO: caratulaDTO};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/reingreso',
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
      getListaNotarios: function (query) {
          var paramsObj = {metodo: 'getListaNotarios', query: query};

          var deferred = $q.defer();

          $http({
          	method: 'GET',
          	url: '../do/service/reingreso',
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
      ,
      getListaNotariosFull: function () {
          var paramsObj = {metodo: 'getListaNotariosFull'};

          var deferred = $q.defer();

          $http({
          	method: 'GET',
          	url: '../do/service/reingreso',
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
