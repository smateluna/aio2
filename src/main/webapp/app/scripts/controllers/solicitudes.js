'use strict';

app.controller('SolicitudesCtrl', function ($rootScope, $scope, $timeout, $location, solicitudesModel, $filter, $modal, $modalStack, inscripcionDigitalService,indiceService,RutHelper,solicitudService) {
	//modelos
	$scope.tab = solicitudesModel.getTab();
	$scope.busquedaTitulo = solicitudesModel.getBusquedaTitulo();

	$scope.datosSesion = {
		rut: null,
		requirente: null
	}

	$scope.contador = {
		usuario: null,
		total: null
	}

	$scope.solicita = {
		foja: null,
		numero: null,
		ano: null,
		bis: false,
		origen: 'solicitudes',
		consultaDocumentoDTO: {},
		estado: {}
	};
	
	$scope.resolveModal = {
		refresca: false
	};

	$scope.$watchCollection('busquedaTitulo', function(){

		if($scope.busquedaTitulo.rut === undefined || $scope.busquedaTitulo.rut===''){
			$scope.busquedaTitulo.nombre = '';
		}
	});

	$scope.busquedaRut = solicitudesModel.getBusquedaRut();

	$scope.$watchCollection('busquedaRut', function(){

		if($scope.busquedaRut.rut === undefined || $scope.busquedaRut.rut===''){
			$scope.busquedaRut.nombre = '';
		}

		if($.Rut.validar($scope.busquedaRut.rut)){
			solicitudesModel.setBusquedaRut($scope.busquedaRut);
		}
	});

	$scope.busquedaMis = solicitudesModel.getBusquedaMis();

	//$scope.$watchCollection('busquedaMis', function(){
	//solicitudesModel.setBusquedaMis($scope.busquedaMis);
	//});

	$scope.states = solicitudesModel.getStates();

	//$scope.$watchCollection('states', function(){
	//solicitudesModel.setStates($scope.states);
	//});

	$scope.listaMis = {
		data: [],
		inicio: 0,
		fin: 15,
		offset: 15
	};

	$scope.listaRut = {
		data: [],
		inicio: 0,
		fin: 15,
		offset: 15
	};

	$scope.resolveModal = {
		refresca: false,
		origen: 'solicitudes'
	};

	$scope.$watchCollection('resolveModal', function(){

		if($scope.resolveModal.refresca){
			$scope.buscarMis(false);
		}

	});


	$scope.promiseUpdateSolicitudMis = null;
	$scope.promiseUpdateSolicitudRut = null;

	//fin modelos

	//controles tabs
	$scope.isActiveParent = function(id){
		return $scope.tab.parentActive === id;
	};

	$scope.activateParent = function(id){
		$scope.tab.parentActive = id;
		solicitudesModel.setTab($scope.tab);

		if($scope.tab.parentActive===1){

			//buscar rut en sesion y buscar datos si existe
			var promise = indiceService.getRutSesion();
			promise.then(function(data) {      
				if(data.status===null){
				}else if(data.status){
					$scope.busquedaTitulo.rut = RutHelper.format(data.rut);
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});

			if($scope.busquedaTitulo.rut!==null){
				$timeout(function(){
					$scope.doFocus('foja');
				}, 200);
			}else{
				$timeout(function(){
					$scope.doFocus('rutt');
				}, 200);
			}
		}else if($scope.tab.parentActive===2){
			$timeout(function(){
				//buscar rut en sesion y buscar datos si existe
				var promise = indiceService.getRutSesion();
				promise.then(function(data) {      
					if(data.status===null){
					}else if(data.status){
						$scope.busquedaRut.rut = RutHelper.format(data.rut);
					}
				}, function(reason) {
					$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
				});

				$scope.doFocus('rut');
			}, 200);
		}else if($scope.tab.parentActive===3){
			if($scope.busquedaMis.resultados===undefined || $scope.busquedaMis.resultados.length===0){
				$scope.buscarMis(true);
			}
		}
	};
	//fin controles tabs

	//titulo
	$scope.verTitulo = function(titulo){
		var bis = titulo.bis==1?true:false,
		                       estado = titulo.estadoDigitalId;

		$rootScope.go('/verInscripcion/prop/'+titulo.foja+'/'+titulo.numero+'/'+titulo.ano+'/'+bis+'/solicitudes/'+estado);
	};

	$scope.resetResultadoTitulo = function(){
		$scope.busquedaTitulo.data = null;

		$scope.busquedaTitulo.req.rut = null;
		$scope.busquedaTitulo.req.nombre = '';

		if($scope.busquedaTitulo.resultados!==undefined){
			$scope.busquedaTitulo.resultados.length = 0;
		}

		$scope.busquedaTitulo.titulosSeleccionados = {};
		$scope.busquedaTitulo.checkTodo = false;
	};

	$scope.limpiarTitulo = function(){
		$scope.resetResultadoTitulo();

		$scope.busquedaTitulo.rut = null;
		$scope.busquedaTitulo.nombre = '';
		$scope.busquedaTitulo.foja = null;
		$scope.busquedaTitulo.numero = null;
		$scope.busquedaTitulo.ano = null;
		$scope.busquedaTitulo.bis = false;
		$scope.busquedaTitulo.anteriores = true;

		$scope.myform.$setPristine(true);

		$scope.doFocus('rutt');
	};

	$scope.limpiarFnaTitulo = function(){
		$scope.resetResultadoTitulo();

		$scope.busquedaTitulo.foja = null;
		$scope.busquedaTitulo.numero = null;
		$scope.busquedaTitulo.ano = null;
		$scope.busquedaTitulo.bis = false;
		$scope.busquedaTitulo.anteriores = true;

		$scope.myform.$setPristine(true);

		$scope.doFocus('foja');
	};

	$scope.buscarTitulo = function(){
		var foja = $scope.busquedaTitulo.foja,
		numero = $scope.busquedaTitulo.numero,
		ano = $scope.busquedaTitulo.ano,
		bis = $scope.busquedaTitulo.bis,
		anteriores = $scope.busquedaTitulo.anteriores;

		$scope.resetResultadoTitulo();

		$scope.states.titulo.isLoading = true;

		$scope.openLoadingModal( 'Buscando título...', '');
		
		$scope.agregarNuevaAtencion();

		var promise = inscripcionDigitalService.getBusquedaTitulo(foja, numero, ano, bis, anteriores);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				if(data.tieneRechazo){
					$scope.openMensajeModal('error', 'Hay rechazo', 'Una solicitud con los mismos antecedentes ya ha sido rechazada anteriormente.');
				}else {

					//angular knows
					if (data.solicita) {

					} else {

					}

					if (!data.fna && data.req.ano >= 2014) {
						$scope.openMensajeModal('error', 'No está en índice', 'La información citada no ha sido encontrada en nuestros registros.');
					} else if (!data.fna && data.req.ano < 2014) {
						//TODO show warn and can continue

						//						$scope.openMensajeModal('warn', 'No está en índice', 'La información citada no ha sido encontrada en ' +
						//						'nuestros registros. <br>Si está seguro de los datos citados puede continuar.');

						$scope.busquedaTitulo.req.rut = $scope.busquedaTitulo.rut;
						$scope.busquedaTitulo.req.nombre = $scope.busquedaTitulo.nombre;

						$scope.busquedaTitulo.data = data;
						//						$scope.busquedaTitulo.resultados = data.aaData.slice(0, 20);

						$scope.buscarTituloPost();

					} else {
						$scope.busquedaTitulo.req.rut = $scope.busquedaTitulo.rut;
						$scope.busquedaTitulo.req.nombre = $scope.busquedaTitulo.nombre;

						$scope.busquedaTitulo.data = data;
						$scope.busquedaTitulo.resultados = data.aaData.slice(0, 20);
					}
				}
			}else{
				$scope.raiseErr('titulo','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('titulo','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.openSolicitar = function () {
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

	$scope.buscarTituloPost = function(){
		var foja = $scope.busquedaTitulo.foja,
		numero = $scope.busquedaTitulo.numero,
		ano = $scope.busquedaTitulo.ano,
		bis = $scope.busquedaTitulo.bis,
		bisN = $scope.busquedaTitulo.bis===true?1:0;

		$scope.states.titulo.isLoading = true;

		$scope.openLoadingModal( 'Buscando...', '');

		var promise = inscripcionDigitalService.getInscripcion(foja, numero, ano, bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.solicita.foja = data.inscripcionDigitalDTO.foja;
				$scope.solicita.numero = data.inscripcionDigitalDTO.numero;
				$scope.solicita.ano = data.inscripcionDigitalDTO.ano;
				$scope.solicita.bis = data.inscripcionDigitalDTO.bis;
				$scope.solicita.consultaDocumentoDTO = data.consultaDocumentoDTO;
				$scope.solicita.estado = data.estado;

				if(data.estado.tieneRechazo){
					//tiene rechazo
					$scope.openSolicitar();

				}else if(!data.consultaDocumentoDTO.hayDocumento){
					//no tiene imagen y es solicitable 1

					//TODO: no tiene imagen y es solicitable

					$scope.openSolicitar();


				}else if(data.consultaDocumentoDTO.hayDocumento &&
						(data.consultaDocumentoDTO.tipoDocumento===9 || data.consultaDocumentoDTO.tipoDocumento===10)){
					//tiene imagen y es solicitable 2

					//TODO: ver referencial o solicitar?

					$scope.openSolicitar();

				}else if(data.consultaDocumentoDTO.hayDocumento && data.consultaDocumentoDTO.tipoDocumento===8){
					//esta digitalizada 3

					digitalModel.setDataState(data);
					$scope.verTitulo({foja: foja, numero: numero, ano: ano, bis: bisN});
				}

			}else{
				$scope.raiseErr('titulo','Problema detectado', data.msg);
			}
		}, function(reason) {

			$scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');


			//$scope.raiseErr('titulo','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.cerrarResultadoTitulo = function(){
		$scope.resetResultadoTitulo();

		$scope.doFocus('foja');
	};

	$scope.seleccionaSingle = function(id){

		if(!$scope.busquedaTitulo.titulosSeleccionados[id]){
			$scope.busquedaTitulo.checkTodo = false;
		}
	};

	$scope.seleccionarTodo = function() {

		for (var i = 0; i < $scope.busquedaTitulo.resultados.length; i++) {
			if (($scope.busquedaTitulo.resultados[i].tipoDocumento === 7 || $scope.busquedaTitulo.resultados[i].tipoDocumento === 2
					|| $scope.busquedaTitulo.resultados[i].tipoDocumento === 0)) {
				if ($scope.busquedaTitulo.resultados[i].puedeSolicitar) {
					$scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[i].firma] = $scope.busquedaTitulo.checkTodo;
				}
			}
		}
	};

	$scope.solicitarTodoTitulo = function(){
		var titulosParaSolicitar = [];

		for(var i = 0; i<$scope.busquedaTitulo.resultados.length; i++){

			if($scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[i].firma]){
				titulosParaSolicitar.push(
					{foja: $scope.busquedaTitulo.resultados[i].foja,
						numero: $scope.busquedaTitulo.resultados[i].numero,
						ano: $scope.busquedaTitulo.resultados[i].ano,
						bis: $scope.busquedaTitulo.resultados[i].bis,
						firma: $scope.busquedaTitulo.resultados[i].firma
					}
				);
			}
		}

		if(titulosParaSolicitar.length>0){
			var dataJson = angular.toJson(titulosParaSolicitar);

			$scope.promiseSaveSolicitud = solicitudService.save(dataJson, $scope.busquedaTitulo.req.rut, $scope.busquedaTitulo.req.nombre);
			$scope.promiseSaveSolicitud.then(function(data) {
				if(data.status===null){

				}else if(data.status){

					for(var j = 0; j<$scope.busquedaTitulo.resultados.length; j++){

						for(var x = 0; x<titulosParaSolicitar.length; x++){

							if(titulosParaSolicitar[x].firma === $scope.busquedaTitulo.resultados[j].firma){
								$scope.busquedaTitulo.resultados[j].puedeSolicitar = false;
								$scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[j].firma] = false;
								break;
							}
						}
					}
				}else{

				}
			}, function(reason) {});
		}
	};
	//fin titulo

	//rut
	$scope.columnSortRut = function(field){
		$scope.busquedaRut.sorter.reverse = $scope.busquedaRut.sorter.predicate == field && !$scope.busquedaRut.sorter.reverse;
		$scope.busquedaRut.sorter.predicate = field;
	};

	$scope.loadMoreRut = function() {

		var carga = $scope.busquedaRut.resultados.slice($scope.listaRut.inicio, $scope.listaRut.fin);

		angular.forEach(carga, function(sol){
			$scope.listaRut.data.push(sol);
		});

		$scope.listaRut.inicio = $scope.listaRut.inicio + $scope.listaRut.offset;
		$scope.listaRut.fin = $scope.listaRut.fin + $scope.listaRut.offset;
	};

	$scope.resetResultadoRut = function(){

		if($scope.busquedaRut.resultados!==undefined){
			$scope.busquedaRut.resultados.length = 0;
		}

		if($scope.listaRut.data!==undefined){
			$scope.listaRut.data.length = 0;
		}

		$scope.busquedaRut.data = null;

		$scope.busquedaRut.fecha = null;
		$scope.listaRut.inicio = 0;
		$scope.listaRut.fin = $scope.listaRut.offset;

		$scope.cleanErr('rut');
	};

	$scope.limpiarRut = function(){
		$scope.busquedaRut.rut = null;
		$scope.busquedaRut.nombre = '';

		$scope.resetResultadoRut();

		$scope.myformRut.$setPristine(true);

		$scope.doFocus('rut');
	};

	$scope.cerrarResultadoRut = function(){
		$scope.resetResultadoRut();
		$scope.doFocus('rut');
	};

	$scope.buscarRut = function(rut){
		//var rut = $scope.busquedaRut.rut;

		$scope.resetResultadoRut();
		$scope.openLoadingModal('Buscando...', '');

		var promise = solicitudService.getByRut(rut);
		promise.then(function(data) {console.log(data);
		$scope.closeModal();
		if(data.status===null){

		}else if(data.status){
			$scope.busquedaRut.fecha = new Date();
			$scope.busquedaRut.data = data;
			$scope.busquedaRut.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);

			//var dataParaMostrar = $filter('filter')(data.aaData, $scope.isSolicitudRevisada, true);
			//
			//        if(dataParaMostrar!==undefined && dataParaMostrar.length>0){
			//          $scope.busquedaRut.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);
			//        }else{
			//          $scope.busquedaRut.resultados = [];
			//        }
		}else{
			$scope.raiseErr('rut','Problema detectado', data.msg);
		}
		}, function(reason) {
			$scope.raiseErr('rut','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.hayDataEnTablaRut = function(){

		var dataParaMostrar = $filter('filter')($scope.busquedaRut.resultados, $scope.isSolicitudRevisada, true);

		return (dataParaMostrar!==undefined && dataParaMostrar.length>0)
	};

	//fin rut

	//inicio mis
	$scope.openSolicitarEnMis = function () {

		var myModal = $modal.open({
			templateUrl: 'solicitarModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'SolicitarModalCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
			},
	        servicio: function () {
		      return solicitudService;
		    }
		}
		});

	};

	$scope.columnSortMis = function(field){
		$scope.busquedaMis.sorter.reverse = $scope.busquedaMis.sorter.predicate == field && !$scope.busquedaMis.sorter.reverse;
		$scope.busquedaMis.sorter.predicate = field;
	};

	$scope.resetResultadoMis = function(){

		if($scope.busquedaMis.resultados!==undefined){
			$scope.busquedaMis.resultados.length = 0;
		}

		if($scope.listaMis.data!==undefined){
			$scope.listaMis.data.length = 0;
		}

		$scope.busquedaMis.data = null;

		$scope.busquedaMis.fecha = null;
		$scope.listaMis.inicio = 0;
		$scope.listaMis.fin = $scope.listaMis.offset;

		$scope.cleanErr('mis');
	};

	$scope.buscarMis = function(loading){
		$scope.resetResultadoMis();

		if(loading){
			$scope.openLoadingModal('Obteniendo solicitudes...', '');
		}

		var promise = solicitudService.getByUserInSession();
		promise.then(function(data) {
			if(loading){
				$scope.closeModal();
			}
			if(data.status===null){

			}else if(data.status){
				$scope.busquedaMis.fecha = new Date();

				$scope.busquedaMis.data = data;
				$scope.busquedaMis.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);

			}else{
				$scope.raiseErr('mis', 'Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('mis', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.loadMoreMis = function() {
		var carga = $scope.busquedaMis.resultados.slice($scope.listaMis.inicio, $scope.listaMis.fin);

		//console.log('busquedaMis.resultados.length>'+$scope.busquedaMis.resultados.length+' $scope.listaMis.data.length'+$scope.listaMis.data.length);
		//console.log('desde:'+$scope.listaMis.inicio+' fin:'+ $scope.listaMis.fin);
		//console.log(carga);

		angular.forEach(carga, function(sol){
			$scope.listaMis.data.push(sol);
		});

		$scope.listaMis.inicio = $scope.listaMis.inicio + $scope.listaMis.offset;
		$scope.listaMis.fin = $scope.listaMis.fin + $scope.listaMis.offset;
	};


	$scope.hayDataEnTablaMis = function(){

		var dataParaMostrar = $filter('filter')($scope.busquedaMis.resultados, $scope.isSolicitudRevisada, true);

		return (dataParaMostrar!==undefined && dataParaMostrar.length>0)
	};


	$scope.accionSolicitud = function(promesa, solicitud){

		if(solicitud.estadoDTO.idEstado==5){
			var bis = solicitud.bis==1?true:false;

			$rootScope.go('/verInscripcion/prop/'+solicitud.foja+'/'+solicitud.numero+'/'+solicitud.ano+'/'+bis+'/solicitudes/3');
		}else{

			var accion = '';
			if(solicitud.estadoDTO.idEstado==2){
				accion = 'remove-ok';

			}else if(solicitud.estadoDTO.idEstado==3){
				accion = 'remove-rechazo';
			}

			$scope[promesa] = solicitudService.actualizar(solicitud.idSolicitud, accion);
			$scope[promesa].then(function(data) {

				if(data.status==null){

				}else if(data.status){
					solicitud.fechaEstado = data.fechaEstado;
					solicitud.estadoDTO = {idEstado: data.idEstado, descripcion: data.descripcionEstado};

					if(accion=='remove-ok'){
						var bis = solicitud.bis==1?true:false;

						$rootScope.go('/verInscripcion/prop/'+solicitud.foja+'/'+solicitud.numero+'/'+solicitud.ano+'/'+bis+'/solicitudes/3');
					}

				}else{

				}
			}, function(reason) {

			});
		}
	};


	//fin mis

	//utiles
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	$scope.raiseErr = function(key, title, msg){
		$scope.closeModal();
		$scope.states[key].isError = true;
		$scope.states[key].title = title;
		$scope.states[key].msg = msg;
	};

	$scope.cleanErr = function(key){
		$scope.states[key].isError = false;
		$scope.states[key].title = '';
		$scope.states[key].msg = '';
	};

	$scope.openMensajeModal = function (tipo, titulo, detalle, autoClose, segundos) {
		$modal.open({
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


	$scope.isSolicitudRevisada = function(sol){
		return sol.estadoDTO.idEstado!==6;
	};

	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};
	//fin validaciones titulo

	$timeout(function(){

		if($scope.tab.parentActive===1){

			//buscar rut en sesion y buscar datos si existe
			var promise = indiceService.getRutSesion();
			promise.then(function(data) {      
				if(data.status===null){
				}else if(data.status){
					$scope.busquedaTitulo.rut = RutHelper.format(data.rut);
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});


			if($scope.busquedaTitulo.rut!==null){
				$scope.doFocus('foja');

				if(!$.Rut.validar($scope.busquedaTitulo.rut)){
					$scope.busquedaTitulo.rut = '';
					$scope.doFocus('rutt');
				}
			}else{
				$scope.doFocus('rutt');
			}
		}else if($scope.tab.parentActive===2){

			//buscar rut en sesion y buscar datos si existe
			var promise = indiceService.getRutSesion();
			promise.then(function(data) {      
				if(data.status===null){
				}else if(data.status){
					$scope.busquedaRut.rut = RutHelper.format(data.rut);
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});

			$scope.doFocus('rut');

			if(!$.Rut.validar($scope.busquedaRut.rut)){
				$scope.busquedaRut.rut = '';
			}
		}

	}, 200);

	$scope.contarAtencion = function(){

		var myModal = $modal.open({
			templateUrl: 'ingresaRutModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'ingresaRutModalCtrl'
		});

		myModal.result.then(function () {
			//buscar rut en sesion y buscar datos si existe
			var promise = indiceService.getRutSesion();
			promise.then(function(data) {      
				if(data.status===null){
				}else if(data.status){
					$scope.datosSesion.rut=RutHelper.format(data.rut);
					$scope.datosSesion.requirente=data.requirente;

					$scope.busquedaTitulo.rut = RutHelper.format(data.rut);
					$scope.busquedaRut.rut = RutHelper.format(data.rut);

					$scope.obtenerAtencionPorDia();
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
		}, function () {

			//buscar rut en sesion y buscar datos si existe
			var promise = indiceService.getRutSesion();
			promise.then(function(data) {      
				if(data.status===null){
				}else if(data.status){
					$scope.datosSesion.rut=RutHelper.format(data.rut);
					$scope.datosSesion.requirente=data.requirente;

					$scope.busquedaTitulo.rut = RutHelper.format(data.rut);
					$scope.busquedaRut.rut = RutHelper.format(data.rut);

					$scope.obtenerAtencionPorDia();
				}
			}, function(reason) {
				$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
			//			$scope.datosSesion.requirente='no existe en nuestros registros';
			//			
			//			$scope.obtenerAtencionPorDia();
		});
	};

	$scope.obtenerAtencionPorDia = function(){

		var promise = indiceService.obtenerAtencionPorDia();
		promise.then(function(data) {      
			if(data.status===null){
			}else if(data.status){
				$scope.contador.total=data.contadores.total;
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});

	};
	
	$scope.agregarNuevaAtencion = function () {

		var promise = indiceService.agregarNuevaAtencion($scope.busquedaTitulo.rut);
		promise.then(function(data) {
			if(data.status===null){
			}else if(data.status){
			}else{
				$scope.setErr('Problema detectado.', 'No se ha podido registrar nueva atencion.');
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', 'No se ha guardado la nueva atencion.');
		});

	};
});