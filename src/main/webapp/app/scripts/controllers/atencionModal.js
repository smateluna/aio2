'use strict';

app.controller('AtencionModalCtrl', function ($modalStack,$scope) {
	
	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};
	
});