'use strict';

app.factory('anexoService', function ($q, $http) {

    return {
      getAnexos: function () {
        var paramsObj = {metodo: 'getAnexos'};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/anexo',
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
	  guardarAnexo: function (anexo) {
        var paramsObj = {metodo: 'guardarAnexo', anexoReq:anexo};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/anexo',
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
