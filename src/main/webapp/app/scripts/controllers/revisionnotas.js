'use strict';

app.controller('RevisionNotasCtrl', function ($scope,$timeout,$rootScope,solicitudesModel,revisionNotasModel,indiceService,solicitudService,inscripcionDigitalService,$filter,$modal,$modalStack,filterFilter) {

	$scope.busquedaRevisionNotas = revisionNotasModel.getBusquedaRevisionNotas();
	$scope.states = revisionNotasModel.getStates();
	$scope.tab = solicitudesModel.getTab();

	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
//		filteredTodos: [],
		todos: []
	}	
	
	$scope.filtro = {
		filterExpr: ""
	};
	
	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacion.todos=$scope.busquedaRevisionNotas.resultados;
	
	if($scope.busquedaRevisionNotas.anos==undefined || $scope.busquedaRevisionNotas.anos.length==0){
		var ano = (new Date).getFullYear();
		var j=0;
		//for(var i = ano-2; i <= ano; i++){
		for(var i = 1937; i <= ano; i++){
			$scope.busquedaRevisionNotas.anos.push({codigo: i, descripcion: i});

			if($scope.busquedaRevisionNotas.anos[j].codigo == ano){
				$scope.busquedaRevisionNotas.ano = $scope.busquedaRevisionNotas.anos[j];
			}	 

			j++;
		}
	}

	$scope.buscarFojaIniFojaFinCuadernillo = function(){
		var cuadernillo = $scope.busquedaRevisionNotas.cuadernillo,
		ano=$scope.busquedaRevisionNotas.ano.codigo;

		if(cuadernillo){
			$scope.openLoadingModal( 'Buscando Rango Cuadernillo...', '');

			var promise = inscripcionDigitalService.getFojaIniFojaFinCuadernillo(cuadernillo,ano);
			promise.then(function(data) {
				$scope.closeModal();
				if(data.status===null){

				}else if(data.status){

					if(data.fojainifojafin){
						$scope.busquedaRevisionNotas.fojaini = data.fojainifojafin.fojaIni;
						$scope.busquedaRevisionNotas.fojafin = data.fojainifojafin.fojaFin;

						$scope.buscarNotas();
					}else{
						$scope.raiseErr('No se encontro cuadernillo');
					}

				}else{
					$scope.raiseErr('Problema detectado');
				}
			}, function(reason) {

				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
		}
	};

	$scope.buscarNotas = function(){
		var fojaini = $scope.busquedaRevisionNotas.fojaini,
		fojafin = $scope.busquedaRevisionNotas.fojafin,
		ano = $scope.busquedaRevisionNotas.ano.codigo,
		cuadernillo = $scope.busquedaRevisionNotas.cuadernillo;

		$scope.openLoadingModal( 'Buscando...', '');

		var promise = inscripcionDigitalService.getAnotacionParaRevision(fojaini, fojafin, ano, cuadernillo);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.busquedaRevisionNotas.resultados = data.anotaciones;
				$scope.busquedaRevisionNotas.tieneDespacho = data.tieneDespacho;
				$scope.busquedaRevisionNotas.data = data;
				$scope.busquedaRevisionNotas.resultado_cuadernillo = $scope.busquedaRevisionNotas.cuadernillo;
				if($scope.busquedaRevisionNotas.resultados.length>0){
					$scope.makeTodos(data.anotaciones);
				}

			}else{
				$scope.raiseErr('Problema detectado');
			}
		}, function(reason) {

			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});
	};



	
	$scope.makeTodos = function(data) {
		$scope.paginacion = angular.copy($scope.paginacionMaster);
		$scope.paginacion.todos=data;
		$scope.filtrar();
//		$scope.busquedaRevisionNotas.indicebusqueda=true;

	};	

	$scope.aprobarNota = function(anotacion){

		$scope.openLoadingModal( 'Aprobando...', '');

		var promise = inscripcionDigitalService.actualizaEstadoAnotacion(anotacion.idAnotacion);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){
				$scope.busquedaRevisionNotas.resultados.splice($scope.busquedaRevisionNotas.resultados.indexOf(anotacion), 1);
				$scope.makeTodos($scope.busquedaRevisionNotas.resultados);				
			}else{
				$scope.raiseErr('Problema detectado '+data.msg);
			}
		}, function(reason) {

			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});

	};
	
	$scope.despacharCuadernillo = function(){
		var cuadernillo = $scope.busquedaRevisionNotas.resultado_cuadernillo;
		
		if(confirm("¿Está seguro que desea despachar el cuadernillo " + cuadernillo + "?")){
			
	
			if(cuadernillo){
				$scope.openLoadingModal( 'Buscando Rango Cuadernillo...', '');
	
				var promise = inscripcionDigitalService.despacharCuadernillo(cuadernillo);
				promise.then(function(data) {
					$scope.closeModal();
					if(data.status===null){
	
					}else if(data.status){
						$scope.openMensajeModal('success','Cuadernillo despachado', '',  true, 2);
						$scope.busquedaRevisionNotas.tieneDespacho = true;
	
					}else{
						$scope.raiseErr('Problema detectado');
					}
				}, function(reason) {
	
					$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
				});
			}
		}
	};	

	$scope.colapsar = function(){

		if($scope.busquedaRevisionNotas.indicebusqueda){
			$scope.busquedaRevisionNotas.indicebusqueda = false;
		}else{
			$scope.busquedaRevisionNotas.indicebusqueda = true;
		}

	};

	$scope.colapsarnuevabusqueda = function(){

		$scope.busquedaRevisionNotas.indicebusqueda = false;
		$scope.limpiarFormulario();
		$timeout(function(){
			$scope.doFocus('fojaini');
		}, 200);
	};

	$scope.limpiarFormulario = function(){
		$scope.resetResultadoFormulario();

		$scope.busquedaRevisionNotas.fojaini = null;
		$scope.busquedaRevisionNotas.fojafin = null;
		$scope.busquedaRevisionNotas.cuadernillo = null;

		$scope.myform.$setPristine(true);

		$scope.doFocus('fojaini');

	};

	$scope.resetResultadoFormulario = function(){
		$scope.busquedaRevisionNotas.data = null;
		$scope.states.buscar.isError = false;

		if($scope.busquedaRevisionNotas.resultados!==undefined){
			$scope.busquedaRevisionNotas.resultados.length = 0;
		}

	};

	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	$timeout(function(){
		if(null==$scope.busquedaRevisionNotas.data)
			$scope.doFocus('fojaini');
	}, 200);

	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};
	//fin validaciones titulo

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

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('warning',msg, '',  true, 2);
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
	
	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.verTitulo = function(titulo){

		var bis = titulo.bis==1?true:false,
		                       //realizar alternativa para saber tipo de documento;                       
		                       estado = '0';

		$rootScope.go('/verInscripcion/prop/'+titulo.foja+'/'+titulo.numero+'/'+titulo.ano+'/'+titulo.bis+'/revisionNotas/'+estado);

	};
	
	// $watch search to update pagination
	$scope.$watch('filtro.filterExpr', function (newVal, oldVal) {
		$scope.filtrar();
		
	});
	
	$scope.filtrar = function(){
		$scope.filtered = filterFilter($scope.busquedaRevisionNotas.resultados, $scope.filtro.filterExpr);
		$scope.paginacion.todos = $scope.filtered;
		$scope.paginacion.currentPage = 1;
	}
	
});