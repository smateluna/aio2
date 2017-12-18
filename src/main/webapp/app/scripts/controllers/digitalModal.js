'use strict';

app.controller('DigitalModalCtrl', function ($rootScope, $scope, $modalStack, $modalInstance, $window, $log, solicita, $timeout, servicio, origen) {
  $scope.verObj = {
    foja: solicita.foja,
    numero: solicita.numero,
    ano: solicita.ano,
    bis: solicita.bis
  };

  $scope.solicitudStatus = {
    ok: false,
    error : false,
    warning: false,
    msgTitle: null,
    msg: null
  };

  $scope.isLoadingSolicitar = false;

  $scope.solicita = solicita;

  $scope.solicitar = function () {
    $scope.resetStatus();
    $scope.isLoadingSolicitar = true;

    var promise = servicio.saveSingle(solicita.foja, solicita.numero, solicita.ano, solicita.bis);
    promise.then(function(data) {
      if(data.status===null){
        $window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
      }else if(data.status){
        $scope.solicitudStatus.ok = true;

        if(!$scope.solicita.consultaDocumentoDTO.hayDocumento){
          $timeout(function(){
            $scope.cancel();
          },2000);
        }else{
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
    //$modalInstance.dismiss('cancel');

    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };

  $scope.limpiar = function(){
    $scope.resetSolicita();
    $scope.resetStatus();
    $scope.resetVerObj();
    $scope.$broadcast('sfojaIsFocused');
  };

  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
    return !(moment(new Date()).year()<value);
  };

  $scope.verInscripcion = function(){
	if(origen=='digitalProhibiciones')  
    	$rootScope.go('/verInscripcion/proh/'+$scope.verObj.foja+'/'+$scope.verObj.numero+'/'+$scope.verObj.ano+'/'+$scope.verObj.bis+'/'+origen);
	else if(origen=='digitalHipotecas')
		$rootScope.go('/verInscripcion/hip/'+$scope.verObj.foja+'/'+$scope.verObj.numero+'/'+$scope.verObj.ano+'/'+$scope.verObj.bis+'/'+origen);
	else
		$rootScope.go('/verInscripcion/prop/'+$scope.verObj.foja+'/'+$scope.verObj.numero+'/'+$scope.verObj.ano+'/'+$scope.verObj.bis+'/'+origen);
  };

  $scope.setErr = function(title, mensaje){
    $scope.solicitudStatus.error = true;
    $scope.solicitudStatus.msgTitle = title;
    $scope.solicitudStatus.msg = mensaje;
  };

  $scope.resetStatus = function(){
    $scope.solicitudStatus.ok = false;
    $scope.solicitudStatus.error = false;
    $scope.solicitudStatus.warning = false;
    $scope.solicitudStatus.msgTitle = null;
    $scope.solicitudStatus.msg = null;
  };

  $scope.resetVerObj = function(){
    $scope.verObj.foja = null;
    $scope.verObj.numero = null;
    $scope.verObj.ano = null;
    $scope.verObj.bis = false;
  };

  $scope.resetSolicita = function(){
    $scope.solicita.foja = null;
    $scope.solicita.numero = null;
    $scope.solicita.ano = null;
    $scope.solicita.bis = false;
    $scope.solicita.tipo = null;
    $scope.solicita.estado = {};
    $scope.solicita.consultaDocumentoDTO = {};
  };

  //util
  $scope.tieneRechazo = function(){
     return $scope.solicita.estado.tieneRechazo;
  };

  $scope.esSolicitableSinImagen = function(){
    return (!$scope.solicita.estado.tieneRechazo && !$scope.solicita.consultaDocumentoDTO.hayDocumento);
  };

  $scope.esSolicitableConImagen = function(){
    return !$scope.solicita.estado.tieneRechazo && $scope.solicita.consultaDocumentoDTO.hayDocumento &&
      ($scope.solicita.consultaDocumentoDTO.tipoDocumento===9 || $scope.solicita.consultaDocumentoDTO.tipoDocumento===10);
  };

  $scope.esVersionado = function(){
    return !$scope.solicita.estado.tieneRechazo && $scope.solicita.consultaDocumentoDTO.hayDocumento &&
      $scope.solicita.consultaDocumentoDTO.tipoDocumento===8;
  };
  
  $scope.existeIndice = function(){
	if(origen=='digitalProhibiciones')  
    	return (!$scope.solicita.estado.fna && !$scope.solicita.consultaDocumentoDTO.hayDocumento);
	else
		return false;
  };

  $timeout(function(){
    $scope.$broadcast('sfojaIsFocused');
  },100);

});