'use strict';

app.controller('LiquidacionCtrl', function ($scope, $window, $timeout, $rootScope, $modal, $modalStack, $routeParams, tareasService, estadoService, $filter) {

	$scope.nuevoDocumento = {
			nombre: null,
			valor: null,
			caratula: null
	};	
	
	$scope.bitacora = {
			resultado: []
	};

	$timeout(function(){
		$scope.doFocus('caratula');
	}, 500);

	$scope.buscarCaratula = function(){

		var caratula = $scope.caratula;

		$scope.openLoadingModal('Buscando...', '');

		var promise = tareasService.getCaratulaLiquidacion(caratula);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){
				$scope.liquidacionCaratula = data.liquidacionCaratula;
//				$scope.valorRealCaratula = $scope.liquidacionCaratula.caratulaDTO.valorReal;
//				$scope.liquidacionCaratula.caratulaDTO.valorReal = $scope.getTotalPapeles();
				$scope.liquidacionCaratula.caratulaDTO.diferencia = $scope.liquidacionCaratula.caratulaDTO.valorReal - $scope.liquidacionCaratula.caratulaDTO.valorPagado;
				$scope.liquidacionCaratula.status = data.status;
				$scope.liquidacionCaratula.estaIngresadaCtaCte = data.estaIngresadaCtaCte;
				$scope.liquidacionCaratula.puedeLiquidar = data.puedeLiquidar;
				$scope.liquidacionCaratula.caratulaLiquidada = data.caratulaLiquidada;
				$scope.liquidacionCaratula.caratulaPendienteEntregaDoc = data.caratulaPendienteEntregaDoc;
				$scope.liquidacionCaratula.documentosLiquidacion = data.documentosLiquidacion;
				$scope.liquidacionCaratula.busqueda = true; 		

				$scope.contadorPapel =0;
				for(var i = 0; i < $scope.liquidacionCaratula.papeles.length; i++){
					var papel =$scope.liquidacionCaratula.papeles[i];
					if(papel!=null && papel.codArchivoAlpha!=null){
						papel.glosaCobroCertificado = $scope.getGlosa(papel);
					}
					
				}
				
				$scope.bitacora.resultado = data.listabitacoras;
				$scope.ultimaLiquidacion = $filter('filter')(data.listabitacoras, {observacion: 'Liquidacion de caratula aprobada'})[0];
				$scope.ultimaAnulacion = $filter('filter')(data.listabitacoras, {observacion: 'Se elimino caratula de lista pendientes a Entrega Documentos'})[0];
			}else{
				$scope.raiseErr(data.msg);
			}
		}, function(reason) {
			$scope.closeModal();
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});			

		$scope.buscarEscritura();

	}; 
	
	$scope.getGlosa = function (papel) {
		var promise = tareasService.getGlosaDocumento(papel.codArchivoAlpha);
		promise.then(function(data) {

			if(data.status===null){

			}else if(data.status){
				papel.glosaCobroCertificado = data.glosaDocumento;
				$scope.contadorPapel++;
			}else{
				$scope.raiseErr('No se pudo obtener glosa documento', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};	
	
	$scope.buscarEscritura = function(){
		var promise = estadoService
		.getDocumentos(
				$scope.caratula,
		'ESCRITURAS');
		promise
		.then(
				function(data) {
					if (data.status === null) {

					} else if (data.success) {
						if(data.children.length>0){
							for ( var i = 0; i < data.children.length; i++){
								if(data.children[i].children[0].vigente){
									$scope.escritura = data.children[i].children[0];
									break;
								}
							}
						}

						$scope.statusEscritura = data.success;

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

	$scope.limpiar = function(){
		$scope.liquidacionCaratula = null;
		$scope.bitacora.resultado = null;
		$scope.caratula = null;
		$scope.doFocus('caratula');  		
	}

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
						$window.open('../do/service/escritura?metodo=verDocumentoEstudio&caratula='+ $scope.liquidacionCaratula.caratulaDTO.numeroCaratula +'&version='+escritura.version+'&idTipoDocumento='+escritura.idTipoDocumento+'&type=uri','popup','width=800,height=600');

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

	$scope.masInfoLiquidacion = function (caratula) {

		var myModal = $modal.open({
			templateUrl: 'masInfoLiquidacionModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'masInfoLiquidacionModalCtrl',
			resolve: { 
				caratula: function(){
					return caratula;
				}
			}
		});
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

	$scope.raiseErr = function(msg){
		$scope.openMensajeModal('error', msg, '',  false, null);
	};
	
	$scope.raiseWarn = function(msg){
		$scope.openMensajeModal('warn', msg, '',  false, null);
	};	

	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
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

		myModal.result.then(function () {
			$scope.doFocus('caratula');
		}, function () {
			$scope.doFocus('caratula');
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

	$scope.obtenerEscritura = function (sol) {

		var myModal = $modal.open({
			templateUrl: 'escrituraelectronicaModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'escrituraElectronicaModalCtrl',
			resolve: { 
				sol: function(){
					return sol;
				}
			}
		});

		myModal.result.then(function () {
			$scope.buscarLiquidaciones();
		}, function () {
			$scope.buscarLiquidaciones();
		});
	};

	$scope.obtenerEscrituraPorFuera = function (caratula) {

		var myModal = $modal.open({
			templateUrl: 'escrituraelectronicaModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'escrituraElectronicaModalCtrl',
			resolve: { 
				sol: function(){
					return caratula;
				}
			}
		});

		myModal.result.then(function () {
			$scope.buscarLiquidaciones();
		}, function () {
			$scope.buscarLiquidaciones();
		});
	};

	$scope.aprobarCaratula = function(){
		$scope.openLoadingModal('procesando...', '');
		var actualizarCierre = $scope.liquidacionCaratula.estaIngresadaCtaCte;		
		var canal = $scope.liquidacionCaratula.caratulaDTO.idCanal;
		var seccionDestino = 51; 
		
		if($rootScope.subPermisos['liquidacion'].indexOf('LIQUIDADOR_HIPOTECA') >= 0)
			seccionDestino=53;
		else if($rootScope.subPermisos['liquidacion'].indexOf('LIQUIDADOR_PROHIBICIONES') >= 0)
			seccionDestino=54;
		
		var promise = tareasService.aprobarCaratula($scope.liquidacionCaratula.caratulaDTO.numeroCaratula,$scope.liquidacionCaratula.caratulaDTO.idCanal, encodeURIComponent(JSON.stringify($scope.liquidacionCaratula.papeles)), $scope.getTotalPapeles(), actualizarCierre,seccionDestino);
		//var promise = tareasService.aprobarCaratula($scope.liquidacionCaratula.caratulaDTO.numeroCaratula, null, $scope.getTotalPapeles(), actualizarCierre);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.estado===null){
			}else if(data.status){
				$scope.limpiar();
				if(canal==1)
					$scope.openMensajeModal('success','Carátula liquidada, pero pendiente de envio a Entrega Documentos', '',  true, 3);
				else
					$scope.openMensajeModal('success','Carátula liquidada exitosamente', '',  true, 3);
				
				if(data.warn)
					$scope.raiseWarn(data.msg);
			}else{
				$scope.raiseErr(data.msg);
			}
		}, function(reason) {
			$scope.closeModal();
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});	
	};

	$scope.downloadFirma = function(documentoEntrega) {
		var documento ={
				"nombreArchivo": documentoEntrega.nombreArchivoVersion,
				"idTipoDocumento": 3,
				"fechaDocumento": documentoEntrega.fechaPdf,
				"rutFirmador": documentoEntrega.usuario
		};

		//existe documento
		var promise = estadoService
		.existeFirma(documento);
		promise
		.then(
				function(data) {
					if (data.hayDocumento) {
						$window.open($window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=downloadFirma&documento='+ encodeURIComponent(JSON.stringify(documento))+'&download=false','popupDoc','width=800,height=600');
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

	$scope.downloadBoleta = function(numeroBoleta) {
		var documento ={
				"nombreArchivo": numeroBoleta+".pdf",
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

	$scope.getTotalPapeles = function(){
		var total = 0;
		for(var i = 0; i < $scope.liquidacionCaratula.papeles.length; i++){
			var papel =$scope.liquidacionCaratula.papeles[i];
			if(papel!=null && papel.valorDocumento!=null){	    		
				total += parseInt(papel.valorDocumento);
			}
		}
		for(var i = 0; i < $scope.liquidacionCaratula.documentosLiquidacion.length; i++){
			var doc =$scope.liquidacionCaratula.documentosLiquidacion[i];
			if(doc!=null && doc.valor!=null){	    		
				total += parseInt(doc.valor);
			}
		}
		
		$scope.redondeoInferior=false;
		$scope.redondeoSuperior=false;
		var ultimoDigito = Number(total.toString().split('').pop());
		if(ultimoDigito!=0){
			if(ultimoDigito<=5){
				total -= ultimoDigito;
				$scope.redondeoInferior=true;
			} else{
				total += (10-ultimoDigito);
				$scope.redondeoSuperior=true;
			}
		}
		
		return total;		

	};

	$scope.agregarDocumento = function(nombre, valor, caratula){
		var documento = {
				nombre: nombre,
				valor: valor,
				caratula: caratula
		};	
		$scope.openLoadingModal('Agregando documento', '');
		var promise = tareasService.agregarDocumentoLiquidacion(documento);
		promise.then(
			function(data) {
				$scope.closeModal();
				if(data.status){
					$scope.liquidacionCaratula.documentosLiquidacion.push(angular.copy(data.documentoLiquidacion));
					$scope.resetNuevoDocumento();
					if(data.warn)
						$scope.raiseWarn(data.msg);
				} else
					$scope.raiseErr(data.msg);
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});			

		
		$scope.doFocus('descripcion');
	};

	$scope.eliminarDocumento = function(papel){
		$scope.openLoadingModal('Eliminando documento', '');
		var promise = tareasService.anularDocumento(papel.codArchivoAlpha, papel.tipoDocumentoDTO.descripcion, $scope.liquidacionCaratula.caratulaDTO.numeroCaratula);
		promise.then(function(data) {
			$scope.closeModal();
			if (data.status) {
				$scope.openMensajeModal('success','Documento anulado', '',  true, 2);
				$scope.eliminarDocumentoLista(papel);
				if(data.warn)
					$scope.raiseWarn(data.msg);
			} else {
				$scope.raiseErr(data.msg);
			}
		},
		function(reason) {
			$scope
			.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});			

	};	
	
	$scope.eliminarDocumentoLiquidacion = function(doc){
		$scope.openLoadingModal('Eliminando documento', '');
		var promise = tareasService.eliminarDocumentoLiquidacion(doc);
		promise.then(function(data) {
			$scope.closeModal();
			if (data.status) {
				$scope.openMensajeModal('success','Documento eliminado', '',  true, 2);
				$scope.eliminarDocumentoLiquidacionLista(doc);
				if(data.warn)
					$scope.raiseWarn(data.msg);
			} else {
				$scope.raiseErr(data.msg);
			}
		},
		function(reason) {
			$scope
			.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});	
	};		

	$scope.eliminarDocumentoLiquidacionLista = function(doc){
		$scope.liquidacionCaratula.documentosLiquidacion.splice($scope.liquidacionCaratula.documentosLiquidacion.indexOf(doc), 1);
	};		
	
	$scope.eliminarDocumentoLista = function(papel){
		$scope.liquidacionCaratula.papeles.splice($scope.liquidacionCaratula.papeles.indexOf(papel), 1);
	};	

	$scope.resetNuevoDocumento = function(){
		$scope.nuevoDocumento = {
				caratula: null,
				nombre: null,
				valor: null
		};
	};

	$scope.verTransaccion = function (caratulaDTO) {

		var myModal = $modal.open({
			templateUrl: 'transaccionModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'transaccionModalCtrl',
			resolve: { 
				caratulaDTO: function(){
					return caratulaDTO;
				}
			}
		});
	};		
	
	$scope.verListadoPendiente = function () {

		var myModal = $modal.open({
			templateUrl: 'listadoPendienteModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'listadoPendienteCtrl'
		});
	};		

	$scope.colapsar = function(){
		if($scope.liquidacionCaratula.busqueda){
			$scope.liquidacionCaratula.busqueda = false;
		}else{
			$scope.liquidacionCaratula.busqueda = true;
		}

	};     

	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};    
	
	$scope.myRightButton = function () {
        alert('!!! first function call!');
	};
	
	$scope.stringIsNumber = function(s) {
		var x = +s;
		return x.toString() === s;
	};
	
	$timeout(function() {
		//init
		if ($routeParams.caratula !== undefined) {
			//Si caratula viene en el request, buscar
			if ($scope.stringIsNumber($routeParams.caratula)
					&& $routeParams.caratula.length <= 10) {
				
				$scope.caratula = $routeParams.caratula;

				$timeout(function() {
					$scope.buscarCaratula();
				}, 500);
			} 
		}

		$scope.doFocus('numeroCaratula');
	}, 500);	

});