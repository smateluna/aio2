/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerImprimeCaratulaCtrl', function ($scope, $routeParams, $sce, $timeout, $modal, $modalInstance, $modalStack,nc, $q, $http) {

	$scope.caratula = nc;
	
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

	$scope.buscar = function(){
		var nc = $scope.caratula;
		$scope.openLoadingModal('Preparando Impresion...', '');		
		
	    var promise = $http.get("../do/service/estado?metodo=getEstadoReporteFirstPage&nc="+nc, {responseType: "arraybuffer"});
	    promise.then(function(response) {	
	    	$scope.closeModal();
	    	if(response.data.byteLength>0){
		    	var file = new Blob([response.data], {type: 'application/pdf'});
		        var fileURL = URL.createObjectURL(file);
		        $scope.urlPDF = $sce.trustAsResourceUrl(fileURL); 
		    	$timeout(function(){
					$scope.imprimir();
				}, 500);
	    	} else{
	    		$scope.cerrar();
	    		$scope.raiseErr('Error al obtener documentos para imprimir');
	    	}
          }, function(response) {
        	  $scope.cerrar();
        	$scope.raiseErr('Error al imprimir documentos');
        });

	}
	
	$scope.cerrar = function(){

		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}

	};

	$scope.imprimir = function(){

		window.frames["printf"].focus();
		window.frames["printf"].print();

	};

	$scope.buscar();
	
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
	
});