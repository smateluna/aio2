'use strict';

app.controller('EstadoIndiceModalCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, numeroCaratula) {
	$scope.numeroCaratula = numeroCaratula;

    $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };

});
