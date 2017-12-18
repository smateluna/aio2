'use strict';

app.filter('startFrom', function () {
	return function (input, start) {
		if (input) {
			start = +start;
			return input.slice(start);
		}
		return [];
	};
});

app.controller('TareasCtrl', function ($scope, $timeout, $rootScope, $location, $interval, $filter, $modal, $modalStack, $routeParams, tareasService, tareasModel, caratulaService,filterFilter) {

	$scope.busquedaTareas = tareasModel.getBusquedaTareas();
	
	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 150,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}
	
	$scope.paginacionTareas = angular.copy($scope.paginacionMaster);
	
	$scope.buscarLiquidaciones = function(){	
		$scope.openLoadingModal('Buscando carátulas...', '');	
		var promise = tareasService.obtenerCaratulasPorUsuario();
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){
				$scope.busquedaTareas.data = data;
				//$scope.busquedaTareas.caratulas = data.caratulas;
				//$scope.fechaRehacerImagen = data.FECHA_REHACER_IMAGEN;
				
				$scope.makeTodos();
			}else{
				$scope.raiseErr(data.msg);
			}
		}, function(reason) {
			$scope.closeModal();
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});		  
	}

	$scope.refrescar = function(){
		$scope.openLoadingModal('Actualizando...', '');
		var promise = tareasService.obtenerCaratulasPorUsuario();
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){
			}else if(data.status){
				$scope.busquedaTareas.data = data;
			}else{
				$scope.setErr('data.msg', data.msg);

			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
		});
	}; 

	$scope.redistribuirCaratula = function(inscripcion){
		$scope.openLoadingModal('Redistribuyendo...', '');
		var promise = caratulaService.movercaratula(inscripcion.numeroCaratula, inscripcion.codSeccion);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){
			}else if(data.status){	 
				var tipoFormulario =  inscripcion.codSeccion==='C2'?10:inscripcion.idtipoFormulario;	    	
				$rootScope.go('/verInscripcionCertificar/prop/'+inscripcion.numeroCaratula+'/'+inscripcion.foja+'/'+inscripcion.numero+'/'+inscripcion.ano+'/'+inscripcion.bis+'/'+inscripcion.fechaDocLong+'/'+inscripcion.rehaceImagen+'/'+tipoFormulario+'/tareas/');
			}else{
				$scope.setErr('data.msg', data.msg);
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
		});

	}  
	
	$scope.distribuir = function(){
		var caratulasDistribucion = [];
		for(var i in $scope.busquedaTareas.data.caratulas){
			if($scope.busquedaTareas.data.caratulas[i].check)
				caratulasDistribucion.push($scope.busquedaTareas.data.caratulas[i].numeroCaratula);			
		};
		$location.path('/distribucion/').search({'caratulas': JSON.stringify(caratulasDistribucion)});
	};
	
	$scope.hayCaratulaSeleccionada = function(){
		var caratulasDistribucion = [];
		if($scope.busquedaTareas.data!=undefined)
			for(var i in $scope.busquedaTareas.data.caratulas)
				if($scope.busquedaTareas.data.caratulas[i].check)
					return true;		
		return false;
	};

	$scope.caratulafuera = {
		caratula:null,
		status:false,
		muestra:false
	};  

	$scope.buscarCaratulaFuera = function(){

		var caratula = $scope.busquedaTareas.caratula;

		$scope.openLoadingModal('Buscando...', '');

		var promise = caratulaService.obtenerCaratula(caratula);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.caratulafuera.caratula = data.caratula;
				$scope.caratulafuera.status = data.status;
				$scope.caratulafuera.muestra = data.muestra;

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};  

	$scope.masInfoLiquidacion = function (caratula) {

		var myModal = $modal.open({
			templateUrl: 'masInfoLiquidacionModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'masInfoLiquidacionModalCtrl',
			resolve: { 
			caratula: function(){
			return caratula;
		}
		}
		});
	};

	$scope.verInscripcionCertificar = function(caratula){
		var rehacerImagen = $scope.busquedaTareas.data.FECHA_REHACER_IMAGEN<=caratula.inscripcionDigitalDTO.ano?0:1	 
		                                                                                                       var bis = caratula.inscripcionDigitalDTO.bis?1:0;
		var tipoFormulario =  caratula.estadoActualCaratulaDTO.seccionDTO.codigo==='C2'?10:caratula.tipoFormularioDTO.id;

		$rootScope.go('/verInscripcionCertificar/prop/'+caratula.numeroCaratula+'/'+caratula.inscripcionDigitalDTO.foja+'/'+caratula.inscripcionDigitalDTO.numero+'/'+caratula.inscripcionDigitalDTO.ano+'/'+bis+'/'+caratula.inscripcionDigitalDTO.fechaActualizacionL+'/'+rehacerImagen+'/'+tipoFormulario+'/tareas/');
	};   

	$scope.verCbrsVisor = function(caratula){
		$rootScope.go('cbrsVisor:'+caratula.numeroCaratula);
	};     

	$scope.verEstadoCaratula = function(numeroCaratula){
		$modal.open({
			templateUrl: 'estadoIndiceModal.html',
			backdrop: 'static',
			windowClass: 'modal modal-dialog-xl', 
			controller: 'EstadoIndiceModalCtrl',
			size: 'lg',
			resolve: {  
			numeroCaratula : function(){
			return parseInt(numeroCaratula);
		}
		}
		});
	};    

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error',msg, '',  false, null);
	};

	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	$scope.openMensajeModal = function (tipo, titulo, detalle, autoClose, segundos) {
		$modal.open({
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

	$timeout(function(){
		$scope.buscarLiquidaciones();
	}, 500);      

	$scope.obtenerEscritura = function (sol) {

		var myModal = $modal.open({
			templateUrl: 'escrituraelectronicaModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'escrituraElectronicaModalCtrl',
			resolve: { 
			sol: function(){
			return sol;
		}
		}
		});
		
		myModal.result.then(function () {
			$scope.buscarLiquidaciones();
		}, function () {
			$scope.buscarLiquidaciones();
		});
	};
	
	$scope.obtenerEscrituraPorFuera = function (caratula) {

		var myModal = $modal.open({
			templateUrl: 'escrituraelectronicaModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'escrituraElectronicaModalCtrl',
			resolve: { 
			sol: function(){
			return caratula;
		}
		}
		});
		
		myModal.result.then(function () {
			$scope.buscarLiquidaciones();
		}, function () {
			$scope.buscarLiquidaciones();
		});
	};
	
	$scope.verDocumentoEstudio = function(sol){
		$rootScope.go('/verEscrituraEstudio/'+sol.numeroCaratula+'/tareas');
	};

	$scope.imprimeCaratula = function(sol) {
		$modal.open( {
			templateUrl : 'verImpresionCaratula.html',
			backdrop : true,
			keyboard : true,
			size : 'lg',
			windowClass : 'modal modal-dialog-xl',
			controller : 'VerImprimeCaratulaCtrl',
			resolve : {
			nc : function() {
			return sol.numeroCaratula;
		}
		}
		});

	};
	
	$scope.makeTodos = function() {

		$scope.paginacionTareas = angular.copy($scope.paginacionMaster);

		$scope.paginacionTareas.todos=$scope.busquedaTareas.data.caratulas;

		var begin = (($scope.paginacionTareas.currentPage - 1) * $scope.paginacionTareas.numPerPage)
		, end = begin + $scope.paginacionTareas.numPerPage;

		$scope.paginacionTareas.filteredTodos = $scope.paginacionTareas.todos.slice(begin, end);
		
		$scope.paginacionTareas.maxSize = Math.round($scope.paginacionTareas.todos.length / $scope.paginacionTareas.numPerPage);

	};
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		
		if(newVal!=undefined && oldVal!=undefined){
			$scope.filtered = filterFilter($scope.busquedaTareas.data.caratulas, newVal);
			$scope.paginacionTareas.todos = $scope.filtered;
			$scope.paginacionTareas.currentPage = 1;

			var begin = (($scope.paginacionTareas.currentPage - 1) * $scope.paginacionTareas.numPerPage)
			, end = begin + $scope.paginacionTareas.numPerPage;

			$scope.paginacionTareas.filteredTodos = $scope.paginacionTareas.todos.slice(begin, end);
			
			$scope.paginacionTareas.maxSize = Math.round($scope.paginacionTareas.todos.length / $scope.paginacionTareas.numPerPage);
		}
		
	}, true);
	
	$scope.$watch('busquedaTareas.tiemporefresco', function() {
	  $scope.start();
    });
	
	var promise;
    
    $scope.start = function() {
      // stops any running interval to avoid two intervals running at the same time
      $scope.stop(); 
      
      // store the interval promise
      if($scope.busquedaTareas.tiemporefresco!=0){
      			promise = $interval(function(){
      						$scope.refrescar();
   						  }.bind(this), $scope.busquedaTareas.tiemporefresco*60000);
      }
      
    };
  
    // stops the interval
    $scope.stop = function() {
      $interval.cancel(promise);
    };
    
    $scope.start();
   
    $scope.$on('$destroy', function() {
    	$scope.stop(); 
//    	console.log('saliendo de tareas');
    });

});