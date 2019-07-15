'use strict';

var versionAIO = 'AIO Version 1.4 09/04/2019\n'
	+'----------------------------\n'
	+'mejoras reingreso gp';

agGrid.LicenseManager.setLicenseKey("Conservador_de_Bienes_Raíces_de_Santiago__MultiApp_1Devs14_March_2020__MTU4NDE0NDAwMDAwMA==e88abe9a3e7fcf67bd38763a6891e639");

agGrid.initialiseAgGridWithAngular1(angular);


var app = angular
.module('aioApp', [
                   'ngCookies',
                   'ngResource',
                   'ngSanitize',
                   'ngRoute',
                   'LocalStorageModule',
                   'ngICheck',
                   'ui.utils',
                   'platanus.rut',
                   'ui.bootstrap',
                   'cgBusy',
                   'infinite-scroll',
                   'angularMoment',
                   'ui.sortable',
                   'ngTextTruncate',
                   'textAngular',
                   'angularjs-dropdown-multiselect',
                   'badwing.autoselect',
                   'ngFileUpload',
                   'ngWebSocket',
                   'agGrid'
                   ]);

app.config(function ($routeProvider, $compileProvider) {
	$compileProvider.aHrefSanitizationWhitelist(/^\s*(https?|ftp|cbrsvisor):/);


	$routeProvider
	.when('/repertorio', {
		templateUrl: 'views/repertorio.html',
		controller: 'RepertorioCtrl'
	})
	.when('/auditoria', {
		templateUrl: 'views/auditoria.html',
		controller: 'AuditoriaCtrl'
	})
	.when('/gponline', {
		templateUrl: 'views/gponline.html',
		controller: 'GponlineCtrl'
	})
	.when('/gponline/:borrador/:folio', {
		templateUrl: 'views/gponline.html',
		controller: 'GponlineCtrl'
	})
	.when('/estado', {
		templateUrl: 'views/estado.html',
		controller: 'EstadoCtrl'
	})
	.when('/estado/:caratula', {
		templateUrl: 'views/estado.html',
		controller: 'EstadoCtrl'
	})
//	.when('/generaTicket/:colilla?', {
//	//templateUrl: 'bwip-js/demo.html',
//	templateUrl: 'views/generaTicket.html',
//	controller: 'generaTicketCtrl'
//	})
	.when('/home', {
		templateUrl: 'views/home.html',
		controller: 'HomeCtrl'
	})
	.when('/home/:tipo', {
		templateUrl: 'views/home.html',
		controller: 'HomeCtrl'
	})
	.when('/indice', {
		templateUrl: 'views/indice.html',
		controller: 'IndiceCtrl'
	})
	.when('/reingreso', {
		templateUrl: 'views/reingreso.html',
		controller: 'ReingresoCtrl'
	})
	.when('/bodega', {
		templateUrl: 'views/bodega.html',
		controller: 'BodegaCtrl'
	})
	.when('/desbloqueo', {
		templateUrl: 'views/desbloqueo.html',
		controller: 'DesbloqueoCtrl'
	})
	.when('/urgencia', {
		templateUrl: 'views/urgencia.html',
		controller: 'UrgenciaCtrl'
	})
	.when('/documentos', {
		templateUrl: 'views/documentos.html',
		controller: 'DocumentosCtrl'
	})
	.when('/anexos', {
		templateUrl: 'views/anexos.html',
		controller: 'AnexosCtrl'
	})
	.when('/distribucion', {
		templateUrl: 'views/distribucion.html',
		controller: 'DistribucionCtrl'
	})
	.when('/distribucion/:caratula', {
		templateUrl: 'views/distribucion.html',
		controller: 'DistribucionCtrl'
	})
	.when('/solicitudes', {
		templateUrl: 'views/solicitudes.html',
		controller: 'SolicitudesCtrl'
	})
	.when('/verInscripcion/:registro/:foja/:numero/:ano/:bis/:origen/:estado?', {
		templateUrl: 'views/verInscripcion.html',
		controller: 'VerInscripcionCtrl'
	})
	.when('/verInscripcion/:registro/:foja/:numero/:ano/:bis/:origen/:estado/:caratula/:borrador/:folio?', {
		templateUrl: 'views/verInscripcion.html',
		controller: 'VerInscripcionCtrl'
	})
	.when('/verInscripcionReg/:registro/:foja/:numero/:ano/:bis/:origen?', {
		templateUrl: 'views/verInscripcionReg.html',
		controller: 'VerInscripcionRegCtrl'
	})
	.when('/verInscripcionCertificar/:registro/:caratula/:foja/:numero/:ano/:bis/:fechaDoc/:rehaceImagen/:idtipoFormulario/:origen', {
		templateUrl: 'views/verInscripcionCertificar.html',
		controller: 'VerInscripcionCertificarCtrl'
	})
	.when('/verInscripcionCertificarHipo/:registro/:caratula/:foja/:numero/:ano/:bis/:fechaDoc/:rehaceImagen/:idtipoFormulario/:origen', {
		templateUrl: 'views/verInscripcionCertificarHipo.html',
		controller: 'VerInscripcionCertificarHipoCtrl'
	})
	.when('/verInscripcionCertificarProh/:registro/:caratula/:foja/:numero/:ano/:bis/:fechaDoc/:rehaceImagen/:idtipoFormulario/:origen', {
		templateUrl: 'views/verInscripcionCertificarProh.html',
		controller: 'VerInscripcionCertificarProhCtrl'
	})
	.when('/verVistaPreviaPlantilla/:nombreArchivo/:origen', {
		templateUrl: 'views/verVistaPreviaPlantilla.html',
		controller: 'VerVistaPreviaPlantillaCtrl'
	})
	.when('/verVistaPreviaGP/:nombreArchivo/:origen/:caratula', {
		templateUrl: 'views/verVistaPreviaGP.html',
		controller: 'VerVistaPreviaGPCtrl'
	})
	.when('/digital', {
		templateUrl: 'views/digital.html',
		controller: 'DigitalCtrl'
	})
	.when('/digitalHipotecas', {
		templateUrl: 'views/digitalHipotecas.html',
		controller: 'DigitalHipotecasCtrl'
	})   
	.when('/digitalProhibiciones', {
		templateUrl: 'views/digitalProhibiciones.html',
		controller: 'DigitalProhibicionesCtrl'
	})        
	.when('/liquidacion', {
		templateUrl: 'views/liquidacion.html',
		controller: 'LiquidacionCtrl'
	})
	.when('/carteles', {
		templateUrl: 'views/carteles/carteles.html',
		controller: 'CartelesCtrl'
	})
	.when('/verEscritura/:caratula/:origen', {
		templateUrl: 'views/verEscritura.html',
		controller: 'VerEscrituraCtrl'
	})
	.when('/verEscrituraEstudio/:caratula/:version/:origen', {
		templateUrl: 'views/verEscrituraEstudio.html',
		controller: 'VerEscrituraEstudioCtrl'
	})
	.when('/verGP/:codigoAlpha/:origen', {
		templateUrl: 'views/verGP.html',
		controller: 'VerGPCtrl',
		resolve: {
			codigoAlphap: function () {
				return '';
			},
			origenp: function () {
				return '';
			}
		}
	})
	.when('/verCartel/:numero/:mes/:ano/:registro/:bis/:tipo/:origen/:rehacer?', {
		templateUrl: 'views/carteles/verCartel.html',
		controller: 'VerCartelCtrl'
	})
	.when('/mantenedorDistribucion', {
		templateUrl: 'views/mantenedorCertifica.html',
		controller: 'MantenedorCertificaCtrl'
	})
	.when('/certificacion', {
		templateUrl: 'views/certificacion.html',
		controller: 'CertificacionCtrl'
	})
	.when('/certificacionHipotecas', {
		templateUrl: 'views/certificacionHipotecas.html',
		controller: 'CertificacionHipotecasCtrl'
	})
	.when('/certificacionProhibiciones', {
		templateUrl: 'views/certificacionProhibiciones.html',
		controller: 'CertificacionProhibicionesCtrl'
	})
	.when('/certificacionenparte', {
		templateUrl: 'views/certificacionenparte.html',
		controller: 'CertificacionEnParteCtrl'
	})
	.when('/perfiles', {
		templateUrl: 'views/perfiles.html',
		controller: 'PerfilesCtrl'
	})
	.when('/tareas', {
		templateUrl: 'views/tareas.html',
		controller: 'TareasCtrl'
	})
	.when('/uaf', {
		templateUrl: 'views/uaf.html',
		controller: 'UAFCtrl'
	})      
	.when('/uaf1', {
		templateUrl: 'views/UAFPersonas.html',
		controller: 'UAFCtrl'
	})
	.when('/uaf2', {
		templateUrl: 'views/UAFBienes.html',
		controller: 'UAFCtrl'
	})      
	.when('/listadorepertorio', {
		templateUrl: 'views/listadorepertorio.html',
		controller: 'ListadoRepertorioCtrl'
	})
	.when('/listadoctacte', {
		templateUrl: 'views/listadoctacte.html',
		controller: 'ListadoCtaCteCtrl'
	})
	.when('/revisionNotas', {
		templateUrl: 'views/revisionNotas.html',
		controller: 'RevisionNotasCtrl'
	})
	.when('/nominactacte', {
		templateUrl: 'views/nominactacte.html',
		controller: 'NominaCtaCteCtrl'
	})
	.when('/consultadiablito/:caratula/:borrador/:folio?', {
		templateUrl: 'views/consultadiablito.html',
		controller: 'ConsultaDiablitoCtrl'
	})
	.when('/info', {
		templateUrl: 'views/info.html',
		controller: 'InfoCtrl'
	})
	.when('/reingresoEscritura', {
		templateUrl: 'views/reingresoescritura.html',
		controller: 'ReingresoEscrituraCtrl'
	})
	.when('/oficio', {
		templateUrl: 'views/oficio/oficio.html',
		controller: 'OficioCtrl'
	})
	.when('/mantenedorCtasCtes', {
		templateUrl: 'views/mantenedorCtasCtes.html',
		controller: 'MantenedorCtasCtesCtrl'
	})    
	.when('/recepcionPlanos', {
		templateUrl: 'views/recepcionPlanos.html',
		controller: 'RecepcionPlanosCtrl'
	})
	.when('/plantillero', {
		templateUrl: 'views/plantillero.html',
		controller: 'PlantilleroCtrl'
	})
	.when('/listadoCuadernillo', {
		templateUrl: 'views/listadoCuadernillo.html',
		controller: 'DespachoCuadernilloCtrl'
	})	
	.otherwise({
		redirectTo: '/home',
		controller: 'HomeCtrl'
	});

});

app.run(function($rootScope, $location, Usuario, $log, $modalStack, amMoment, $window, socketService,$timeout){	

	amMoment.changeLanguage('es');

	console.log(versionAIO);

	$rootScope.go = function (path) {

		if (path === 'back') { // Allow a 'back' keyword to go to previous page
			$window.history.back();
		}

		else { // Go to the specified path
			$location.path(path);
		}
	};

	var mytimeout=null;

	$rootScope.init = function() {
		$timeout.cancel(mytimeout);
		mytimeout = $timeout($rootScope.onTimeout, 1000);
		$rootScope.openedContador=true;
//		console.log("init");
	};

	$rootScope.onTimeout = function() {
		$rootScope.countdown--;
		mytimeout = $timeout($rootScope.onTimeout, 1000);

		if(0==$rootScope.countdown){
//			console.log("onTimeout kill");
			$timeout.cancel(mytimeout);
			$rootScope.openedContador=false;
		}
	};

	$rootScope.stopTimeout = function () {  
		$timeout.cancel(mytimeout);  
//		console.log("Timer Stopped");  
	}  

	$rootScope.quitaPadding = function(){
		return ($location.path().indexOf('verInscripcion')>0) || ($location.path().indexOf('verCartel')>0) || ($location.path().indexOf('consultadiablito')>0) || ($location.path().indexOf('verVistaPreviaPlantilla')>0) || ($location.path().indexOf('verVistaPreviaGP')>0);
	};

	$rootScope.$on('$routeChangeSuccess', function() {

		//quita modal
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	});  

	$rootScope.$on('$locationChangeStart', function(event) {
		var path = $location.path();

		if($rootScope.permisos!=null){	     
			if(!Usuario.tienePermiso(path, $rootScope.permisos)){
				$location.path('/home/sa');
				//event.preventDefault();
				//$location.path('/home/sa');
			}else{  
			}      
		}	

		//Validar si sesion sigue activa antes de cargar modulo
		var promise = Usuario.setSessionActiva(path, $rootScope.perfil);
		promise.then(function(data) {
			if(data.estado==null){
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			}else if(data.estado){
				//Sesion activa
			}else{
				//Sesion expirada
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			}
		}, function(reason) {
			alert('No se ha podido establecer comunicacion con el servidor.');
		}); 	  
	});  

	//Perfiles usuario  
	var promise = Usuario.getSessionAttribute('perfil');
	promise.then(function(data) {
		if(data.estado==null){
			$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
		}else if(data.estado){
			$rootScope.perfil = data.value;

			var promise = Usuario.getPermisosUsuario($rootScope.perfil);
			promise.then(function(data) {
				if(data.status==null){
					$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
				}else if(data.status){
					$rootScope.permisos = data.permisosUsuario; 
					$rootScope.subPermisos = Usuario.getAllSubPermisos($rootScope.permisos)

					//agregado para atencion
					$rootScope.activoAtencion = data.activoAtencion;
					$rootScope.modulo = data.modulo;
					$rootScope.grupo = data.grupo;
					$rootScope.userIpAddress = data.userIpAddress;
					$rootScope.ipSocket = data.ipSocket;
					$rootScope.puertoSocket = data.puertoSocket;
					$rootScope.nombreCliente='';
					$rootScope.idPerfil = data.idPerfil;
					
					if($rootScope.idPerfil==67)
						$rootScope.developer=true; //Desarrollo AIO
					
					//llamado a socket
					socketService.conexion();

					//fin agregado para atencion

					var filas = Math.ceil(parseFloat($rootScope.permisos.length/4));

					$rootScope.misFilas = [];		
					for(var i = 0 ; i<filas; i++){
						var inicio = i * 4;
						var fin = 4 * (i+1);		
						$rootScope.misFilas[i] = $rootScope.permisos.slice(inicio, fin);
					} 

					//permisos de app
					var path = $location.path();
					if(!Usuario.tienePermiso(path, $rootScope.permisos)){
						$location.path('/home/sa');
					}else{				    	  
					}

				}else{
					//$scope.raiseErr(data.msg);
					alert(data.msg);
				}
			}, function(reason) {
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
				//$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
				//alert("app: " + reason);
				//$location.path('/home/sa')
			});

			//Setear perfiles del usuario en el $rootScope
			var promise = Usuario.getSessionAttribute('perfilesUsuario');
			promise.then(function(data) {
				if(data.estado==null){
				}else if(data.estado){ 
					$rootScope.perfiles = data.value.split(',');
				}
			}, function(reason) {
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			});			  

		}else{ 
			var promise = Usuario.getPerfilesUsuario();
			promise.then(function(data) {
				if(data.status==null){
					$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
				}else if(data.status){
					$rootScope.perfiles = data.perfilesUsuario; 
					Usuario.setSessionAttribute('perfilesUsuario', data.perfilesUsuario.toString());

					if(data.perfilesUsuario.length == 1){

						$rootScope.perfil = data.perfilesUsuario[0].perfil.trim();
						$rootScope.idPerfil = data.perfilesUsuario[0].idPerfil;						
						if($rootScope.idPerfil==67)
							$rootScope.developer=true; //Desarrollo AIO
						
						Usuario.setSessionAttribute('idPerfil', data.perfilesUsuario[0].idPerfil);

						//Grabar perfil en sesion
						var promise = Usuario.setSessionAttribute('perfil', $rootScope.perfil);
						promise.then(function(data) {
							if(data.estado==null){
								$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
							}else if(data.estado){
							}else{
								alert(data.msg);
							}
						}, function(reason) {
							alert('No se ha podido establecer comunicacion con el servidor.');
						});

						//Permisos usuario
						var promise = Usuario.getPermisosUsuario($rootScope.perfil);
						promise.then(function(data) {
							if(data.status==null){
								$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
							}else if(data.status){
								$rootScope.permisos = data.permisosUsuario; 
								$rootScope.subPermisos = Usuario.getAllSubPermisos($rootScope.permisos);

								//agregado para atencion
								$rootScope.activoAtencion = data.activoAtencion;
								$rootScope.modulo = data.modulo;
								$rootScope.grupo = data.grupo;
								$rootScope.userIpAddress = data.userIpAddress;
								$rootScope.ipSocket = data.ipSocket;
								$rootScope.puertoSocket = data.puertoSocket;
								$rootScope.nombreCliente='';

								//llamado a socket
								socketService.conexion();

								//fin agregado para atencion

								var filas = Math.ceil(parseFloat($rootScope.permisos.length/4));

								$rootScope.misFilas = [];		
								for(var i = 0 ; i<filas; i++){
									var inicio = i * 4;
									var fin = 4 * (i+1);		
									$rootScope.misFilas[i] = $rootScope.permisos.slice(inicio, fin);
								} 

								//permisos de app
								var path = $location.path();
								if(!Usuario.tienePermiso(path, $rootScope.permisos)){
									$location.path('/home/sa');
								}else{
								}

							}else{
								alert(data.msg);
								$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
							}
						}, function(reason) {
							$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
						});			  

					} else if(data.perfilesUsuario<1){
						alert(data.msg);
						$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
					} 

				}else{
					//$scope.raiseErr(data.msg);
					alert(data.msg);
					$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
				}
			}, function(reason) {
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
				//$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
				//alert("app: " + reason);
				//$location.path('/home/sa')
			});  
		}
	}, function(reason) {
		alert('No se ha podido establecer comunicacion con el servidor.');
	}); 

	//Buscamos el nombre de usuario logueado y version de AIO y lo seteamos en $rootScope
	var promise = Usuario.getUsuario();
	promise.then(function(data) {
		if(data.estado==null){
			$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
		}else if(data.estado){
			$rootScope.userLogin = data.nombreUsuario;
			var newusername = data.nombreUsuario.replace('CBRS\\','');
			$rootScope.userLoginSinCBRS = newusername.trim();

			$rootScope.modulo=data.modulo;
			$rootScope.grupo=data.grupo;
			$rootScope.activoAtencion=data.activoAtencion;
			$rootScope.userIpAddress=data.userIpAddress;
			$rootScope.moduloDisponible=0;
			$rootScope.ipSocket=data.ipSocket;
			$rootScope.puertoSocket=data.puertoSocket;
			$rootScope.nombreCliente='';

			//Parametros AIO
			$rootScope.aioParametros = {
					'sistema': data.sistema,
					'anoArchivoNacional': data.anoArchivoNacional,	
					'anoDigitalPropiedades': data.anoDigitalPropiedades,
					'anoDigitalHipotecas': data.anoDigitalHipotecas,
					'anoDigitalProhibiciones': data.anoDigitalProhibiciones,
					'fojasDigitalProhibiciones': data.fojasDigitalProhibiciones
			};
		

		}else{
			$location.path('/home/sa');
		}
	}, function(reason) {
		alert('No se ha podido establecer comunicacion con el servidor.');
	});  

});
