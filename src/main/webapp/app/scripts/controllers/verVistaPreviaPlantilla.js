/**
 * Created by rgonzaleza on 07/07/2014.
 */

'use strict';

app.controller('VerVistaPreviaPlantillaCtrl', function ($scope, $routeParams, $rootScope, $modal, $modalStack, $sce, $timeout, certificacionService, plantilleroModel) {

	$scope.parametros = {
		nombreArchivo: $routeParams.nombreArchivo,
		origen: $routeParams.origen
	};

	$scope.states = {
		isLoading: false,
		isError: false,
		isOk: false,
		title: null,
		msg: null
	};

	$scope.buscar = function(){
		$scope.isLoading = true;

		$scope.urlPDF = $sce.trustAsResourceUrl("../do/service/certificacion?metodo=obtenerPdf&nombreArchivo="+$scope.parametros.nombreArchivo);
		$scope.isLoading  = false;
	};
	
	$scope.cerrar = function(){
		$rootScope.go('/'+$scope.parametros.origen);
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

	$scope.certificar = function(){
		$scope.openLoadingModal('Certificando...', '');

		var promise = certificacionService.certificarPdf($scope.parametros.nombreArchivo);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){
			}else if(data.status){
				$scope.raiseOk('', 'Certificacion realizada');
				if(data.warn)
					$scope.raiseOk('', 'Certificacion realizada, pero no se pudo enviar a Entrega Documentos');
				plantilleroModel.resetPlantillero();
				$scope.salirSave();
			}else{
				$scope.raiseErr('No se pudo certificar documento', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	};

	$scope.raiseErr = function(title, msg){
		$scope.states.isError = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};

	$scope.raiseOk = function(title, msg){
		$scope.states.isOk = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};
	
	$scope.salirSave = function(){
		$timeout(function(){
			//CertificacionCtrl.refrescar();
			$scope.cerrar();
		},4000);
	};

	$scope.buscar();

});