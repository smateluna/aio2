'use strict';

app.controller('llamarClienteModalCtrl', function ($rootScope,$scope,$modalStack,$timeout,socketService) {

	$scope.llamar = {
			nombre: ''
	};
	
	$scope.cancel = function () {
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.llamarCliente = function () {
		$rootScope.nombreCliente=$scope.llamar.nombre;
		socketService.disponible();
	};
    
    $timeout(function(){
    	$scope.$broadcast('nombreIsFocused');
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