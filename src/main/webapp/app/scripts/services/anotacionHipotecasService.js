'use strict';

app.factory('AnotacionHipotecasService', function ($http, $q) {

  // Public API here
  return {
    addAnotacion: function (idInscripcion, acto, texto, caratula, repertorio, version) {

      var paramsObj = 'metodo=addAnotacion&idInscripcion='+idInscripcion+'&acto='+encodeURIComponent(acto)+'&texto='+encodeURIComponent(texto)+'&caratula='+caratula+'&repertorio='+repertorio+'&version='+version ;

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/anotacionHipotecas',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    deleteAnotacion: function (idAnotacion) {

      var paramsObj = 'metodo=removeAnotacion&idAnotacion='+idAnotacion;

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/anotacionHipotecas',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getTipos: function () {

      var paramsObj = 'metodo=getTiposAnotacion';

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/anotacionHipotecas',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    modificaAnotacion: function (texto,caratula,version,idAnotacion) {

      var paramsObj = 'metodo=modificaAnotacion&texto='+texto+'&caratula='+caratula+'&version='+version+'&idAnotacion='+idAnotacion;

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/anotacionHipotecas',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
      }).
        success(function(data, status, headers, config){
          deferred.resolve(data);
        }).
        error(function(data, status, headers, config){
          deferred.reject(status);
        });

      return deferred.promise;
    },
    getRepertorioCaratula: function (caratula) {

      var paramsObj = 'metodo=getRepertorioCaratula&caratula='+caratula;

      var deferred = $q.defer();

      $http({
        method: 'POST',
        url: '../do/service/anotacionHipotecas',
        dataType: 'json',
        data: paramsObj,
        headers: {'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8'}
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