/**
 * Created by rgonzaleza on 25/07/2014.
 */

'use strict';

app.controller('ComentariosCaratulaModalCtrl', function ($scope, $modalInstance, $log, $timeout, $window, $rootScope, data, caratulaService) {

	$scope.dataImagen = data;

	$scope.caratula = {
		resultados: []
	};
	
	if(data.resultados != undefined)
		$scope.caratula.resultados=data.resultados;
	else{
		var promise = caratulaService.obtenerBitacoraCaratula($scope.dataImagen.numeroCaratula);
		promise.then(function(data) {

			if(data.status===null){

			}else if(data.status){

				$scope.caratula.resultados = data.listabitacoras;
				$scope.status = data.status;

				$scope.doFocus('buttonClose');

			}else{
				$scope.raiseErr('No se pudo obtener bitacora caratula', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	}

	$scope.doFocus = function(name) {
		$scope.$broadcast(name + 'IsFocused');
	};

	$scope.states = {
		isLoading: false,
		isError: false,
		isOk: false,
		title: null,
		msg: null
	};

	$scope.close = function () {
		$modalInstance.dismiss('cancel');
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

});