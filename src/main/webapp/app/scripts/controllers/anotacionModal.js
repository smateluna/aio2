'use strict';

app.controller('AnotacionModalCtrl', function ($rootScope, $scope, $modalInstance, $timeout, $window, $modalStack, $filter, anotacionTipoModel, 
		inscripcionDigitalService, inscripcionDigitalHipotecasService, inscripcionDigitalProhibicionesService,
		AnotacionService,AnotacionHipotecasService, AnotacionProhibicionesService, borradorService ,
		Usuario, anotaciones, notas, inscripcionDigitalDTO, origen, paginacionNotas, $routeParams, $modal, solicitudService) {

	$scope.savingAnotacion = false;
	$scope.nuevaInscripcion = {};
	$scope.puedeGuardar = true;
	$scope.enProceso = false;

	$scope.anotacionStatus = {
			ok: false,
			error : false,
			msg: null
	};

	$scope.parametros = {
			registro : $routeParams.registro,
			foja : $routeParams.foja,
			numero : $routeParams.numero,
			ano : $routeParams.ano,
			bis : $routeParams.bis,
			origen : $routeParams.origen,
			estado : $routeParams.estado,
			caratula : $routeParams.caratula,
			borrador : $routeParams.borrador,
			folio : $routeParams.folio,
	};
	
	//modal
	$scope.solicita = {
		foja: null,
		numero: null,
		ano: null,
		bis: false,
		origen: 'digital',
		consultaDocumentoDTO: {},
		estado: {},
		solicitudDTO: null
	};	

	$scope.servicioAnotacion = $scope.parametros.registro=="prop"?AnotacionService:$scope.parametros.registro=="hip"?AnotacionHipotecasService:AnotacionProhibicionesService;
	$scope.servicioInscripcion = $scope.parametros.registro=="prop"?inscripcionDigitalService:$scope.parametros.registro=="hip"?inscripcionDigitalHipotecasService:inscripcionDigitalProhibicionesService;
	
	$scope.getTiposAnotacion = function(){
		var promise = $scope.servicioAnotacion.getTipos();
		promise.then(function(data) {
			if(data.status===null){
			}else if(data.status){
				$scope.tipos = data.listaTiposAnotaciones;        
				$scope.cambioTipo();
				//var userData = Usuario.getData();

				var dataSaveTipo = {
						usuario: $rootScope.userLogin,
						tipos: data.listaTiposAnotaciones
				};

				anotacionTipoModel.setData(dataSaveTipo);

			}else{
				$scope.savingAnotacion = false;
				$scope.anotacionStatus.error = true;
				$scope.anotacionStatus.msg = data.msg;
			}
		}, function(reason) {
			$scope.savingAnotacion = false;
			$scope.anotacionStatus.error = true;
			$scope.anotacionStatus.msg = 'No se ha podido establecer comunicacion con el servidor.';
		});
	};

	$scope.anotacion = {
			selectedTipo: {nombreAnotacion: "Texto Libre", valorAnotacion: "0", tipoAnotacion: "libre"},
			caratula: null,
			repertorio: null,
			borrador: null,
			repertorioNotarial: null,
			fechaNotario: null,
			foja: null,
			numero: null,
			ano: null,
			texto: null,
			fecha: null,
			version: null,
			anotacionMasiva: false
	};

	$scope.paginacionMaster = {
			currentPage: 1,
			numPerPage: 10,
			maxSize: 2,
			totalpaginas: 0,
			filteredTodos: [],
			todos: [],
			filterExprNotas: ''
	}

	$scope.loaders = {
			borradores : {
				isLoading : false,
				error : false
			},
			enProceso : {
				isLoading : false,
				error : false
			},
			terminadas : {
				isLoading : false,
				error : false
			},
			anotaciones : {
				isLoading : false,
				error : false
			},
			notas : {
				isLoading : false,
				error : false
			},
			titulosanteriores : {
				isLoading : false,
				error : false
			}
	};

	$scope.idInscripcion = inscripcionDigitalDTO.idInscripcion;

	$scope.doFocus = function(conT, campo){

		if(conT){
			$timeout(function(){
				$scope.$broadcast(campo+'IsFocused');
			},100);
		}else{
			$scope.$broadcast(campo+'IsFocused');
		}
	};

	$scope.cambioTipo = function(){
		$scope.anotacion.caratula = null;
		$scope.anotacion.repertorio = null;
		$scope.anotacion.repertorioNotarial = null;
		$scope.anotacion.borrador = null;
		$scope.anotacion.fechaNotario = null;
		$scope.anotacion.foja = null;
		$scope.anotacion.numero = null;
		$scope.anotacion.ano = null;
		$scope.anotacion.texto = null;
		$scope.anotacion.version = null;

		if($scope.anotacion.selectedTipo.valorAnotacion == 0 || $scope.anotacion.selectedTipo.valorAnotacion == 20){
			$scope.doFocus(true, 'texto');
			$scope.soloTexto = true;

		}else{
			$scope.doFocus(true, 'caratula');
			$scope.soloTexto = false;
		}
	};

	$scope.closeAnotacion = function () {

		var top = $modalStack.getTop();
		if (top) {
			$modalInstance.dismiss('cancel');
		}
	};

	$scope.guardaAnotacion = function () {
		if($scope.anotacion.anotacionMasiva){
			$scope.saveAnotacionMasiva();
		}else{
			$scope.saveAnotacion();
		}

	};

	$scope.saveAnotacion = function () {
		var acto = $scope.anotacion.selectedTipo.nombreAnotacion;

		//logica por anotacion
		if($scope.soloTexto){
			if(acto=="Texto Libre")
				acto='Anotaci\u00F3n';
			$scope.agregaAnotacion($scope.idInscripcion, acto, $scope.anotacion.texto, null, null, null, null, null);
		} else{
			var fecha = moment($scope.anotacion.fecha, 'YYYY-MM-DD').format('LL');
			var texto = acto+'<br>C: '+$scope.anotacion.caratula+'<br>REP. CBRS: '+$scope.anotacion.repertorio;
			if($scope.parametros.registro=='proh'){
				if($scope.anotacion.borrador!=null)
					texto +="<br>B.: "+$scope.anotacion.borrador;
				if($scope.anotacion.repertorioNotarial!=null)
					texto +="<br>REP. NOT.: "+$scope.anotacion.repertorioNotarial;			
				if($scope.anotacion.fechaNotario!=null){
					var fechaNotario = moment($scope.anotacion.fechaNotario, 'YYYY-MM-DD').format('LL');
					texto +="<br>FECHA NOT.: "+fechaNotario;
				}
				if($scope.anotacion.foja!=null && $scope.anotacion.numero!=null && $scope.anotacion.ano!=null)
					texto +="<br>F:"+$scope.anotacion.foja+" N:"+$scope.anotacion.numero+" A:"+$scope.anotacion.ano;
			}

			if($scope.anotacion.texto!=null && $scope.anotacion.texto!="")
				texto += '<br>'+$scope.anotacion.texto;
			texto += '<br>FECHA: '+fecha + '.<br><label></label>';

			$scope.agregaAnotacion($scope.idInscripcion, acto, texto, $scope.anotacion.caratula, $scope.anotacion.repertorio, $scope.anotacion.version, $scope.anotacion.fecha, $scope.anotacion.borrador);
		}					

	};

	$scope.agregaAnotacion = function(idIns, acto, text, caratula, repertorio, version, fecha, borrador){
		$scope.savingAnotacion = true;

		$scope.anotacionStatus = {
				ok: false,
				error : false,
				msg: null
		};

		var promise = $scope.servicioAnotacion.addAnotacion(idIns, acto, text, caratula, repertorio, version, fecha, borrador);
		promise.then(function(data) {
			if(data.status===null){
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			}else if(data.status){
				$scope.anotacionStatus.ok = true;

				if(origen=="digital"){
					$scope.buscaNotas();
				}else{

					var promiseIns = $scope.servicioInscripcion.getInscripcion(inscripcionDigitalDTO.foja, inscripcionDigitalDTO.numero, inscripcionDigitalDTO.ano, inscripcionDigitalDTO.bis);
					promiseIns.then(function(data) {

						anotaciones.length = 0;
						notas.length = 0;
						angular.forEach(data.inscripcionDigitalDTO.anotacionsForIdInscripcionDestino, function(obj){
							if(obj.tipoAnotacionDTO.idTipoAnotacion===3){
								anotaciones.push(obj);
							}
							else if( (obj.tipoAnotacionDTO.idTipoAnotacion===1 || obj.tipoAnotacionDTO.idTipoAnotacion===2) && obj.estadoAnotacionDTO.idEstado!==7){
								notas.push(obj);
							}
						});

					}, function(reason) {
					});

					$scope.makeTodos();
				}
				$scope.salirSave();

			}else{
				$scope.savingAnotacion = false;
				$scope.anotacionStatus.error = true;
				$scope.anotacionStatus.msg = data.msg;
			}
		}, function(reason) {
			$scope.savingAnotacion = false;
			$scope.anotacionStatus.error = true;
			$scope.anotacionStatus.msg = 'Problemas contactando al servidor.';
		});
	};

	$scope.saveAnotacionMasiva = function () {
		var acto = $scope.anotacion.selectedTipo.nombreAnotacion;

		for(var j in $scope.titulos) {
			(function(j) {
				if($scope.titulos[j].Selected){
					//logica por anotacion
					if($scope.anotacion.selectedTipo.valorAnotacion == 0){
						$scope.agregaAnotacionMasiva($scope.titulos[j].idInscripcion, 'Anotaci\u00F3n', $scope.anotacion.texto, null, null, null, $scope.titulos[j].vigente, $scope.anotacion.fecha);
					} else{
						var fecha = moment($scope.anotacion.fecha, 'YYYY-MM-DD').format('LL');
						var texto = acto+'<br>C: '+$scope.anotacion.caratula+'<br>REP.: '+$scope.anotacion.repertorio;

						if($scope.anotacion.texto!=null && $scope.anotacion.texto!="")
							texto += '<br>'+$scope.anotacion.texto;
						texto += '<br>FECHA: '+fecha + '.<br><label></label>';										

						$scope.agregaAnotacionMasiva($scope.titulos[j].idInscripcion, acto, texto, $scope.anotacion.caratula, $scope.anotacion.repertorio, $scope.anotacion.version,$scope.titulos[j].vigente, $scope.anotacion.fecha);
					}	

					if($scope.anotacionStatus.error){
						$scope.titulos[j].flag=false;
					}else{
						$scope.titulos[j].flag=true;
					}
				}
			})(j);
		}
		
		$scope.salirSaveMasivo();
	};

	$scope.agregaAnotacionMasiva = function(idIns, acto, text, caratula, repertorio, version,vigente, fecha){
		$scope.savingAnotacion = true;
		
		$scope.anotacionStatus = {
				ok: false,
				error : false,
				msg: null
		};

		var promise = $scope.servicioAnotacion.addAnotacion(idIns, acto, text, caratula, repertorio, version, fecha);
		promise.then(function(data) {
			if(data.status===null){
				$window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
			}else if(data.status){
				$scope.anotacionStatus.ok = true;
				
				if(vigente){
					if(origen=="digital"){
						$scope.buscaNotas();
					}else{

						var promiseIns = $scope.servicioInscripcion.getInscripcion(inscripcionDigitalDTO.foja, inscripcionDigitalDTO.numero, inscripcionDigitalDTO.ano, inscripcionDigitalDTO.bis);
						promiseIns.then(function(data) {

							anotaciones.length = 0;
							notas.length = 0;
							angular.forEach(data.inscripcionDigitalDTO.anotacionsForIdInscripcionDestino, function(obj){
								if(obj.tipoAnotacionDTO.idTipoAnotacion===3){
									anotaciones.push(obj);
								}
								else if( (obj.tipoAnotacionDTO.idTipoAnotacion===1 || obj.tipoAnotacionDTO.idTipoAnotacion===2) && obj.estadoAnotacionDTO.idEstado!==7){
									notas.push(obj);
								}
							});

						}, function(reason) {
						});

						$scope.makeTodos();
					}
				}

			}else{
				$scope.savingAnotacion = false;
				$scope.anotacionStatus.error = true;
				$scope.anotacionStatus.msg = data.msg;
			}
		}, function(reason) {
			$scope.savingAnotacion = false;
			$scope.anotacionStatus.error = true;
			$scope.anotacionStatus.msg = 'Problemas contactando al servidor.';
		});
	};

	$scope.getRepertorioCaratula = function(){
		var promise = $scope.servicioAnotacion.getRepertorioCaratula($scope.anotacion.caratula);
		promise.then(function(data) {
			if(data.status===null){
			}else if(data.status){

				if(data.repertorio!=0){
					$scope.anotacion.repertorio = data.repertorio;
					var now = new Date(data.fechaIngresoRepertorio);

					var day = ("0" + now.getDate()).slice(-2);
					var month = ("0" + (now.getMonth() + 1)).slice(-2);

					var today = now.getFullYear()+"-"+(month)+"-"+(day) ;
					$scope.anotacion.fecha = today;
				}else{
					$scope.anotacion.repertorio = '';
					$scope.anotacion.fecha = '';
				}

			}else{
				$scope.savingAnotacion = false;
				$scope.anotacionStatus.error = true;
				$scope.anotacionStatus.msg = data.msg;
			}
		}, function(reason) {
			$scope.savingAnotacion = false;
			$scope.anotacionStatus.error = true;
			$scope.anotacionStatus.msg = 'No se ha podido establecer comunicacion con el servidor.';
		});
	};

	$scope.salirSave = function(){
		$timeout(function(){
			$scope.savingAnotacion = false;
			$scope.closeAnotacion();
		},2000);
	};
	
	$scope.salirSaveMasivo = function(){
		$timeout(function(){
			$scope.savingAnotacion = false;
			$scope.closeAnotacion();
		},2000);
	};

	$scope.buscaNotas = function() {

		//			alert('buscando notas');

		$scope.loaders['anotaciones'].isLoading = true;
		$scope.loaders['notas'].isLoading = true;

		var foja = inscripcionDigitalDTO.foja, numero = inscripcionDigitalDTO.numero, ano = inscripcionDigitalDTO.ano, bis = inscripcionDigitalDTO.bis;

		var promise = $scope.servicioInscripcion.getNotas(
				foja, numero, ano, bis);
		promise
		.then(
				function(data) {
					if (data.status == null) {
					} else if (data.status) {
						anotaciones.length = 0;
						notas.length = 0;

						angular
						.forEach(
								data.anotaciones,
								function(obj) {
									if ((obj.tipoAnotacionDTO.idTipoAnotacion === 1 || obj.tipoAnotacionDTO.idTipoAnotacion === 2)
											&& obj.estadoAnotacionDTO.idEstado !== 7) {
										notas.push(obj);

									} else if (obj.tipoAnotacionDTO.idTipoAnotacion === 3) {
										anotaciones
										.push(obj);
									}
								});



						$scope.makeTodos();

					} else {
						$scope.raiseErr('error','Error Obteniendo Notas.',data.msg);
						$scope.loaders['anotaciones'].error = true;
						$scope.loaders['notas'].error = true;
					}

					$scope.loaders['anotaciones'].isLoading = false;
					$scope.loaders['notas'].isLoading = false;

				},
				function(reason) {
					$scope.loaders['anotaciones'].isLoading = false;
					$scope.loaders['notas'].isLoading = false;

					$scope.loaders['anotaciones'].error = true;
					$scope.loaders['notas'].error = true;

					$scope.raiseErr('error','Error detectado.',	'No se ha podido establecer comunicación.');

				});

	};

	$scope.makeTodos = function() {

		paginacionNotas = angular.copy($scope.paginacionMaster);

		paginacionNotas.todos=notas;

		var begin = ((paginacionNotas.currentPage - 1) * paginacionNotas.numPerPage)
		, end = begin + paginacionNotas.numPerPage;

		paginacionNotas.filteredTodos = paginacionNotas.todos.slice(begin, end);

		paginacionNotas.totalpaginas = Math.round(paginacionNotas.todos.length / paginacionNotas.numPerPage);

	};

	$scope.titulosanteriores = function () {
		if(!$scope.anotacion.anotacionMasiva){
			$scope.titulos = [];
			$scope.loaders['titulosanteriores'].isLoading = true;
			$scope.puedeGuardar = true;
			$scope.enProceso=true;

			var promise = borradorService.obtenerTitulosAnteriores($scope.parametros.foja, $scope.parametros.numero, $scope.parametros.ano, $scope.parametros.bis);
			promise.then(function(data) {
				if(data.status===null){
				}else if(data.status){
					$scope.totalTitulosAnteriores = data.totalTitulosAnteriores;

					if(data.listaTitulosAnteriores.length>0){
						for( var i = 0; i < data.listaTitulosAnteriores.length; i++ ) {
							if(data.listaTitulosAnteriores[i].consultaDocumentoDTO.tipoDocumento!=8)//&& data.listaTitulosAnteriores[i].estado.tieneRechazo || 
								$scope.puedeGuardar = false;
							$scope.titulos.push({'Selected':data.listaTitulosAnteriores[i].vigente, 'foja':data.listaTitulosAnteriores[i].foja, 'numero': data.listaTitulosAnteriores[i].numero, 'anio':data.listaTitulosAnteriores[i].ano,'vigente':data.listaTitulosAnteriores[i].vigente, 'id':$scope.titulos.length+1, 'idInscripcion':data.listaTitulosAnteriores[i].idInscripcion, 'flag':'' , 'consultaDocumentoDTO': data.listaTitulosAnteriores[i].consultaDocumentoDTO});
						}
					}else{
						$scope.titulos.push({ 'Selected':true, 'foja':$scope.foja, 'numero': $scope.numero, 'anio':$scope.anio,'vigente':false, 'id':1, 'idInscripcion':$scope.idInscripcion, 'flag':'' });
					}

				}else{
					$scope.setErr('data.msg', data.msg);
					$scope.loaders['titulosanteriores'].error = true;
				}

				$scope.loaders['titulosanteriores'].isLoading = false;
				$scope.enProceso=false;
			}, function(reason) {
				$scope.setErr('Problema contactando al servidor.', '');
				$scope.loaders['titulosanteriores'].isLoading = false;
				$scope.loaders['titulosanteriores'].error = true;
			});
		}
	};
	
	$scope.selectTitulo = function(){
		$scope.puedeGuardar = true;
		angular.forEach($scope.titulos, function (titulo) {
			if(titulo.consultaDocumentoDTO.tipoDocumento!=8 && titulo.Selected==true)
				$scope.puedeGuardar = false;
		});
		
	};
	
	$scope.refrescarDocumentosInscripciones = function(){
		$scope.enProceso=true;
		$scope.puedeGuardar = true;
		angular.forEach($scope.titulos, function (titulo) {
			$scope.enProceso=true;
			var promise = inscripcionDigitalService.getInscripcionParaDigital(titulo.foja, titulo.numero, titulo.anio, false);
			promise.then(function(data) {				
				if(data.status===null){
				}else if(data.status){
					titulo.consultaDocumentoDTO = data.consultaDocumentoDTO;
					titulo.estado = data.estado;
					titulo.inscripcionDigitalDTO = data.inscripcionDigitalDTO;
					
					if(data.estado.tieneRechazo){
						titulo.Selected=false;
					}
					if(titulo.Selected==true && titulo.consultaDocumentoDTO.tipoDocumento!=8)
						$scope.puedeGuardar = false;
				}
				$scope.enProceso=false;
			}, function(reason) {
				$scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			});
		});		
	};
		
	$scope.agregarInscripcion = function(){
		
		var fojas=$scope.nuevaInscripcion.fojas;
		var numero=$scope.nuevaInscripcion.numero;
		var ano= $scope.nuevaInscripcion.ano;
		var bis = false;
		
		//Omitir repetido
		for(var i=0; i<$scope.titulos.length; i++){
			if ($scope.titulos[i].foja===fojas && $scope.titulos[i].numero===numero && $scope.titulos[i].anio===ano) {
				$scope.nuevaInscripcion.fojas = "";
				$scope.nuevaInscripcion.numero = "";
				$scope.nuevaInscripcion.ano = "";	
				return;
			}
		}

		$scope.openLoadingModal( 'Buscando...', '');

		var promise = inscripcionDigitalService.getInscripcionParaDigital(fojas, numero, ano, bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){				
				$scope.nuevaInscripcion.fojas = "";
				$scope.nuevaInscripcion.numero = "";
				$scope.nuevaInscripcion.ano = "";				
				
				if(data.estado.tieneRechazo){
					//tiene rechazo
					$scope.openSolicitar(data);

				}else if(!data.consultaDocumentoDTO.hayDocumento){
					//no tiene imagen y es solicitable 1

					//TODO: no tiene imagen y es solicitable

					$scope.openSolicitar(data);


				}else if(data.consultaDocumentoDTO.hayDocumento &&
						(data.consultaDocumentoDTO.tipoDocumento===9 || data.consultaDocumentoDTO.tipoDocumento===10)){
					//tiene imagen y es solicitable 2

					//TODO: ver referencial o solicitar?

					$scope.openSolicitar(data);

				}else if(data.consultaDocumentoDTO.hayDocumento && data.consultaDocumentoDTO.tipoDocumento===8){
					//esta digitalizada 3					
				}

				$scope.titulos.push({ 'Selected':!data.estado.tieneRechazo, 'foja':fojas, 'numero': numero, 
					'anio':ano,'vigente':false, 'id':$scope.titulos.length+1, 'idInscripcion':data.inscripcionDigitalDTO.idInscripcion, 
					'flag':'', 'consultaDocumentoDTO': data.consultaDocumentoDTO, 
					'inscripcionDigitalDTO': data.inscripcionDigitalDTO,
					'estado': data.estado});
				
				if(!data.estado.tieneRechazo && data.consultaDocumentoDTO.tipoDocumento!=8)
					$scope.puedeGuardar = false;
			}else{
				$scope.raiseErr('titulo','Problema detectado', data.msg);
			}
		}, function(reason) {

			$scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');


			//$scope.raiseErr('titulo','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};	

	$scope.setErr = function(title, mensaje){
		$scope.anotacionStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};

	$scope.setStatusMsg = function(title, mensaje){
		$scope.anotacionStatus.msgTitle = title;
		$scope.anotacionStatus.msg = mensaje;
	};

	$scope.checkAll = function () {

		if (!$scope.selectedAll) {
			$scope.selectedAll = true;
		} else {
			$scope.selectedAll = false;
		}

		$scope.puedeGuardar = true;
		angular.forEach($scope.titulos, function (titulo) {
			if (!titulo.vigente && (titulo.estado && !titulo.estado.tieneRechazo || !titulo.estado)) {
				titulo.Selected = $scope.selectedAll;
			}			
			
			if(titulo.consultaDocumentoDTO.tipoDocumento!=8 && titulo.Selected==true)
				$scope.puedeGuardar = false;
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
	
	$scope.openSolicitar = function (data) {
		$scope.solicita.foja = data.inscripcionDigitalDTO.foja;
		$scope.solicita.numero = data.inscripcionDigitalDTO.numero;
		$scope.solicita.ano = data.inscripcionDigitalDTO.ano;
		$scope.solicita.bis = data.inscripcionDigitalDTO.bis;
		$scope.solicita.consultaDocumentoDTO = data.consultaDocumentoDTO;
		$scope.solicita.estado = data.estado;		
		if(data.estado.tieneRechazo)
			$scope.solicita.solicitudDTO=data.solicitudDTO;
		
		$scope.$broadcast('sfojaIsFocused');

		$modal.open({
			templateUrl: 'digitalModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'DigitalModalCtrl',
			resolve: {
				solicita: function () {
				return $scope.solicita;
				},
				servicio: function(){
					return solicitudService;
				},
				origen: function(){
					return $scope.solicita.origen;
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
	
	$scope.verInscripcionDesdePreCaratula = function(obj) {
		$rootScope.go('/verInscripcion/'
			+ $scope.parametros.registro + '/' + obj.foja
			+ '/' + obj.numero + '/' + obj.anio + '/'
			+ false + '/' + $scope.parametros.origen);
	};
	
	$scope.verInscripcion = function(obj) {
		$window.open('/aio/index.html#/verInscripcion/'
			+ $scope.parametros.registro + '/' + obj.foja
			+ '/' + obj.numero + '/' + obj.anio + '/'
			+ false + '/' + $scope.parametros.origen,'popup','width=800,height=600');
	};	
	
	$scope.verPdfInscripcion = function(obj) {console.log(obj);
		$window.open('../do/service/pdf?metodo=getInscripcion&registro='
				+ $scope.parametros.registro
				+ '&foja='
				+ obj.foja
				+ '&numero='
				+ obj.numero
				+ '&ano='
				+ obj.anio
				+ '&bis='
				+ false
				+ '&estado='
				+ 8,'popup','width=800,height=600');
	};
	
	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};	
	
	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};	

	$scope.getTiposAnotacion();

});