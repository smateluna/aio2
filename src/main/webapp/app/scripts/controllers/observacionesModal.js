'use strict';

app.controller('ObservacionesModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, data, estadoService) {
	
	$scope.data = data;
	$scope.observacion = {comentario:"",categoria:0, enviarCorreo: false};
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  	

  $scope.agregarObservacion = function(){
	$scope.openLoadingModal('Guardando...', '');
	
	var correo= $scope.observacion.enviarCorreo?data.res.caratulaDTO.requirenteDTO.email:null;
	  
    var promise = estadoService.agregarBitacora($scope.data.req.numeroCaratula, $scope.observacion.comentario,$scope.observacion.categoria, correo);
    promise.then(function(data) {
    	$scope.closeModal();
      if(data.status===null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
        $scope.openMensajeModal('success','Comentario agregado exitosamente', '',  true, 2);

      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    });
    
  };
  
  //Util
  
  $scope.raiseErr = function(msg){
    $scope.openMensajeModal('error',msg, '',  false, null);
  };
  
  $scope.doFocus = function(name){
    $scope.$broadcast(name+'IsFocused');
  };

  $scope.openMensajeModal = function (tipo, titulo, detalle, autoClose, segundos) {
    $modal.open({
      templateUrl: 'mensajeModal.html',
      backdrop: true,
      keyboard: true,
      windowClass: 'modal',
      controller: 'MensajeModalCtrl',
      resolve: {
        tipo: function () {
          return tipo;
        },
        titulo: function () {
          return titulo;
        },
        detalle: function () {
          return detalle;
        }
      }
    });

    if(autoClose){
      $timeout(function(){
        $scope.closeModal();
      },segundos*1000);
    }
  };
  
  $scope.openLoadingModal = function (titulo, detalle) {
    $modal.open({
      templateUrl: 'loadingModal.html',
      backdrop: 'static',
      keyboard: false,
      size: 'sm',
      windowClass: 'modal',
      controller: 'LoadingModalCtrl',
      resolve: {
        titulo: function () {
          return titulo;
        },
        detalle: function () {
          return detalle;
        }
      }
    });
  };    
  
  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };
  
  $timeout(function(){
	    $scope.doFocus('email');
  }, 500);

});