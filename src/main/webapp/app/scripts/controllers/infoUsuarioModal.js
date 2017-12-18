'use strict';

app.controller('infoUsuarioModalCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, usuarioDTO) {
	$scope.usuarioDTO = usuarioDTO;

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

});
