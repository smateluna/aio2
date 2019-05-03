/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app
.controller(
	'VerInscripcionCtrl',
	function($scope, $routeParams, $rootScope, $sce, $modal, $timeout,$filter, $window,
		solicitudService,solicitudHipotecasService,solicitudProhibicionesService, 
		AnotacionService, AnotacionHipotecasService, AnotacionProhibicionesService,
		inscripcionDigitalService, inscripcionDigitalHipotecasService, borradorService,inscripcionDigitalProhibicionesService, 
		indiceService, 
		digitalModel, digitalHipotecasModel, digitalProhibicionesModel,filterFilter) {
		
		$scope.reverseNotas=$routeParams.reverseNotas;
		$scope.hoy = moment().format('DD/MM/YYYY');
		$scope.data = {};
		$scope.anotaciones = [];
		$scope.embargos = [];
		$scope.notas = [];
		$scope.alertas = [];

		$scope.borradores = [];
		$scope.proceso = [];
		$scope.terminadas = [];

		$scope.paginas = [];

		$scope.titulos = [];
		$scope.selectedAll = false;

		$scope.errorObj = {
			hay : false,
			level : 'warning', // warning o error
			title : '',
			msg : ''
		};

		$scope.tab = {
			parentActive : 1
		};

		$scope.imagen = {
			page : 1,
			isPdfLoaded : false
		};

		$scope.paginacionMaster = {
			currentPage: 1,
			numPerPage: 10,
			maxSize: 2,
			totalpaginas: 0,
			filteredTodos: [],
			todos: [],
			filterExprNotas: ''
		}

		$scope.filaTicketMasivo = {
			foja: null,
			num: null,
			ano: null
		}

		$scope.busquedaNotas = {
			predicate: null,
			reverse: null
		}

		$scope.paginacionNotas = angular.copy($scope.paginacionMaster);

		$scope.parametros = {
			registro : $routeParams.registro,
			foja : $routeParams.foja,
			numero : $routeParams.numero,
			ano : $routeParams.ano,
			bis : $routeParams.bis,
			origen : $routeParams.origen,
			estado : $routeParams.estado,
			caratula : $routeParams.caratula,
			borrador : $routeParams.borrador,
			folio : $routeParams.folio,
		};

		$scope.servicioSolicitud = $scope.parametros.registro=="prop"?solicitudService:$scope.parametros.registro=="hip"?solicitudHipotecasService:solicitudProhibicionesService;
		$scope.servicioInscripcion = $scope.parametros.registro=="prop"?inscripcionDigitalService:$scope.parametros.registro=="hip"?inscripcionDigitalHipotecasService:inscripcionDigitalProhibicionesService;
		$scope.servicioAnotacion = $scope.parametros.registro=="prop"?AnotacionService:$scope.parametros.registro=="hip"?AnotacionHipotecasService:AnotacionProhibicionesService;
		$scope.modeloDigital = $scope.parametros.registro=="prop"?digitalModel:$scope.parametros.registro=="hip"?digitalHipotecasModel:digitalProhibicionesModel;	
		$scope.inscripcionDigital = $scope.parametros.registro=="prop"?"inscripcionDigital":$scope.parametros.registro=="hip"?"inscripcionDigitalHipotecas":"inscripcionDigitalProhibiciones";
		
		$timeout(function(){
			if($rootScope.aioParametros!==undefined){
				$scope.anoDigital = $scope.parametros.registro=="prop"?$rootScope.aioParametros.anoDigitalPropiedades:$scope.parametros.registro=="hip"?$rootScope.aioParametros.anoDigitalHipotecas:$rootScope.aioParametros.anoDigitalProhibiciones;
				$scope.fojasDigital = $scope.parametros.registro=="proh"?$rootScope.aioParametros.fojasDigitalProhibiciones:0;
			}
		}, 100);
	

		$scope.izquierdaStatus = {
			anotaciones : true,
			alertas : true,
			borradores : false,
			borradoresLoaded : false,
			proceso : false,
			procesoLoaded : false,
			terminadas : false,
			terminadasLoaded : false,
			alertasLoaded : true
		};

		$scope.barraDerechaStatus = {
			nota : true,
			formulario : false
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
		},
		notas : {
			isLoading : false,
			error : false
		},
		titulosanteriores : {
			isLoading : false,
			error : false
		}
		};

		$scope.derechaStatus = {
			notasExpanded : false
		};

		$scope.isLoadingSolicitar = false;
		$scope.statusSolicitar = {
			ok : false,
			error : false
		};

		// controles tabs
		$scope.isActiveParent = function(id) {
			return $scope.tab.parentActive === id;
		};

		$scope.activateParent = function(id) {
			$scope.tab.parentActive = id;

			if (id == 2) {
				$scope.imagen.isPdfLoaded = true;
			}
		};
		// fin controles tabs


		$scope.openEmbargosModal = function(){
			$modal.open({     
				templateUrl: 'embargoModal.html',
				backdrop: 'static',
				windowClass: 'modal modal-dialog-xl', 
				controller: 'EmbargoModalCtrl',
				size: 'lg',  
				resolve: {  
				embargos : function(){
				return $scope.embargos;
			}
			}
			});
		};

		$scope.openMensajeModal = function (tipo, titulo, detalle, autoClose, segundos) {
			$modal.open({
				templateUrl: 'mensajeModal.html',
				backdrop: 'static',
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

//			if(autoClose){
//				$timeout(function(){
//					$scope.closeModal();
//				},segundos*1000);
//			}
		};

		$scope.cerrar = function() {
			if($scope.parametros.origen=='consultadiablito'){
				$rootScope.go('/' + $scope.parametros.origen+'/'+ $scope.parametros.caratula + '/' + $scope.parametros.borrador
					+ '/' + $scope.parametros.folio);	
			}else{	
				$rootScope.go('/' + $scope.parametros.origen);
			}
		};

		$scope.verInscripcion = function(obj) {
			$rootScope.go('/verInscripcion/'
				+ $scope.parametros.registro + '/' + obj.foja
				+ '/' + obj.numero + '/' + obj.ano + '/'
				+ obj.bis + '/' + $scope.parametros.origen);
		};

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
			} else if (target === 'alertas') {
				if ($scope.izquierdaStatus.alertas) {
					$scope.izquierdaStatus.alertas = false;
				} else {
					$scope.izquierdaStatus.alertas = true;
				}

//				$scope.izquierdaStatus.anotaciones = false;
//				$scope.izquierdaStatus.borradores = false;
//				$scope.izquierdaStatus.proceso = false;
			}
		};

		$scope.acordionDerecha = function(target) {

			if (target === 'nota') {

				if ($scope.barraDerechaStatus.nota) {
					$scope.barraDerechaStatus.nota = false;
				} else {
					$scope.barraDerechaStatus.nota = true;
				}

				$scope.barraDerechaStatus.formulario = false;

			} else if (target === 'formulario') {
				if ($scope.barraDerechaStatus.formulario) {
					$scope.barraDerechaStatus.formulario = false;
					$scope.acordionNotas();
				} else {
					$scope.barraDerechaStatus.formulario = true;
					$scope.acordionNotas();
					$scope.titulosanteriores();
				}

				$scope.barraDerechaStatus.nota = false;
			} 
		};

		$scope.esReferencial = function() {
			return ($scope.data.consultaDocumentoDTO.tipoDocumento === 9 || $scope.data.consultaDocumentoDTO.tipoDocumento == 10);
		};

		$scope.esVersionado = function() {
			return ($scope.data.consultaDocumentoDTO.tipoDocumento === 8);
		};

		$scope.tieneRechazo = function() {
			return ($scope.data.estado.tieneRechazo);
		};

		$scope.esMalCitadaDesde = function() {
			return (!$scope.data.estado.fna && $scope.data.inscripcionDigitalDTO.ano >= $scope.anoDigital && $scope.data.inscripcionDigitalDTO.foja >= $scope.fojasDigital);
		};

		$scope.hayDocumento = function() {
			return $scope.data.consultaDocumentoDTO.hayDocumento;
		};

		$scope.hayPaginas = function() {
			return ($scope.data.consultaDocumentoDTO.cantidadPaginas > 0);
		};

		$scope.acordionNotas = function() {
			return $scope.derechaStatus.notasExpanded = !$scope.derechaStatus.notasExpanded;
		};

		$scope.loadWidgets = function(tipo) {
			var foja = $scope.data.inscripcionDigitalDTO.foja, numero = $scope.data.inscripcionDigitalDTO.numero, ano = $scope.data.inscripcionDigitalDTO.ano, bis = $scope.data.inscripcionDigitalDTO.bis, promise;

			if (tipo === 'borradores') {
				promise = $scope.servicioInscripcion
				.obtenerBorradores(foja, numero, ano, bis);
			} else if (tipo === 'enProceso') {
				promise = $scope.servicioInscripcion
				.obtenerCaratulasPorEstado(foja, numero,
					ano, bis, 0);
			} else if (tipo === 'terminadas') {
				promise = $scope.servicioInscripcion
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

		$scope.deleteAnotacion = function(anotacion) {

			var pregunta = 'Desea eliminar la nota?';
			var esNota = true;

			if (anotacion.tipoAnotacionDTO.idTipoAnotacion === 1
					|| anotacion.tipoAnotacionDTO.idTipoAnotacion === 2) {
				esNota = true;
			} else if (anotacion.tipoAnotacionDTO.idTipoAnotacion === 3) {
				esNota = false;
				pregunta = 'Desea eliminar la anotaci\u00F3n?';
			}

			if (confirm(pregunta)) {
				$scope.actualizandoAlgo = true;

				var promise = $scope.servicioAnotacion
				.deleteAnotacion(anotacion.idAnotacion);
				promise
				.then(
					function(data) {
						if (data.status === null) {
							$window.location.href = $window.location.protocol
							+ '//'
							+ $window.location.host
							+ $window.location.pathname;
						} else if (data.status) {

							if (esNota) {
								$scope.notas
								.splice(
									$scope.notas
									.indexOf(anotacion),
									1);
							} else {
								anotacion.estadoAnotacionDTO.idEstado = 7;
								anotacion.nombreUsuarioEliminador = data.usuarioEliminador;
								anotacion.fechaEliminacion = data.fechaEliminacion;
							}

							$scope.actualizandoAlgo = false;

						} else {
							$scope.actualizandoAlgo = false;
							alert('Se ha detectado un problema.');
						}
					},
					function(reason) {
						$scope.actualizandoAlgo = false;
						alert('Se ha detectado un problema en el servidor.');
					});
			}
		};
		
		
		$scope.imprimirNotas = function() {
			$window.open($window.location.protocol+'//'+$window.location.host+'/aio/do/service/anotacion?metodo=printNotas&fojas='+$scope.parametros.foja+'&numero='+$scope.parametros.numero+'&ano='+$scope.parametros.ano+'&bis='+$scope.parametros.bis+'&registro='+$scope.parametros.registro+'&download=false','popupNotas','width=800,height=600');			
		};		

		$scope.modificarAnotacion = function(nota) {
			$modal
			.open({
				templateUrl : 'modificaAnotacionModal.html',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'modal',
				controller : 'ModificaAnotacionModalCtrl',
				resolve : {
				nota : function() {
				return nota;
			},inscripcionDigitalDTO : function() {
				return $scope.data.inscripcionDigitalDTO;
			},
			anotaciones : function() {
				return $scope.anotaciones;
			},
			notas : function() {
				return $scope.notas;
			},
			origen : function(){
				return $scope.parametros.origen;
			},
			paginacionNotas : function(){
				return $scope.paginacionNotas;
			}
			}
			});
		};

		$scope.openAnotacion = function() {
			$modal
			.open({
				templateUrl : 'anotacionModal.html',
				backdrop : 'static',
				keyboard : false,
				windowClass : 'modal',
				controller : 'AnotacionModalCtrl',
				resolve : {
				idInscripcion : function() {
				return $scope.data.inscripcionDigitalDTO.idInscripcion;
			},
			anotaciones : function() {
				return $scope.anotaciones;
			},
			inscripcionDigitalDTO : function() {
				return $scope.data.inscripcionDigitalDTO;
			},
			notas : function() {
				return $scope.notas;
			},
			origen : function(){
				return $scope.parametros.origen;
			},
			paginacionNotas : function(){
				return $scope.paginacionNotas;
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

		// utils
		$scope.solicitar = function() {
			var dtoIns = $scope.data.inscripcionDigitalDTO;
			$scope.resetStatusSolicitar();
			$scope.isLoadingSolicitar = true;

			var promise = $scope.servicioSolicitud.saveSingle(dtoIns.foja,
				dtoIns.numero, dtoIns.ano, dtoIns.bis);
			promise
			.then(
				function(data) {
					$scope.isLoadingSolicitar = false;

					if (data.status === null) {
						$window.location.href = $window.location.protocol
						+ '//'
						+ $window.location.host
						+ $window.location.pathname;
					} else if (data.status) {
						$scope.setSolicitarOK();
					} else {
						// $scope.setSolicitarError('Problema
						// detectado.', 'No se ha
						// guardado la solicitud.');

						alert('Problema detectado, no se ha guardado la solicitud');
					}
				},
				function(reason) {
					$scope.isLoadingSolicitar = false;
					alert('Problema contactando al servidor, no se ha guardado la solicitud.');

					// $scope.setSolicitarError('Problema
					// contactando al servidor.', 'No se
					// ha guardado la solicitud.');
				});
		};

		//					$scope.irGpOnlineAIO = function(borrador,folio){
		//						$rootScope.go('/gponline/'+borrador+'/'+folio);
		//					};

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

		// $scope.buscar();

		$scope.openSolicitar = function (indice,registro) {

			var foja=null,
			numero=null,
			ano = null,
			bis = null,
			registroCaratula = null;

			foja=indice.foja;
			numero=indice.numero;
			ano=indice.ano;
			bis='false';
			registroCaratula = indice.registro;

			var promise = indiceService.getIndicePropiedades(null,null,null,foja, numero, ano, bis,null,null,null,null,null,true,false,false,false,null,null,null);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					for (var i in data.aaData){
						registro = data.aaData[i];
						break;   
					}

					var myModal = $modal.open({
						templateUrl: 'indiceModal.html',
						backdrop: true,
						windowClass: 'modal',
						controller: 'IndiceModalCtrl',
						resolve: {
						resolveModal : function(){
						return $scope.resolveModal;
					},
					foja: function(){
						return foja;
					},
					numero: function(){
						return numero;
					},
					ano: function(){
						return ano;
					},
					registro: function(){
						return registroCaratula;
					},
					indice: function(){
						return registro;
					},
					origen: function(){
						return 'verinscripcion';
					},
					titulos: function(){
						return '';
					}
					}
					});

				}else{
					$scope.raiseErr('buscar','Problema detectado', data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});
		};

		$scope.ticket = function (indice,registro) {

			var foja=null,
			numero=null,
			ano = null,
			bis = null,
			nombre = null,
			direccion = null,
			comuna=null,
			registroCaratula=null;

			foja=indice.foja;
			numero=indice.numero;
			ano=indice.ano;
			bis='false';
			registroCaratula = indice.registro;

			var promise = indiceService.getIndicePropiedades(null,null,null,foja, numero, ano, bis,null,null,null,null,null,true,false,false,false,null,null,null);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					for (var i in data.aaData){
						registro = data.aaData[i];
						break;   
					}

					var myModal = $modal.open({
						templateUrl: 'TicketInformacion.html',
						backdrop: true,
						windowClass: 'modal',
						controller: 'TicketInformacionCtrl',
						resolve: {
						resolveModal : function(){
						return $scope.resolveModal;
					},
					foja: function(){
						return foja;
					},
					numero: function(){
						return numero;
					},
					ano: function(){
						return ano;
					},
					nombre: function(){
						return registro.nombre;
					},
					direccion: function(){
						return registro.dir;
					},
					comuna: function(){
						return registro.comuna;
					},
					registro: function(){
						return registroCaratula;
					},
					titulos: function(){
						return '';
					}
					}
					});

				}else{
					$scope.raiseErr('buscar','Problema detectado', data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});

		};

		$scope.inicia = function() {
			if (!$scope.tieneRechazo()
					&& !$scope.esMalCitadaDesde()
					&& $scope.hayDocumento() && $scope.hayPaginas()) {				
				
				var foja = $scope.data.inscripcionDigitalDTO.foja, numero = $scope.data.inscripcionDigitalDTO.numero, ano = $scope.data.inscripcionDigitalDTO.ano, bis = $scope.data.inscripcionDigitalDTO.bis, estado = $scope.data.consultaDocumentoDTO.tipoDocumento, registro = $scope.parametros.registro;

				for (var i = 0; i < $scope.data.consultaDocumentoDTO.cantidadPaginas; i++) {
					var d = new Date(), dateNumber = d.getTime(), num = i + 1;

					var url = '../do/service/'+$scope.inscripcionDigital+'?v='
					+ dateNumber
					+ '&metodo=getJPG&p='
					+ num
					+ '&fojas='
					+ foja
					+ '&numero='
					+ numero
					+ '&ano='
					+ ano
					+ '&bis='
					+ bis
					+ '&tipoDoc='
					+ $scope.data.consultaDocumentoDTO.tipoDocumento;

					var urlProc = $sce.trustAsResourceUrl(url);

					$scope.paginas
					.push({
						num : i + 1,
						foja : foja,
						numero : numero,
						ano : ano,
						bis : bis,
						tipoDoc : $scope.data.consultaDocumentoDTO.tipoDocumento,
						url : urlProc
					});
				}

				$scope.urlPDF = $sce
				.trustAsResourceUrl('../do/service/pdf?metodo=getInscripcion&registro='
					+ registro
					+ '&foja='
					+ foja
					+ '&numero='
					+ numero
					+ '&ano='
					+ ano
					+ '&bis='
					+ bis
					+ '&estado='
					+ estado);
			}



			if(registro=='prop'){
				$scope.buscaNotas();
			}else{
				if ($scope.data.consultaDocumentoDTO.tipoDocumento === 8) {

					angular
					.forEach(
						$scope.data.inscripcionDigitalDTO.anotacionsForIdInscripcionDestino,
						function(obj) {
							if ((obj.tipoAnotacionDTO.idTipoAnotacion === 1 || obj.tipoAnotacionDTO.idTipoAnotacion === 2)
									&& obj.estadoAnotacionDTO.idEstado !== 7) {
								$scope.notas.push(obj);

								if((obj.tipoAnotacionDTO.idTipoAnotacion === 2 || obj.tipoAnotacionDTO.idTipoAnotacion === 1) && (obj.acto=='Prejudicial' || obj.acto=='Embargo' || obj.acto=='Medida Precautoria' || obj.acto=='Medida Prejudicial Precautoria' || obj.acto=='Quiebra' || obj.acto=='Interdiccion' || obj.acto=='Prohibicion Judicial' || obj.acto=='Prohibicion Voluntaria' || obj.acto=='Desposeimiento' || obj.acto=='Litigio' || obj.acto=='LIQUIDACION LEY 20720')){
									$scope.embargos.push(obj);
								}
							} else if (obj.tipoAnotacionDTO.idTipoAnotacion === 3) {
								$scope.anotaciones
								.push(obj);
							}
						});
				}

				$scope.makeTodos();

				if($scope.embargos.length>0){
					$timeout(function(){
						$scope.openEmbargosModal();
					}, 2000);

				}
			}
			
			//Revisar alertas
			if($scope.data.alertas){
				for(var i=0; i<$scope.data.alertas.length; i++){
					//Solo alertas vigentes de accion 2
					if($scope.data.alertas[i].vigente && $scope.data.alertas[i].accion==2){
						$scope.alertas.push($scope.data.alertas[i]);								
					}
				}	
				
				$timeout(function(){
					for(var i=0; i<$scope.alertas.length; i++){
							$scope.openMensajeModal('warn',$scope.alertas[i].texto, '',  false, null);								
					}
				}, 2000);
			}

		};

		$scope.buscar = function(sol) {
			$scope.isLoading = true;

			var registro = $scope.parametros.registro, foja = $scope.parametros.foja, numero = $scope.parametros.numero, ano = $scope.parametros.ano, bis = $scope.parametros.bis;
			// estado = $scope.parametros.estado;

			var promise = $scope.servicioInscripcion.getInscripcion(
				foja, numero, ano, bis);
			promise
			.then(
				function(data) {
					if (data.status == null) {
						$window.location.href = $window.location.protocol
						+ '//'
						+ $window.location.host
						+ $window.location.pathname;
					} else if (data.status) {

						$scope.data = data;
						$scope.inicia();
						


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

		$scope.buscaNotas = function() {

			//			alert('buscando notas');

			$scope.loaders['anotaciones'].isLoading = true;
			$scope.loaders['notas'].isLoading = true;

			var foja = $scope.parametros.foja, numero = $scope.parametros.numero, ano = $scope.parametros.ano, bis = $scope.parametros.bis;

			var promise = $scope.servicioInscripcion.getNotas(
				foja, numero, ano, bis);
			promise
			.then(
				function(data) {
					if (data.status == null) {
					} else if (data.status) {

						if ($scope.data.consultaDocumentoDTO.tipoDocumento === 8) {

							angular
							.forEach(
								data.anotaciones,
								function(obj) {
									if ((obj.tipoAnotacionDTO.idTipoAnotacion === 1 || obj.tipoAnotacionDTO.idTipoAnotacion === 2)
											&& obj.estadoAnotacionDTO.idEstado !== 7) {
										$scope.notas.push(obj);

										if((obj.tipoAnotacionDTO.idTipoAnotacion === 2 || obj.tipoAnotacionDTO.idTipoAnotacion === 1) && (obj.acto=='Prejudicial' || obj.acto=='Embargo' || obj.acto=='Medida Precautoria' || obj.acto=='Medida Prejudicial Precautoria' || obj.acto=='Quiebra' || obj.acto=='Interdiccion' || obj.acto=='Prohibicion Judicial' || obj.acto=='Prohibicion Voluntaria' || obj.acto=='Desposeimiento' || obj.acto=='Litigio' || obj.acto=='LIQUIDACION LEY 20720')){
											$scope.embargos.push(obj);
										}
									} else if (obj.tipoAnotacionDTO.idTipoAnotacion === 3) {
										$scope.anotaciones
										.push(obj);
									}
								});

						}

						$scope.makeTodos();

						if($scope.embargos.length>0){
							//				console.log("pase embargo")

							$timeout(function(){
								$scope.openEmbargosModal();
							}, 2000);

							//				console.log("listo modal embargo")
						}

					} else {
						$scope.raiseErr('error','Error Obteniendo Notas.',data.msg);
						$scope.loaders['anotaciones'].error = true;
						$scope.loaders['notas'].error = true;
					}

					$scope.loaders['anotaciones'].isLoading = false;
					$scope.loaders['notas'].isLoading = false;

				},
				function(reason) {
					$scope.loaders['anotaciones'].isLoading = false;
					$scope.loaders['notas'].isLoading = false;

					$scope.loaders['anotaciones'].error = true;
					$scope.loaders['notas'].error = true;

					$scope.raiseErr('error','Error detectado.',	'No se ha podido establecer comunicación.');

				});

		};

		if ($scope.parametros.origen === 'digital') {
			var dataState = digitalModel.getDataState();

			if (dataState.inscripcionDigitalDTO) {

				var temp = dataState.inscripcionDigitalDTO.bis ? 'true'
				                                                 : 'false';

				if ($scope.parametros.foja == dataState.inscripcionDigitalDTO.foja
						&& $scope.parametros.numero == dataState.inscripcionDigitalDTO.numero
						&& $scope.parametros.ano == dataState.inscripcionDigitalDTO.ano
						&& $scope.parametros.bis == temp) {

					$scope.data = dataState;
					$scope.inicia();
				} else {
					$scope.buscar();
				}
			} else {
				$scope.buscar();
			}

		} else {
			$scope.buscar();
		}

		$scope.solicitarPreCaratulaMasiva = function (registro) {
			
			$scope.titulosMasivo = [];
			
			angular
			.forEach(
				$scope.titulos,
				function(obj) {
					if (obj.Selected) {
						$scope.titulosMasivo.push(obj);
					} 
				});
			
			var myModal = $modal.open({
				templateUrl: 'indiceModal.html',
				backdrop: true,
				windowClass: 'modal',
				controller: 'IndiceModalCtrl',
				resolve: {
				resolveModal : function(){
				return $scope.resolveModal;
			},
			foja: function(){
				return '';
			},
			numero: function(){
				return '';
			},
			ano: function(){
				return '';
			},
			registro: function(){
				return registro;
			},
			indice: function(){
				return '';
			},
			origen: function(){
				return 'verinscripcion';
			},
			titulos: function(){
				return $scope.titulosMasivo;
			}
			}
			});
		
		};

		//validaciones titulo
		$scope.archivoNacional = function(value){
			return !(value<$rootScope.aioParametros.anoArchivoNacional);
		};

		$scope.anoActual = function(value){
			return !(moment(new Date()).year()<value);
		};
		//fin validaciones titulo
		//fin formulario precaratula

		$scope.makeTodos = function() {

			$scope.paginacionNotas = angular.copy($scope.paginacionMaster);

			$scope.paginacionNotas.todos=$scope.notas;

			var begin = (($scope.paginacionNotas.currentPage - 1) * $scope.paginacionNotas.numPerPage)
			, end = begin + $scope.paginacionNotas.numPerPage;

			$scope.paginacionNotas.filteredTodos = $scope.paginacionNotas.todos.slice(begin, end);

			$scope.paginacionNotas.totalpaginas = Math.ceil($scope.paginacionNotas.todos.length / $scope.paginacionNotas.numPerPage);
			
			//$scope.paginacionNotas.maxSize = Math.round($scope.paginacionNotas.todos.length / $scope.paginacionNotas.numPerPage);

		};

		// $watch search to update pagination
		$scope.$watch('paginacionNotas.filterExprNotas', function (newVal, oldVal) {

			if(newVal!=undefined && oldVal!=undefined){
				$scope.filtered = filterFilter($scope.notas, newVal);
				$scope.paginacionNotas.todos = $scope.filtered;
				$scope.paginacionNotas.currentPage = 1;

				var begin = (($scope.paginacionNotas.currentPage - 1) * $scope.paginacionNotas.numPerPage)
				, end = begin + $scope.paginacionNotas.numPerPage;

				$scope.paginacionNotas.filteredTodos = $scope.paginacionNotas.todos.slice(begin, end);

				//				$scope.paginacionNotas.maxSize = Math.round($scope.paginacionNotas.todos.length / $scope.paginacionNotas.numPerPage);
			}

		}, true);

		$scope.ticketMasiva = function (registro) {

			$scope.titulosTicketMasivo = [];

			for(var j in $scope.titulos) {
				(function(j) {
					if($scope.titulos[j].Selected){
						indiceService.getIndicePropiedades(null,null,null,$scope.titulos[j].foja, $scope.titulos[j].numero, $scope.titulos[j].anio, $scope.titulos[j].bis,null,null,null,null,null,true,false,false,false,false,null,null).then(function(data) {
							console.debug(j);

							if(data.status===null){
							}else if(data.status){
								if(data.aaData.length>0){
									var fila=null;

									for (var i in data.aaData){
										fila = data.aaData[i];
										break;   
									}

									$scope.titulosTicketMasivo[j]=fila;

								}else{
									$scope.fila = angular.copy($scope.filaTicketMasivo);
									$scope.fila.foja=$scope.titulos[j].foja;
									$scope.fila.num=$scope.titulos[j].numero;
									$scope.fila.ano=$scope.titulos[j].anio;
									$scope.titulosTicketMasivo[j]=$scope.fila;
								}

							}else{
								$scope.raiseErr('buscar','Problema detectado', data.msg);
							}
						}, function(reason) {
							$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
						});
					}
				})(j);
			}

			$timeout(function(){

				var myModal = $modal.open({
					templateUrl: 'TicketInformacion.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'TicketInformacionCtrl',
					resolve: {
					resolveModal : function(){
					return $scope.resolveModal;
				},
				foja: function(){
					return '';
				},
				numero: function(){
					return '';
				},
				ano: function(){
					return '';
				},
				nombre: function(){
					return '';
				},
				direccion: function(){
					return '';
				},
				comuna: function(){
					return '';
				},
				registro: function(){
					return registro;
				},
				titulos: function(){
					return $scope.titulosTicketMasivo;
				}
				}
				});

			}, 2000);

		};
		
		$scope.titulosanteriores = function () {
			
			$scope.titulos = [];
			$scope.loaders['titulosanteriores'].isLoading = true;
			
			var promise = borradorService.obtenerTitulosAnteriores($scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					$scope.totalTitulosAnteriores = data.totalTitulosAnteriores;
					
					if(data.listaTitulosAnteriores.length>0){
						for( var i = 0; i < data.listaTitulosAnteriores.length; i++ ) {
							if(data.listaTitulosAnteriores[i].vigente)
								$scope.titulos.push({'Selected':true, 'foja':data.listaTitulosAnteriores[i].foja, 'numero': data.listaTitulosAnteriores[i].numero, 'anio':data.listaTitulosAnteriores[i].ano,'vigente':data.listaTitulosAnteriores[i].vigente, 'id':$scope.titulos.length+1 });
							else
								$scope.titulos.push({'Selected':false, 'foja':data.listaTitulosAnteriores[i].foja, 'numero': data.listaTitulosAnteriores[i].numero, 'anio':data.listaTitulosAnteriores[i].ano,'vigente':data.listaTitulosAnteriores[i].vigente, 'id':$scope.titulos.length+1 });
						}
					}else{
						$scope.titulos.push({ 'Selected':true, 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':'1' });
					}
					
				}else{
					$scope.setErr('data.msg', data.msg);
					$scope.loaders['titulosanteriores'].error = true;
				}
				
				$scope.loaders['titulosanteriores'].isLoading = false;
			}, function(reason) {
				$scope.setErr('Problema contactando al servidor.', '');
				$scope.loaders['titulosanteriores'].isLoading = false;
				$scope.loaders['titulosanteriores'].error = true;
			});
			
		};
		
		//formulario precaratula

		$scope.addRow = function(){		
			$scope.titulos.push({ 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':$scope.titulos.length+1 });
		};

		$scope.removeRow = function(name){				
			var index = -1;		
			var comArr = eval( $scope.titulos );
			for( var i = 0; i < comArr.length; i++ ) {
				if( comArr[i] === name ) {
					index = i;
					break;
				}
			}
			if( index === -1 ) {
				alert( "algo ocurrio" );
			}
			$scope.titulos.splice( index, 1 );		
		};

		$scope.verInscripcionDesdePreCaratula = function(obj) {
			$rootScope.go('/verInscripcion/'
				+ $scope.parametros.registro + '/' + obj.foja
				+ '/' + obj.numero + '/' + obj.anio + '/'
				+ false + '/' + $scope.parametros.origen);
		};
		
		$scope.checkAll = function () {

			if (!$scope.selectedAll) {
				$scope.selectedAll = true;
			} else {
				$scope.selectedAll = false;
			}

			angular.forEach($scope.titulos, function (titulo) {
				if (!titulo.vigente) {
					titulo.Selected = $scope.selectedAll;
				}

			});

    	};

	});





