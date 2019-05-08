'use strict';

app.controller('listadoPendienteCtrl', function ($scope, $modal, $modalStack, $modalInstance, $timeout, caratulaService) {

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
	$scope.envioParcial = false;
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
		$scope.envioParcial = false;
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

	$scope.enviarEntregaDoc = function (nCaratulas) {
		$scope.limpiarmensajes();
		var exito=true;
		$scope.envioEnProceso = true;
		var contador=0;
		var contError=0;
		$scope.porcentaje=0;		
		
		$scope.listadoPendiente.resultados.forEach(function(resultado, index) {
			if(resultado.id.Selected){  				
				caratulaService.moverCaratulaLiquidacion(resultado.id.caratula, resultado.id.esCtaCte).then(function(data) {
					contador++;
					if(data.status===null){
					}else if(data.status){
						$scope.listadoPendiente.resultados.splice($scope.listadoPendiente.resultados.indexOf(resultado), 1);
					}else{
						contError++;
					}
					
					$scope.porcentaje =parseInt(contador/nCaratulas*100);
					
					if(contador==nCaratulas){
						$scope.envioEnProceso = false;						
						
						if(contError==0){
							$scope.envioExitoso=true;	
						} else if(contError>0 && contError<nCaratulas){
							$scope.mensaje='Algunas carátulas no se han podido enviar. Intente nuevamente.';
							$scope.envioParcial=true;
							exito=false;
						} else{
							$scope.mensaje='No se pudo enviar las carátulas a Entrega Documentos. Intente nuevamente.';
							$scope.envioError=true;
							exito=false;
						}
					}					
				}, function(reason) {
					contError++;					
					$scope.porcentaje =parseInt(contador/nCaratulas*100);
					$scope.mensaje='No se ha podido establecer comunicación con el servidor';					
				});					
					
			}
		});		
		
		
//		angular.forEach($scope.listadoPendiente.resultados, function (resultado) {
//			if(resultado.id.Selected){
//				
//				$timeout(function(){
//					console.log((contador++) + " de " + nCaratulas);
//				},2000);
//				
//				var caratula ={
//						"numeroCaratula": resultado.id.caratula,
//						"esCtaCte": resultado.id.esCtaCte
//				};
////				caratulas.push(caratula);
//				
////				caratulaService.movercaratulaLiquidacion(caratula).then(function(data) {
////					if(data.status===null){
////					}else if(data.status){
////						$scope.openMensajeModal('success', 'Carátulas enviadas a Entrega Documentos', '', true, 3);
////						$scope.envioExitoso=true;
////						$scope.envioEnProceso = false;
////						$scope.verListadoPendienteUsuario();
////					}else{
////						$scope.envioError=true;
////						$scope.mensaje=data.msg;
////						exito=false;
////						$scope.envioEnProceso = false;
////					}
////				}, function(reason) {
////					$scope.envioError=true;
////					$scope.mensaje='No se ha podido establecer comunicación con el servidor';
////					exito=false;
////					$scope.envioEnProceso = false;
////				});				
//			}
//		});

		
		
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
	
	$scope.raiseSuccess = function(msg) {
		$scope.openMensajeModal('success', msg, '', false, null);
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
