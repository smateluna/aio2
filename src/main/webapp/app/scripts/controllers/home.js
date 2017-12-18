'use strict';

app.controller('HomeCtrl', function ($scope, $routeParams, $modal, $rootScope, $timeout) {

  	$scope.tipo = $routeParams.tipo;
	$scope.cartel = {
    	bienvenido: false,
    	sinAcceso: false
  	};
		
  	if($scope.tipo==='b'){
    	$scope.cartel.bienvenido = true;
  	}

  	if($scope.tipo==='sa'){
    	$scope.cartel.sinAcceso = true;
  	}
 
  	
    $scope.openPerfilesModal = function(){
	    $modal.open({
	      templateUrl: 'perfilesModal.html',
	      backdrop: 'static',
	      keyboard: false,
	      size: 'sm',
	      windowClass: 'modal',
	      controller: 'PerfilesModalCtrl'
	    });
    };
    
    $rootScope.$watch('perfiles', function() {
 	   if($rootScope.perfiles!==undefined && $rootScope.perfil==undefined){ 		   
 		    $timeout(function(){
 		    	$scope.openPerfilesModal();
 		    }, 500);
 	   }
    });

});
