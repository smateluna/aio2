/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerInscripcionRegCtrl', function ($scope, $routeParams, $rootScope, $sce,$modal,$timeout,$filter,indiceService,gponlineService) {

	$scope.parametros = {
		foja: $routeParams.foja,
		numero: $routeParams.numero,
		agno: $routeParams.ano,
		bis: $routeParams.bis,
		registro: $routeParams.registro,
		origen: $routeParams.origen
	};

	$scope.raiseErr = function(level, title, msg) {
		$scope.errorObj.hay = true;
		$scope.errorObj.level = level;
		$scope.errorObj.title = title;
		$scope.errorObj.msg = msg;
	};

	$scope.errorObj = {
		hay : false,
		level : 'warning', // warning o error
		title : '',
		msg : ''
	};

	$scope.loaders = {
		titulosanteriores : {
		isLoading : false,
		error : false
	}
	};

	$scope.filaTicketMasivo = {
		foja: null,
		num: null,
		ano: null
	}

	$scope.titulos = [];

	$scope.buscar = function(){
		$scope.isLoading = true;
		var foja = $scope.parametros.foja,
		numero = $scope.parametros.numero,
		ano = $scope.parametros.agno,
		bis = $scope.parametros.bis,
		registro = $scope.parametros.registro;

		$scope.urlPDF = $sce.trustAsResourceUrl("../do/service/inscripcionDigital?metodo=verDocumentoReg&foja="+foja+"&numero="+numero+"&ano="+ano+"&bis="+bis+"&registro="+registro);
		$scope.isLoading  = false;

		$scope.titulosanteriores();
	};

	$scope.cerrar = function(){
		$rootScope.go('/'+$scope.parametros.origen);
	};

	$scope.openSolicitar = function (indice,registro) {

		var foja=null,
		numero=null,
		ano = null,
		bis = null;

		foja=indice.foja;
		numero=indice.numero;
		ano=indice.agno;
		bis='false';

		var myModal = $modal.open({
			templateUrl: 'indiceModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'IndiceModalCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
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
		registro: function(){
			return registro;
		},
		indice: function(){
			return indice;
		},
		origen: function(){
			return 'indice';
		},
		titulos: function(){
			return '';
		}
		}
		});

	};

	$scope.ticket = function (indice,registro) {

		var foja=null,
		numero=null,
		ano = null,
		bis = null,
		nombre = null,
		direccion = null,
		comuna=null,
		registroCaratula,
		fila,
		promise = null;

		foja=indice.foja;
		numero=indice.numero;
		ano=indice.agno;
		bis='false';
		registroCaratula = indice.registro;

		if('hip'==registro){

			promise = indiceService.getIndicePropiedades(null,null,null,foja, numero, ano, bis,null,null,null,null,null,false,true,false,false,true,null,null);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					for (var i in data.aaDataHipotecas){
						fila = data.aaDataHipotecas[i];
						break;   
					}

					var myModal = $modal.open({
						templateUrl: 'TicketInformacion.html',
						backdrop: true,
						windowClass: 'modal',
						controller: 'TicketInformacionCtrl',
						resolve: {
						resolveModal : function(){
						return $scope.resolveModal;
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
					nombre: function(){
						return fila.nombre;
					},
					direccion: function(){
						return fila.dir;
					},
					comuna: function(){
						return fila.comuna;
					},
					registro: function(){
						return registroCaratula;
					},
					titulos: function(){
						return '';
					}
					}
					});

				}else{
					$scope.raiseErr('buscar','Problema detectado', data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});

		} else if('proh'==registro){

			promise = indiceService.getIndicePropiedades(null,null,null,foja, numero, ano, bis,null,null,null,null,null,false,false,true,false,null,true,null);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					for (var i in data.aaDataProhibiciones){
						fila = data.aaDataProhibiciones[i];
						break;   
					}

					var myModal = $modal.open({
						templateUrl: 'TicketInformacion.html',
						backdrop: true,
						windowClass: 'modal',
						controller: 'TicketInformacionCtrl',
						resolve: {
						resolveModal : function(){
						return $scope.resolveModal;
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
					nombre: function(){
						return fila.nombre;
					},
					direccion: function(){
						return fila.dir;
					},
					comuna: function(){
						return fila.comuna;
					},
					registro: function(){
						return registroCaratula;
					},
					titulos: function(){
						return '';
					}
					}
					});
				}else{
					$scope.raiseErr('buscar','Problema detectado', data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});

		} else if('com'==registro){

			promise = indiceService.getIndicePropiedades(null,null,null,foja, numero, ano, bis,null,null,null,null,null,false,false,false,true,null,null,null);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					for (var i in data.aaDataComercio){
						fila = data.aaDataComercio[i];
						break;   
					}

					var myModal = $modal.open({
						templateUrl: 'TicketInformacion.html',
						backdrop: true,
						windowClass: 'modal',
						controller: 'TicketInformacionCtrl',
						resolve: {
						resolveModal : function(){
						return $scope.resolveModal;
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
					nombre: function(){
						return fila.nombreSociedad;
					},
					direccion: function(){
						return fila.dir;
					},
					comuna: function(){
						return fila.comuna;
					},
					registro: function(){
						return registroCaratula;
					},
					titulos: function(){
						return '';
					}
					}
					});
				}else{
					$scope.raiseErr('buscar','Problema detectado', data.msg);
				}
			}, function(reason) {
				$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});

		}

	};

	$scope.addRow = function(){		
		$scope.titulos.push({ 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':$scope.titulos.length+1 });
	};

	$scope.removeRow = function(name){				
		var index = -1;		
		var comArr = eval( $scope.titulos );
		for( var i = 0; i < comArr.length; i++ ) {
			if( comArr[i] === name ) {
				index = i;
				break;
			}
		}
		if( index === -1 ) {
			alert( "algo ocurrio" );
		}
		$scope.titulos.splice( index, 1 );		
	};

	$scope.verInscripcionDesdePreCaratula = function(obj) {
		$rootScope.go('/verInscripcionReg/'
			+ $scope.parametros.registro + '/' + obj.foja
			+ '/' + obj.numero + '/' + obj.anio + '/'
			+ false + '/' + $scope.parametros.origen);
	};

	$scope.ticketMasiva = function (registro) {

		var foja=null,
		numero=null,
		ano = null,
		bis = null,
		nombre = null,
		direccion = null,
		comuna=null,
		fila=null;

		$scope.titulosTicketMasivo = [];
		var orderBy = $filter('orderBy');
		
		var indicadorfila=0;
		if('com'==registro){
			for(var j in $scope.titulos){
				var titulo=$scope.titulos[j];
				var promise = indiceService.getIndicePropiedades(null,null,null,titulo.foja, titulo.numero, titulo.anio, titulo.bis,null,null,null,null,null,false,false,false,true,null,null,null);
				promise.then(function(data) {
					if(data.status===null){
					}else if(data.status){
						for (var i in data.aaDataComercio){
							fila = data.aaDataComercio[i];
							break;   
						}

						$scope.titulosTicketMasivo[indicadorfila]=fila;
						indicadorfila++;

					}else{
						$scope.raiseErr('buscar','Problema detectado', data.msg);
					}
				}, function(reason) {
					$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
				});
			}


			$timeout(function(){

				var myModal = $modal.open({
					templateUrl: 'TicketInformacion.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'TicketInformacionCtrl',
					resolve: {
					resolveModal : function(){
					return $scope.resolveModal;
				},
				foja: function(){
					return '';
				},
				numero: function(){
					return '';
				},
				ano: function(){
					return '';
				},
				nombre: function(){
					return '';
				},
				direccion: function(){
					return '';
				},
				comuna: function(){
					return '';
				},
				registro: function(){
					return registro;
				},
				titulos: function(){
					return $scope.titulosTicketMasivo;
				}
				}
				});

			}, 2000);
		}else if('hip'==registro){
			
			for (var i in $scope.titulos){
				if($scope.titulos[i].Selected){
					$scope.fila = angular.copy($scope.filaTicketMasivo);
					$scope.fila.foja=$scope.titulos[i].foja;
					$scope.fila.num=$scope.titulos[i].numero;
					$scope.fila.ano=$scope.titulos[i].anio;
					$scope.titulosTicketMasivo[i]=$scope.fila;
				}
			}
			
			$timeout(function(){

				var myModal = $modal.open({
					templateUrl: 'TicketInformacion.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'TicketInformacionCtrl',
					resolve: {
					resolveModal : function(){
					return $scope.resolveModal;
				},
				foja: function(){
					return '';
				},
				numero: function(){
					return '';
				},
				ano: function(){
					return '';
				},
				nombre: function(){
					return '';
				},
				direccion: function(){
					return '';
				},
				comuna: function(){
					return '';
				},
				registro: function(){
					return registro;
				},
				titulos: function(){
					return orderBy($scope.titulosTicketMasivo,'-ano', true);
				}
				}
				});

			}, 2000);
		}else if('proh'==registro){	
			for (var i in $scope.titulos){
				if($scope.titulos[i].Selected){
					$scope.fila = angular.copy($scope.filaTicketMasivo);
					$scope.fila.foja=$scope.titulos[i].foja;
					$scope.fila.num=$scope.titulos[i].numero;
					$scope.fila.ano=$scope.titulos[i].anio;
					$scope.titulosTicketMasivo[i]=$scope.fila;
				}
			}
			
			$timeout(function(){

				var myModal = $modal.open({
					templateUrl: 'TicketInformacion.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'TicketInformacionCtrl',
					resolve: {
					resolveModal : function(){
					return $scope.resolveModal;
				},
				foja: function(){
					return '';
				},
				numero: function(){
					return '';
				},
				ano: function(){
					return '';
				},
				nombre: function(){
					return '';
				},
				direccion: function(){
					return '';
				},
				comuna: function(){
					return '';
				},
				registro: function(){
					return registro;
				},
				titulos: function(){
					return orderBy($scope.titulosTicketMasivo,'-ano', false);
				}
				}
				});

			}, 2000);
		}else{
			for(var j in $scope.titulos){
				var titulo=$scope.titulos[j];
				var promise = indiceService.getIndicePropiedades(null,null,null,titulo.foja, titulo.numero, titulo.anio, titulo.bis,null,null,null,null,null,true,false,false,false,false,null,null);
				promise.then(function(data) {
					if(data.status===null){
					}else if(data.status){
						for (var i in data.aaData){
							fila = data.aaData[i];
							break;   
						}

						$scope.titulosTicketMasivo[indicadorfila]=fila;
						indicadorfila++;

					}else{
						$scope.raiseErr('buscar','Problema detectado', data.msg);
					}
				}, function(reason) {
					$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
				});
			}


			$timeout(function(){

				var myModal = $modal.open({
					templateUrl: 'TicketInformacion.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'TicketInformacionCtrl',
					resolve: {
					resolveModal : function(){
					return $scope.resolveModal;
				},
				foja: function(){
					return '';
				},
				numero: function(){
					return '';
				},
				ano: function(){
					return '';
				},
				nombre: function(){
					return '';
				},
				direccion: function(){
					return '';
				},
				comuna: function(){
					return '';
				},
				registro: function(){
					return registro;
				},
				titulos: function(){
					return orderBy($scope.titulosTicketMasivo,'-ano', true);
				}
				}
				});

			}, 2000);
		}
	};

	$scope.solicitarPreCaratulaMasiva = function (registro) {
		
		$scope.titulosMasivo = [];

		angular
		.forEach(
			$scope.titulos,
			function(obj) {
				if (obj.Selected) {
					$scope.titulosMasivo.push(obj);
				} 
			});

		var orderBy = $filter('orderBy');
		$scope.titulosMasivo=orderBy($scope.titulosMasivo,'-anio', false);
		
		var myModal = $modal.open({
			templateUrl: 'indiceModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'IndiceModalCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
		},
		foja: function(){
			return '';
		},
		numero: function(){
			return '';
		},
		ano: function(){
			return '';
		},
		registro: function(){
			return registro;
		},
		indice: function(){
			return '';
		},
		origen: function(){
			return 'verinscripcionReg';
		},
		titulos: function(){
			return $scope.titulosMasivo;
		}
		}
		});


	};

	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};

	$scope.checkAll = function () {

		if (!$scope.selectedAll) {
			$scope.selectedAll = true;
		} else {
			$scope.selectedAll = false;
		}

		angular.forEach($scope.titulos, function (titulo) {
			if (!titulo.vigente) {
				titulo.Selected = $scope.selectedAll;
			}

		});

	};

	$scope.titulosanteriores = function () {

		var foja = $scope.parametros.foja,
		numero = $scope.parametros.numero,
		ano = $scope.parametros.agno,
		bis = $scope.parametros.bis,
		registro = $scope.parametros.registro;

		$scope.titulos = [];
		$scope.data = [];
		$scope.loaders['titulosanteriores'].isLoading = true;

		var promise = gponlineService.buscarBorradorPorInscripcionRegistro(foja,numero,ano,bis,registro);
		promise.then(function(data) {
			if(data.status===null){

			}else if(data.status){
				//				if(data.listaBorradores.length==1){
				//				var borrador = data.listaBorradores[0].borrador;
				//				var folio = data.listaBorradores[0].folio;

				//		if(folio==='' || folio==null){
				//			$scope.buscarFolio();
				//		}else{
				if($scope.parametros.registro=='hip'){
					$scope.totalTitulosAnteriores = data.hipotecasVigentes.length;
					$scope.data = data.hipotecasVigentes;
					//					$scope.busquedaGponline.tieneNoVigenteHipo=data.tieneNoVigenteHipo;
				}else if($scope.parametros.registro=='proh'){
					$scope.totalTitulosAnteriores = data.prohibicionesVigentes.length;
					$scope.data = data.prohibicionesVigentes;
					//					$scope.busquedaGponline.tieneNoVigenteProh=data.tieneNoVigenteProh;
				}

				if($scope.data.length>0){
					for( var i = 0; i < $scope.data.length; i++ ) {
						if($scope.data[i].vigente)
							$scope.titulos.push({'Selected':true, 'foja':$scope.data[i].fojas, 'numero': $scope.data[i].numero, 'anio':$scope.data[i].anyo,'vigente':$scope.data[i].vigente, 'id':$scope.titulos.length+1 });
						else
							$scope.titulos.push({'Selected':false, 'foja':$scope.data[i].fojas, 'numero': $scope.data[i].numero, 'anio':$scope.data[i].anyo,'vigente':$scope.data[i].vigente, 'id':$scope.titulos.length+1 });
					}
				}else{
					$scope.titulos.push({ 'Selected':true, 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':'1' });
				}


				//		}

				//				} else {
				//					//levantar modal con lista folios	
				//					$scope.busquedaGponline.folios=data.listaBorradores;
				//					$scope.openfolioModal();
				//				}
				$scope.loaders['titulosanteriores'].isLoading = false;
			}else{
				$scope.raiseErr('borrador','Problema Detectado', data.msg);
				$scope.loaders['titulosanteriores'].error = true;
				$scope.loaders['titulosanteriores'].isLoading = false;
				$scope.titulos.push({ 'Selected':true, 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':'1' });
			}
		}, function(reason) {
			$scope.raiseErr('borrador','Problema Detectado', data.msg);
			$scope.loaders['titulosanteriores'].isLoading = false;
			$scope.loaders['titulosanteriores'].error = true;
			$scope.titulos.push({ 'Selected':true, 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':'1' });
		});

	};

	$scope.buscar();

});