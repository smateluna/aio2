'use strict';

app.controller('IndiceModalCtrl', function ($rootScope, $routeParams, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, caratulaService, indiceService, resolveModal, RutHelper, foja, numero, ano, registro, indice, origen,titulos) {

	$scope.foja = foja;
	$scope.numero = numero;
	$scope.ano = ano;
	$scope.registro = registro;
	$scope.indice = indice;
	$scope.origen = origen;
	$scope.titulos = titulos;

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null
	};

	$scope.isLoadingSolicitar = false;

	$scope.pasoPreSolicitar = false;

	$scope.solicitaCaratula = {
		rut: null,
		nombre: null,
		apep: null,
		apem: null,
		direccion: null,
		telefono: null,
		correo: null,
		foja: null,
		numero: null,
		ano: null,
		registro:null,
		tramite1: false,
		obs1:$scope.indice.dir,
		tramite2: false,
		obs2:$scope.indice.dir,
		tramite3: false,
		obs3:$scope.indice.dir,
		tramite4: false,
		obs4:$scope.indice.dir,
		tramite5: false,
		obs5:$scope.indice.dir,
		rutConsultado: false
	};
	
	$scope.limpiarCache = function(){
		localStorageService.remove('rawScreens');
	};
	
	$timeout(function(){
		$scope.doFocus('rutt');

		if (typeof $scope.indice.dir === "undefined") {
			$scope.solicitaCaratula.obs1='';
			$scope.solicitaCaratula.obs2='';
			$scope.solicitaCaratula.obs3='';
			$scope.solicitaCaratula.obs4='';
			$scope.solicitaCaratula.obs5='';
		}

		
		//buscar rut en sesion y buscar datos si existe
		var promise = indiceService.getRutSesion();
		promise.then(function(data) {      
			if(data.status===null){
			}else if(data.status){
				$scope.solicitaCaratula.rut = RutHelper.format(data.rut);
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});

	}, 200);


	$scope.generaTicketModal = function (colilla,tramites,registro,foja,numero,ano,titulos,colillacanvas,email) {
		var myModal2 = $modal.open({
			templateUrl  : 'views/generaTicket.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'generaTicketCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
		},
		colilla: function(){
			return colilla;
		},
		tramites: function(){
			return tramites;
		},
		registro: function(){
			return registro;
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
		requirente: function(){
			return $scope.solicitaCaratula.nombre+' '+$scope.solicitaCaratula.apep;
		},
		rutrequirente: function(){
			return $scope.solicitaCaratula.rut;
		},
		titulos: function(){
			return titulos;
		},
		colillacanvas: function(){
			return colillacanvas;
		},
		email: function(){
			return email;
		}
		}
		});
	};

	$scope.preSolicitaCaratula = function(){
		$scope.resetStatus();
		$scope.isLoadingSolicitar = true;
		var promise = '';
		
		$scope.solicitaCaratula.rut = RutHelper.clean($scope.solicitaCaratula.rut);
		
		if($scope.titulos!='')
			promise = caratulaService.savePreCaratulaMasiva($scope.foja, $scope.numero, $scope.ano, $scope.solicitaCaratula.rut,$scope.solicitaCaratula.nombre,$scope.solicitaCaratula.apep,$scope.solicitaCaratula.apem,$scope.solicitaCaratula.direccion,$scope.solicitaCaratula.telefono,$scope.solicitaCaratula.correo,$scope.solicitaCaratula.tramite1,$scope.solicitaCaratula.tramite2,$scope.solicitaCaratula.tramite3,$scope.solicitaCaratula.tramite4,$scope.solicitaCaratula.tramite5,$scope.solicitaCaratula.obs1,$scope.solicitaCaratula.obs2,$scope.solicitaCaratula.obs3,$scope.solicitaCaratula.obs4,$scope.solicitaCaratula.obs5,$scope.registro,$scope.titulos);
		else	
			promise = caratulaService.savePreCaratula($scope.foja, $scope.numero, $scope.ano, $scope.solicitaCaratula.rut,$scope.solicitaCaratula.nombre,$scope.solicitaCaratula.apep,$scope.solicitaCaratula.apem,$scope.solicitaCaratula.direccion,$scope.solicitaCaratula.telefono,$scope.solicitaCaratula.correo,$scope.solicitaCaratula.tramite1,$scope.solicitaCaratula.tramite2,$scope.solicitaCaratula.tramite3,$scope.solicitaCaratula.tramite4,$scope.solicitaCaratula.tramite5,$scope.solicitaCaratula.obs1,$scope.solicitaCaratula.obs2,$scope.solicitaCaratula.obs3,$scope.solicitaCaratula.obs4,$scope.solicitaCaratula.obs5,$scope.registro,$scope.titulos);
		
		promise.then(function(data) {
			if(data.status===null){
	
			}else if(data.status){
				if(data.colilla!=0){
					if($scope.titulos!='')
						$scope.generaTicketModal(data.colilla, data.tramitesGeneral, data.registro,data.foja,data.numero,data.ano,$scope.titulos,data.colillacanvas,$scope.solicitaCaratula.correo);
					else
						$scope.generaTicketModal(data.colilla, data.tramites, data.registro,data.foja,data.numero,data.ano,$scope.titulos,data.colillacanvas,$scope.solicitaCaratula.correo);
				} 

				$scope.isLoadingSolicitar = false;
				$scope.solicitudStatus.ok = true;
				$scope.solicitudStatus.msg = 'Solicitud realizada correctamente';

			}else{
				$scope.isLoadingSolicitar = false;
				$scope.setErr('Problema detectado.', 'No se ha guardado solicitud de caratula.');
			}
		}, function(reason) {
			$scope.isLoadingSolicitar = false;
			$scope.setErr('Problema contactando al servidor.', 'No se ha guardado solicitud de caratula.');
		});
	};

	$scope.volverPreSolicita = function(){
		$scope.pasoPreSolicitar = false;
		$scope.solicita.tipo = null;
		$scope.solicita.esDigital = false;

		$scope.resetStatus();

		$timeout(function(){
			$scope.doFocus('rutt');
		}, 100);
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};

	$scope.limpiarSolicitaCaratula = function(){
		$scope.resetSolicita();
		$scope.resetStatus();
		$scope.doFocus('rutt');
	};

	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};

	$scope.verInscripcion = function(){
		$rootScope.go('/verInscripcion/prop/'+$scope.solicita.foja+'/'+$scope.solicita.numero+'/'+$scope.solicita.ano+'/'+$scope.solicita.bis+'/indice/'+$scope.solicita.tipo);
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

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	//utiles	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};
	
	$scope.updateQuestionValue = function(choice){
        $scope.value = $scope.value || [];
        if(choice.checked){
            $scope.value.push(choice.value);
            $scope.value = _.uniq($scope.value);
        }else{
            $scope.value = _.without($scope.value, choice.value);
        }
    };

});