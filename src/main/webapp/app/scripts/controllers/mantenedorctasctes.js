'use strict';

app.controller('MantenedorCtasCtesCtrl', function ($scope,$timeout,$rootScope,$location, mantenedorService, mantenedorCertificaModel, $modal,$modalStack,$routeParams,filterFilter) {
	
	$scope.busquedaUsuario = mantenedorCertificaModel.getBusquedaUsuario();
	$scope.states = mantenedorCertificaModel.getStates();
	$scope.listaCtasCtes = [];
	$scope.paginacionMaster = {
			currentPage: 1,
			numPerPage: 10,
			maxSize: 10,
			filteredTodos: [],
			todos: []
		}	
	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacion.todos=$scope.listaCtasCtes;
	
	$scope.obtenerCtasCtes = function(){
		var promise = mantenedorService.getCtasCtes();
		promise.then(function(data) {
		    if(data.status===null){
		    }else if(data.status){
		      $scope.listaCtasCtes = data.listaCtasCtes;
			  //Paginacion
			  $scope.makeTodos(data.listaCtasCtes);		
		    }else{
		      $scope.setErr('data.msg', 'data.msg');
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
	}
	
	$scope.obtenerCtasCtes();
	
	$scope.editarCtaCte = function(ctaCte){
		$scope.openCtaCteModal(ctaCte);		
	}
	
	$scope.openCtaCteModal = function(ctaCte) {
		$modal.open( {
			templateUrl : 'ctaCteModal.html',
			backdrop : false,
			windowClass : 'modal',
			controller : 'CtaCteModalCtrl',
			resolve : {
				ctaCte : function() {
					return ctaCte;
				}
			}
		});
	};	
  
	$scope.openLoadingModal = function (titulo, detalle) {
	    $modal.open({
	      templateUrl: 'loadingModal.html',
	      backdrop: 'static',
	      keyboard: false,
	      size: 'sm',
	      windowClass: 'modal',
	      controller: 'LoadingModalCtrl',
	      resolve: {
	        titulo: function () {
	          return titulo;
	        },
	        detalle: function () {
	          return detalle;
	        }
	      }
	    });
  	};
	
  	$scope.closeModal = function(){
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
	};
	  	
	$scope.raiseErr = function(key, title, msg){
	    $scope.closeModal();
	    $scope.states[key].isError = true;
	    $scope.states[key].title = title;
	    $scope.states[key].msg = msg;
  	};
  	
	$scope.makeTodos = function(data) {
		$scope.paginacion = angular.copy($scope.paginacionMaster);
		$scope.paginacion.todos=data;
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;
		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	};  	
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		$scope.filtered = filterFilter($scope.listaCtasCtes, newVal);
		$scope.paginacion.todos = $scope.filtered;
		$scope.paginacion.currentPage = 1;
		
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		
	}, true);	
});