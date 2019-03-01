'use strict';

app.controller('DespachoCuadernilloCtrl', function ($scope,$timeout,$rootScope,$location, inscripcionDigitalService, $modal,$modalStack,$routeParams,filterFilter) {
	
	$scope.fechahoy = new Date().toJSON().split('T')[0];
	
	$scope.despachoCuadernillo = {
			resultados: [],
			fechaDesde: $scope.fechahoy,
			fechaHasta: $scope.fechahoy
	}
	
	$scope.paginacionMaster = {
			currentPage: 1,
			numPerPage: 10,
			maxSize: 10,
//			filteredTodos: [],
			todos: []
		}	
	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacion.todos=$scope.despachoCuadernillo.resultados;
	
	
	
	$scope.buscar = function(){
		var promise = inscripcionDigitalService.getDespachoCuadernillos($scope.despachoCuadernillo.fechaDesde, $scope.despachoCuadernillo.fechaHasta);
		promise.then(function(data) {
		    if(data.status===null){
		    }else if(data.status){
		      $scope.despachoCuadernillo.resultados = data.listaCuadernillos;
		      $scope.despachoCuadernillo.data = data;
			  angular.forEach($scope.despachoCuadernillo.resultados, function (cuadernillo) {
				  cuadernillo.selected = false;
			  });		      
			  //Paginacion
			  $scope.makeTodos($scope.despachoCuadernillo.resultados);		
		    }else{
		      $scope.setErr('data.msg', 'data.msg');
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
	};
	
	$scope.asignarUsuario = function(usuario){
		var cuadernillos = [];
		angular.forEach($scope.paginacion.todos, function (cuadernillo, index) {			
			if(cuadernillo.selected){
				cuadernillos.push(cuadernillo.id);				
			}
		});
		
		$scope.openLoadingModal("Reasignando cuadernillos","");
		var promise = inscripcionDigitalService.reasignarCuadernillos(encodeURIComponent(JSON.stringify(cuadernillos)), usuario);
		promise.then(function(data) {
			$scope.closeModal();
		    if(data.status===null){
		    }else if(data.status){
		    	$scope.openMensajeModal('success','Todos los cuadernillos fueron reasignados', '',  true, 3);
		    }else{
				$scope.openMensajeModal('error','No se pudieron reasignar todos los cuadernillos', '',  true, 3);
		    }
		    
			angular.forEach($scope.paginacion.todos, function (cuadernillo) {
				if(cuadernillo.selected)
					angular.forEach(data.listaOK, function (cuadernilloOK) {
						if(cuadernillo.id == cuadernilloOK.id){
							cuadernillo.selected=false;
							cuadernillo.usuario=cuadernilloOK.usuario;
							cuadernillo.fecha=cuadernilloOK.fecha;
						}
					});
			});
		    $scope.validarSelected();
		  }, function(reason) {
			  $scope.closeModal();
			  cuadernillosError++;
		});

	};	
	
	$scope.buscar();
	
	$scope.limpiar = function(){
		
		$scope.despachoCuadernillo.fechaDesde = null;
		$scope.despachoCuadernillo.fechaHasta = null;
		$scope.despachoCuadernillo.resultados = null;
		
		$scope.formListaCuadernillo.$setPristine(true);
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
  	
	$scope.openMensajeModal = function (tipo, titulo, detalle, autoClose, segundos) {
		var myModal = $modal.open({
			templateUrl: 'mensajeModal.html',
			backdrop: true,
			keyboard: true,
			windowClass: 'modal',
			controller: 'MensajeModalCtrl',
			resolve: {
				tipo: function () {
					return tipo;
				},
				titulo: function () {
					return titulo;
				},
				detalle: function () {
					return detalle;
				}
			}
		}); 
		
		if(autoClose){
			$timeout(function(){
				$scope.closeModal();
			},segundos*1000);
		}

		myModal.result.then(function () {
			$scope.doFocus('caratula');
		}, function () {
			$scope.doFocus('caratula');
		});			
	};
	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
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
		$scope.filtrar();
//		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
//		, end = begin + $scope.paginacion.numPerPage;
//		if(end>0)
//			$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	};  
	
	$scope.validarSelected = function(){
		if($scope.paginacion.todos.length>0){
			$scope.someSelected = false;
			$scope.allSelected = true;
			angular.forEach($scope.paginacion.todos, function (cuadernillo) {
				$scope.someSelected = $scope.someSelected || cuadernillo.selected;
				$scope.allSelected = $scope.allSelected && cuadernillo.selected;
			});
		}
	};
	
	$scope.checkAll = function () {

		if (!$scope.allSelected) {
			$scope.allSelected = true;
		} else {
			$scope.allSelected = false;
		}
		
		$scope.someSelected = $scope.allSelected;

		angular.forEach($scope.paginacion.todos, function (cuadernillo) {
			cuadernillo.selected = $scope.allSelected;
		});

	};	
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		$scope.filtrar();
		$scope.validarSelected();
		
//		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
//		, end = begin + $scope.paginacion.numPerPage;
//
//		if(end>0)
//			$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		
	}, true);
	
	$scope.filtrar = function(){
		$scope.filtered = filterFilter($scope.despachoCuadernillo.resultados, $scope.filterExpr);
		$scope.paginacion.todos = $scope.filtered;
		$scope.paginacion.currentPage = 1;
	}
});