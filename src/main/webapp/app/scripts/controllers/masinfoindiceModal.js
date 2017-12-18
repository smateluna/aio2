'use strict';

app.controller('masinfoindiceModalCtrl', function ($rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, inscripcionDigitalService, masInfoindiceModel,caratulaService, borradorService, escrituraService, planosService, resolveModal, indiceModel, titulo, foja, numero, ano,caratula) {

	$scope.titulo=titulo;
	$scope.foja = foja;
	$scope.numero = numero;
	$scope.ano = ano;
	$scope.caratula = caratula;
	
	$scope.resolveModal = {
		refresca: false
	};
	
	$scope.tituloAnterior = {
		foja: null,
		num: null,
		ano: null,
		bis:null,
		tipodocumento:null
	};

	$scope.states = masInfoindiceModel.getStates();
	$scope.busquedaBorrador = masInfoindiceModel.getBusquedaBorrador();

	if($scope.busquedaBorrador.borradores!==undefined){
		$scope.busquedaBorrador.borradores.length = 0;
	}
	if($scope.busquedaBorrador.caratulasEnProceso!==undefined){
		$scope.busquedaBorrador.caratulasEnProceso.length = 0;
	} 
	if($scope.busquedaBorrador.caratulasTerminadas!==undefined){
		$scope.busquedaBorrador.caratulasTerminadas.length = 0;
	} 
	if($scope.busquedaBorrador.planos!==undefined){
		$scope.busquedaBorrador.planos.length = 0;
	}
	if($scope.busquedaBorrador.titulosAnteriores!==undefined){
		$scope.busquedaBorrador.titulosAnteriores.length = 0;
	}
	if($scope.urlPdf!==undefined){
		$scope.urlPdf = '';
	} 


	var promise = borradorService.obtenerCantidadBorradores($scope.foja, $scope.numero, $scope.ano);
	promise.then(function(data) {
		//$scope.closeModal();
		if(data.status===null){
		}else if(data.status){
			$scope.totalBorradores = data.totalBorradores;
		}else{
			$scope.setErr('data.msg', 'data.msg');
		}
	}, function(reason) {
		$scope.setErr('Problema contactando al servidor.', '');
	});

	var promise = caratulaService.obtenerCantidadCaratula($scope.foja, $scope.numero, $scope.ano);
	promise.then(function(data) {
		//$scope.closeModal();
		if(data.status===null){
		}else if(data.status){
			$scope.totalCaratulasEnProceso = data.totalCaratulasEnProceso;
			$scope.totalCaratulasTerminadas = data.totalCaratulasTerminadas;
		}else{
			$scope.setErr('data.msg', 'data.msg');
		}
	}, function(reason) {
		$scope.setErr('Problema contactando al servidor.', '');
	});

	var promise = escrituraService.obtenerEscrituraPorInscripcion($scope.caratula);
	promise.then(function(data) {
		//$scope.closeModal();
		if(data.status===null){
		}else if(data.status){
			$scope.totalEscrituras = data.tieneImagen;
			$scope.urlPdf = data.urlPdf;
		}else{
			$scope.setErr('data.msg', 'data.msg');
		}
	}, function(reason) {
		$scope.setErr('Problema contactando al servidor.', '');
	});

	var promise = planosService.obtenerPlanosPorTitulos($scope.foja, $scope.numero, $scope.ano);
	promise.then(function(data) {
		//$scope.closeModal();
		if(data.status===null){
		}else if(data.status){
			$scope.totalPlanos = data.totalPlanos;
			$scope.busquedaBorrador.planos = data.listaPlanos;
		}else{
			$scope.setErr('data.msg', 'data.msg');
		}
	}, function(reason) {
		$scope.setErr('Problema contactando al servidor.', '');
	});

	var promise = borradorService.obtenerTitulosAnteriores($scope.foja, $scope.numero, $scope.ano);
	promise.then(function(data) {
		//$scope.closeModal();
		if(data.status===null){
		}else if(data.status){
			$scope.totalTitulosAnteriores = data.totalTitulosAnteriores;
			$scope.busquedaBorrador.titulosAnteriores = data.listaTitulosAnteriores;
		}else{
			$scope.setErr('data.msg', 'data.msg');
		}
	}, function(reason) {
		$scope.setErr('Problema contactando al servidor.', '');
	});

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null
	};

	$scope.isLoadingSolicitar = false;

	$scope.pasoPreSolicitar = false;

	$scope.obtenerBorradores = function(){

		if($scope.totalBorradores>0){
			if($scope.busquedaBorrador.borradores==undefined || $scope.busquedaBorrador.borradores.length == 0){

				$scope.openLoadingModal('Buscando Borradores...', 'por favor espere un momento');
				var promise = borradorService.obtenerBorradores($scope.foja, $scope.numero, $scope.ano);
				promise.then(function(data) {
					$scope.closeModal();
					if(data.status===null){

					}else if(data.status){
						$scope.busquedaBorrador.data = data;
						$scope.busquedaBorrador.borradores = data.listaBorradores;

					}else{
						$scope.setErr('Problema detectado.', 'Problema detectado');
					}
				}, function(reason) {
					$scope.setErr('Problema detectado.', 'No se ha podido establecer comunicación con el servidor.');
				});
			} 
		}
	};

	$scope.obtenerCaratulas = function(estado){

		if(($scope.totalCaratulasEnProceso>0 && estado==0) || ($scope.totalCaratulasTerminadas>0 && estado==1)){
			if(($scope.busquedaBorrador.caratulasEnProceso.length==0 && estado==0 ) || ($scope.busquedaBorrador.caratulasTerminadas.length==0 && estado==1)){
				$scope.openLoadingModal('Buscando Caratulas...', 'por favor espere un momento');
				var promise = caratulaService.obtenerCaratulasPorFoja($scope.foja, $scope.numero, $scope.ano,estado);
				promise.then(function(data) {
					$scope.closeModal();
					if(data.status===null){

					}else if(data.status){
						$scope.busquedaBorrador.data = data;
						if(estado==0)
							$scope.busquedaBorrador.caratulasEnProceso = data.listacaratulas;
						if(estado==1)
							$scope.busquedaBorrador.caratulasTerminadas = data.listacaratulas;

					}else{
						$scope.setErr('Problema detectado.', 'Problema detectado');
					}
				}, function(reason) {
					$scope.setErr('Problema detectado.', 'No se ha podido establecer comunicación con el servidor.');
				});
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

	$scope.verTipoImagen = function(sol){

		var bis = sol.bis==1?true:false;

		$scope.openLoadingModal('Buscando...', '');

		var promise = inscripcionDigitalService.getInscripcionJPG(sol.foja,sol.numero,sol.ano,bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				if(data.res.tienerechazo){
					$scope.titulo.tipodocumento=0;
					$scope.openVerInscripcion(sol.foja,sol.numero,sol.ano,sol.bis,$scope.titulo.tipodocumento);
					$scope.pasoPreSolicitar = false;
				} else {
					$scope.titulo.esDigital=data.res.anodigital;

					if(data.res.consultaDocumentoDTO.hayDocumento){

						if(data.res.consultaDocumentoDTO.tipoDocumento!=8){
							if(data.res.consultaDocumentoDTO.tipoDocumento==9){
								$scope.titulo.tipodocumento=2;	  
							} else if(data.res.consultaDocumentoDTO.tipoDocumento==10){
								$scope.titulo.tipodocumento=4;
							}
							//esDigital: false
							$scope.openVerInscripcion(sol.foja,sol.numero,sol.ano,sol.bis,$scope.titulo.tipodocumento);
							$scope.pasoPreSolicitar = false;
						} else {
							$scope.verTitulo(sol.foja,sol.numero,sol.ano,sol.bis);
						}	
					} else {
						$scope.titulo.tipodocumento=1;	  
						$scope.openVerInscripcion(sol.foja,sol.numero,sol.ano,sol.bis,$scope.titulo.tipodocumento);
						$scope.pasoPreSolicitar = false;
					}

				}
			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}

		, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	};

	$scope.verTitulo = function(foja,num,ano,bis){

		var bis = bis==1?true:false,
		                       //realizar alternativa para saber tipo de documento;                       
		                       estado = '0';

		$rootScope.go('/verInscripcion/prop/'+foja+'/'+num+'/'+ano+'/'+bis+'/indice/'+estado);

	};
	
	$scope.openVerInscripcion = function (foja,num,ano,bis,tipodocumento) {
		
		$scope.tituloAnterior.foja=foja;
		$scope.tituloAnterior.num=num;
		$scope.tituloAnterior.ano=ano;
		$scope.tituloAnterior.bis=bis;
		$scope.tituloAnterior.tipodocumento=tipodocumento;

		$scope.resolveModal.refresca = false;

		var myModal = $modal.open({
			templateUrl: 'solicitarIndiceModal.html',
			backdrop: 'static',
			windowClass: 'modal',
			controller: 'SolicitarIndiceModalCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
		},
		titulo: function(){
			return $scope.tituloAnterior;
		}
		}
		});

		myModal.result.then(function () {

			console.log($scope.resolveModal.refresca);

			if($scope.resolveModal.refresca){
				//$scope.buscarMis();
			}
		}, function () {
			console.log($scope.resolveModal.refresca);


			if($scope.resolveModal.refresca){
				//$scope.buscarMis();
			}

		});


	};

	$scope.volverPreSolicita = function(){
		$scope.pasoPreSolicitar = false;
		$scope.solicita.tipo = null;
		$scope.solicita.esDigital = false;

		$scope.resetStatus();

		$timeout(function(){
			$scope.$broadcast('sfojaIsFocused');
		}, 100);
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

	$scope.limpiarSolicitaCaratula = function(){
		$scope.resetSolicita();
		$scope.resetStatus();
		$scope.$broadcast('srutIsFocused');
	};

	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};

	$scope.verEscritura = function(caratula){
//		$rootScope.go('/verEscritura/'+$scope.caratula+'/indice');
		$window.open('../do/service/escritura?metodo=verDocumento&caratula='+$scope.caratula+'&type=uri','','width=800,height=600');
	};

	$scope.setErr = function(title, mensaje){
		$scope.solicitudStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};

	$scope.setStatusMsg = function(title, mensaje){
		$scope.solicitudStatus.msgTitle = title;
		$scope.solicitudStatus.msg = mensaje;
	};

	$scope.setOK = function(title, mensaje){
		$scope.solicitudStatus.ok = true;
		$scope.setStatusMsg(title, mensaje);

	};

	$scope.resetStatus = function(){
		$scope.solicitudStatus.ok = false;
		$scope.solicitudStatus.error = false;
		$scope.solicitudStatus.warning = false;
		$scope.solicitudStatus.msgTitle = null;
		$scope.solicitudStatus.msg = null;
	};

	$scope.resetSolicita = function(){
		$scope.solicitaCaratula.rut = null;
		$scope.solicitaCaratula.nombre = null;
		$scope.solicitaCaratula.apep = null;
		$scope.solicitaCaratula.apem = null;
		$scope.solicitaCaratula.direccion = null;
		$scope.solicitaCaratula.telefono = null;
		$scope.solicitaCaratula.correo = null;
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

	$timeout(function(){
		$scope.$broadcast('sfojaIsFocused');
	},100);

	$scope.raiseErr = function(key, title, msg){
		$scope.closeModal();
		$scope.states[key].isError = true;
		$scope.states[key].title = title;
		$scope.states[key].msg = msg;
	};
	
//	$scope.irGpOnlineAIO = function(borrador,folio){
//		$rootScope.go('/gponline/'+borrador+'/'+folio);
//	};
	
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

});