'use strict';

app.factory('firmaService', function ($http, $q) {

    return {
    	obtenerTiposCertificadosPorPerfil: function (idPerfil) {
        var paramsObj = {metodo: 'obtenerTiposCertificadosPorPerfil', idPerfil:idPerfil};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/firma',
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
 