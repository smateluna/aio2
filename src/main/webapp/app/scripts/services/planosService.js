'use strict';

app.factory('planosService', function ($http, $q) {

    return {
      obtenerPlanosPorTitulos: function (foja,numero,ano) {
        var paramsObj = {metodo: 'obtenerPlanosPorTitulos', foja:foja, numero:numero, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/planos',
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
      
      obtenerPlanosPendientes: function () {
          var paramsObj = {metodo: 'obtenerPestamosPendientes'};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/planos',
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
