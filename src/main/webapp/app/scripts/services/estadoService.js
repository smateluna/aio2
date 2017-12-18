'use strict';

app.factory('estadoService', function ($q, $http) {

    // Public API here
    return {
      getEstado: function (caratula) {
        var paramsObj = {metodo: 'getEstado', numeroCaratula: caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      cambiarEstado: function (caratula, idEstado, descEstado) {
        var paramsObj = {metodo: 'cambiarEstado', numeroCaratula: caratula, idEstadoFormulario: idEstado, descEstadoFormulario: descEstado};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      cambiarCorreo: function (caratula, correo) {
        var paramsObj = {metodo: 'updateRequirente', caratula: caratula, correo: correo};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      cambiarInscripcion: function (caratula, citadoDTO) {
        var paramsObj = {metodo: 'editarDatosCitado', caratula: caratula, citadoDTO: citadoDTO};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      agregarTarea: function (caratula, codTarea, descTarea) {
        var paramsObj = {metodo: 'agregarTarea', caratula: caratula, codTarea: codTarea, descTarea: descTarea};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      eliminarTarea: function (caratula, codTarea, descTarea) {
        var paramsObj = {metodo: 'eliminarTarea', caratula: caratula, codTarea: codTarea, descTarea: descTarea};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      agregarCaja: function (caratula, fechaCaja, horaCaja) {
        var paramsObj = {metodo: 'agregarCaja', caratula: caratula, fechaCaja: fechaCaja, horaCaja: horaCaja};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      obtenerListaTareas: function () {
        var paramsObj = {metodo: 'obtenerListaTareas'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      obtenerCausasRechazo: function () {
        var paramsObj = {metodo: 'getCausalesRechazo'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      rechazarCaratula: function (caratula, codigo, motivo) {
        var paramsObj = {metodo: 'rechazarCaratula', caratula: caratula, codigo: codigo, motivo: motivo};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getCaratulaSesion: function () {
        var paramsObj = {metodo: 'getCaratulaSesion'};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getDocumentosEntrega: function(caratula) {
        var paramsObj = {metodo: 'getDocumentosEntrega', numeroCaratula: caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getRepertorios: function(caratula) {
        var paramsObj = {metodo: 'getRepertorios', numeroCaratula: caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getIngresosEgresos: function(caratula) {
        var paramsObj = {metodo: 'getIngresosEgresos', numeroCaratula: caratula};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getCuentaCorriente: function(codigo) {
        var paramsObj = {metodo: 'getCuentaCorriente', codigo: codigo};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getProductoWeb: function(productoWebDTO) {
        var paramsObj = {metodo: 'getProductoWeb', productoWebDTO: productoWebDTO};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      getDocumentos: function(caratula, tipo) {
        var paramsObj = {metodo: 'getDocumentos', numeroCaratula: caratula, tipoDocumento: tipo};

        var deferred = $q.defer();

        $http({
          method: 'GET',
          url: '../do/service/estado',
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
      existeEscritura: function(escritura) {
          var paramsObj = {metodo: 'existeEscritura', escritura : escritura};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      existeDocumento: function(documento) {
          var paramsObj = {metodo: 'existeDocumento', documento : documento};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      existeFirma: function(documento) {
          var paramsObj = {metodo: 'existeFirma', documento : documento};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      getCaratulasPorRut: function(rut,resultado) {
          var paramsObj = {metodo: 'getCaratulasPorRut', rut : rut, resultado:resultado};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      agregarBitacora: function(caratula, comentario,categoria, correo) {
          var paramsObj = {metodo: 'agregarBitacora', caratula: caratula, comentario : comentario,categoria:categoria, correo: correo};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      dejarDocumentoNoVigente: function(codigoDocumento) {
          var paramsObj = {metodo: 'dejarDocumentoNoVigente', codigoDocumento: codigoDocumento};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
      vincularCaratula: function(caratulaObj, caratulaSrc) {
          var paramsObj = {metodo: 'vincularCaratula', caratulaObj: caratulaObj, caratulaSrc: caratulaSrc};

          var deferred = $q.defer();

          $http({
            method: 'GET',
            url: '../do/service/estado',
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
