'use strict';

app.controller('MovimientosModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, caratula, estadoService) {

	$scope.caratulaDTO = caratula.caratulaDTO;
	$scope.movimiento = {fechaCaja:"", horaCaja:""};
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  $scope.agregarCaja = function(){
	$scope.openLoadingModal('Agregando movimiento...', '');
	
    var promise = estadoService.agregarCaja($scope.caratulaDTO.datosFormularioDTO.numeroCaratula, $scope.movimiento.fechaCaja, $scope.movimiento.horaCaja);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status==null){

      }else if(data.status){
    	$scope.openMensajeModal('success','Caja agregada exitosamente', '',  false, null);
    	$scope.closeModal();
    	$scope.refrescar();
      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    });
    
  };
  
  $scope.refrescar = function(){
	    var promise = estadoService.getEstado($scope.caratulaDTO.datosFormularioDTO.numeroCaratula);
	    promise.then(function(data) {
	      if(data.status===null){
	      }else if(data.status){
	    	  $scope.caratulaDTO.movimientoDTOs = data.res.caratulaDTO.movimientoDTOs;
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
	    $scope.doFocus('fecha');  
}, 500);    

});