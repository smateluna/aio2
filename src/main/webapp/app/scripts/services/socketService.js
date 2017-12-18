'use strict';

app.factory('socketService', function ($rootScope, $websocket, $modal, $window) {

	var dataStream= null;
	var opened = false;
	$rootScope.openedContador = false;
	var myModal= null;
	var modalLlamadoCliente= null;

	return {
		conexion: function(){
			if($rootScope.activoAtencion){

				var ruta = 'ws://'+$rootScope.ipSocket+':'+$rootScope.puertoSocket+'/atencion-publico/websocket/atencion/aio/operador/'+$rootScope.userIpAddress+'/'+$rootScope.modulo+'/'+$rootScope.grupo+'/0';
				dataStream = $websocket(ruta, null,
						{
					reconnectIfNotNormalClose: true
						}
				);

				dataStream.onOpen(function() {
					console.log('connection open');
				});

				dataStream.onMessage(function(messageS) {
					var message = JSON.parse(messageS.data);

					console.log(message);

					if(message.evento === 'MODULO_OBTENER_ESTADO_RESPONSE'){
						$rootScope.indisponibilidad=false;
						if(message.message.evento === 'MODULO_DISPONIBLE'){
							$rootScope.moduloDisponible = 1;//true

							//Evita que se abra mas de un modal

							if (opened) return;

							myModal = $modal.open({
								templateUrl : 'atencionModal.html',
								backdrop : true,
								keyboard : true,
								windowClass : 'modal, modal2',
								controller : 'AtencionModalCtrl'
							});

							opened = true;

							myModal.result.then(function () {

								opened = false;
								$rootScope.openedContador = false;
								
								$rootScope.stopTimeout();
								
//								console.log('pase en el cancel');
								
							}, function () {
								var json = {
										"origen": "AIO",
										"evento": "MODULO_OCUPADO",
										"modulo": $rootScope.modulo,
										"usuario": $rootScope.userLoginSinCBRS,
										"ip": $rootScope.userIpAddress,
										"nombreCliente": ""
								};

								if(null!=modalLlamadoCliente){
									modalLlamadoCliente.close();
									modalLlamadoCliente=null;
								}

								dataStream.send(json);

								opened = false;
								$rootScope.openedContador=false;
								
								$rootScope.stopTimeout();
//								console.log('pase en el cancel 2');

							});
						}else if(message.message.evento === 'MODULO_OCUPADO'){
							$rootScope.moduloDisponible = 0;//false

							if(opened){
								myModal.close();

								if(null!=modalLlamadoCliente){
									modalLlamadoCliente.close();
									modalLlamadoCliente=null;
								}
								
								$rootScope.countdown=0;

							}
						}else if(message.message.evento === 'MODULO_LOGOUT'){
							$window.location.href = '../login/logout.jsp';
						} 

					}else if(message.evento === 'MODULO_DISPONIBLE_PRIMERO'){

						$rootScope.moduloDisponible = 2;//true

						//console.log("$rootScope.openedContador "+$rootScope.openedContador);
						
						if(!$rootScope.openedContador){
							//$rootScope.countdown=10;
							$rootScope.init();
						}

					} 
				});

				dataStream.onClose(function () {
					console.log('connection closed', event);
					if(!event.wasClean){
						$rootScope.indisponibilidad=true;
					}
				});
			}
		},
		disponible: function() {

			var json = {
					"origen": "AIO",
					"evento": "MODULO_DISPONIBLE",
					"modulo": $rootScope.modulo,
					"usuario": $rootScope.userLoginSinCBRS,
					"ip": $rootScope.userIpAddress,
					"nombreCliente": $rootScope.nombreCliente
			};

			dataStream.send(json);
			
			$rootScope.countdown=10;
			
			$rootScope.nombreCliente='';

		},
		ocupado: function() {
			var json = {
					"origen": "AIO",
					"evento": "MODULO_OCUPADO",
					"modulo": $rootScope.modulo,
					"usuario": $rootScope.userLoginSinCBRS,
					"ip": $rootScope.userIpAddress,
					"nombreCliente": ""
			};

			dataStream.send(json);
		},
		desconectar: function(){
			console.log('desconectar');

			var json = {
					"origen": "AIO",
					"evento": "MODULO_LOGOUT",
					"modulo": $rootScope.modulo,
					"usuario": $rootScope.userLoginSinCBRS,
					"ip": $rootScope.userIpAddress,
					"nombreCliente": ""
			};

			dataStream.send(json);

			if (dataStream !== null) {
				dataStream.close();
				dataStream = null;
			}
		},
		llamarCliente : function(){
			modalLlamadoCliente = $modal.open({
				templateUrl: 'llamarClienteModal.html',
				backdrop: true,
				windowClass: 'modal',
				controller: 'llamarClienteModalCtrl'
			});
		}
	};
});