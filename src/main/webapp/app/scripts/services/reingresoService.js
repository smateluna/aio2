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
      reingresarCaratula: function (caratulaDTO, observacion, workflow, codigoExtracto, notario) {
        var paramsObj = {metodo: 'reingresarCaratula', caratulaDTO: caratulaDTO, observacion: observacion, workflow: workflow, codigoExtracto: codigoExtracto, notario: notario};

        var deferred = $q.defer();

        $http({
        	method: 'POST',
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
      reingresarCaratulaGP: function (caratulaDTO, observacion) {
          var paramsObj = {metodo: 'reingresarCaratulaGP', caratulaDTO: caratulaDTO, observacion: observacion};

          var deferred = $q.defer();

          $http({
          	method: 'POST',
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
      getListas: function (idRegistro) {
        var paramsObj = {metodo: 'getListas', idRegistro: idRegistro};

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
        },
        clonarCaratula: function (caratulaDTO, observacion) {
	        var paramsObj = {metodo: 'clonarCaratula', caratulaDTO: caratulaDTO, observacion: observacion};
	
	        var deferred = $q.defer();
	
	        $http({
	        	method: 'POST',
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
        clonarEscritura: function (caratulaOrigen, caratulaDestino) {
	        var paramsObj = {metodo: 'clonarEscritura', caratulaOrigen: caratulaOrigen, caratulaDestino: caratulaDestino};
	
	        var deferred = $q.defer();
	
	        $http({
	        	method: 'POST',
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
