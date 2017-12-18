'use strict';

app.controller('ingresaRutModalCtrl', function ($scope,$modalStack,$timeout,indiceService) {

	$scope.req = {
		rut: null
	};
	
	$scope.cancel = function () {
		//$modalInstance.dismiss('cancel');

		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.agregarNuevaAtencion = function () {

		var promise = indiceService.agregarNuevaAtencion($scope.req.rut);
		promise.then(function(data) {
			if(data.status===null){

			}else if(data.status){
				$scope.cancel();				
			}else{

				$scope.setErr('Problema detectado.', 'No se ha podido registrar nueva atencion.');
			}
		}, function(reason) {

			$scope.setErr('Problema contactando al servidor.', 'No se ha guardado la nueva atencion.');
		});

	};
    
    $timeout(function(){
    	$scope.$broadcast('rutIsFocused');
    }, 200);

    $scope.setErr = function(title, mensaje){
		$scope.solicitudStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};

	$scope.setStatusMsg = function(title, mensaje){
		$scope.solicitudStatus.msgTitle = title;
		$scope.solicitudStatus.msg = mensaje;
	};
});