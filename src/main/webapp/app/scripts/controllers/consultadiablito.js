/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app
.controller(
	'ConsultaDiablitoCtrl',
	function($scope, $routeParams, $rootScope, $sce, $modal, $timeout,
		solicitudService, AnotacionService,
		inscripcionDigitalService, indiceService, digitalModel) {

		$scope.hoy = moment().format('DD/MM/YYYY');
		$scope.data = {};

		$scope.errorObj = {
			hay : false,
			level : 'warning', // warning o error
			title : '',
			msg : ''
		};

		$scope.parametros = {
			borrador : $routeParams.borrador,
			folio : $routeParams.folio,
			caratula : $routeParams.caratula
		};

		$scope.paginacionMaster = {
			currentPage: 1,
			numPerPage: 10,
			maxSize: 10,
			filteredTodos: [],
			todos: []
		}

		$scope.busquedaResumen = {
			foja: null,
			numero: null,
			ano: null,
			bis: null,
			estado: null,
			data: null,
			predicate: null,
			reverse: null
		};

		$scope.isLoadingSolicitar = false;
		$scope.statusSolicitar = {
			ok : false,
			error : false
		};

		$scope.cerrar = function() {
			$rootScope.go('/home');
		};

		$scope.setSolicitarOK = function() {
			$scope.resetStatusSolicitar();
			$scope.statusSolicitar.ok = true;
			$scope.setSolicitarMsg('', '');
		};

		$scope.setSolicitarError = function(msgTitle, msg) {
			$scope.resetStatusSolicitar();
			$scope.statusSolicitar.error = true;
			$scope.setSolicitarMsg(msgTitle, msg);
		};

		$scope.resetStatusSolicitar = function() {
			$scope.statusSolicitar.ok = false;
			$scope.statusSolicitar.error = false;
			$scope.statusSolicitar.msgTitle = '';
			$scope.statusSolicitar.msg = '';
		};

		$scope.setSolicitarMsg = function(msgTitle, msg) {
			$scope.statusSolicitar.msgTitle = msgTitle;
			$scope.statusSolicitar.msg = msg;
		};

		$scope.raiseErr = function(level, title, msg) {
			$scope.errorObj.hay = true;
			$scope.errorObj.level = level;
			$scope.errorObj.title = title;
			$scope.errorObj.msg = msg;
		};

		$scope.inicia = function() {
			$scope.isLoading = true;
			
			var promise = inscripcionDigitalService.getResumenNotas(
				$scope.parametros.caratula, $scope.parametros.borrador, $scope.parametros.folio);
			promise
			.then(
				function(data) {
					if (data.status == null) {

					} else if (data.status) {

						$scope.busquedaResumen.data = data;

						$scope.makeTodos();

						$scope.isLoading = false;
					} else {
						$scope.raiseErr('error',
							'Error detectado.',
							data.msg);
					}
					$scope.isLoading = false;

				},
				function(reason) {
					$scope.isLoading = false;
					$scope
					.raiseErr('error',
						'Error detectado.',
					'No se ha podido establecer comunicación.');

				});
		};

		$scope.inicia();

		$scope.verInscripcion = function(titulo){
			var bis = titulo.bis==1?true:false;

			$rootScope.go('/verInscripcion/prop/'+titulo.foja+'/'+titulo.numero+'/'+titulo.ano+'/'+bis+'/consultadiablito/0/'+$scope.parametros.caratula+'/'+$scope.parametros.borrador+'/'+$scope.parametros.folio);
		};

		$scope.verEstadoCaratula = function(numeroCaratula) {
			$modal.open({
				templateUrl : 'estadoIndiceModal.html',
				backdrop : 'static',
				windowClass : 'modal modal-dialog-xl',
				controller : 'EstadoIndiceModalCtrl',
				size : 'lg',
				resolve : {
				numeroCaratula : function() {
				return parseInt(numeroCaratula);
			}
			}
			});
		};

		$scope.verRepertorio = function(numeroRepertorio) {
			$modal.open({
				templateUrl : 'repertorioModal.html',
				backdrop : 'static',
				windowClass : 'modal modal-dialog-xl',
				controller : 'RepertorioModalCtrl',
				size : 'lg',
				resolve : {
				numeroRepertorio : function() {
				return parseInt(numeroRepertorio);
			}
			}
			});
		};	

		$scope.irGpOnlineAIO = function(borrador,folio){
			$modal.open({
				templateUrl: 'gpOnlineModal.html',
				backdrop: 'static',
				windowClass: 'modal modal-dialog-xl', 
				controller: 'gpOnlineModalCtrl',
				size: 'lg',
				resolve: {  
				borrador : function(){
				return parseInt(borrador);
			},
			folio : function(){
				return parseInt(folio);
			}
			}
			});
		};

		$scope.makeTodos = function() {

			$scope.paginacionResumen = angular.copy($scope.paginacionMaster);

			$scope.paginacionResumen.todos=$scope.busquedaResumen.data.anotaciones;

			var begin = (($scope.paginacionResumen.currentPage - 1) * $scope.paginacionResumen.numPerPage)
			, end = begin + $scope.paginacionResumen.numPerPage;

			$scope.paginacionResumen.filteredTodos = $scope.paginacionResumen.todos.slice(begin, end);

//			$scope.paginacionResumen.maxSize = Math.round($scope.paginacionResumen.todos.length / $scope.paginacionResumen.numPerPage);

		};

		// $watch search to update pagination
		$scope.$watch('filterExpr', function (newVal, oldVal) {

			if(newVal!=undefined && oldVal!=undefined){
				$scope.filtered = filterFilter($scope.busquedaTareas.data.caratulas, newVal);
				$scope.paginacionResumen.todos = $scope.filtered;
				$scope.paginacionResumen.currentPage = 1;

				var begin = (($scope.paginacionResumen.currentPage - 1) * $scope.paginacionResumen.numPerPage)
				, end = begin + $scope.paginacionResumen.numPerPage;

				$scope.paginacionResumen.filteredTodos = $scope.paginacionResumen.todos.slice(begin, end);

//				$scope.paginacionResumen.maxSize = Math.round($scope.paginacionResumen.todos.length / $scope.paginacionResumen.numPerPage);
			}

		}, true);

	});





