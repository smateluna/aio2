'use strict';

app.controller('PerfilesModalCtrl', function ($scope, $rootScope, $window, Usuario, $modalInstance, $websocket,socketService) {

	$scope.data = {
			perfil : null,
			otrosPerfiles : null,
			activoAtencion : null,
			modulo : null,
			grupo : null,
			userIpAddress : null
	};	

	$rootScope.$watch('perfiles', function() {
		if($rootScope.perfiles!==undefined){
			$scope.data.perfil = $rootScope.perfiles[0];		
			$scope.perfiles = angular.copy($rootScope.perfiles);
		}
	});

	$scope.setPerfil = function(perfilSeleccionado){
		$scope.data.perfil = perfilSeleccionado;
		var promise = Usuario.setSessionAttribute('permisosUsuario', null);
		$scope.entrar();		
	}

	$scope.entrar = function(){
		//Permisos usuario
		var perfil = $scope.data.perfil.perfil.trim();
		
		var promise = Usuario.getPermisosUsuario(perfil);
		promise.then(function(data) {
			if(data.status==null){
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			}else if(data.status){
				$rootScope.permisos = data.permisosUsuario;
				$rootScope.subPermisos = Usuario.getAllSubPermisos($rootScope.permisos);
				$rootScope.perfil = perfil;
				$rootScope.idPerfil = $scope.data.perfil.idPerfil;

				$rootScope.activoAtencion = data.activoAtencion;
				$rootScope.modulo = data.modulo;
				$rootScope.grupo = data.grupo;
				$rootScope.userIpAddress = data.userIpAddress;
				$rootScope.ipSocket = data.ipSocket;
				$rootScope.puertoSocket = data.puertoSocket;

				//llamado a socket
				socketService.conexion();

				//Grabar perfil en sesion
				var promise = Usuario.setSessionAttribute('perfil', perfil);
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
				
				Usuario.setSessionAttribute('idPerfil', $scope.data.perfil.idPerfil);


				var filas = Math.ceil(parseFloat($rootScope.permisos.length/4));

				$rootScope.misFilas = [];		
				for(var i = 0 ; i<filas; i++){
					var inicio = i * 4;
					var fin = 4 * (i+1);		
					$rootScope.misFilas[i] = $rootScope.permisos.slice(inicio, fin);
				} 

				$modalInstance.dismiss('cancel');
//				$location.path('/home');
//				$location.path('/home/');

//				$window.location.reload();

			}else{
				//$scope.raiseErr(data.msg);
				alert(data.msg);
			}
		}, function(reason) {
			$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
		});	
	};


});
