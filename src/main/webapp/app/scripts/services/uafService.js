'use strict';

app.factory('uafService', function ($q, $http) {

    
    return {
      buscarPersonas: function (nInscripciones, ano) {
        var paramsObj = {metodo: 'buscarPersonas', nInscripciones: nInscripciones, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/uaf',
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
      buscarBienes: function (nInscripciones, ano, naturaleza) {
          var paramsObj = {metodo: 'buscarBienes', nInscripciones: nInscripciones, ano:ano, naturalezas: naturaleza};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/uaf',
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
