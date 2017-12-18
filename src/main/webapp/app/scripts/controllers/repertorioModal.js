'use strict';

app.controller('RepertorioModalCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, numeroRepertorio) {
	$scope.numeroRepertorio = numeroRepertorio;

    $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	  };

});
