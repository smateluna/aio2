'use strict';

app.controller('SolicitarModalCtrl', function ($rootScope, $scope, $modalInstance, $modalStack, $window, $timeout, resolveModal, servicio) {

  $scope.solicitudStatus = {
    ok: false,
    error : false,
    warning: false,
    msgTitle: null,
    msg: null
  };

  $scope.isLoadingSolicitar = false;

  $scope.pasoPreSolicitar = false;

  $scope.solicita = {
    foja: null,
    numero: null,
    ano: null,
    bis: false,
    tipo: null,
    esAnoDigital: false
  };

  $scope.preSolicitar = function(){
    $scope.resetStatus();
    $scope.isLoadingSolicitar = true;

    var promise = servicio.saveSingleConValidacion($scope.solicita.foja, $scope.solicita.numero, $scope.solicita.ano, $scope.solicita.bis);
    promise.then(function(data) {
      if(data.status===null){

      }else if(data.status){
        $scope.solicita.tipo = data.estado.tipo;
        $scope.solicita.esAnoDigital = data.estado.esAnoDigital;

        if($scope.solicita.tipo===-1){
          $scope.isLoadingSolicitar = false;
          $scope.setErr('Mal citada.', 'Esta misma solicitud de foja, n\u00FAmero, a\u00F1o y bis, ya ha sido realizada anteriormente y fue rechazada por t\u00EDtulo mal citado.');
          $scope.$broadcast('sfojaIsFocused');
        }else if($scope.solicita.tipo===7){
          resolveModal.refresca = true;
          var recibe = '<strong>el libro</strong> t\u00EDtulo solicitado.';

          if($scope.solicita.esAnoDigital){
            recibe = 'una <strong>imagen al d\u00EDa</strong> del t\u00EDtulo solicitado.';
          }

          $scope.setOK('Solicitud creada.', 'Recibir\u00E1 '+recibe);
          $timeout(function(){
            $scope.closeModal();
          },4000);

        }else if($scope.solicita.tipo===0 || $scope.solicita.tipo===2){
          $scope.isLoadingSolicitar = false;
          $scope.pasoPreSolicitar = true;

        }else if($scope.solicita.tipo===1){
          $scope.isLoadingSolicitar =  false;
          $scope.pasoPreSolicitar = true;
        }
      }else{
        $scope.isLoadingSolicitar = false;
        $scope.setErr('Problema detectado.', 'No se ha guardado la solicitud.');
      }
    }, function(reason) {
      $scope.isLoadingSolicitar = false;
      $scope.setErr('Problema contactando al servidor.', 'No se ha guardado la solicitud.');
    });
  };

  $scope.volverPreSolicita = function(){
    $scope.pasoPreSolicitar = false;
    $scope.solicita.tipo = null;
    $scope.solicita.esAnoDigital = false;

    $scope.resetStatus();

    $timeout(function(){
      $scope.$broadcast('sfojaIsFocused');
    }, 100);
  };


  $scope.solicitar = function () {
    $scope.resetStatus();
    $scope.isLoadingSolicitar = true;

    var promise = servicio.saveSingle($scope.solicita.foja, $scope.solicita.numero, $scope.solicita.ano, $scope.solicita.bis);
    promise.then(function(data) {
      if(data.status===null){

      }else if(data.status){
        $scope.solicitudStatus.ok = true;

        if($scope.solicita.tipo===7){
          resolveModal.refresca = true;

          $timeout(function(){
            $scope.closeModal();
          },4000);
        }else{
          resolveModal.refresca = true;
          $scope.isLoadingSolicitar = false;
        }
      }else{
        $scope.isLoadingSolicitar = false;
        $scope.setErr('Problema detectado.', 'No se ha guardado la solicitud.');
      }
    }, function(reason) {
      $scope.isLoadingSolicitar = false;
      $scope.setErr('Problema contactando al servidor.', 'No se ha guardado la solicitud.');
    });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.limpiar = function(){
    $scope.resetSolicita();
    $scope.resetStatus();
    $scope.$broadcast('sfojaIsFocused');
  };

  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
    return !(moment(new Date()).year()<value);
  };

  $scope.verInscripcion = function(){

    var origen = resolveModal.origen;

    $rootScope.go('/verInscripcion/prop/'+$scope.solicita.foja+'/'+$scope.solicita.numero+'/'+$scope.solicita.ano+'/'+$scope.solicita.bis+'/'+origen);
  };

  $scope.setErr = function(title, mensaje){
    $scope.solicitudStatus.error = true;
    $scope.setStatusMsg(title, mensaje);
  };

  $scope.setStatusMsg = function(title, mensaje){
    $scope.solicitudStatus.msgTitle = title;
    $scope.solicitudStatus.msg = mensaje;
  };

  $scope.setOK = function(title, mensaje){
    $scope.solicitudStatus.ok = true;
    $scope.setStatusMsg(title, mensaje);

  };

  $scope.resetStatus = function(){
    $scope.solicitudStatus.ok = false;
    $scope.solicitudStatus.error = false;
    $scope.solicitudStatus.warning = false;
    $scope.solicitudStatus.msgTitle = null;
    $scope.solicitudStatus.msg = null;
  };

  $scope.resetSolicita = function(){
    $scope.solicita.foja = null;
    $scope.solicita.numero = null;
    $scope.solicita.ano = null;
    $scope.solicita.bis = false;
    $scope.solicita.tipo = null;
    $scope.solicita.esAnoDigital = false;
  };

  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };

  $timeout(function(){
    $scope.$broadcast('sfojaIsFocused');
  },100);

});