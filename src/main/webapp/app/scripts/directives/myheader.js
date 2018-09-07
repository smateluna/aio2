'use strict';

app.directive('myHeader', function () {
	
//	var opened = false;
	
	return {
		restrict: 'E',
		//replace: false,
		//controller: 'PerfilesModalCtrl',
		templateUrl: './views/tpl/user.html',
		scope: {
			'username': '@',
			'titulo': '@',
			'icono': '@',
			'perfil': '@',
			'perfiles': '@',
			'activoAtencion': '@',
			'modulo': '@',
			'grupo' : '@',
			'userIpAddress' : '@',
			'moduloDisponible' : '@',
			'indisponibilidad' : '@',
			'enModal' : '@',
			'countdown' : '@'
		},
		controller: function($scope, socketService,indiceService, $rootScope, $timeout, $modal){

//			$rootScope.$watch($rootScope.userIpAddress, function(newValue,oldValue){
//				console.log($rootScope.userIpAddress);
//			});
			
//			var mytimeout=null;
			
//			$scope.username=$rootScope.userLogin;
//			$scope.perfil=$rootScope.perfil;
//			$scope.perfiles=$rootScope.perfiles;
//			$scope.activoAtencion=$rootScope.activoAtencion;
//			$scope.modulo=$rootScope.modulo;
//			$scope.grupo=$rootScope.grupo;
//			$scope.userIpAddress=$rootScope.userIpAddress;
//			$scope.moduloDisponible=$rootScope.moduloDisponible;

			$scope.callDisponible = function(){
				socketService.disponible();
				
//				if(!opened)
//					$scope.init();
//				else
//					$rootScope.countdown=10;
			};

			$scope.callOcupado = function(){
				socketService.ocupado();
			};

			$scope.desconectar = function(){
				socketService.desconectar();
			};
			
			$scope.contarAtencionService = function(){
				indiceService.contarAtencionService($scope.titulo);
			};
			
			$scope.llamarCliente = function(){
				socketService.llamarCliente();
			};

		}
	};
});
