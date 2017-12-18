'use strict';

app.controller('escrituraElectronicaModalCtrl', function ($scope, $modal, $modalStack, $timeout, tareasService, reingresoService, sol) {
	
	$scope.sol = sol;
	
	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null
	};
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};

//  	var promise = reingresoService.getListaNotariosFull();
//  	promise.then(function(data) {
//  		
//  		$scope.listaNotarios = data;
//  	}, function(reason) {
//  		$scope.raiseErr('Problema contactando al servidor.');
//  	});
  	
  	$scope.getListaNotarios = function(query){
  		var listaNotarios = [];
	    var promise = reingresoService.getListaNotarios(query);
	    return promise.then(function(data){
	    	return data;
	    	
	        /*return data.map(function(item){
	            return item.nombre;
	          });*/
	        });
  	};
  	
  $scope.escritura = {
	  caratula:null,
	  idnotario:null,
	  codescritura:null
  };
  
  //Util
  
  $scope.raiseErr = function(msg){
    $scope.openMensajeModal('error',msg, '',  false, null);
  };
  
  $scope.doFocus = function(name){
    $scope.$broadcast(name+'IsFocused');
  };

 
  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };
  
  $scope.buscarDocumento = function(){	
	  $scope.openLoadingModal('Buscando Documento...', '');	
	  var promise = tareasService.buscarDocumento($scope.escritura.notario.idNotario,$scope.escritura.codescritura,$scope.escritura.notario.empresa,$scope.escritura.caratula);
	  promise.then(function(data) {
		  $scope.closeModal();
		  if(data.estado===null){
		  }else if(data.status){
//			  $scope.buscarLiquidaciones();
			  $scope.solicitudStatus.ok = true;
			  $timeout(function(){
				  $scope.closeModal();
			  },2000);
		  }else{
			  $scope.raiseErr(data.msg);
		  }
	  }, function(reason) {
		  $scope.closeModal();
		  $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	  });		  
  }
  
  $timeout(function(){
	  	$scope.escritura.caratula = $scope.sol.numeroCaratula;
	    //$scope.escritura.notario = $scope.sol.idNotarioElectronico;
	    	    
	    if($scope.sol.codigoDocumentoElectronico==0)
	    	$scope.escritura.codescritura = '';
	    else
	    	$scope.escritura.codescritura =$scope.sol.codigoDocumentoElectronico;
	    	
	    $scope.doFocus('caratula');
  }, 500);
  
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

});