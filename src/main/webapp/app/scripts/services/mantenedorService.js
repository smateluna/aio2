'use strict';

app.factory('mantenedorService', function ($http, $q) {

    return {
      obtenerUsuarios: function (seccion) {
        var paramsObj = {metodo: 'obtenerUsuarios', seccion:seccion};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/mantenedor',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },cambiaEstadoUsuario: function (nombreUsuario,estado,perfil) {
        var paramsObj = {metodo: 'cambiaEstadoUsuario', nombreUsuario:nombreUsuario,estado:estado,perfil:perfil};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/mantenedor',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },redistribuirCaratulas: function (nombreUsuario,perfil) {
        var paramsObj = {metodo: 'redistribuirCaratulas', nombreUsuario:nombreUsuario,perfil:perfil};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/mantenedor',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },getCtasCtes: function () {
          var paramsObj = {metodo: 'getCtasCtes'};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/mantenedor',
            params: paramsObj
          }).
            success(function(data, status, headers, config){
              deferred.resolve(data);
            }).
            error(function(data, status, headers, config){
              deferred.reject(status);
            });

          return deferred.promise;
        },guardarCtaCte: function (ctaCte) {
	        var paramsObj = 'metodo=guardarCtaCte&ctaCte='+ctaCte;
	
	        var deferred = $q.defer();
	
	        $http({
	          method: 'POST',
	          url: '../do/service/mantenedor',
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
      }
    };
  });
