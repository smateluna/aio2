'use strict';

app.controller('RequirenteModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, data, estadoService) {
	
	$scope.data = data;
//	$scope.requirente = {email:""};
	$scope.requirente = angular.copy($scope.data.res.caratulaDTO.requirenteDTO);
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  	

  $scope.updateRequirente = function(){
	$scope.openLoadingModal('Guardando...', '');
	  
    var promise = estadoService.updateRequirente($scope.data.req.numeroCaratula, $scope.requirente);
    promise.then(function(data) {
    	$scope.closeModal();
      if(data.status===null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.requirenteDTO = $scope.requirente;
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
        $scope.openMensajeModal('success','Datos requiente modificado exitosamente', '',  false, null);

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
  
//  $timeout(function(){
//	    $scope.doFocus('email');
//  }, 500);

});