'use strict';

app.factory('desbloqueoService', function ($q, $http) {

    return {
      buscar: function (inscripcion) {
        var paramsObj = {metodo: 'buscar', inscripcion: inscripcion};
        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/desbloqueo',
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
      
      desbloquear: function (solicitud, listaSolicitudes) {
        var paramsObj = {metodo: 'desbloquear', solicitud: solicitud, listaSolicitudes: listaSolicitudes};
        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/desbloqueo',
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

      getInscripcionSesion: function (inscripcion) {
          var paramsObj = {metodo: 'getInscripcionSesion', inscripcion: inscripcion};
          var deferred = $q.defer();

          $http({
          	method: 'GET',
          	url: '../do/service/desbloqueo',
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
