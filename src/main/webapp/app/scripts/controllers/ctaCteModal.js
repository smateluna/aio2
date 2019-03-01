'use strict';

app.controller('CtaCteModalCtrl', function ($log, $rootScope, $scope, $modal, $route, $modalInstance, $modalStack, $window, $timeout, ctaCte, mantenedorService) {
	
	$scope.ctaCte = {};
	if(ctaCte!=undefined)
		angular.copy(ctaCte, $scope.ctaCte);
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  	

  $scope.guardarCtaCte = function(){
	$scope.openLoadingModal('Guardando...', '');
	  
    var promise = mantenedorService.guardarCtaCte(encodeURIComponent(JSON.stringify($scope.ctaCte)));
    promise.then(function(data) {
    	$scope.closeModal();
      if(data.status===null){

      }else if(data.status){
		$scope.closeModal();
		if($scope.ctaCte.codigo){
			$scope.openMensajeModal('success','Cuenta corriente actualizada exitosamente', '',  false, null);
			angular.copy($scope.ctaCte, ctaCte);
		} else{
			$scope.openMensajeModal('success','Cuenta corriente ingresada exitosamente', '',  false, null);
	  	    $timeout(function(){
	  	    	$scope.closeModal();
	  	    	$route.reload();	  
		    }, 2000);				
		}

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
	    $scope.doFocus('institucion');
  }, 500);

});