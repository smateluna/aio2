'use strict';

app.controller('transaccionModalCtrl', function ($rootScope, $scope, $timeout, $modalStack, $modalInstance, $modal, tareasService, caratulaDTO) {	
	
	$scope.caratulaDTO = caratulaDTO;
	
	  $timeout(function(){
		  $scope.openLoadingModal('Obteniendo transacción #' + $scope.caratulaDTO.productoWebDTO.idTransaccion, '');
		  
		  var promise = tareasService.buscarTransaccion($scope.caratulaDTO.productoWebDTO.idTransaccion);
		  promise.then(function(data) {
			  $scope.closeModal();
			  if(data.estado===null){
		  	  }else if(data.status){
		  		  $scope.transaccionDTO = data.transaccionDTO;
		  	  }else{
		    	$scope.raiseErr(data.msg);
		  	  }
		  }, function(reason) {
			  $scope.closeModal();
			  $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		  });			  
	  }, 500);
	
	
	
	  $scope.verEstadoCaratula = function(numeroCaratula){
		  $modal.open({
		      templateUrl: 'estadoIndiceModal.html',
		      backdrop: 'static',
		      windowClass: 'modal modal-dialog-xl', 
		      controller: 'EstadoIndiceModalCtrl',
		      size: 'lg',
		      resolve: {  
		        numeroCaratula : function(){
			          return parseInt(numeroCaratula);
			    }
		      }
		  });
	  };  
	
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

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
});