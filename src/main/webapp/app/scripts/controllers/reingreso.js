'use strict';

app.controller('ReingresoCtrl', function ($scope, $timeout, localStorageService, $log, reingresoService,caratulaService, $modal, $modalStack, $routeParams, $sce, $rootScope) {


	$scope.submitted = false;
	$scope.req = {
		numeroCaratula: null
	};
	//$scope.notario = undefined;
	//$scope.codigoExtracto = undefined;
	//$scope.workflow = undefined;


	$timeout(function(){
		$scope.doFocus('numeroCaratula');
	}, 500);

	$scope.buscarCaratula = function(){
		$scope.openLoadingModal('Buscando carátula #'+$scope.req.numeroCaratula+'...', '');

		var promise = reingresoService.getCaratula($scope.req.numeroCaratula);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.estado){

				$scope.data = data;
				$scope.dataOriginal = angular.copy($scope.data);

				if(data.inscripcionDigitalDTO != null)
					$scope.data.caratulaDTO.inscripcionDigitalDTO = data.inscripcionDigitalDTO;

				var promise = reingresoService.getListas($scope.data.caratulaDTO);
				promise.then(function(data) {
					if(data.estado===null){
					}else if(data.estado){
						$scope.listaRegistros = data.listaRegistros;
						$scope.listaTiposFormulario = data.listaTiposFormulario;	
						
						$scope.listaWorkflowsJSONIns=data.listaWorkflowsJSONIns;
						$scope.listaWorkflowsJSONSolVig=data.listaWorkflowsJSONSolVig;
						$scope.listaWorkflowsJSONSolCv=data.listaWorkflowsJSONSolCv;
						$scope.listaWorkflowsJSONSolSv=data.listaWorkflowsJSONSolSv;
						
						$scope.llenaComboWorkFlow();
						
					}else{
						$scope.raiseErr(data.msg);
					}
				}, function(reason) {
					$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
				});
				
				$scope.obtenerBitacoraCaratula();

//				$scope.doFocus('formulario');  

			}else{
				$scope.raiseErr(data.msg);
				$scope.limpiar();
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});
	};

	$scope.recargaListas = function(){
		var promise = reingresoService.getListas($scope.data.caratulaDTO);
		promise.then(function(data) {
			if(data.estado===null){
			}else if(data.estado){
				$scope.listaTiposFormulario = data.listaTiposFormulario;	
			}else{
				$scope.raiseErr(data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		}); 		
	}


	$scope.getListaNotarios = function(query){
		var listaNotarios = [];
		var promise = reingresoService.getListaNotarios(query);
		return promise.then(function(data){
			return data;

			/*return data.map(function(item){
	            return item.nombre;
	          });*/
		});
	};



	$scope.reingresarCaratula = function(){
		$scope.openLoadingModal('Reingresando carátula #'+$scope.req.numeroCaratula+'...', '');
		if ($scope.formReingresar.$valid) {
			var promise = reingresoService.reingresarCaratula($scope.data.caratulaDTO, $scope.dataOriginal.caratulaDTO, 
				$scope.data.observacion, $scope.data.workflow, $scope.data.codigoExtracto, $scope.data.notario);
			promise.then(function(data) {
				$scope.closeModal();
				if(data.estado===null){
				}else if(data.estado){
					$scope.data = data;
					$scope.dataOriginal = angular.copy($scope.data);
					if(data.msg){
						$scope.raiseSuccess(data.msg);
						$timeout(function(){
							$scope.closeModal();
							if(data.reingresoGP){
								$scope.verEstadoCaratula(data.reingresoGP);
//								$scope.urlPDF = $sce.trustAsResourceUrl('../do/service/reingreso?metodo=imprimirReingresoGP&caratula='+data.reingresoGP);
//								$timeout(function(){
//									 $scope.printReingresoGP();
//								},8000);
								$scope.printReingresoGPCaratula(data.reingresoGP);
							}
						},2000);
					}

					$scope.limpiar();
					
					
				}else{
					$scope.raiseErr(data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
			});

		} else {
			$scope.formEditar.submitted = true;
		}
	};	
	
	$scope.printReingresoGPCaratula = function(caratula) {
		$scope.openLoadingModal('Generando pdf...', '');
		$scope.urlPDF = $sce.trustAsResourceUrl('../do/service/reingreso?metodo=imprimirReingresoGP&caratula='+caratula);
		$timeout(function(){
			 $scope.printReingresoGP();
			 $scope.closeModal();
		},4000);
	}	
	
	$scope.printReingresoGP = function() {
	    window.frames["pdfReingreso"].focus();
	    window.frames["pdfReingreso"].print();
	}		

	$scope.limpiar = function(){
		$scope.reset();
		$scope.req.numeroCaratula = null;
		//$scope.workflow = undefined;
		//$scope.notario = undefined;
		//$scope.codigoExtracto = undefined;
		$scope.doFocus('numeroCaratula');
	};

	$scope.reset = function(){
		$scope.data = {};
		$scope.dataOriginal = {};
	};

	//UTILES  
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error',msg, '',  false, null);
	};

	$scope.raiseSuccess = function(msg){
		$scope.openMensajeModal('success',msg, '',  false, null);
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


	$scope.openRechazoModal = function () {
		var dataRechazo = {
			numeroCaratula : $scope.req.numeroCaratula
		};

		$modal.open({
			templateUrl: 'rechazoModal.html',
			backdrop: 'static',
			keyboard: false,
			//size: 'sm',
			windowClass: 'modal',
			controller: 'RechazoModalCtrl',
			resolve: {
			dataRechazo: function(){
			return dataRechazo;
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

	$scope.stringIsNumber = function(s) {
		var x = +s;
		return x.toString() === s;
	};

	//init
	if($routeParams.caratula!==undefined){
		//Si caratula viene en el request, buscar
		if($scope.stringIsNumber($routeParams.caratula) && $routeParams.caratula.length<=10){

			$scope.req.numeroCaratula = $routeParams.caratula;

			$timeout(function(){
				$scope.buscarCaratula();
			}, 500);
		}
	} else{
		//Si no, buscar caratula en sesion y buscar datos si existe
		var promise = reingresoService.getCaratulaSesion();
		promise.then(function(data) {      
			if(data.status===null){
			}else if(data.status){
				$scope.req.numeroCaratula = data.numeroCaratula;

//				$timeout(function(){
//					$scope.buscarCaratula();
//				}, 500);
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.openComentariosModal = function () {
		var data = {
			numeroCaratula : $scope.req.numeroCaratula
		};

		$modal.open({
			templateUrl: 'comentariosCaratulaModal.html',
			backdrop: 'static',
			keyboard: false,
			//size: 'sm',
			windowClass: 'modal',
			controller: 'ComentariosCaratulaModalCtrl',
			resolve: {
			data: function(){
			return data;
		}
		}
		});
	};

	$scope.caratula = {
		resultados: []
	};
	
	$scope.obtenerBitacoraCaratula = function () {
		var promise = caratulaService.obtenerBitacoraCaratula($scope.req.numeroCaratula);
		promise.then(function(data) {

			if(data.status===null){

			}else if(data.status){
				
				$scope.caratula.resultados = data.listabitacoras;
				$scope.status = data.status;

				if($scope.caratula.resultados.length!==0){

					$scope.openComentariosModal(); 

				}	

			}else{
				$scope.raiseErr('No se pudo obtener bitacora caratula', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};
	
	$scope.openDocumentosEntregaModal = function () {
		var data = {
			numeroCaratula : $scope.req.numeroCaratula
		};

		$modal.open({
			templateUrl: 'documentosEntregaModal.html',
			backdrop: 'static',
			keyboard: false,
			//size: 'sm',
			windowClass: 'modal',
			controller: 'documentoEntregaModalCtrl',
			resolve: {
			data: function(){
			return data;
		}
		}
		});
	};
	
	$scope.llenaComboWorkFlow = function () {

		if($scope.data!=undefined){
			if($scope.data.caratulaDTO!=undefined){
				if($scope.data.caratulaDTO.tipoFormularioDTO.id==2){
					$scope.listaWorkflow=$scope.listaWorkflowsJSONIns;
				}else if($scope.data.caratulaDTO.tipoFormularioDTO.id==7){
					$scope.listaWorkflow=$scope.listaWorkflowsJSONSolVig;
				}else if($scope.data.caratulaDTO.tipoFormularioDTO.id==4){
					$scope.listaWorkflow=$scope.listaWorkflowsJSONSolCv;
					if($scope.listaWorkflow!=undefined)
						$scope.data.workflow = $scope.listaWorkflow[0];
				}else{
					$scope.listaWorkflow=$scope.listaWorkflowsJSONSolSv;
					if($scope.listaWorkflow!=undefined)
						$scope.data.workflow = $scope.listaWorkflow[0];
				}
			}
		}

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
	
	$scope.$watch('data.caratulaDTO.tipoFormularioDTO', function() {
		$scope.llenaComboWorkFlow();
	});

});
