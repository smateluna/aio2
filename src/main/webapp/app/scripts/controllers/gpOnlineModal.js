'use strict';

app.controller('gpOnlineModalCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, borrador, folio) {
	$scope.borrador = borrador;
	$scope.folio = folio;

	$scope.cancelModal = function () {
		$modalInstance.dismiss('cancel');
	};

});
