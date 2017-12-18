'use strict';

app.controller('CaratulasPorRutCtrl', function ($scope, $window, $timeout, $log, $modal, $modalStack, $routeParams, $modalInstance, estadoModel, requirenteService, estadoService, numeroCaratula) {
	$scope.req = estadoModel.getCaratulasPorRut();
	$scope.dataRequirente = estadoModel.getDatosRequirente();
	$scope.listaCaratulas = estadoModel.getListaCaratulas();
	$scope.numeroCaratula = numeroCaratula;
	
	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 5,
		totalpaginas: 0,
		filteredTodos: [],
		todos: []
	}	

	$scope.paginacionCaratulasPorRut = angular.copy($scope.paginacionMaster);
	
	$scope.buscar = function(){
		$scope.buscando=true;
		
		//Buscar Requirente
        var promise = requirenteService.getRequirenteFull($scope.req.rut);
        promise.then(function(data) {
          if(data.status && data.hayNombre){
            $scope.dataRequirente = data;
            estadoModel.setDatosRequirente(data);
          }else{
        	  $scope.dataRequirente = null;
        	  estadoModel.setDatosRequirente(null);
          }
        }, function(reason) {
           });	
        
		//Buscar Caratulas
        var promise = estadoService.getCaratulasPorRut($scope.req.rut,$scope.req.resultado);
        promise.then(function(data) {
        	$scope.buscando=false;
          if(data.status){
        	  $scope.listaCaratulas = data.res.listaCaratulas;
        	  estadoModel.setListaCaratulas(data.res.listaCaratulas);
        	  
        	  $scope.makeTodos();
          } else{
      		$scope.listaCaratulas = [];
      		estadoModel.setListaCaratulas([]);
          }
        }, function(reason) {
        });  
        
	};
	
	$scope.caratulasPorRutConMaximo = function(){
		$scope.buscando=true;
		$scope.listaCaratulas = [];
		
		var promise = estadoService.getCaratulasPorRut($scope.req.rut,$scope.req.resultado);
		promise.then(function(data) {
			$scope.buscando=false;
			if(data.status){
				$scope.listaCaratulas = data.res.listaCaratulas;
				estadoModel.setListaCaratulas(data.res.listaCaratulas);
				
				$scope.makeTodos();
			} else{
				$scope.listaCaratulas = [];
				estadoModel.setListaCaratulas([]);
			}
		}, function(reason) {
		});  
	};
	
	$scope.buscarCaratula = function(caratula){
        //estadoSetCaratula(caratula);
        //estadoBuscar();
        //$scope.cancel();
		$modalInstance.close(caratula);
	};

    $scope.cancel = function () {
	    $modalInstance.dismiss('cancel');
	};
	
	$scope.limpiar = function(){
		$scope.req.rut = null;
		$scope.dataRequirente = null;
		$scope.listaCaratulas = [];

	}
	
	$scope.makeTodos = function() {

		$scope.paginacionCaratulasPorRut = angular.copy($scope.paginacionMaster);

		$scope.paginacionCaratulasPorRut.todos=$scope.listaCaratulas;

		var begin = (($scope.paginacionCaratulasPorRut.currentPage - 1) * $scope.paginacionCaratulasPorRut.numPerPage)
		, end = begin + $scope.paginacionCaratulasPorRut.numPerPage;

		$scope.paginacionCaratulasPorRut.filteredTodos = $scope.paginacionCaratulasPorRut.todos.slice(begin, end);

		$scope.paginacionCaratulasPorRut.totalpaginas = Math.round($scope.paginacionCaratulasPorRut.todos.length / $scope.paginacionCaratulasPorRut.numPerPage);
		//$scope.paginacionCaratulasPorRut.maxSize = Math.round($scope.paginacionCaratulasPorRut.todos.length / $scope.paginacionCaratulasPorRut.numPerPage);

	};
	
    //utils

    $scope.doFocus = function(name){
      $scope.$broadcast(name+'IsFocused');
    };	
	
	$timeout(function(){		  
		$scope.doFocus('rut');
	}, 500);	

});
