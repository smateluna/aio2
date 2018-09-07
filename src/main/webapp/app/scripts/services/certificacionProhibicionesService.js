'use strict';

app.factory('certificacionProhibicionesService', function ($http, $q) {

    return {
      obtenerCaratulasParaCertificar: function () {
        var paramsObj = {metodo: 'obtenerCaratulasParaCertificar'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/certificacionProhibiciones',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },obtenerCaratulasParaCertificarEnParte: function () {
        var paramsObj = {metodo: 'obtenerCaratulasParaCertificarEnParte'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/certificacionProhibiciones',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },certificar: function (caratula,foja,numero,ano,bis,tipoCertificacion) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'certificar', caratula:caratula, foja: foja, numero: numero, ano:ano, bis:bis, tipoCertificacion:tipoCertificacion};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/certificacionProhibiciones',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },rehacerImagen: function (caratula,foja,numero,ano,bis,motivo) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'rehacerImagen', caratula:caratula, foja: foja, numero: numero, ano:ano, bis:bis, motivo:motivo};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/certificacionProhibiciones',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },distribuir: function (caratula,tipoCertificacion) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'distribuir', caratula:caratula, tipoCertificacion:tipoCertificacion};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/certificacionProhibiciones',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },vistaprevia: function (caratula,titulo,cuerpocertificado,prefijo,valor) {
	      
	      var paramsObj = 'metodo=vistaPrevia&caratula='+caratula+'&titulo='+encodeURIComponent(titulo)+'&cuerpocertificado='+encodeURIComponent(cuerpocertificado)+'&prefijo='+prefijo+'&valor='+valor ;

	      var deferred = $q.defer();

	      $http({
	        method: 'POST',
	        url: '../do/service/certificacionProhibiciones',
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
     },certificarvistapreviaplantilla: function (caratula,prefijo) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'certificarvistapreviaplantilla', caratula:caratula, prefijo: prefijo};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/certificacionProhibiciones',
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