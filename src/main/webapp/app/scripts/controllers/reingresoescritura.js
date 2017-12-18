'use strict';

app.controller('ReingresoEscrituraCtrl', function ($scope,$http,$timeout,$rootScope,$location,$anchorScroll,$sce,solicitudesModel,reingresoEscrituraModel, certificacionService, caratulaService, estadoService, reingresoService, escrituraService, tareasService, $filter, $modal, $modalStack, Modal) {

	$scope.busquedaReingreso = reingresoEscrituraModel.getBusquedaReingreso();
	$scope.states = reingresoEscrituraModel.getStates();
	$scope.tab = solicitudesModel.getTab();
	$scope.statesEscritura = reingresoEscrituraModel.getStatesEscritura();
	$scope.statesListado = reingresoEscrituraModel.getStatesListado();
	$scope.statesDescarga = reingresoEscrituraModel.getStatesDescarga();
	$scope.statesAnexo = reingresoEscrituraModel.getStatesAnexo();
	$scope.statesDescargaFusion = reingresoEscrituraModel.getStatesDescargaFusion();

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null,
		tipo: null
	};

	$scope.doFocus = function(name) {
		$scope.$broadcast(name + 'IsFocused');
	};

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error',msg, '',  true, 2);
	};

	$scope.raiseSuccess = function(msg){
		$scope.openMensajeModal('success',msg, '',  true, 2);
	};

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	//Buscar Escrituras		
	$scope.buscarEscriturasCaratula = function() {
		$scope.openLoadingModal('Buscando...', '');
		
		$scope.limpiarResultado();
		
		var promise = estadoService
		.getDocumentos(
			$scope.busquedaReingreso.caratula,
		'ESCRITURAS');
		promise
		.then(
			function(data) {
				$scope.closeModal();
				
				if (data.status === null) {

				} else if (data.success) {
					if(data.children.length>0){	
						for ( var i = 0; i < data.children.length; i++){
							if(!$scope.busquedaReingreso.version){
								if(data.children[i].children[0]){
									if(data.children[i].children[0].vigente){
										$scope.busquedaReingreso.version=data.children[i].children[0].version;
										//									console.log($scope.busquedaReingreso.version);
									}
								}
							}
							$scope.busquedaReingreso.escrituras.splice($scope.busquedaReingreso.escrituras.length,0,data.children[i].children[0]);
						}
					}
					$scope.busquedaReingreso.data = data;

				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicacion con el servidor.');
			});
	}

	$scope.verDocumentoEstudio = function(escritura) {
		//existe documento
		var promise = estadoService
		.existeEscritura(escritura);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					//download documento
					$scope.busquedaReingreso.caratula;
					escritura.version;
					$scope.urlPDF = $sce.trustAsResourceUrl('../do/service/escritura?metodo=verDocumentoEstudio&caratula='+ $scope.busquedaReingreso.caratula +'&version='+escritura.version+'&idTipoDocumento='+escritura.idTipoDocumento+'&type=uri');
				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicacion con el servidor.');
			});		

	};

	$scope.subirArchivo = function(file) {
		$scope.openLoadingModal('Versionando...', '');
		var fd = new FormData();
		fd.append("observacion", $scope.busquedaReingreso.observacion1);
		fd.append("caratula", $scope.busquedaReingreso.caratula);
		fd.append("file", file);
		$http({
			withCredentials: true,
			method: 'POST',
			url: '../upload',
			data: fd,
			headers: { 'Content-Type': undefined },
			transformRequest: angular.identity
		}).then(function successCallback(response) {
			$scope.closeModal();
			if(response.data.status){
				$scope.raiseSuccess(response.data.message);
				//				$scope.busquedaReingreso.observacion1 = '';
				//				angular.element("input[type='file']").val(null);
				$scope.resetResultado();
			}else{
				$scope.statesEscritura.ok=response.data.status;
				$scope.statesEscritura.msg=response.data.message;
			}
		}, function errorCallback(response) {
			$scope.closeModal();
			//			alert(response.data);
			$scope.statesEscritura.ok=response.data.status;
			$scope.statesEscritura.msg=response.data.message;
		});
	}

	$scope.fusionarArchivo = function(file) {
		$scope.openLoadingModal('Fusionando...', '');
		var fd = new FormData();
		fd.append("version", $scope.busquedaReingreso.version);
		fd.append("observacion", $scope.busquedaReingreso.observacion2);
		fd.append("caratula", $scope.busquedaReingreso.caratula);
		fd.append("file", file);
		$http({
			withCredentials: true,
			method: 'POST',
			url: '../fusionUpload',
			data: fd,
			headers: { 'Content-Type': undefined },
			transformRequest: angular.identity
		}).then(function successCallback(response) {
			$scope.closeModal();
			if(response.data.status){
				$scope.raiseSuccess(response.data.message);
				//				$scope.busquedaReingreso.observacion2 = '';
				//				angular.element("input[type='file']").val(null);
				$scope.resetResultado();
			}else{
				$scope.statesAnexo.ok=response.data.status;
				$scope.statesAnexo.msg=response.data.message;
			}
		}, function errorCallback(response) {
			$scope.closeModal();
			//			alert(response.data);
			$scope.statesAnexo.ok=response.data.status;
			$scope.statesAnexo.msg=response.data.message;
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

	$scope.buscarDocumento = function(){	
		$scope.openLoadingModal('buscando documento...', '');	
		var promise = tareasService.buscarDocumento($scope.busquedaReingreso.notario.idNotario,$scope.busquedaReingreso.codescritura,$scope.busquedaReingreso.notario.empresa,$scope.busquedaReingreso.caratula);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){

				//Guardo en bitacora
				var promise = estadoService.agregarBitacora($scope.busquedaReingreso.caratula, 'Reingreso Escritura Electronica',0);
				promise.then(function(data) {
					if(data.status===null){
					}else if(data.status){
					}else{
						$scope.raiseErr(data.msg);
					}
				}, function(reason) {
					//					$scope.raiseErr('No se ha podido establecer comunicacion con el servidor - Bitacora');
					$scope.statesDescarga.ok=response.data.status;
					$scope.statesDescarga.msg='Problema en Guardar Bitacora';
				});

				$scope.raiseSuccess('Descarga Archivo Exitoso y Versionado');
				$scope.resetResultado();
				//Fin Guardo en bitacora

			}else{
				//				$scope.raiseErr(data.msg);
				$scope.statesDescarga.ok=false;
				$scope.statesDescarga.msg=data.msg;
			}
		}, function(reason) {
			//			$scope.closeModal();
			//			$scope.raiseErr('no se ha podido establecer comunicacion con el servidor.');
			$scope.statesDescarga.ok=false;
			$scope.statesDescarga.msg='no se ha podido establecer comunicacion con el servidor.';
		});		  
	}

	$scope.buscarDocumentoFusion = function(){	
		$scope.openLoadingModal('buscando documento...', '');	
		var promise = tareasService.buscarDocumentoFusion($scope.busquedaReingreso.notario2.idNotario,$scope.busquedaReingreso.codescritura2,$scope.busquedaReingreso.notario2.empresa,$scope.busquedaReingreso.caratula,$scope.busquedaReingreso.version);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){

				//Guardo en bitacora
				var promise = estadoService.agregarBitacora($scope.busquedaReingreso.caratula, 'Reingreso Fusion Escritura Electronica',0);
				promise.then(function(data) {
					if(data.status===null){
					}else if(data.status){
					}else{
						$scope.raiseErr(data.msg);
					}
				}, function(reason) {
					//					$scope.raiseErr('No se ha podido establecer comunicacion con el servidor - Bitacora');
					$scope.statesDescargaFusion.ok=response.data.status;
					$scope.statesDescargaFusion.msg='Problema en Guardar Bitacora';
				});

				$scope.raiseSuccess('Descarga Archivo Exitoso y Fusionado');
				$scope.resetResultado();
				//Fin Guardo en bitacora

			}else{
				//				$scope.raiseErr(data.msg);
				$scope.statesDescargaFusion.ok=false;
				$scope.statesDescargaFusion.msg=data.msg;
			}
		}, function(reason) {
			//			$scope.closeModal();
			//			$scope.raiseErr('no se ha podido establecer comunicacion con el servidor.');
			$scope.statesDescargaFusion.ok=false;
			$scope.statesDescargaFusion.msg='no se ha podido establecer comunicacion con el servidor.';
		});		  
	}

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

	$scope.resetResultado = function(){
		$scope.busquedaReingreso.caratula='';
		$scope.limpiarResultado();
		$scope.doFocus('caratula');
	};

	$scope.limpiarResultado = function(){
		$scope.busquedaReingreso.data=null;
		$scope.busquedaReingreso.escrituras.length = 0;
		$scope.states.buscar.isError = false;
		$scope.statesEscritura.ok = null;
		$scope.statesEscritura.msg = null;
		$scope.statesDescarga.ok = null;
		$scope.statesDescarga.msg = null;
		$scope.statesAnexo.ok = null;
		$scope.statesAnexo.msg = null;
		$scope.statesDescargaFusion.ok = null;
		$scope.statesDescargaFusion.msg = null;
		$scope.busquedaReingreso.codescritura='';
		$scope.busquedaReingreso.notario='';
		$scope.busquedaReingreso.codescritura2='';
		$scope.busquedaReingreso.notario2='';
		$scope.busquedaReingreso.observacion1 = '';
		$scope.busquedaReingreso.observacion2 = '';
		$scope.busquedaReingreso.version = null;
		angular.element("input[type='file']").val(null);
		$scope.urlPDF='';
	};

	//	$scope.removeVersiondddd = function(escritura) {
	//		 
	//		if(confirm('Desea eliminar la version '+ escritura.version+' De caratula '+$scope.busquedaReingreso.caratula +'?')){
	//			$scope.openLoadingModal('Eliminando version de escritura...', '');	
	//			var promise = escrituraService.eliminarDocumento(escritura.idDocumento,$scope.busquedaReingreso.caratula,escritura.version);
	//			promise.then(function(data) {
	//				$scope.closeModal();
	//				if(data.estado===null){
	//				}else if(data.status){
	//
	//					$scope.raiseSuccess('Se ha eliminado la version de Escritura');
	//					$scope.buscarEscriturasCaratula();
	//
	//				}else{
	//					$scope.statesDescarga.ok=false;
	//					$scope.statesDescarga.msg=data.msg;
	//				}
	//			}, function(reason) {
	//				$scope.statesDescarga.ok=false;
	//				$scope.statesDescarga.msg='no se ha podido establecer comunicacion con el servidor.';
	//			});		  
	//		}
	//	}

	$scope.removeVersion = Modal.confirm.delete(function(escritura){
		$scope.openLoadingModal('Eliminando version de escritura...', '');	

		var promise = escrituraService.eliminarDocumento(escritura.idDocumento,$scope.busquedaReingreso.caratula,escritura.version);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){

				$scope.raiseSuccess('Se ha eliminado la version de Escritura');
				$scope.buscarEscriturasCaratula();

			}else{
				$scope.statesListado.ok=false;
				$scope.statesListado.msg=data.msg;
			}
		}, function(reason) {
			$scope.statesListado.ok=false;
			$scope.statesListado.msg='no se ha podido establecer comunicacion con el servidor.';
		});		
	});
});