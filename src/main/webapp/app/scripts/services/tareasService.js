'use strict';

app.factory('tareasService', function ($http, $q) {

    return {
      obtenerCaratulasLiquidacionPorUsuario: function () {
        var paramsObj = {metodo: 'getCaratulasLiquidacionPorUsuario'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/tareas',
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
      obtenerCaratulasPorUsuario: function () {
          var paramsObj = {metodo: 'getCaratulasPorUsuario'};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/tareas',
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
      buscarTransaccion: function (idTransaccion) {
          var paramsObj = {metodo: 'buscarTransaccion', idTransaccion: idTransaccion};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/tareas',
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
	    getCaratulaLiquidacion: function (numeroCaratula) {
	        var paramsObj = {metodo: 'getCaratulaLiquidacion', numeroCaratula: numeroCaratula};
	
	        var deferred = $q.defer();
	
	        $http({
	          method: 'GET',
	          url: '../do/service/tareas',
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
	      aprobarCaratula: function (numeroCaratula, canal, papeles, total, actualizarCierre,seccionDestino) {
	    	var paramsObj = 'metodo=aprobarCaratula&numeroCaratula='+numeroCaratula+'&canal='+canal+'&papeles='+papeles+'&total='+total+'&actualizarCierre='+actualizarCierre+'&seccionDestino='+seccionDestino;
	
	        var deferred = $q.defer();

	        $http({
	            method: 'POST',
		        url: '../do/service/tareas',
	            //dataType: 'json',
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
      	 buscarDocumento: function (idNotario,codDocumento,origen,numeroCaratula) {
          var paramsObj = {metodo: 'buscarDocumento', idNotario:idNotario,codDocumento:codDocumento,origen:origen,numeroCaratula:numeroCaratula};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/tareas',
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
      	 buscarDocumentoFusion: function (idNotario,codDocumento,origen,numeroCaratula,version) {
          var paramsObj = {metodo: 'buscarDocumentoFusion', idNotario:idNotario,codDocumento:codDocumento,origen:origen,numeroCaratula:numeroCaratula,version:version};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/tareas',
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
     	 anularDocumento: function (codArchivoAlpha, descripcion, caratula) {
             var paramsObj = {metodo: 'anularDocumento', codArchivoAlpha:codArchivoAlpha, descripcion: descripcion, caratula: caratula};

             var deferred = $q.defer();

             $http({
               method: 'GET',
               url: '../do/service/tareas',
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
     	 agregarDocumentoLiquidacion: function (documento) {
             var paramsObj = {metodo: 'agregarDocumentoLiquidacion', documento:documento};

             var deferred = $q.defer();

             $http({
               method: 'GET',
               url: '../do/service/tareas',
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
     	 eliminarDocumentoLiquidacion: function (documento) {
             var paramsObj = {metodo: 'eliminarDocumentoLiquidacion', documento:documento};

             var deferred = $q.defer();

             $http({
               method: 'GET',
               url: '../do/service/tareas',
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
