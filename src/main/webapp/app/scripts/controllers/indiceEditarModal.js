'use strict';

app.controller('IndiceModalCtrl', function ($rootScope, $routeParams, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, indiceService, resolveModal, indice) {

	$scope.indice = indice;

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null
	};

	$scope.isLoadingSolicitar = false;

	$scope.actualizarIndice =function(){		
		$scope.resetStatus();
		
		var promise = indiceService.actualizarIndice($scope.indice);
		promise.then(function(data) {
			//$scope.closeModal();
			if(data.status===null){
			}else if(data.status){
				if(data.warn){
					$scope.setWarn('No se pudo actualizar.', data.msg);
				} else
					$scope.setOK('Actualización en curso.', 'El proceso de actualización podría tardar algunos minutos en reflejar los cambios');
			}else{
				$scope.setErr('Error.', data.msg);
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
		});
	}
	
	$scope.limpiarCache = function(){
		localStorageService.remove('rawScreens');
	};
	
	$timeout(function(){
		$scope.doFocus('rutt');

	}, 200);


	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

	$scope.setErr = function(title, mensaje){
		$scope.solicitudStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};
		
	$scope.setWarn = function(title, mensaje){
		$scope.solicitudStatus.warning = true;
		$scope.setStatusMsg(title, mensaje);
	};
	
	$scope.setOK = function(title, mensaje){
		$scope.solicitudStatus.ok = true;
		$scope.setStatusMsg(title, mensaje);
	};	

	$scope.setStatusMsg = function(title, mensaje){
		$scope.solicitudStatus.msgTitle = title;
		$scope.solicitudStatus.msg = mensaje;
	};

	$scope.setOK = function(title, mensaje){
		$scope.solicitudStatus.ok = true;
		$scope.setStatusMsg(title, mensaje);

	};

	$scope.resetStatus = function(){
		$scope.solicitudStatus.ok = false;
		$scope.solicitudStatus.error = false;
		$scope.solicitudStatus.warning = false;
		$scope.solicitudStatus.msgTitle = null;
		$scope.solicitudStatus.msg = null;
	};

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	//utiles	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};
	
	$scope.updateQuestionValue = function(choice){
        $scope.value = $scope.value || [];
        if(choice.checked){
            $scope.value.push(choice.value);
            $scope.value = _.uniq($scope.value);
        }else{
            $scope.value = _.without($scope.value, choice.value);
        }
    };

});