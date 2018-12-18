'use strict';

app
.controller('EstadoCtrl', function($scope, $http, $window, $location,
	$timeout, localStorageService, $log, estadoService,
	reingresoService, inscripcionDigitalService, $modal,
	$modalStack, $route, $routeParams, Usuario, $rootScope, escrituraService, Modal) {

	//models
	var tab = {
		parentActive : 1
	};

	$scope.tab = tab;

	$scope.req = {
		numeroCaratula : null,
		simpleMode : false
	};

	$scope.documentos = {
		escrituras : [],
		terminada : null
	};

	//fin models

	//controles tabs
	$scope.isActiveParent = function(id) {
		return $scope.tab.parentActive === id;
	};

	$scope.activateParent = function(id) {
		$scope.tab.parentActive = id;

		if ($scope.tab.parentActive === 1) {

		} else if ($scope.tab.parentActive === 2) {

		}
	};
	//fin controles tabs

	//params

	$scope.limpiarCache = function() {
		localStorageService.remove('rawScreens');
	};

	$scope.colapsar = function(area) {

		//Buscar "Documentos Entrega" al maximizar area de documentos
		if (area.title == 'Documentos Entrega'
			&& !$scope.data.res.entregaEnLineaDTO
			&& area.collapsed) {
			var promise = estadoService
			.getDocumentosEntrega($scope.req.numeroCaratula);
			promise
			.then(
				function(data) {
					if (data.status === null) {

					} else if (data.status) {
						$scope.data.res.entregaEnLineaDTO = data.res.entregaEnLineaDTO;
					} else {
						$scope.raiseErr(data.msg);
					}
				},
				function(reason) {
					$scope
					.raiseErr('No se ha podido establecer comunicación con el servidor.');
				});
		}

		if (area.collapsed) {
			area.collapsed = false;
		} else {
			area.collapsed = true;
		}
		localStorageService.set('rawScreens', $scope.rawScreens);
	};

	$scope.defaultRawScreens = [ [ {
		title : 'Datos formulario',
		url : 'views/estado/datos_formulario.html',
		collapsed : false
	}, {
		title : 'Requirente',
		url : 'views/estado/requirente.html',
		collapsed : false
	}, {
		title : 'Cuenta Corriente',
		url : 'views/estado/cuenta_corriente.html',
		collapsed : false
	}, {
		title : 'Observaciones',
		url : 'views/estado/observaciones.html',
		collapsed : false
	}, {
		title : 'Documentos Entrega',
		url : 'views/estado/documentos_entrega.html',
		collapsed : true
	}, {
		title : 'Escrituras',
		url : 'views/estado/escritura.html',
		collapsed : false
	} ], [ {
		title : 'Estado Actual',
		url : 'views/estado/estado_actual.html',
		collapsed : false
	}, {
		title : 'Citado',
		url : 'views/estado/citado.html',
		collapsed : false
	}, {
		title : 'Tareas',
		url : 'views/estado/tareas.html',
		collapsed : false
	},

	{
		title : 'Movimientos',
		url : 'views/estado/movimientos.html',
		collapsed : false
	},

	{
		title : 'Ingresos - Egresos',
		url : 'views/estado/ingresos_egresos.html',
		collapsed : false
	},

	{
		title : 'Solicitud Web',
		url : 'views/estado/solicitud_web.html',
		collapsed : false
	}, {
		title : 'Repertorios',
		url : 'views/estado/repertorio.html',
		collapsed : false
	} ] ];

	$scope.rawScreens = localStorageService.get('rawScreens');

	if ($scope.rawScreens === null) {

		$scope.rawScreens = $scope.defaultRawScreens
		localStorageService.set('rawScreens', $scope.rawScreens);

	} else {

		//Buscar cambios en defaultRawScreens -> Agregar nuevos modulos    
		for ( var i = 0; i < $scope.defaultRawScreens.length; i++) {
			for ( var j = 0; j < $scope.defaultRawScreens[i].length; j++) {
				var existe = false;
				for ( var l = 0; l < $scope.rawScreens.length; l++)
					for ( var m = 0; m < $scope.rawScreens[l].length; m++)
						if ($scope.defaultRawScreens[i][j].title == $scope.rawScreens[l][m].title)
							existe = true;
				if (!existe)
					$scope.rawScreens[i].splice(j, 0,
						$scope.defaultRawScreens[i][j]);
			}
		}

		//Buscar cambios en defaultRawScreens -> Eliminar modulos obsoletos
		for ( var i = 0; i < $scope.rawScreens.length; i++) {
			for ( var j = 0; j < $scope.rawScreens[i].length; j++) {
				var existe = false;
				for ( var l = 0; l < $scope.defaultRawScreens.length; l++)
					for ( var m = 0; m < $scope.defaultRawScreens[l].length; m++)
						if ($scope.rawScreens[i][j].title == $scope.defaultRawScreens[l][m].title)
							existe = true;

				if (!existe)
					$scope.rawScreens[i].splice(j, 1);

			}
		}

		localStorageService.set('rawScreens', $scope.rawScreens);

	}

	$scope.sortableOptions = {
		containment : 'body',
		placeholder : 'placeholder-estado',
		handle : '.handle',
		//cursor: '-webkit-grabbing',
		//revert: false,
		opacity : 0.9,
		connectWith : '.apps-container',
		forcePlaceholderSize : true,
		update : function(event, ui) {

		if (event.target.id !== 'screen-1'
			&& ui.item.sortable.droptarget.attr('id') === 'screen-1'
				&& $scope.rawScreens[0].length === 1) {
			ui.item.sortable.cancel();
		}

		if (event.target.id !== 'screen-0'
			&& ui.item.sortable.droptarget.attr('id') === 'screen-0'
				&& $scope.rawScreens[1].length === 1) {
			ui.item.sortable.cancel();
		}
	},
	stop : function(event, ui) {
		localStorageService
		.set('rawScreens', $scope.rawScreens);
	}
	};

	//funciones
	$scope.reset = function() {
		$scope.data = {};
		$scope.documentos = {
			escrituras : [],
			terminada : null
		};

	};

	$scope.verDocumentoTerminado = function() {
		$scope.verDocumentoEstudio($scope.documentos.terminada);
	};

	$scope.limpiar = function() {
		$scope.reset();

		$scope.req.numeroCaratula = null;

		$scope.doFocus('numeroCaratula');
	};

	$scope.openDatosFormularioModal = function() {
		$modal.open( {
			templateUrl : 'datos_formularioModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'DatosFormularioModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		}
		}
		});
	};

	$scope.openRequirenteModal = function() {
		$modal.open( {
			templateUrl : 'requirenteModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'RequirenteModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		}
		}
		});
	};

	$scope.openCitadoModal = function() {
		$modal.open( {
			templateUrl : 'citadoModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'CitadoModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		}
		}
		});
	};

	$scope.openMovimientosModal = function() {
		var caratula = {
			caratulaDTO : $scope.data.res.caratulaDTO
		};

		$modal.open( {
			templateUrl : 'movimientosModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'MovimientosModalCtrl',
			resolve : {
			caratula : function() {
			return caratula;
		}
		}
		});
	};

	$scope.openTareasModal = function(operacion, tareaDTO) {
		$modal.open( {
			templateUrl : 'tareasModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'TareasModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		},
		operacion : function() {
			return operacion;
		},
		tareaDTO : function() {
			return tareaDTO;
		}
		}
		});
	};

	$scope.abrirCaratulasPorRutModal = function() {
		var modalCaratulaPorRut = $modal.open( {
			templateUrl : 'caratulasPorRutModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'CaratulasPorRutCtrl',
			resolve : {
			numeroCaratula : function() {
			return $scope.req.numeroCaratula;
		}
		}
		});

		modalCaratulaPorRut.result.then(function(caratula) {
			$scope.req.numeroCaratula = caratula;
			$scope.buscar();
		}, function() {
		});
	};

	$scope.openObservacionesModal = function(operacion, tareaDTO) {
		$modal.open( {
			templateUrl : 'observacionesModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'ObservacionesModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		},
		operacion : function() {
			return operacion;
		},
		tareaDTO : function() {
			return tareaDTO;
		}
		}
		});
	};
	
	$scope.openDocumentosEntregaModal = function() {
		$modal.open( {
			templateUrl : 'documentos_entregaModal.html',
			backdrop : true,
			windowClass : 'modal',
			controller : 'DocumentosEntregaModalCtrl',
			resolve : {
			data : function() {
			return $scope.data;
		}
		}
		});
	};	

	$scope.buscar = function() {
		$scope.reset();

		$scope.openLoadingModal(
			'Buscando #' + $scope.req.numeroCaratula + '...',
		'');

		var promise = estadoService
		.getEstado($scope.req.numeroCaratula);
		promise
		.then(
			function(data) {
				$scope.closeModal();
				if (data.status == null) {

				} else if (data.status) {

					$scope.data = data;
					$scope.anulada = data.res.caratulaDTO.estadoActualDTO.descripcionEnFlujo == 'Anulada' ? true
					                                                                                      : false;

					//Dejar collapsed de "Documentos Entrega" en true luego de buscar caratula
					for ( var i = 0; i < $scope.rawScreens.length; i++)
						for ( var j = 0; j < $scope.rawScreens[i].length; j++)
							if ($scope.rawScreens[i][j].title == 'Documentos Entrega' || $scope.rawScreens[i][j].title == 'Escrituras')
								$scope.rawScreens[i][j].collapsed = true;

					//Buscar documentos entrega si estado actual es Finalizada
					if (data.res.caratulaDTO.estadoActualDTO.descripcionEnFlujo == 'Finalizada') {
						var promise = estadoService
						.getDocumentosEntrega($scope.req.numeroCaratula);
						promise
						.then(
							function(data) {
								if (data.status === null) {

								} else if (data.status) {
									$scope.data.statusDocumentosEntrega = data.status;
									$scope.data.res.entregaEnLineaDTO = data.res.entregaEnLineaDTO;

									//Dejar collapsed de "Documentos Entrega" en false		        
									for ( var i = 0; i < $scope.rawScreens.length; i++) {
										for ( var j = 0; j < $scope.rawScreens[i].length; j++)
											if ($scope.rawScreens[i][j].title == 'Documentos Entrega')
												$scope.rawScreens[i][j].collapsed = false;
									}

								} else {
									$scope
									.raiseErr(data.msg);
								}
							},
							function(reason) {
								$scope
								.raiseErr('No se ha podido establecer comunicación con el servidor.');
							});
					} else
						$scope.data.statusDocumentosEntrega = true;

					//Buscar Repertorios
					var promise = estadoService
					.getRepertorios($scope.req.numeroCaratula);
					promise
					.then(
						function(data) {
							if (data.status === null) {

							} else if (data.status) {
								$scope.data.statusRepertorios = data.status;
								$scope.data.res.caratulaDTO.repertorioDTOs = data.res.repertorioDTOs;
								
								for(var repertorio in $scope.data.res.caratulaDTO.repertorioDTOs){
									if($scope.data.res.caratulaDTO.repertorioDTOs[repertorio].vigente==0){
										$scope.openComentariosModal($scope.data.res.caratulaDTO.repertorioDTOs[repertorio]);
										break;
									}	
								}

							} else {
								$scope
								.raiseErr(data.msg);
							}
						},
						function(reason) {
							$scope
							.raiseErr('No se ha podido establecer comunicación con el servidor.');
						});

					//Buscar Ingresos Egresos
					var promise = estadoService
					.getIngresosEgresos($scope.req.numeroCaratula);
					promise
					.then(
						function(data) {
							if (data.status === null) {

							} else if (data.status) {
								$scope.data.statusIngresosEgresos = data.status;
								$scope.data.res.caratulaDTO.ingresoEgresoDTOs = data.res.ingresoEgresoDTOs;

							} else {
								$scope
								.raiseErr(data.msg);
							}
						},
						function(reason) {
							$scope
							.raiseErr('No se ha podido establecer comunicación con el servidor.');
						});

					//Buscar Cuenta Corriente
					if ($scope.data.res.caratulaDTO.datosFormularioDTO.codigo != null
							&& $scope.data.res.caratulaDTO.datosFormularioDTO.codigo != 0) {
						var promise = estadoService
						.getCuentaCorriente($scope.data.res.caratulaDTO.datosFormularioDTO.codigo);
						promise
						.then(
							function(data) {
								if (data.status === null) {

								} else if (data.status) {
									$scope.data.statusCuentaCorriente = data.status;
									$scope.data.res.caratulaDTO.cuentaCorrienteDTO = data.res.cuentaCorrienteDTO;

								} else {
									$scope
									.raiseErr(data.msg);
								}
							},
							function(reason) {
								$scope
								.raiseErr('No se ha podido establecer comunicación con el servidor.');
							});
					} else
						$scope.data.statusCuentaCorriente = true;

					//Buscar Producto Web
					if ($scope.data.res.caratulaDTO.productoWebDTO.idUsuarioWeb != null
							&& $scope.data.res.caratulaDTO.productoWebDTO.idUsuarioWeb != 0) {
						var promise = estadoService
						.getProductoWeb($scope.data.res.caratulaDTO.productoWebDTO);
						promise
						.then(
							function(data) {
								if (data.status === null) {

								} else if (data.status) {
									$scope.data.statusProductoWeb = data.status;
									$scope.data.res.caratulaDTO.productoWebDTO = data.res.productoWebDTO;

								} else {
									$scope
									.raiseErr(data.msg);
								}
							},
							function(reason) {
								$scope
								.raiseErr('No se ha podido establecer comunicación con el servidor.');
							});
					} else
						$scope.data.statusProductoWeb = true;

					//Buscar Escrituras		
					$scope.buscarEscrituras();

					//Buscar Caratula Terminada
					var promise = estadoService
					.getDocumentos(
						$scope.req.numeroCaratula,
						'TERMINADAS');
					promise
					.then(
						function(data) {
							if (data.status === null) {

							} else if (data.success) {
								$scope.data.res.caratulaDTO.documentos = {};

//								Se modifica la busqueda para guardar la ultima version de caratula terminada, no la primera
//								for ( var i = 0; i < data.children.length; i++)
//									for ( var j = 0; j < data.children[i].children.length; j++)
//										$scope.documentos.terminada = data.children[i].children[j];
								if(data.children[0]!= undefined && data.children[0].children[0]!=undefined)
									$scope.documentos.terminada = data.children[0].children[0];
								
								$scope.data.statusDocumentosTerminado = data.success;

							} else {
								$scope
								.raiseErr(data.errormsg);
							}
						},
						function(reason) {
							$scope
							.raiseErr('No se ha podido establecer comunicación con el servidor.');
						});

				} else {
					$scope.limpiar();
					$scope.raiseErr(data.msg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
	};
	
	$scope.buscarEscrituras = function(){
		var promise = estadoService
		.getDocumentos(
			$scope.req.numeroCaratula,
			'ESCRITURAS');
		promise
		.then(
			function(data) {
				if (data.status === null) {

				} else if (data.success) {
					$scope.data.res.caratulaDTO.documentos = {};
					$scope.documentos.escrituras = [];
					for ( var i = 0; i < data.children.length; i++)
						$scope.documentos.escrituras
						.splice(
							$scope.documentos.escrituras.length,
							0,
							data.children[i].children[0]);
					//for(var j=0; j<data.children[i].children.length; j++)
						//$scope.documentos.escrituras.splice($scope.documentos.escrituras.length, 0, data.children[i].children[0]);

					$scope.data.statusDocumentos = data.success;

				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});		
	}

	$scope.reingresarCaratula = function() {
		var tipoFormulario = $scope.data.res.caratulaDTO.datosFormularioDTO.tipoFormularioDTO.id;

		if (tipoFormulario == 1 || tipoFormulario == 2) {
			$scope
			.openLoadingModal(
				'Reingresando carátula #' + $scope.req.numeroCaratula + '...',
			'');
			var promise = reingresoService
			.getCaratula($scope.req.numeroCaratula);
			promise
			.then(
				function(data) {
					if (data.estado === null) {
					} else if (data.estado) {
						promise = reingresoService
						.reingresarCaratula(
							data.caratulaDTO,
							data.caratulaDTO,
							"reingreso desde AIO Estado",
							null, null,
							null);
						promise
						.then(
							function(data) {
								$scope
								.closeModal();
								if (data.estado === null) {
								} else if (data.estado) {
									$scope
									.raiseSuccess(data.msg);
									$scope.data.res.caratulaDTO.bitacoraDTOs
									.splice(
										0,
										0,
										data.bitacoraDTO);
								} else {
									$scope
									.raiseErr("No se puedo reingresar la caratula. Reintente desde modulo de Reingreso");
								}
							},
							function(reason) {
								$scope
								.raiseErr('No se ha podido establecer comunicacion con el servidor.');
							});
					} else {
						$scope
						.raiseErr("No se puedo reingresar la caratula. Reintente desde modulo de Reingreso");
					}
				},
				function(reason) {
					$scope
					.raiseErr('No se ha podido establecer comunicacion con el servidor.');
				});

		} else {
			$location.path('/reingreso');
		}
	}

	//  $scope.exportarPDF = function(){
	//	  
	//	  $window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=getEstadoReporte&ingresoEgreso=true&tipo=pdf&nc='+$scope.req.numeroCaratula;
	//	  
	//  };

	$scope.exportarPDF = function(gp) {
		$modal.open( {
			templateUrl : 'verExportarPDF.html',
			backdrop : 'static',
			keyboard : false,
			size : 'lg',
			windowClass : 'modal',
			controller : 'VerExportarPDFCtrl',
			resolve : {
			nc : function() {
			return $scope.req.numeroCaratula;
		}
		}
		});
	};

	$scope.muestraRechazo = function() {
		if ($scope.data !== undefined
				&& $scope.data.res !== undefined
				&& $scope.data.res.caratulaDTO.estadoActualDTO !== null
				&& $scope.data.res.caratulaDTO.estadoActualDTO.seccionDTO !== null) {
			return !($scope.data.res.caratulaDTO.estadoActualDTO.seccionDTO.id === '10' || $scope.data.res.caratulaDTO.estadoActualDTO.seccionDTO.id === '08');
		} else {
			return false;
		}
	};

	//  $scope.showRechazarModal = function(){
	//
	//  $scope.openRechazoModal();
	//
	//  };

	//fin funciones

	$scope.raiseErr = function(msg) {
		$scope.openMensajeModal('error', msg, '', false, null);
	};

	$scope.raiseSuccess = function(msg) {
		$scope.openMensajeModal('success', msg, '', false, null);
	};

	//utils

	$scope.doFocus = function(name) {
		$scope.$broadcast(name + 'IsFocused');
	};

	$scope.openMensajeModal = function(tipo, titulo, detalle,
		autoClose, segundos) {
		$modal.open( {
			templateUrl : 'mensajeModal.html',
			backdrop : true,
			keyboard : true,
			windowClass : 'modal',
			controller : 'MensajeModalCtrl',
			resolve : {
			tipo : function() {
			return tipo;
		},
		titulo : function() {
			return titulo;
		},
		detalle : function() {
			return detalle;
		}
		}
		});

		if (autoClose) {
			$timeout(function() {
				$scope.closeModal();
			}, segundos * 1000);
		}
	};

	$scope.openLoadingModal = function(titulo, detalle) {
		$modal.open( {
			templateUrl : 'loadingModal.html',
			backdrop : 'static',
			keyboard : false,
			size : 'sm',
			windowClass : 'modal',
			controller : 'LoadingModalCtrl',
			resolve : {
			titulo : function() {
			return titulo;
		},
		detalle : function() {
			return detalle;
		}
		}
		});
	};

	$scope.openSolicitar = function() {
		$scope.$broadcast('sfojaIsFocused');
	};

	//  $scope.openRechazoModal = function () {
	//    var dataRechazo = {
	//      numeroCaratula : $scope.req.numeroCaratula
	//    };
	//
	//    $modal.open({
	//      templateUrl: 'rechazoModal.html',
	//      backdrop: 'static',
	//      keyboard: false,
	//      //size: 'sm',
	//      windowClass: 'modal',
	//      controller: 'RechazoModalCtrl',
	//      resolve: {
	//        dataRechazo: function(){
	//          return dataRechazo;
	//        }
	//      }
	//    });
	//  };

	$scope.openRechazoModal = function() {
		var dataRechazo = {
			numeroCaratula : $scope.req.numeroCaratula
		};

		$modal.open( {
			templateUrl : 'rechazoCertificacionModal.html',
			backdrop : 'static',
			keyboard : false,
			//size: 'sm',
			windowClass : 'modal',
			controller : 'RechazoCertificacionModalCtrl',
			resolve : {
			dataRechazo : function() {
			return dataRechazo;
		},
		origen : function() {
			return 'estado';
		}
		}
		});
	};

	$scope.closeModal = function() {
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.stringIsNumber = function(s) {
		var x = +s;
		return x.toString() === s;
	};

	//fin utils
	$timeout(function() {
		//init
		if ($routeParams.caratula !== undefined) {
			//Si caratula viene en el request, buscar
			if ($scope.stringIsNumber($routeParams.caratula)
					&& $routeParams.caratula.length <= 10) {

				$scope.req.numeroCaratula = $routeParams.caratula;

				$timeout(function() {
					$scope.buscar();
				}, 500);
			} else if ($routeParams.caratula === 'limpiar') {
				$scope.limpiarCache();
			}
		} else if ($scope.req.simpleMode != "true") {
			//Si no, buscar caratula en sesion y buscar datos si existe
			var promise = estadoService.getCaratulaSesion();
			promise
			.then(
				function(data) {
					if (data.status === null) {
					} else if (data.status) {
						$scope.req.numeroCaratula = data.numeroCaratula;
						//$scope.buscar();

					}
				},
				function(reason) {
					$scope
					.raiseErr('No se ha podido establecer comunicación con el servidor.');
				});
		}

		$scope.doFocus('numeroCaratula');
	}, 500);

	$scope.verTipoImagenHipo = function(titulo) {

		var bis = titulo.bis == 1 ? true : false;
		$rootScope.go('/verInscripcion/hip/' + titulo.foja
			+ '/' + titulo.numero + '/' + titulo.ano + '/'
			+ bis + '/estado');
	};

	$scope.verTipoImagenProh = function(titulo) {

		var bis = titulo.bis == 1 ? true : false;
		$rootScope.go('/verInscripcion/proh/' + titulo.foja
			+ '/' + titulo.numero + '/' + titulo.ano + '/'
			+ bis + '/estado');
	};

	$scope.verTitulo = function(titulo) {

		var bis = titulo.bis == 1 ? true : false,
		                          //realizar alternativa para saber tipo de documento;                       
		                          estado = '0';

		$rootScope.go('/verInscripcion/prop/' + titulo.foja + '/'
			+ titulo.numero + '/' + titulo.ano + '/' + bis
			+ '/estado/' + estado);

	};
	
	$scope.downloadBoleta = function(documentoEntrega) {
		var documento ={
			"nombreArchivo": documentoEntrega.numero+".pdf",
			"idTipoDocumento": 12, //idTipoDocumento BOLETA
			"idReg": 0
		};
		
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
	
	$scope.downloadFirma = function(documentoEntrega) {
		var documento ={
			"nombreArchivo": documentoEntrega.nombreArchivoVersion,
			"fechaDocumento": documentoEntrega.fechaFirma,
			"rutFirmador": documentoEntrega.usuario
		};
		
		//existe documento
		var promise = estadoService
		.existeFirma(documento);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=downloadFirma&documento='+ encodeURIComponent(JSON.stringify(documento));
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

	$scope.verDocumentoEstudio = function(escritura) {
		var documento ={
			"nombreArchivo": escritura.nombreArchivoVersion,
			"idTipoDocumento": escritura.idTipoDocumento,
			"idReg": escritura.idReg,
			"fechaDocumento": escritura.fechaProcesa
		};		
		
		//existe documento
		var promise = estadoService
		.existeDocumento(documento);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					//download documento
					$window.open('../do/service/escritura?metodo=verDocumentoEstudio&caratula='+ $scope.req.numeroCaratula +'&version='+escritura.version+'&idTipoDocumento='+escritura.idTipoDocumento+'&type=uri','','width=800,height=600');

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
	
	$scope.vincularDocumentos = function(){
		
		
	}
	
	$scope.removeVersion = Modal.confirm.delete(function(escritura){
		$scope.openLoadingModal('Eliminando version de escritura...', '');

		var promise = escrituraService.eliminarDocumento(escritura.idDocumento, $scope.req.numeroCaratula, escritura.version);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){

				$scope.raiseSuccess('Se ha eliminado la version de Escritura');
				$scope.buscarEscrituras();

			}else{
				$scope.raiseErr(data.msg);				
			}
		}, function(reason) {
			$scope.statesDescarga.ok=false;
			$scope.statesDescarga.msg='no se ha podido establecer comunicacion con el servidor.';
		});	
		
		
	});	
	
	$scope.openComentariosModal = function(repertorios) {
		$modal.open( {
			templateUrl : 'comentariosModal.html',
			backdrop : true,
			keyboard : true,
			windowClass : 'modal',
			controller : 'ComentariosModalCtrl',
			resolve : {
			repertorios : function() {
			return repertorios;
		}
		}
		});

	};

});
