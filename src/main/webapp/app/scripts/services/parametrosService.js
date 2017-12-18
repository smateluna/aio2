'use strict';

app.factory('parametrosService', function ($q, $http) {

    
    return {
    	getNaturalezas: function () {
        var paramsObj = {metodo: 'getNaturalezas'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/parametros',
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
