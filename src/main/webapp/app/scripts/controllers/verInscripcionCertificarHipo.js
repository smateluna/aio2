/**
 * Created by rgonzaleza on 07/07/2014.
 */

'use strict';

app.controller('VerInscripcionCertificarHipoCtrl', function ($scope, $routeParams, $rootScope, $modal, $modalStack, $sce, $timeout,inscripcionDigitalHipotecasService,caratulaService, certificacionHipotecasService) {

	$scope.parametros = {
		caratula: $routeParams.caratula,
		foja: $routeParams.foja,
		numero: $routeParams.numero,
		ano: $routeParams.ano,
		bis: $routeParams.bis,
		fechaDoc: $routeParams.fechaDoc,
		rehaceImagen: $routeParams.rehaceImagen,
		idtipoFormulario: $routeParams.idtipoFormulario,
		registro: $routeParams.registro,
		origen: $routeParams.origen,
		mostrar: null
	};

	$scope.states = {
		isLoading: false,
		isError: false,
		isOk: false,
		title: null,
		msg: null
	};

	$scope.certificacion = {
		tipo: {}
	};

	$scope.anotaciones = [];
	$scope.data = {};

	$scope.borradores = [];
	$scope.proceso = [];
	$scope.terminadas = [];

	$scope.izquierdaStatus = {
		anotaciones : true,
		borradores : false,
		borradoresLoaded : false,
		proceso : false,
		procesoLoaded : false,
		terminadas : false,
		terminadasLoaded : false
	};

	$scope.loaders = {
		borradores : {
		isLoading : false,
		error : false
	},
	enProceso : {
		isLoading : false,
		error : false
	},
	terminadas : {
		isLoading : false,
		error : false
	},
	anotaciones : {
		isLoading : false,
		error : false
	}
	};

	$scope.derechaStatus = {
		notasExpanded : true
	};

	$scope.acordionNotas = function() {
		return $scope.derechaStatus.notasExpanded = !$scope.derechaStatus.notasExpanded;
	};

	$scope.buscar = function(){
		$scope.isLoading = true;
		var caratula = $scope.parametros.caratula,
		registro = $scope.parametros.registro;

		$scope.urlPDF = $sce.trustAsResourceUrl("../do/service/inscripcionDigitalHipotecas?metodo=verInscripcionCertificar&caratula="+caratula);
		$scope.isLoading  = false;


		var promise = inscripcionDigitalHipotecasService.getInscripcion(
			$scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis);
		promise
		.then(
			function(data) {
				if (data.status == null) {
				} else if (data.status) {

					$scope.data = data;

					$scope.inicia();
					
					var promise = caratulaService.obtenerBitacoraCaratula($scope.parametros.caratula);
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
					//fin comentarios caratula

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

	$scope.inicia = function() {
		var tieneanotacion2 = false;
		
//		$scope.loaders['anotaciones'].isLoading = true;
//		var promise = inscripcionDigitalHipotecasService.getNotas(
//			$scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis);
//		promise
//		.then(
//			function(data) {
//				if (data.status == null) {
//				} else if (data.status) {

					if ($scope.data.consultaDocumentoDTO.tipoDocumento === 8) {

						angular
						.forEach(
							$scope.data.inscripcionDigitalDTO.anotacionsForIdInscripcionDestino,
							function(obj) {
								if (obj.tipoAnotacionDTO.idTipoAnotacion === 2) {
									tieneanotacion2=true;
								} else if (obj.tipoAnotacionDTO.idTipoAnotacion === 3) {
									$scope.anotaciones
									.push(obj);
								}
							});

					}

//				} else {
//					$scope.raiseErr('error','Error Obteniendo Anotaciones.',data.msg);
//					$scope.loaders['anotaciones'].error = true;
//				}
//
//				$scope.loaders['anotaciones'].isLoading = false;
//
//			},
//			function(reason) {
//				$scope.loaders['anotaciones'].isLoading = false;
//
//				$scope.loaders['anotaciones'].error = true;
//
//				$scope.raiseErr('error','Error detectado.',	'No se ha podido establecer comunicación.');
//
//			});

		if(($scope.userLoginSinCBRS=='jbelmar') && $scope.data.inscripcionDigitalDTO.ano<$rootScope.aioParametros.anoDigitalHipotecas){
			$scope.parametros.rehaceImagen='1';
		}else if((!tieneanotacion2 && $scope.data.estado.rehacerImagen) && $scope.data.inscripcionDigitalDTO.ano<$rootScope.aioParametros.anoDigitalHipotecas){
			$scope.parametros.rehaceImagen='1';
		}else{
			$scope.parametros.rehaceImagen='0';
		}

		$scope.parametros.mostrar=$scope.parametros.rehaceImagen;

	};

	$scope.cerrar = function(){
		$rootScope.go('/'+$scope.parametros.origen);
	};


	//comentarios caratula para ver si muestro boton ver comentarios caratula
	$scope.caratula = {
		resultados: []
	};

	


	$scope.modificaTitulo = function(){

		$scope.openLoadingModal('Modificando...', '');

		var promise = caratulaService.actualizarInscripcion($scope.parametros.caratula,$scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.raiseOk('', 'Se modifico titulo');
				$scope.salirSave();

			}else{
				$scope.raiseErr('No se pudo modificar caratula', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	}

	$scope.obtenerInscripcionBis = function(){

		$scope.openLoadingModal('Modificando...', '');

		var promise = caratulaService.obtenerInscripcionBis($scope.parametros.caratula,$scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.raiseOk('', 'Se envio inscripcion BIS');
				$scope.salirSave();

			}else{
				$scope.raiseErr('No se pudo agregar BIS a inscripcion', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	}

	$scope.rehacerImagen = function () {
		var dataImagen = {
			numeroCaratula : $scope.parametros.caratula,
			foja : $scope.parametros.foja,
			numero : $scope.parametros.numero,
			ano : $scope.parametros.ano,
			bis : $scope.parametros.bis,
		};

		$modal.open({
			templateUrl: 'rehacerImagenModal.html',
			backdrop: 'static',
			keyboard: false,
			//size: 'sm',
			windowClass: 'modal',
			controller: 'RehacerImagenModalCtrl',
			resolve: {
				dataImagen: function(){
					return dataImagen;
				},
				servicioCertificacion: function(){
					return certificacionHipotecasService;
				}
		}
		});
	};



	$scope.openRechazoModal = function () {
		var dataRechazo = {
			numeroCaratula : $scope.parametros.caratula
		};

		$modal.open({
			templateUrl: 'rechazoCertificacionModal.html',
			backdrop: 'static',
			keyboard: false,
			//size: 'sm',
			windowClass: 'modal',
			controller: 'RechazoCertificacionModalCtrl',
			resolve: {
			dataRechazo: function(){
			return dataRechazo;
		},
		origen : function() {
			return 'certificacion';
		}
		}
		});
	};

	$scope.openComentariosModal = function () {
		var data = {
			numeroCaratula : $scope.parametros.caratula
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

	$scope.certificar = function(){
		$scope.openLoadingModal('Certificando...', '');

		var promise = certificacionHipotecasService.certificar($scope.parametros.caratula,$scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis,$scope.certificacion.tipo.codigo);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.raiseOk('', 'Certificacion realizada');
				$scope.salirSave();

			}else{
				$scope.raiseErr('No se pudo certificar caratula', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	};

	$scope.distribuir = function(){
		$scope.openLoadingModal('Distribuyendo...', '');

		var promise = certificacionHipotecasService.distribuir($scope.parametros.caratula,$scope.certificacion.tipo.codigo);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.raiseOk('', 'Distribucion realizada');
				$scope.salirSave();

			}else{
				$scope.raiseErr('No se pudo distribuir la caratula', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	};

	$scope.tipoCertificacion = [{
		codigo:8,
		descripcion: 'C/ VIGENCIA'
	},{
		codigo:9,
		descripcion: 'S/ VIGENCIA'
	},{
		codigo:10,
		descripcion: 'EN PARTE'
	},{
		codigo:11,
		descripcion: 'CV ESCRITURA'
	},{
		codigo:12,
		descripcion: 'SV ESCRITURA'
	},{
		codigo:13,
		descripcion: 'ERROR DE OFICINA'
	}];


	for(var i = 0; i<$scope.tipoCertificacion.length;i++){

		if($scope.tipoCertificacion[i].codigo == $scope.parametros.idtipoFormulario){
			$scope.certificacion.tipo = $scope.tipoCertificacion[i];
			break;
		}	 
	}

	$scope.raiseErr = function(title, msg){
		$scope.states.isError = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};

	$scope.raiseOk = function(title, msg){
		$scope.states.isOk = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};

	$scope.closeRechazo = function () {		 
		$rootScope.go('/'+$scope.parametros.origen);
	};

	$scope.salirSave = function(){
		$timeout(function(){
			//CertificacionCtrl.refrescar();
			$scope.closeRechazo();
		},2000);
	};

	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};
	//fin validaciones titulo

	$scope.buscar();


	$scope.acordionIzquierda = function(target) {

		if (target === 'anotaciones') {

			if ($scope.izquierdaStatus.anotaciones) {
				$scope.izquierdaStatus.anotaciones = false;
			} else {
				$scope.izquierdaStatus.anotaciones = true;
			}

			$scope.izquierdaStatus.borradores = false;
			$scope.izquierdaStatus.proceso = false;
			$scope.izquierdaStatus.terminadas = false;

		} else if (target === 'borradores') {
			if ($scope.izquierdaStatus.borradores) {
				$scope.izquierdaStatus.borradores = false;
			} else {
				$scope.izquierdaStatus.borradores = true;

				if (!$scope.izquierdaStatus.borradoresLoaded
						&& $scope.data.contadores.borradores > 0) {
					$scope.izquierdaStatus.borradoresLoaded = true;

					$scope.loadWidgets('borradores');
				}
			}

			$scope.izquierdaStatus.anotaciones = false;
			$scope.izquierdaStatus.proceso = false;
			$scope.izquierdaStatus.terminadas = false;
		} else if (target === 'proceso') {
			if ($scope.izquierdaStatus.proceso) {
				$scope.izquierdaStatus.proceso = false;
			} else {
				$scope.izquierdaStatus.proceso = true;

				if (!$scope.izquierdaStatus.procesoLoaded
						&& $scope.data.contadores.proceso > 0) {
					$scope.izquierdaStatus.procesoLoaded = true;
					$scope.loadWidgets('enProceso');

				}
			}

			$scope.izquierdaStatus.anotaciones = false;
			$scope.izquierdaStatus.borradores = false;
			$scope.izquierdaStatus.terminadas = false;
		} else if (target === 'terminadas') {
			if ($scope.izquierdaStatus.terminadas) {
				$scope.izquierdaStatus.terminadas = false;
			} else {
				$scope.izquierdaStatus.terminadas = true;

				if (!$scope.izquierdaStatus.terminadasLoaded
						&& $scope.data.contadores.terminadas > 0) {
					$scope.izquierdaStatus.terminadasLoaded = true;
					$scope.loadWidgets('terminadas');

				}
			}

			$scope.izquierdaStatus.anotaciones = false;
			$scope.izquierdaStatus.borradores = false;
			$scope.izquierdaStatus.proceso = false;
		}
	};

	$scope.loadWidgets = function(tipo) {
		var foja = $scope.data.inscripcionDigitalDTO.foja, numero = $scope.data.inscripcionDigitalDTO.numero, ano = $scope.data.inscripcionDigitalDTO.ano, bis = $scope.data.inscripcionDigitalDTO.bis, promise;

		if (tipo === 'borradores') {
			promise = inscripcionDigitalService
			.obtenerBorradores(foja, numero, ano, bis);
		} else if (tipo === 'enProceso') {
			promise = inscripcionDigitalService
			.obtenerCaratulasPorEstado(foja, numero,
				ano, bis, 0);
		} else if (tipo === 'terminadas') {
			promise = inscripcionDigitalService
			.obtenerCaratulasPorEstado(foja, numero,
				ano, bis, 1);
		}

		$scope.loaders[tipo].isLoading = true;

		promise
		.then(
			function(data) {
				if (data.status == null) {
					$window.location.href = $window.location.protocol
					+ '//'
					+ $window.location.host
					+ $window.location.pathname;
				} else if (data.status) {

					if (tipo === 'borradores') {
						$scope.borradores = data.borradores;
					} else if (tipo === 'enProceso') {
						$scope.proceso = data.caratulas;
					} else if (tipo === 'terminadas') {
						$scope.terminadas = data.caratulas;
					}
				} else {
					$scope.loaders[tipo].error = true;
				}
				$scope.loaders[tipo].isLoading = false;
			},
			function(reason) {
				$scope.loaders[tipo].isLoading = false;
				$scope.loaders[tipo].error = true;
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

});