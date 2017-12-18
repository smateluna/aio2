'use strict';

app.factory('repertorioService', function ($q, $http) {

    
    return {
      getRepertorio: function (numCaratula,numRepertorio,anio) {
        var paramsObj = {metodo: 'getRepertorio', numCaratula: numCaratula, numRepertorio:numRepertorio,anio:anio};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/repertorio',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },consultaDetalleRepertorio: function (numCaratula,numRepertorio,anio) {
        var paramsObj = {metodo: 'consultaDetalleRepertorio', numCaratula: numCaratula, numRepertorio:numRepertorio,anio:anio};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/repertorio',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },getListadoRepertorio: function (fechaDesde,fechaHasta) {
        var paramsObj = {metodo: 'getListadoRepertorio', fechaDesde: fechaDesde, fechaHasta:fechaHasta};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/repertorio',
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
