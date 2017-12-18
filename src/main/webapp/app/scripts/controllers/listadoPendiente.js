'use strict';

app.controller('listadoPendienteCtrl', function ($scope, $modal, $modalStack, $modalInstance, caratulaService) {

	$scope.listadoPendiente = {
			resultados: [],
			data:null,
			secciones:null,
			seccion:null
	};

	$scope.caratulas = 
	{
			caratula: null,
			esctacte: null
	};

	$scope.verMisCaratulas=true;
	$scope.selectedAll = true;
	$scope.envioExitoso = false;
	$scope.eliminacionExitosa = false;
	$scope.envioEnProceso = false;
	$scope.envioError = false;
	$scope.mensaje = '';
	$scope.filterExprLiquidacion = '';

	$scope.filterExprLiquidacion = {
			valor : ''
	};

	$scope.raiseErr = function(level, title, msg) {
		$scope.errorObj.hay = true;
		$scope.errorObj.level = level;
		$scope.errorObj.title = title;
		$scope.errorObj.msg = msg;
	};

	$scope.errorObj = {
			hay : false,
			level : 'warning', // warning o error
			title : '',
			msg : ''
	};

	$scope.verListadoPendienteUsuario = function(){

		$scope.openLoadingModal('Buscando...', '');

		$scope.verMisCaratulas=true;
		
		var promise = caratulaService.getListadoCaratulasPendientesPorUsuario();
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){
				$scope.limpiar();

				$scope.listadoPendiente.data = data;
				$scope.listadoPendiente.resultados = data.listacaratulas;

				angular.forEach($scope.listadoPendiente.resultados, function (caratula) {
					caratula.id.Selected = $scope.selectedAll;
				});

			}else{
				$scope.raiseErr(data.msg);
			}
		}, function(reason) {
			$scope.closeModal();
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});
	};

	$scope.verListadoPendienteSeccion = function(seccion){
		
			$scope.openLoadingModal('Buscando...', '');
			
			$scope.verMisCaratulas=false;
			
			var promise = caratulaService.getListadoCaratulasPendientesPorSeccion(seccion);
			promise.then(function(data) {
				$scope.closeModal();
				if(data.estado===null){
				}else if(data.status){
					$scope.limpiar();
	
					$scope.listadoPendiente.data = data;
					$scope.listadoPendiente.resultados = data.listacaratulas;
	
					angular.forEach($scope.listadoPendiente.resultados, function (caratula) {
						caratula.id.Selected = $scope.selectedAll;
					});
	
				}else{
					$scope.raiseErr(data.msg);
				}
			}, function(reason) {
				$scope.closeModal();
				$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
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

	$scope.limpiar = function(){
		$scope.listadoPendiente.resultados = null;
		$scope.selectedAll = true;
	}

	$scope.limpiarmensajes = function(){
		$scope.envioExitoso = false;
		$scope.envioEnProceso = false;
		$scope.envioError = false;
		$scope.mensaje = '';
	}

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

	$scope.checkAll = function () {

		if (!$scope.selectedAll) {
			$scope.selectedAll = true;
		} else {
			$scope.selectedAll = false;
		}

		angular.forEach($scope.listadoPendiente.resultados, function (caratula) {
			caratula.id.Selected = $scope.selectedAll;
		});

	};

	$scope.enviarEntregaDoc = function () {
		$scope.limpiarmensajes();
		var exito=true;
		$scope.envioEnProceso = true;

		caratulaService.movercaratulaLiquidacion($scope.listadoPendiente.resultados).then(function(data) {
			if(data.status===null){
			}else if(data.status){
				$scope.envioExitoso=true;
				$scope.envioEnProceso = false;
				$scope.verListadoPendienteUsuario();
			}else{
				$scope.envioError=true;
				$scope.mensaje=data.msg;
				exito=false;
				$scope.envioEnProceso = false;
			}
		}, function(reason) {
			$scope.envioError=true;
			$scope.mensaje='No se ha podido establecer comunicación con el servidor';
			exito=false;
			$scope.envioEnProceso = false;
		});
	};
	
	$scope.eliminarCaratulaPendiente = function (caratula) {
		$scope.limpiarmensajes();
		var exito=true;
		$scope.envioEnProceso = true;

		caratulaService.eliminarCaratulaPendiente(caratula).then(function(data) {
			if(data.status===null){
			}else if(data.status){
				$scope.eliminacionExitosa=true;
				$scope.envioEnProceso = false;
				$scope.verListadoPendienteUsuario();
			}else{
				$scope.envioError=true;
				$scope.mensaje=data.msg;
				exito=false;
				$scope.envioEnProceso = false;
			}
		}, function(reason) {
			$scope.envioError=true;
			$scope.mensaje='No se ha podido establecer comunicación con el servidor';
			exito=false;
			$scope.envioEnProceso = false;
		});
	};

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error',msg, '',  false, null);
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

	};

	$scope.$watch('filterExprLiquidacion.valor', function (newVal, oldVal) {

		if (newVal!='') {
			$scope.selectedAll = true;
		}else{
			$scope.selectedAll = false;
		}

		$scope.checkAll();

	}, true);

	$scope.verListadoPendienteUsuario();

//	if($scope.listadoPendiente.secciones==undefined || $scope.listadoPendiente.secciones.length==0){
//		$scope.listadoPendiente.secciones = [{
//			codigo:51,
//			descripcion: 'Liquidación Propiedades'
//		},{
//			codigo:53,
//			descripcion: 'Liquidación Hipotecas'
//		},{
//			codigo:54,
//			descripcion:'Liquidación Prohibiciones'
//		}];
//	}

});
