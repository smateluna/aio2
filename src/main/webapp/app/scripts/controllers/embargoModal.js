'use strict';

app.controller('EmbargoModalCtrl', function ($scope, $modalInstance, embargos) {

	$scope.embargos = embargos;

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

});