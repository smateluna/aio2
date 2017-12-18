'use strict';

app.controller('DocumentosEntregaModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, data, estadoService) {
	
	$scope.data = data;
	$scope.documentoEntrega = {
			caratula: null
	};
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  	

  $scope.vincular = function(){
	$scope.openLoadingModal('Vinculando...', '');
	  
    var promise = estadoService.getEstado($scope.documentoEntrega.caratula);
    promise.then(function(data) {
    	$scope.closeModal();
      if(data.status===null){

      }else if(data.status){    
    	  
    	    var promise = estadoService.vincularCaratula($scope.documentoEntrega.caratula, $scope.data.req.numeroCaratula);
    	    promise.then(function(data) {
    	    	$scope.closeModal();
    	      if(data.status===null){

    	      }else if(data.status){    	
    	    	  $scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
    	    	  
    				var promise = estadoService.getDocumentosEntrega($scope.data.req.numeroCaratula);
    				promise.then(function(data) {
    						if (data.status === null) {

    						} else if (data.status) {
    							$scope.data.res.entregaEnLineaDTO = data.res.entregaEnLineaDTO;
    						} else {
    							$scope.raiseErr(data.msg);
    						}
    					},
    					function(reason) {
    						$scope
    						.raiseErr('No se ha podido establecer comunicación con el servidor.');
    					});    	    	  
    	    	  
    	    	  $scope.openMensajeModal('success','Caratula vinculada', '',  true, 2);

    	      }else{
    	        $scope.raiseErr(data.msg);
    	      }
    	    }, function(reason) {
    	      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    	    });    	  

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
	    $scope.doFocus('numeroCaratula');
  }, 500);

});