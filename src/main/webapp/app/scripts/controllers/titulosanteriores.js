'use strict';

app.controller('titulosanterioresCtrl', function ($rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, inscripcionDigitalService, masInfoindiceModel, borradorService, resolveModal, indiceModel, foja, numero, ano,caratula) {
	
	$scope.foja = foja;
	$scope.numero = numero;
	$scope.ano = ano;
	$scope.caratula = caratula;

	$scope.titulo = {
		tipodocumento: null,
		esDigital: null
	}; 
	
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

	if($scope.busquedaBorrador.titulosAnteriores!==undefined){
		$scope.busquedaBorrador.titulosAnteriores.length = 0;
	}
	if($scope.urlPdf!==undefined){
		$scope.urlPdf = '';
	} 

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

						if(data.res.consultaDocumentoDTO.tipoDocumento!=1){
							if(data.res.consultaDocumentoDTO.tipoDocumento==0){
								$scope.titulo.tipodocumento=2;	  
							} else if(data.res.consultaDocumentoDTO.tipoDocumento==2){
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
//		$rootScope.go('/verInscripcion/prop/1/1/2015/'+bis+'/indice/'+estado);

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

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
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


});