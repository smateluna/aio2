'use strict';

app.controller('ComentariosModalCtrl', function ($scope, $timeout, $modalInstance, $modalStack, repertorios) {

	$scope.repertorio = repertorios;

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error',msg, '',  false, null);
	};
	
	$timeout(function() {
		$scope.doFocus('buttonComenClose');
	}, 500);
	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};
	
});