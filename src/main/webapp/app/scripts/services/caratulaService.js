'use strict';

app.factory('caratulaService', function ($http, $q) {

    return {
      savePreCaratulaMasiva: function (foja,numero,ano,rut, nombre, apep, apem, direccion, telefono, correo, tramite1,tramite2,tramite3,tramite4,tramite5,obs1,obs2,obs3,obs4,obs5,registro,titulos) {
        var paramsObj = {metodo: 'savePreCaratulaMasiva', foja:foja, numero:numero, ano:ano, rut:rut, nombre:nombre, apep:apep, apem:apem, direccion:direccion, telefono:telefono, correo:correo, tramite1:tramite1,tramite2:tramite2,tramite3:tramite3,tramite4:tramite4,tramite5:tramite5,obs1:obs1,obs2:obs2,obs3:obs3,obs4:obs4,obs5:obs5,registro:registro,titulos:titulos};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },savePreCaratula: function (foja,numero,ano,rut, nombre, apep, apem, direccion, telefono, correo, tramite1,tramite2,tramite3,tramite4,tramite5,obs1,obs2,obs3,obs4,obs5,registro,titulos) {
        var paramsObj = {metodo: 'savePreCaratula', foja:foja, numero:numero, ano:ano, rut:rut, nombre:nombre, apep:apep, apem:apem, direccion:direccion, telefono:telefono, correo:correo, tramite1:tramite1,tramite2:tramite2,tramite3:tramite3,tramite4:tramite4,tramite5:tramite5,obs1:obs1,obs2:obs2,obs3:obs3,obs4:obs4,obs5:obs5,registro:registro,titulos:titulos};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      }, obtenerCantidadCaratula: function (foja,numero,ano) {
        var paramsObj = {metodo: 'obtenerCantidadCaratula', foja:foja, numero:numero, ano:ano};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      }, obtenerCaratulasPorFoja: function (foja,numero,ano,estado) {
        var paramsObj = {metodo: 'obtenerCaratulasPorFoja', foja:foja, numero:numero, ano:ano,estado:estado};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      }, actualizarInscripcion: function (caratula,foja, numero, ano, bis) {
        var paramsObj = {metodo: 'actualizarInscripcion', caratula:caratula, foja:foja, numero:numero, ano:ano, bis:bis};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },obtenerInscripcionBis: function (caratula,foja, numero, ano, bis) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'obtenerInscripcionBis', caratula:caratula, foja: foja, numero: numero, ano: ano, bis: bis};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
    }, obtenerCausalesRechazo: function () {
        var paramsObj = {metodo: 'obtenerCausalesRechazo'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/caratula',
          params: paramsObj
        }).
          success(function(data, status, headers, config){
            deferred.resolve(data);
          }).
          error(function(data, status, headers, config){
            deferred.reject(status);
          });

        return deferred.promise;
      },rechazarCaratula: function (caratula,codigoRechazo, descripcionRechazo) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'rechazarCaratula', caratula:caratula, codigoRechazo: codigoRechazo, descripcionRechazo: descripcionRechazo};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },obtenerBitacoraCaratula: function (caratula) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'obtenerBitacoraCaratula', caratula:caratula};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },obtenerCaratula: function (caratula) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'obtenerCaratula', caratula:caratula};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },movercaratula: function (caratula, seccion) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'moverCartulaRedistribucion', caratula:caratula, seccion:seccion};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },getListadoCaratulasPorCtaCte: function (fechaDesde,fechaHasta,numCuenta,estado) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'getListadoCaratulasPorCtaCte', fechaDesde:fechaDesde,fechaHasta:fechaHasta,numCuenta:numCuenta,estado:estado};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },getCierreCtaCte: function (cuenta,fecha,tipoCierre) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'getCierreCtaCte', cuenta:cuenta,fecha:fecha,tipoCierre:tipoCierre};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },getListadoCaratulasPendientesPorSeccion: function (seccion) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'getListadoCaratulasPendientesPorSeccion', seccion:seccion};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },moverCaratulaLiquidacion: function (caratula, esCtaCte) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = 'v='+dateNumber+'&metodo=movercaratulaLiquidacion&caratula='+caratula+'&esCtaCte='+esCtaCte;
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'POST',
	        url: '../do/service/caratula',
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
    },getListadoCaratulasPendientesPorUsuario: function () {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'getListadoCaratulasPendientesPorUsuario'};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
	        params: paramsObj
	      }).
	        success(function(data, status, headers, config){
	          deferred.resolve(data);
	        }).
	        error(function(data, status, headers, config){
	          deferred.reject(status);
	        });
	
	      return deferred.promise;
     },eliminarCaratulaPendiente: function (caratula) {
	      var d = new Date();
	      var dateNumber = d.getTime();
	
	      var paramsObj = {v: dateNumber,metodo: 'eliminarCaratulaPendiente', caratula: caratula};
	
	      var deferred = $q.defer();
	
	      $http({
	        method: 'GET',
	        url: '../do/service/caratula',
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
 