'use strict';

app.controller('ListadoRepertorioCtrl', function ($scope,$timeout,$rootScope,$interval,$location,$anchorScroll,solicitudesModel,certificacionModel, repertorioService, $filter,$modal,$modalStack) {
	
	$scope.fechahoy = new Date().toJSON().split('T')[0];
	
	$scope.busquedaCertificacion = {
		fechaDesde:$scope.fechahoy,
	    fechaHasta:$scope.fechahoy,
	    tiemporefresco:2
	};
	
	$scope.states = certificacionModel.getStates();
	$scope.tab = solicitudesModel.getTab();

	$scope.solicitudStatus = {
	    ok: false,
	    error : false,
	    warning: false,
	    msgTitle: null,
	    msg: null,
	    tipo: null
  	};
	
	$scope.solicita = {
	    foja: null,
	    numero: null,
	    ano: null,
	    bis: false,
	    tipo: null,
	    esDigital: false,
	    tieneRechazo: false
  	};
    
    $scope.refrescar = function(){
    	$scope.openLoadingModal('Cargando Listado...', '');
    	var promise = repertorioService.getListadoRepertorio($scope.busquedaCertificacion.fechaDesde,$scope.busquedaCertificacion.fechaHasta);
    	promise.then(function(data) {
    		if(data.status===null){
    		}else if(data.status){

    			$scope.busquedaCertificacion.data = data;
    			$scope.busquedaCertificacion.resultados = data.resultado;
    			$scope.closeModal();
    		}else{
    			$scope.setErr('data.msg', data.msg);
    			$scope.closeModal();
    		}
    	}, function(reason) {
    		$scope.setErr('Problema contactando al servidor.', '');
    		$scope.closeModal();
    	});
    };
	
	$scope.resolveModal = {
    	refresca: false
  	};
  	
  	//utiles	
	 $scope.doFocus = function(name){
	    $scope.$broadcast(name+'IsFocused');
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
  
  $scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  };
    
  $scope.resetStatus = function(){
    $scope.solicitudStatus.ok = false;
    $scope.solicitudStatus.error = false;
    $scope.solicitudStatus.warning = false;
    $scope.solicitudStatus.msgTitle = null;
    $scope.solicitudStatus.msg = null;
  };
  
  $scope.setErr = function(title, mensaje){
    $scope.solicitudStatus.error = true;
    $scope.setStatusMsg(title, mensaje);
  };
  
  $scope.setStatusMsg = function(title, mensaje){
    $scope.solicitudStatus.msgTitle = title;
    $scope.solicitudStatus.msg = mensaje;
  };
  
  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };
  
  $scope.$watch('busquedaCertificacion.tiemporefresco', function() {
	  $scope.start();
  });
  
  $timeout(function(){				  
	  $scope.refrescar();
  }, 500);
  
//   $interval(function(){
//      $scope.refrescar();
//   }.bind(this), $scope.busquedaCertificacion.tiemporefresco*60000);   

    var promise;
    
    $scope.start = function() {
      // stops any running interval to avoid two intervals running at the same time
      $scope.stop(); 
      
      // store the interval promise
      if($scope.busquedaCertificacion.tiemporefresco!=0){
      			promise = $interval(function(){
      						$scope.refrescar();
   						  }.bind(this), $scope.busquedaCertificacion.tiemporefresco*60000);
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
    
});