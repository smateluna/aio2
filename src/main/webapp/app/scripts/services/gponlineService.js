'use strict';

app.factory('gponlineService', function ($http, $q) {

    return {
      buscarFolios: function (borrador) {
        var paramsObj = {metodo: 'buscarFolios', borrador:borrador};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarBorrador: function (borrador, folio, fechaHoy, ano) {
        var paramsObj = {metodo: 'buscarBorrador', borrador:borrador, folio:folio, fechaHoy:fechaHoy, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarBorradorPorCaratula: function (caratula) {
        var paramsObj = {metodo: 'buscarBorradorPorCaratula', caratula:caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarBorradorPorInscripcion: function (foja,numero,ano) {
        var paramsObj = {metodo: 'buscarBorradorPorInscripcion', foja:foja,numero:numero,ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarPlano: function (listaInscripcionesPlano) {
        var paramsObj = {metodo: 'buscarPlano', listaInscripcionesPlano:listaInscripcionesPlano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarCaratulas: function (borrador,folio,listaPropiedades) {
        var paramsObj = {metodo: 'buscarCaratulas', borrador:borrador,folio:folio,listaPropiedades:listaPropiedades};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },buscarBorradorPorInscripcionRegistro: function (foja,numero,ano,bis,registro) {
        var paramsObj = {metodo: 'buscarBorradorPorInscripcionRegistro', foja:foja,numero:numero,ano:ano,bis:bis,registro:registro};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/gponline',
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
 