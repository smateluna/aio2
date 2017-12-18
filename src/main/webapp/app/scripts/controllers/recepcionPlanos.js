'use strict';

app.controller('RecepcionPlanosCtrl', function ($scope, $interval, $timeout, $log, planosService, $modal, $modalStack, $routeParams) {

	$scope.recepcionPlanos = {
		resultados: [],
	    tiemporefresco: 15,
	    predicate: "fechaRetiroL",
	    reverse: false
	};
	
	$scope.refrescar = function(){
	    var promise = planosService.obtenerPlanosPendientes();
	    promise.then(function(data) {
	      //$scope.closeModal();
	      if(data.estado===null){
	      }else if(data.status){
	    	  $scope.recepcionPlanos.resultados = data.listaPlanosPendientes;
	      }else{
	        $scope.raiseErr(data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	    });
	};
	
	$scope.ingresarPlano = function(){
//	    $modal.open({
//		      templateUrl: '../cacheAIO',
//		      keyboard: true,
//		      size: 'lg',
//		      windowClass: 'modal modal-dialog-xl'
//		    });
		$scope.recepcionPlanosModal();
	}
	
    $scope.$watch('recepcionPlanos.tiemporefresco', function() {
	  $scope.start();
    });	
	  
    var promise;
    $scope.start = function() {
        // stops any running interval to avoid two intervals running at the same time
        $scope.stop(); 
        
        // store the interval promise
        if($scope.recepcionPlanos.tiemporefresco!=0){
        			promise = $interval(function(){
        						$scope.refrescar();
     						  }.bind(this), $scope.recepcionPlanos.tiemporefresco*60000);
        }
        
      };	
      // stops the interval
      $scope.stop = function() {
        $interval.cancel(promise);
      };
      
      $scope.start();
     
      $scope.$on('$destroy', function() {
      	$scope.stop(); 
      });      

    $timeout(function(){				  
  	  $scope.refrescar();
    }, 500);
  
  $scope.raiseErr = function(msg){
	    $scope.openMensajeModal('error',msg, '',  false, null);
	  };

	  //utils

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

	  $scope.recepcionPlanosModal = function () {
	    $modal.open({
	      templateUrl: 'recepcionPlanosModal.html',
	      backdrop: 'static',
	      keyboard: false,
	      size: 'lg',
	      windowClass: 'modal',
	      controller: 'RecepcionPlanosModalCtrl',
	      resolve: {
	      }
	    });
	  };
	  
	  $scope.closeModal = function(){
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
	  };

	  $scope.stringIsNumber = function(s) {
	    var x = +s;
	    return x.toString() === s;
	  };

	  //fin utils  
  
});
