'use strict';

app.factory('oficioService', function ($http, $q) {

    return {
      getOficios: function (caratula, institucion, requirente, estado, fechaCreacionDesde, fechaCreacionHasta, fechaEntrega, materia, rol, oficio, identificador, ciudad) {
        var paramsObj = {metodo: 'getOficios', caratula:caratula, institucion:institucion, requirente:requirente, estado:estado, fechaCreacionDesde:fechaCreacionDesde, fechaCreacionHasta:fechaCreacionHasta, fechaEntrega:fechaEntrega, materia:materia, rol:rol, oficio:oficio, identificador:identificador, ciudad:ciudad};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/oficio',
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
      init: function(){
          var paramsObj = {metodo: 'init'};

          var deferred = $q.defer();    	  
          $http({
              method: 'GET',
              url: '../do/service/oficio',
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
