'use strict';

app.factory('CartelesService', function ($http, $q) {

  return {
    buscarCartel: function (numero, mes, ano, registro, bis, tipo) {
      var d = new Date();
      var dateNumber = d.getTime();

      var paramsObj = {v: dateNumber,metodo: 'buscarCartel', numero: numero, mes: mes, ano: ano, registro: registro, bis: bis, tipo: tipo};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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
    certificar: function (caratula, numero, mes, ano, registro, bis, pdesde, phasta, tipo) {
      var d = new Date();
      var dateNumber = d.getTime();

      var paramsObj = {v: dateNumber,metodo: 'certificar', caratula: caratula, numero: numero, mes: mes, ano: ano, registro: registro, bis: bis, pdesde: pdesde, phasta: phasta, tipo: tipo};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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
    listadoCertificadosByUserInSession: function () {
      var paramsObj = {metodo: 'listadoCertificadosByUserInSession'};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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
    firmar: function (idCertificado, registro) {
      var paramsObj = {metodo: 'firmar', idCertificado: idCertificado, registro: registro};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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
    eliminar: function (idCertificado, registro) {
      var paramsObj = {metodo: 'eliminar', idCertificado: idCertificado, registro: registro};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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
    rehacer: function (numero, mes, ano, registro, bis, pdesde, phasta) {
      var paramsObj = {metodo: 'rehacer', numero: numero, mes: mes, ano: ano, registro: registro, bis: bis, pdesde: pdesde, phasta: phasta};

      var deferred = $q.defer();

      $http({
        method: 'GET',
        url: '../do/service/carteles',
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