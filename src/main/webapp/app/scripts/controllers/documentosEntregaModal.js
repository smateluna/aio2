'use strict';

app
.controller('documentoEntregaModalCtrl', function($scope,$timeout, estadoService,$modal,$modalStack, $routeParams, $rootScope,data) {

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

	//funciones
	$scope.reset = function() {
		$scope.data = {};
		$scope.documentos = {
			escrituras : [],
			terminada : null
		};

	};


	$scope.verDocumento = function(escritura) {
		var promise = estadoService.existeDocumento(escritura);
		promise
		.then(
			function(data) {
				if (data.success == null) {
				} else if (data.success) {
					location.href = 'http://192.168.100.164/documentos/do/fileService?metodo=download&idDocumentop='
						+ escritura.idDocumento
						+ '&idDetalleDocumentop='
						+ escritura.idDetalleDocumento
						+ '&caratulap='
						+ escritura.caratula
						+ '&versionp='
						+ escritura.version
						+ '&idTipoDocumentop='
						+ escritura.idTipoDocumento
						+ '&idRegp='
						+ escritura.idReg
						+ '&esVersionp='
						+ escritura.esVersion
						+ '&esPrincipalp='
						+ escritura.esPrincipal
						+ '&esDetallep='
						+ escritura.esDetalle;
				} else {
					$scope.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
	};

	$scope.buscar = function() {
		$scope.reset();

		$scope.openLoadingModal(
			'Buscando #' + $scope.req.numeroCaratula + '...',
		'');

		var promise = estadoService
		.getDocumentosEntrega($scope.req.numeroCaratula);
		promise
		.then(
			function(data) {
				$scope.closeModal();
				if (data.status === null) {

				} else if (data.status) {
					$scope.data = data;
				} else {
					$scope.raiseErr(data.msg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
	};



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

						$scope.buscar();

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
		$rootScope.go('/verInscripcionReg/hip/' + titulo.foja
			+ '/' + titulo.numero + '/' + titulo.ano + '/'
			+ bis + '/estado');
	};

	$scope.verTipoImagenProh = function(titulo) {

		var bis = titulo.bis == 1 ? true : false;
		$rootScope.go('/verInscripcionReg/proh/' + titulo.foja
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

	$scope.verDocumentoEstudio = function(escritura) {
		$rootScope.go('/verEscrituraEstudio/'
			+ $scope.req.numeroCaratula + '/'
			+ escritura.version + '/estado');
	};


	$scope.dejarDocumentoNoVigente = function(doc) {
		var promise = estadoService
		.dejarDocumentoNoVigente(doc.codArchivoAlpha);
		promise
		.then(
			function(data) {
				if (data.status === null) {

				} else if (data.status) {
					$scope.closeModal();
				} else {
					$scope.raiseErr(data.msg);
				}
			},
			function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
	};

	$scope.cancel = function () {
		//$modalInstance.dismiss('cancel');

		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};


});
