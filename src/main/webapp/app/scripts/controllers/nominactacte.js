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

app.controller('NominaCtaCteCtrl', function ($scope,$timeout,$window,solicitudesModel,certificacionModel, caratulaService,$modal,$modalStack,filterFilter, estadoService) {

//	var fechaHoy = new Date().toJSON().split('T')[0];
	var fechaHoy = new Date();
	var m = fechaHoy.getMonth();
	m = m+1;
	var y = fechaHoy.getFullYear();

	$scope.nominaCtaCte = {
		mes:y+'-'+m,
		resultados: [],
		cuenta:null,
		tipoCierre:null,
		total:null,
		fechaInicioCierre:null,
		fechaFinCierre:null,
		numeroBoleta:null,
		urlBoleta:null
	};
	$scope.nominaCtaCte.mes

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
	$scope.paginacion.todos=$scope.nominaCtaCte.resultados;

	$scope.refrescar = function(){
		$scope.openLoadingModal('Cargando Listado...', '');
		
		if($scope.nominaCtaCte.estado!=null)
			var tipoCierre = $scope.nominaCtaCte.estado.codigo;
		
		
		var promise = caratulaService.getCierreCtaCte($scope.nominaCtaCte.cuenta,$scope.nominaCtaCte.mes,tipoCierre);
		promise.then(function(data) {
			if(data.status===null){
			}else if(data.status){

				$scope.nominaCtaCte.data = data;
				$scope.nominaCtaCte.resultados = data.detalleNominas;
				$scope.nominaCtaCte.total = data.total;
				$scope.nominaCtaCte.fechaInicioCierre = data.fechaInicioCierre;
				$scope.nominaCtaCte.fechaFinCierre = data.fechaFinCierre;
				$scope.nominaCtaCte.numeroBoleta = data.numeroBoleta;
				$scope.nominaCtaCte.urlBoleta = data.urlBoleta;

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
	
	$scope.downloadBoleta = function() {
		var documento ={
			"nombreArchivo": $scope.nominaCtaCte.numeroBoleta+".pdf",
			"idTipoDocumento": 12, //idTipoDocumento BOLETA
			"idReg": 0
		};console.log(documento);
		
		//existe documento
		var promise = estadoService
		.existeDocumento(documento);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=downloadDocumento&documento='+ encodeURIComponent(JSON.stringify(documento));
				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
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

	if($scope.nominaCtaCte.estados==undefined || $scope.nominaCtaCte.estados.length==0){
		$scope.nominaCtaCte.estados = [{
			codigo:1,
			descripcion: 'Parcial'
		}];
	}

	$scope.makeTodos = function() {

		$scope.paginacion = angular.copy($scope.paginacionMaster);

		$scope.paginacion.todos=$scope.nominaCtaCte.resultados;

		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	};
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		$scope.filtered = filterFilter($scope.nominaCtaCte.resultados, newVal);
		$scope.paginacion.todos = $scope.filtered;
		$scope.paginacion.currentPage = 1;
		
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		
	}, true);
	
	$scope.limpiarTitulo = function(){
		
		$scope.nominaCtaCte.data = null;
		$scope.states.buscar.isError = false;

		if($scope.nominaCtaCte.resultados!==undefined){
			$scope.nominaCtaCte.resultados.length = 0;
		}

		$scope.nominaCtaCte.mes = y+'-'+m;
		$scope.nominaCtaCte.cuenta = null;
		$scope.nominaCtaCte.tipoCierre = null;
		$scope.nominaCtaCte.total = null;
		
		$scope.formctacte.$setPristine(true);
		
		$scope.doFocus('cuenta');
	};
	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};
	
	//fin utils
	$timeout(function() {
		$scope.doFocus('cuenta');
	}, 500);

	$scope.exportarNominaExcel = function () {

		var tipoCierre = 0;
		if($scope.nominaCtaCte.estado!=null)
			tipoCierre = $scope.nominaCtaCte.estado.codigo;

		$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/caratula?metodo=exportarNominaExcel&tipoCierre='+tipoCierre+'&cuenta='+$scope.nominaCtaCte.cuenta+'&mes='+$scope.nominaCtaCte.mes;

	};

});