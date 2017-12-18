'use strict';

app.factory('requirenteService', function ($q, $http) {

    // Public API here
    return {
      getNombre: function (rut) {
        var paramsObj = {metodo: 'getNombre', rut: rut};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/requirente',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      }, getRequirenteFull: function (rut) {
        var paramsObj = {metodo: 'getRequirenteFull', rut: rut};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/requirente',
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
