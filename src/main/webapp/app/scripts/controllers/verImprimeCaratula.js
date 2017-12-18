/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerImprimeCaratulaCtrl', function ($scope, $routeParams, $sce, $timeout, $modal, $modalInstance, $modalStack,nc) {

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
		$scope.urlPDF = $sce.trustAsResourceUrl("../do/service/estado?metodo=getEstadoReporteFirstPage&nc="+nc);
		
		$timeout(function(){
			$scope.closeModal();
		}, 2000);

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
	
	$timeout(function(){
		$scope.imprimir();
//		$timeout(function(){
//			 $modalInstance.dismiss('cancel');
//		}, 10000);
	}, 5000);	

	
	
});