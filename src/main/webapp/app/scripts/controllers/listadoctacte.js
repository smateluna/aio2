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

app.controller('ListadoCtaCteCtrl', function ($scope,$timeout,solicitudesModel,certificacionModel, caratulaService,$modal,$modalStack,filterFilter) {

	$scope.fechahoy = new Date().toJSON().split('T')[0];

	$scope.busquedaCtaCte = {
		fechaDesde:$scope.fechahoy,
		fechaHasta:$scope.fechahoy,
		resultados: [],
		cuenta:null,
		estado:null
	};

	$scope.states = certificacionModel.getStates();
	$scope.tab = solicitudesModel.getTab();

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null,
		tipo: null
	};

	$scope.solicita = {
		foja: null,
		numero: null,
		ano: null,
		bis: false,
		tipo: null,
		esDigital: false,
		tieneRechazo: false
	};

	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}

	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacion.todos=$scope.busquedaCtaCte.resultados;

	$scope.refrescar = function(){
		$scope.openLoadingModal('Cargando Listado...', '');
		
		if($scope.busquedaCtaCte.estado!=null)
			var estado = $scope.busquedaCtaCte.estado.codigo;
		
		
		var promise = caratulaService.getListadoCaratulasPorCtaCte($scope.busquedaCtaCte.fechaDesde,$scope.busquedaCtaCte.fechaHasta,$scope.busquedaCtaCte.cuenta,estado);
		promise.then(function(data) {
			if(data.status===null){
			}else if(data.status){
				$scope.busquedaCtaCte.data = data;
				$scope.busquedaCtaCte.resultados = data.listacaratulas;

				//Paginacion
				$scope.makeTodos();

				$scope.closeModal();
			}else{
				$scope.setErr('data.msg', data.msg);
				$scope.closeModal();
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
			$scope.closeModal();
		});
	};

	$scope.resolveModal = {
		refresca: false
	};

	//utiles	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
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

	$scope.raiseErr = function(key, title, msg){
		$scope.closeModal();
		$scope.states[key].isError = true;
		$scope.states[key].title = title;
		$scope.states[key].msg = msg;
	};

	$scope.resetStatus = function(){
		$scope.solicitudStatus.ok = false;
		$scope.solicitudStatus.error = false;
		$scope.solicitudStatus.warning = false;
		$scope.solicitudStatus.msgTitle = null;
		$scope.solicitudStatus.msg = null;
	};

	$scope.setErr = function(title, mensaje){
		$scope.solicitudStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};

	$scope.setStatusMsg = function(title, mensaje){
		$scope.solicitudStatus.msgTitle = title;
		$scope.solicitudStatus.msg = mensaje;
	};

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	if($scope.busquedaCtaCte.estados==undefined || $scope.busquedaCtaCte.estados.length==0){
		$scope.busquedaCtaCte.estados = [{
			codigo:0,
			descripcion: 'En Proceso'
		},{
			codigo:2,
			descripcion: 'Rechazadas'
		},{
			codigo:1,
			descripcion:'Finalizadas'
		}];
	}

	$scope.makeTodos = function() {

		$scope.paginacion = angular.copy($scope.paginacionMaster);

		$scope.paginacion.todos=$scope.busquedaCtaCte.resultados;

		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	};
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		$scope.filtered = filterFilter($scope.busquedaCtaCte.resultados, newVal);
		$scope.paginacion.todos = $scope.filtered;
		$scope.paginacion.currentPage = 1;
		
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		
	}, true);
	
	$scope.limpiarTitulo = function(){
		
		$scope.busquedaCtaCte.data = null;
		$scope.states.buscar.isError = false;
		$scope.busquedaCtaCte.predicate = null;

		if($scope.busquedaCtaCte.resultados!==undefined){
			$scope.busquedaCtaCte.resultados.length = 0;
		}

		$scope.busquedaCtaCte.fechaDesde = $scope.fechahoy;
		$scope.busquedaCtaCte.fechaHasta = $scope.fechahoy;
		$scope.busquedaCtaCte.cuenta = null;
		$scope.busquedaCtaCte.estado = null;
		
		$scope.formctacte.$setPristine(true);
	};


});