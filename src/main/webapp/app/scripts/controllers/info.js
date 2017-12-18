'use strict';

app.controller('InfoCtrl', function ($scope, $interval, $timeout, $log, Usuario, $modal, $modalStack, $routeParams) {

	$scope.busquedaInfo = {
	    tiemporefresco:2,
	    predicate: "fechaUltimoAccesoL",
	    reverse: true
	};
	
	$scope.refrescar = function(){
	    var promise = Usuario.getUsuariosLogueados();
	    promise.then(function(data) {
	      //$scope.closeModal();
	      if(data.estado===null){
	      }else if(data.estado){
	        $scope.busquedaInfo.resultados =data.listaUsuarios;	        	
	        $scope.busquedaInfo.numeroSesiones =data.numeroSesiones;
	      }else{
	        $scope.raiseErr(data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	    });
	};
	
	$scope.cacheAIO = function(){
	    $modal.open({
		      templateUrl: '../cacheAIO',
		      keyboard: true,
		      size: 'lg',
		      windowClass: 'modal modal-dialog-xl'
		    });
	}
	
    $scope.$watch('busquedaInfo.tiemporefresco', function() {
	  $scope.start();
    });	
	  
    var promise;
    $scope.start = function() {
        // stops any running interval to avoid two intervals running at the same time
        $scope.stop(); 
        
        // store the interval promise
        if($scope.busquedaInfo.tiemporefresco!=0){
        			promise = $interval(function(){
        						$scope.refrescar();
     						  }.bind(this), $scope.busquedaInfo.tiemporefresco*60000);
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

	  $scope.infoUsuarioModal = function (usuario) {
	    $modal.open({
	      templateUrl: 'infoUsuarioModal.html',
	      backdrop: 'static',
	      keyboard: false,
	      size: 'lg',
	      windowClass: 'modal',
	      controller: 'infoUsuarioModalCtrl',
	      resolve: {
	        usuarioDTO: function () {
	          return usuario;
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

	  $scope.stringIsNumber = function(s) {
	    var x = +s;
	    return x.toString() === s;
	  };

	  //fin utils  
  
});
