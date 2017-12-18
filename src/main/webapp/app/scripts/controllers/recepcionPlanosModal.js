'use strict';

app.controller('RecepcionPlanosModalCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, requirenteService) {
	
	$scope.plano = {
			categoria: "Ley 19.537"
	};
	
	$scope.buscarRequirente = function(){console.log("buscando rut: " + $scope.plano.rut)
		$scope.buscando=true;
		
		//Buscar Requirente
        var promise = requirenteService.getRequirenteFull($scope.plano.rut);
        promise.then(function(data) {console.log(data);
          if(data.status && data.hayNombre){
            $scope.dataRequirente = data;
          }else{
        	  $scope.dataRequirente = null;
          }
        }, function(reason) {
           });	        
	};

    $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	};
	
	$scope.limpiar = function(){
		$scope.plano.rut = null;
		$scope.dataRequirente = null;

	}
	
    //utils

    $scope.doFocus = function(name){
      $scope.$broadcast(name+'IsFocused');
    };	
	
	$timeout(function(){		  
		$scope.doFocus('rut');
	}, 500);	

});
