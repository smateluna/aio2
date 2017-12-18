'use strict';

app.controller('IndiceCtrl', function ($scope,$timeout,$window,$rootScope,$location,$anchorScroll,solicitudesModel,indiceModel,indiceService,solicitudService,inscripcionDigitalService,RutHelper,$filter,$modal,$modalStack,$routeParams) {

	$scope.busquedaIndice = indiceModel.getBusquedaIndice();
	$scope.states = indiceModel.getStates();
	$scope.listaPropiedad = indiceModel.getListaPropiedad();
	$scope.listaProhibiciones = indiceModel.getListaProhibiciones();
	$scope.listaHipoteca = indiceModel.getListaHipoteca();
	$scope.listaComercio = indiceModel.getListaComercio();
	$scope.tab = solicitudesModel.getTab();

	$scope.rowHighilited = function (idSelected) {
		$scope.busquedaIndice.selectedRow = idSelected;
	};

	//   if($routeParams.foja!==undefined){
	//	   $timeout(function(){
	//	   
	//	    if($scope.stringIsNumber($routeParams.foja) && $routeParams.foja.length<=10){
	//	
	//	      $scope.busquedaIndice.foja = $routeParams.foja;
	//		  $scope.buscarIndice();
	//	    }else{
	//	      $scope.resetResultadoIndice();
	//	    }
	//	   }, 100);
	//   }

	$scope.solicitudStatus = {
		ok: false,
		error : false,
		warning: false,
		msgTitle: null,
		msg: null,
		tipo: null
	};

	$scope.solicita = {
		foja: null,
		numero: null,
		ano: null,
		bis: false,
		tipo: null,
		esDigital: false,
		tieneRechazo: false
	};

	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}

	$scope.datosSesion = {
		rut: null,
		requirente: null
	}

	$scope.contador = {
		usuario: null,
		total: null
	}


	if($scope.busquedaIndice.listaactos==undefined || $scope.busquedaIndice.listaactos.length==0){
		var promise = indiceService.obtenerActos();
		promise.then(function(data) {
			//$scope.closeModal();
			if(data.status===null){
			}else if(data.status){
				$scope.busquedaIndice.listaactos = data.actos;
			}else{
				$scope.setErr('data.msg', data.msg);
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
		});
	}

	if($scope.busquedaIndice.listatipos==undefined || $scope.busquedaIndice.listatipos.length==0){
		var promise = indiceService.obtenerTipos();
		promise.then(function(data) {
			//$scope.closeModal();
			if(data.status===null){
			}else if(data.status){
				$scope.busquedaIndice.listatipos = data.tipos;
			}else{
				$scope.setErr('data.msg', data.msg);
			}
		}, function(reason) {
			$scope.setErr('Problema contactando al servidor.', '');
		});
	}	

	if($scope.busquedaIndice.listaconservadores==undefined || $scope.busquedaIndice.listaconservadores.length==0){

		var promise = indiceService.obtenerCBRSDisponibles();
		promise.then(function(data) {
			if(data.status===null){

			}else if(data.status){
				$scope.busquedaIndice.listaconservadores = data.conservadores;

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	}

	$scope.buscarporregistro = function(registro){

		var buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
		buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh;

		if(registro==0){//hipotecas
			if($scope.listaHipoteca.data!==undefined){
				$scope.listaHipoteca.data.length = 0;
			}
			$scope.listaHipoteca.inicio = 0;
			$scope.listaHipoteca.fin = $scope.listaHipoteca.offset;

			if(buscarPorInscricionHipo)
				$scope.buscarIndice();
			else
				$scope.buscarIndiceHipoteca();

		}else if(registro==1){	//prohibiciones
			if($scope.listaProhibiciones.data!==undefined){
				$scope.listaProhibiciones.data.length = 0;
			}
			$scope.listaProhibiciones.inicio = 0;
			$scope.listaProhibiciones.fin = $scope.listaProhibiciones.offset;

			if(buscarPorInscricionProh)
				$scope.buscarIndice();
			else
				$scope.buscarIndiceProhibiciones();
		}
	};

	$scope.buscarIndiceHipoteca = function(){
		var foja = $scope.busquedaIndice.foja,
		numero = $scope.busquedaIndice.numero,
		ano = $scope.busquedaIndice.ano,
		bis = $scope.busquedaIndice.bis,
		rut = $scope.busquedaIndice.rut,
		apellidos = $scope.busquedaIndice.nombre,
		direccion = $scope.busquedaIndice.direccion,
		regPropiedades = $scope.busquedaIndice.propiedades,
		regHipoteca = $scope.busquedaIndice.hipoteca,
		regProhibiciones = $scope.busquedaIndice.prohibiciones,
		regComercio = $scope.busquedaIndice.comercio,
		buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
		buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh,
		exacta = $scope.busquedaIndice.exacta;

		if($scope.busquedaIndice.comuna!=null)
			var comuna = $scope.busquedaIndice.comuna.codigo;
		if($scope.busquedaIndice.anoInscripcion!=null)
			var anoInscripcion = $scope.busquedaIndice.anoInscripcion.codigo;
		if($scope.busquedaIndice.conservador!=null){
			var conservador = $scope.busquedaIndice.conservador.ip;
			$scope.busquedaIndice.conservadorelegido=$scope.busquedaIndice.conservador;
		}else{
			$scope.busquedaIndice.conservadorelegido=null;
		}	

		$scope.openLoadingModal('Buscando...', '');

		var promise = indiceService.getIndicePropiedades(rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,'','',exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){
				//	        $scope.busquedaIndice.fecha = new Date();

				$scope.busquedaIndice.data = data;

				if(data.aaDataHipotecas.length>0){
					if(!buscarPorInscricionProh){
						$scope.busquedaIndice.resultadoshipotecas = data.aaDataHipotecas;
						$scope.loadMoreHipoteca();
						$scope.activateParent(3);
					}
				}	

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	}

	$scope.buscarIndiceProhibiciones = function(){
		var foja = $scope.busquedaIndice.foja,
		numero = $scope.busquedaIndice.numero,
		ano = $scope.busquedaIndice.ano,
		bis = $scope.busquedaIndice.bis,
		rut = $scope.busquedaIndice.rut,
		apellidos = $scope.busquedaIndice.nombre,
		direccion = $scope.busquedaIndice.direccion,
		regPropiedades = $scope.busquedaIndice.propiedades,
		regHipoteca = $scope.busquedaIndice.hipoteca,
		regProhibiciones = $scope.busquedaIndice.prohibiciones,
		regComercio = $scope.busquedaIndice.comercio,
		buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
		buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh,
		exacta = $scope.busquedaIndice.exacta;

		if($scope.busquedaIndice.comuna!=null)
			var comuna = $scope.busquedaIndice.comuna.codigo;
		if($scope.busquedaIndice.anoInscripcion!=null)
			var anoInscripcion = $scope.busquedaIndice.anoInscripcion.codigo;
		if($scope.busquedaIndice.conservador!=null){
			var conservador = $scope.busquedaIndice.conservador.ip;
			$scope.busquedaIndice.conservadorelegido=$scope.busquedaIndice.conservador;
		}else{
			$scope.busquedaIndice.conservadorelegido=null;
		}

		$scope.openLoadingModal('Buscando...', '');

		var promise = indiceService.getIndicePropiedades(rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,'','',exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){
				//	        $scope.busquedaIndice.fecha = new Date();

				$scope.busquedaIndice.data = data;

				if(data.aaDataProhibiciones.length>0){
					if(!buscarPorInscricionHipo){
						$scope.busquedaIndice.resultadosprohibiciones = data.aaDataProhibiciones;
						$scope.loadMoreProhibiciones();
						$scope.activateParent(2);
					}
				}	

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});

	}

	$scope.buscarIndice = function(){

		var foja = $scope.busquedaIndice.foja,
		numero = $scope.busquedaIndice.numero,
		ano = $scope.busquedaIndice.ano,
		bis = $scope.busquedaIndice.bis,
		rut = $scope.busquedaIndice.rut,
		apellidos = $scope.busquedaIndice.nombre,
		direccion = $scope.busquedaIndice.direccion,
		regPropiedades = $scope.busquedaIndice.propiedades,
		regHipoteca = $scope.busquedaIndice.hipoteca,
		regProhibiciones = $scope.busquedaIndice.prohibiciones,
		regComercio = $scope.busquedaIndice.comercio,
		buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
		buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh,
		exacta = $scope.busquedaIndice.exacta;

		if(!buscarPorInscricionHipo && !buscarPorInscricionProh)
			$scope.resetResultadoIndice();

		if($scope.busquedaIndice.comuna!=null)
			var comuna = $scope.busquedaIndice.comuna.codigo;
		if($scope.busquedaIndice.anoInscripcion!=null)
			var anoInscripcion = $scope.busquedaIndice.anoInscripcion.codigo;
		if($scope.busquedaIndice.acto!=null)
			var acto = $scope.busquedaIndice.acto.nombre;
		if($scope.busquedaIndice.tipo!=null)
			var tipo = $scope.busquedaIndice.tipo.nombre;
		if($scope.busquedaIndice.conservador!=null){
			var conservador = $scope.busquedaIndice.conservador.ip;
			$scope.busquedaIndice.conservadorelegido=$scope.busquedaIndice.conservador;
		}else{
			$scope.busquedaIndice.conservadorelegido=null;
		}


		$scope.openLoadingModal('Buscando...', '');

		var promise = indiceService.getIndicePropiedades(rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,acto,tipo,exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){
				//	        $scope.busquedaIndice.fecha = new Date();

				$scope.busquedaIndice.data = data;

				if(data.aaDataComercio.length>0){
					if(!buscarPorInscricionHipo && !buscarPorInscricionProh){
						$scope.busquedaIndice.resultadoscomercio = data.aaDataComercio;
						$scope.loadMoreComercio();
						$scope.activateParent(4);
					}
				}
				if(data.aaDataHipotecas.length>0){
					if(!buscarPorInscricionProh){
						$scope.busquedaIndice.resultadoshipotecas = data.aaDataHipotecas;
						$scope.loadMoreHipoteca();
						$scope.activateParent(3);
					}
				}	
				if(data.aaDataProhibiciones.length>0){
					if(!buscarPorInscricionHipo){
						$scope.busquedaIndice.resultadosprohibiciones = data.aaDataProhibiciones;
						$scope.loadMoreProhibiciones();
						$scope.activateParent(2);
					}
				}	
				if(data.aaData.length>0){
					if(!buscarPorInscricionHipo && !buscarPorInscricionProh){
						$scope.busquedaIndice.resultados = data.aaData;
						$scope.loadMorePropiedad();
						$scope.activateParent(1);
					}
				}	

				//Paginacion
				$scope.makeTodos();

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.loadMorePropiedad = function() {

		var carga = $scope.busquedaIndice.resultados.slice($scope.listaPropiedad.inicio, $scope.listaPropiedad.fin);

		angular.forEach(carga, function(sol){
			$scope.listaPropiedad.data.push(sol);
		});

		$scope.listaPropiedad.inicio = $scope.listaPropiedad.inicio + $scope.listaPropiedad.offset;
		$scope.listaPropiedad.fin = $scope.listaPropiedad.fin + $scope.listaPropiedad.offset;
	};

	$scope.loadMoreProhibiciones = function() {

		var carga = $scope.busquedaIndice.resultadosprohibiciones.slice($scope.listaProhibiciones.inicio, $scope.listaProhibiciones.fin);

		angular.forEach(carga, function(sol){
			$scope.listaProhibiciones.data.push(sol);
		});

		$scope.listaProhibiciones.inicio = $scope.listaProhibiciones.inicio + $scope.listaProhibiciones.offset;
		$scope.listaProhibiciones.fin = $scope.listaProhibiciones.fin + $scope.listaProhibiciones.offset;
	};

	$scope.loadMoreHipoteca = function() {

		var carga = $scope.busquedaIndice.resultadoshipotecas.slice($scope.listaHipoteca.inicio, $scope.listaHipoteca.fin);

		angular.forEach(carga, function(sol){
			$scope.listaHipoteca.data.push(sol);
		});

		$scope.listaHipoteca.inicio = $scope.listaHipoteca.inicio + $scope.listaHipoteca.offset;
		$scope.listaHipoteca.fin = $scope.listaHipoteca.fin + $scope.listaHipoteca.offset;
	};

	$scope.loadMoreComercio = function() {

		var carga = $scope.busquedaIndice.resultadoscomercio.slice($scope.listaComercio.inicio, $scope.listaComercio.fin);

		angular.forEach(carga, function(sol){
			$scope.listaComercio.data.push(sol);
		});

		$scope.listaComercio.inicio = $scope.listaComercio.inicio + $scope.listaComercio.offset;
		$scope.listaComercio.fin = $scope.listaComercio.fin + $scope.listaComercio.offset;
	};

	$scope.resolveModal = {
		refresca: false
	};

	$scope.resetResultadoIndice = function(){
		$scope.busquedaIndice.data = null;
		$scope.states.buscar.isError = false;

		$scope.busquedaIndice.buscarPorInscricionHipo=false;
		$scope.busquedaIndice.buscarPorInscricionProh=false;


		if($scope.busquedaIndice.resultados!==undefined){
			$scope.busquedaIndice.resultados.length = 0;
		}

		if($scope.busquedaIndice.resultadosprohibiciones!==undefined){
			$scope.busquedaIndice.resultadosprohibiciones.length = 0;
		}

		if($scope.busquedaIndice.resultadoshipotecas!==undefined){
			$scope.busquedaIndice.resultadoshipotecas.length = 0;
		}

		if($scope.busquedaIndice.resultadoscomercio!==undefined){
			$scope.busquedaIndice.resultadoscomercio.length = 0;
		}

		if($scope.listaPropiedad.data!==undefined){
			$scope.listaPropiedad.data.length = 0;
		}
		$scope.listaPropiedad.inicio = 0;
		$scope.listaPropiedad.fin = $scope.listaPropiedad.offset;

		if($scope.listaProhibiciones.data!==undefined){
			$scope.listaProhibiciones.data.length = 0;
		}
		$scope.listaProhibiciones.inicio = 0;
		$scope.listaProhibiciones.fin = $scope.listaProhibiciones.offset;

		if($scope.listaHipoteca.data!==undefined){
			$scope.listaHipoteca.data.length = 0;
		}
		$scope.listaHipoteca.inicio = 0;
		$scope.listaHipoteca.fin = $scope.listaHipoteca.offset;

		if($scope.listaComercio.data!==undefined){
			$scope.listaComercio.data.length = 0;
		}
		$scope.listaComercio.inicio = 0;
		$scope.listaComercio.fin = $scope.listaComercio.offset;
	};

	$scope.limpiarTitulo = function(){
		$scope.resetResultadoIndice();

		$scope.busquedaIndice.foja = null;
		$scope.busquedaIndice.numero = null;
		$scope.busquedaIndice.ano = null;
		$scope.busquedaIndice.bis = false;
		$scope.busquedaIndice.rut = null;
		$scope.busquedaIndice.nombre = null;
		$scope.busquedaIndice.direccion = null;
		$scope.busquedaIndice.comuna = null;
		$scope.busquedaIndice.anoInscripcion = null;
		$scope.busquedaIndice.acto = null;
		$scope.busquedaIndice.tipo = null;
		$scope.busquedaIndice.conservador = null;

		$scope.myform.$setPristine(true);

		$scope.doFocus('nombre');
		$scope.indicebusqueda=false;
	};

	//utiles	
	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	//validaciones titulo
	$scope.archivoNacional = function(value){
		return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};

	$scope.anoActual = function(value){
		return !(moment(new Date()).year()<value);
	};
	//fin validaciones titulo

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

	$scope.raiseErr = function(key, title, msg){
		$scope.closeModal();
		$scope.states[key].isError = true;
		$scope.states[key].title = title;
		$scope.states[key].msg = msg;
	};

	$scope.reingresar = function (indice,registro,idregistro) {
		var promise = indiceService.guardaensesion(indice.foja, indice.num, indice.ano, indice.bis, registro,idregistro);
		promise.then(function(data) {

			if(data.status===null){

			}else if(data.status){

				$rootScope.go('/reingreso');
			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.openSolicitar = function (indice,registro) {

		//marco linea consultada
		$scope.rowHighilited(indice.id);

		$scope.resolveModal.refresca = false;

		var foja=null,
		numero=null,
		ano = null,
		bis = null;

		if('prop'==registro || 'com'==registro){
			foja=indice.foja;
			numero=indice.num;
			ano=indice.ano;
			bis='false';
		} else if('hip'==registro){
			foja=indice.fojah;
			numero=indice.numh;
			ano=indice.anoh;
			bis='false';

		} else if('proh'){
			foja=indice.fojaph;
			numero=indice.numph;
			ano=indice.anoph;
			bis='false';
		}

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

	$scope.masInfo = function (indice) {

		//marco linea consultada
		$scope.rowHighilited(indice.id);

		$scope.resolveModal.refresca = false;

		var myModal = $modal.open({
			templateUrl: 'masInfoindiceModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'masinfoindiceModalCtrl',
			resolve: {   
			resolveModal : function(){
			return $scope.resolveModal;
		},
		titulo: function(){
			return indice;
		},
		foja: function(){
			return indice.foja;
		},
		numero: function(){
			return indice.num;
		},
		ano: function(){
			return indice.ano;
		},
		caratula: function(){
			return indice.caratula;
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

	//  $scope.checkTieneImagen = function(valor) {
	//  if (valor == 1) {
	//	   return false;
	//	  }
	//	  else {
	//	   return true;
	//	  }
	//  };

	$scope.solicitar = function (titulo) {
		$scope.resetStatus();
		$scope.isLoadingSolicitar = true;

		var foja=titulo.foja;
		var numero=titulo.num;
		var ano=titulo.ano;
		//intervenir al momento que se habilite bis
		var bis='false';

		var promise = solicitudService.saveSingle(foja, numero, ano, bis);
		promise.then(function(data) {
			if(data.status===null){

			}else if(data.status){
				$scope.solicitudStatus.ok = true;
				titulo.button_clicked = true;
				//        if(scope.solicitudStatus.tipo===1){
				//          resolveModal.refresca = true;
				//
				//          $timeout(function(){
				//            $scope.closeModal();
				//          },4000);
				//        }else{
				//          resolveModal.refresca = true;
				$scope.isLoadingSolicitar = false;
				//        }
			}else{
				$scope.isLoadingSolicitar = false;
				$scope.setErr('Problema detectado.', 'No se ha guardado la solicitud.');
			}
		}, function(reason) {
			$scope.isLoadingSolicitar = false;
			$scope.setErr('Problema contactando al servidor.', 'No se ha guardado la solicitud.');
		});
	};

	$scope.verTitulo = function(titulo){

		//marco linea consultada
		$scope.rowHighilited(titulo.id);

		var bis = titulo.bis==1?true:false,
		                       //realizar alternativa para saber tipo de documento;                       
		                       estado = '0';

		$rootScope.go('/verInscripcion/prop/'+titulo.foja+'/'+titulo.num+'/'+titulo.ano+'/'+bis+'/indice/'+estado);

	};

	$scope.verTipoImagenHipo = function(titulo){

		//marco linea consultada
		$scope.rowHighilited(titulo.id);

		var bis = titulo.bis==1?true:false;

		//	    $rootScope.go('/verInscripcion/hip/'+titulo.fojah+'/'+titulo.numh+'/'+titulo.anoh+'/'+bis+'/indice/'+estado);
		$rootScope.go('/verInscripcionReg/hip/'+titulo.fojah+'/'+titulo.numh+'/'+titulo.anoh+'/'+bis+'/indice');
	};

	$scope.verTipoImagenProh = function(titulo){

		//marco linea consultada
		$scope.rowHighilited(titulo.id);

		var bis = titulo.bis==1?true:false,
		                       //realizar alternativa para saber tipo de documento;                       
		                       estado = '0';

		//	    $rootScope.go('/verInscripcion/proh/'+titulo.fojaph+'/'+titulo.numph+'/'+titulo.anoph+'/'+bis+'/indice/'+estado);
		$rootScope.go('/verInscripcionReg/proh/'+titulo.fojaph+'/'+titulo.numph+'/'+titulo.anoph+'/'+bis+'/indice');
	};

	$scope.verTipoImagenCom = function(titulo){

		//marco linea consultada
		$scope.rowHighilited(titulo.id);

		var bis = titulo.bis==1?true:false,
		                       //realizar alternativa para saber tipo de documento;                       
		                       estado = '0';

		//	    $rootScope.go('/verInscripcion/proh/'+titulo.fojaph+'/'+titulo.numph+'/'+titulo.anoph+'/'+bis+'/indice/'+estado);
		$rootScope.go('/verInscripcionReg/com/'+titulo.foja+'/'+titulo.num+'/'+titulo.ano+'/'+bis+'/indice');
	};

	$scope.resetStatus = function(){
		$scope.solicitudStatus.ok = false;
		$scope.solicitudStatus.error = false;
		$scope.solicitudStatus.warning = false;
		$scope.solicitudStatus.msgTitle = null;
		$scope.solicitudStatus.msg = null;
	};

	$scope.verTipoImagen = function(titulo){

		//marco linea consultada
		$scope.rowHighilited(titulo.id);

		var bis = titulo.bis==1?true:false;

		$scope.openLoadingModal('Buscando...', '');

		var promise = inscripcionDigitalService.getInscripcionJPG(titulo.foja,titulo.num,titulo.ano,bis);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				if(data.res.tienerechazo){
					titulo.tipodocumento=0;
					$scope.openVerInscripcion(titulo);
					$scope.pasoPreSolicitar = false;
				} else {
					titulo.esDigital=data.res.anodigital;

					if(data.res.consultaDocumentoDTO.hayDocumento){

						if(data.res.consultaDocumentoDTO.tipoDocumento!=8){
							if(data.res.consultaDocumentoDTO.tipoDocumento==9){
								titulo.tipodocumento=2;	  
							} else if(data.res.consultaDocumentoDTO.tipoDocumento==10){
								titulo.tipodocumento=4;
							}
							//esDigital: false
							$scope.openVerInscripcion(titulo);
							$scope.pasoPreSolicitar = false;
						} else {
							$scope.verTitulo(titulo);
						}	
					} else {
						titulo.tipodocumento=1;	  
						$scope.openVerInscripcion(titulo);
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

	$scope.openVerInscripcion = function (titulo) {
		$scope.$broadcast('sfojaIsFocused');

		$scope.resolveModal.refresca = false;

		var myModal = $modal.open({
			templateUrl: 'solicitarIndiceModal.html',
			backdrop: true,
			windowClass: 'modal',
			controller: 'SolicitarIndiceModalCtrl',
			resolve: {
			resolveModal : function(){
			return $scope.resolveModal;
		},
		titulo: function(){
			return titulo;
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

	//controles tabs
	$scope.isActiveParent = function(id){
		return $scope.tab.parentActive === id;
	};

	$scope.activateParent = function(id){
		$scope.tab.parentActive = id;
		solicitudesModel.setTab($scope.tab);

	};
	//fin controles tabs

	if($scope.busquedaIndice.comunas==undefined || $scope.busquedaIndice.comunas.length==0){
		$scope.busquedaIndice.comunas = [{
			codigo:23,
			descripcion: 'Cerrillos'
		},{
			codigo:15,
			descripcion: 'Cerro Navia'
		},{
			codigo:22,
			descripcion:'Colina'
		},{
			codigo:26,
			descripcion: 'Conchali'
		},{
			codigo:13,
			descripcion: 'Estacion Central'
		},{
			codigo:12,
			descripcion: 'Huechuraba'
		},{
			codigo:11,
			descripcion: 'Independencia'
		},{
			codigo:9,
			descripcion: 'La Florida'
		},{
			codigo:25,
			descripcion: 'La Reina'
		},{
			codigo:20,
			descripcion: 'Lampa'
		},{
			codigo:3,
			descripcion: 'Las Condes'
		},{
			codigo:5,
			descripcion: 'Lo Barnechea'
		},{
			codigo:16,
			descripcion: 'Lo Prado'
		},{
			codigo:7,
			descripcion: 'Macul'
		},{
			codigo:14,
			descripcion: 'Maipu'
		},{
			codigo:6,
			descripcion: 'Ñuñoa'
		},{
			codigo:8,
			descripcion: 'Peñalolen'
		},{
			codigo:2,
			descripcion: 'Providencia'
		},{
			codigo:17,
			descripcion: 'Pudahuel'
		},{
			codigo:19,
			descripcion: 'Quilicura'
		},{
			codigo:24,
			descripcion: 'Quinta Normal'
		},{
			codigo:10,
			descripcion: 'Recoleta'
		},{
			codigo:18,
			descripcion: 'Renca'
		},{
			codigo:1,
			descripcion: 'Santiago'
		},{
			codigo:21,
			descripcion: 'Til til'
		},{
			codigo:4,
			descripcion: 'Vitacura'
		}];
	}

	if($scope.busquedaIndice.anosInscripciones==undefined || $scope.busquedaIndice.anosInscripciones.length==0){
		var ano = (new Date).getFullYear();
		for(var i = ano-80; i <= ano; i++){
			$scope.busquedaIndice.anosInscripciones.push({codigo: i, descripcion: i});
		}
	}

	$scope.setErr = function(title, mensaje){
		$scope.solicitudStatus.error = true;
		$scope.setStatusMsg(title, mensaje);
	};

	$scope.setStatusMsg = function(title, mensaje){
		$scope.solicitudStatus.msgTitle = title;
		$scope.solicitudStatus.msg = mensaje;
	};

	//	$timeout(function(){
	//		$scope.doFocus('nombre');
	//	}, 200);

	//	$timeout(function(){
	//		if($scope.busquedaIndice.selectedRow!=null){
	//			// set the location.hash to the id of
	//			// the element you wish to scroll to.
	//			$location.hash($scope.busquedaIndice.selectedRow);
	//
	//			// call $anchorScroll()
	//			$anchorScroll();
	//		}
	//	}, 200);

	$scope.ticket = function (indice,registro) {

		//marco linea consultada
		$scope.rowHighilited(indice.id);

		$scope.resolveModal.refresca = false;

		var foja=null,
		numero=null,
		ano = null,
		bis = null,
		nombre = null,
		direccion = null,
		comuna=null;

		if('prop'==registro || 'com'==registro){
			foja=indice.foja;
			numero=indice.num;
			ano=indice.ano;
			bis='false';
			if('prop'==registro){
				nombre=indice.nombre;
				direccion=indice.dir;
				comuna=indice.comuna;
			} else if('com'==registro){
				nombre=indice.nombreSociedad;
			}
		} else if('hip'==registro){
			foja=indice.fojah;
			numero=indice.numh;
			ano=indice.anoh;
			bis='false';
			nombre=indice.nombre;
		} else if('proh'){
			foja=indice.fojaph;
			numero=indice.numph;
			ano=indice.anoph;
			bis='false';
			nombre=indice.nombre;
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
			return nombre;
		},
		direccion: function(){
			return direccion;
		},
		comuna: function(){
			return comuna;
		},
		registro: function(){
			return registro;
		},
		titulos: function(){
			return '';
		}
		}
		});

	};

	$scope.exportarExcel = function () {

		var foja = $scope.busquedaIndice.foja,
		numero = $scope.busquedaIndice.numero,
		ano = $scope.busquedaIndice.ano,
		bis = $scope.busquedaIndice.bis,
		rut = $scope.busquedaIndice.rut,
		apellidos = $scope.busquedaIndice.nombre,
		direccion = $scope.busquedaIndice.direccion,
		regPropiedades = $scope.busquedaIndice.propiedades,
		regHipoteca = $scope.busquedaIndice.hipoteca,
		regProhibiciones = $scope.busquedaIndice.prohibiciones,
		regComercio = $scope.busquedaIndice.comercio,
		buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
		buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh,
		exacta = $scope.busquedaIndice.exacta;

		if(!buscarPorInscricionHipo && !buscarPorInscricionProh)
			$scope.resetResultadoIndice();

		if($scope.busquedaIndice.comuna!=null)
			var comuna = $scope.busquedaIndice.comuna.codigo;
		if($scope.busquedaIndice.anoInscripcion!=null)
			var anoInscripcion = $scope.busquedaIndice.anoInscripcion.codigo;
		if($scope.busquedaIndice.acto!=null)
			var acto = $scope.busquedaIndice.acto.nombre;
		if($scope.busquedaIndice.tipo!=null)
			var tipo = $scope.busquedaIndice.tipo.nombre;
		if($scope.busquedaIndice.conservador!=null){
			var conservador = $scope.busquedaIndice.conservador.ip;
			$scope.busquedaIndice.conservadorelegido=$scope.busquedaIndice.conservador;
		}else{
			$scope.busquedaIndice.conservadorelegido=null;
		}


		//		$scope.openLoadingModal('Generando Excel...', '');
		//		
		//		$timeout(function(){
		//			$scope.closeModal();
		//		}, 200);

		$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/indice?metodo=exportarExcel&rut='+rut+'&apellidos='+apellidos+'&direccion='+direccion+'&foja='+foja+'&numero='+numero+'&ano='+ano+'&bis='+bis+'&comuna='+comuna+'&anoInscripcion='+anoInscripcion+'&acto='+acto+'&tipo='+tipo+'&exacta='+exacta+'&regPropiedades='+regPropiedades+'&regHipoteca='+regHipoteca+'&regProhibiciones='+regProhibiciones+'&regComercio='+regComercio+'&buscarPorInscricionHipo='+buscarPorInscricionHipo+'&buscarPorInscricionProh='+buscarPorInscricionProh+'&conservador='+conservador;

		//		var promise = indiceService.exportarExcel(rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,acto,tipo,exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador);
		//		promise.then(function(data) {
		//		}, function(reason) {
		//			$scope.closeModal();
		//			$scope.setErr('Problema contactando al servidor.', 'No se ha generado excel.');
		//		});
	};

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

	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacioncom = angular.copy($scope.paginacionMaster);
	$scope.paginacionproh = angular.copy($scope.paginacionMaster);
	$scope.paginacionhipo = angular.copy($scope.paginacionMaster);

	$scope.paginacion.todos=$scope.busquedaIndice.resultados;
	$scope.paginacioncom.todos=$scope.busquedaIndice.resultadoscomercio;
	$scope.paginacionhipo.todos=$scope.busquedaIndice.resultadoshipotecas;
	$scope.paginacionproh.todos=$scope.busquedaIndice.resultadosprohibiciones;


	$scope.makeTodos = function() {

		$scope.paginacion = angular.copy($scope.paginacionMaster);
		$scope.paginacioncom = angular.copy($scope.paginacionMaster);
		$scope.paginacionproh = angular.copy($scope.paginacionMaster);
		$scope.paginacionhipo = angular.copy($scope.paginacionMaster);

		$scope.paginacion.todos=$scope.busquedaIndice.resultados;
		$scope.paginacioncom.todos=$scope.busquedaIndice.resultadoscomercio;
		$scope.paginacionhipo.todos=$scope.busquedaIndice.resultadoshipotecas;
		$scope.paginacionproh.todos=$scope.busquedaIndice.resultadosprohibiciones;

		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;
		var begincom = (($scope.paginacioncom.currentPage - 1) * $scope.paginacioncom.numPerPage)
		, endcom = begincom + $scope.paginacioncom.numPerPage;
		var beginproh = (($scope.paginacionproh.currentPage - 1) * $scope.paginacionproh.numPerPage)
		, endproh = beginproh + $scope.paginacionproh.numPerPage;
		var beginhipo = (($scope.paginacionhipo.currentPage - 1) * $scope.paginacionhipo.numPerPage)
		, endhipo = beginhipo + $scope.paginacionhipo.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		$scope.paginacioncom.filteredTodos = $scope.paginacioncom.todos.slice(begincom, endcom);
		$scope.paginacionproh.filteredTodos = $scope.paginacionproh.todos.slice(beginproh, endproh);
		$scope.paginacionhipo.filteredTodos = $scope.paginacionhipo.todos.slice(beginhipo, endhipo);

		$scope.busquedaIndice.indicebusqueda=true;
	};

	$scope.$watch('paginacion.currentPage + paginacion.numPerPage', function() {
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	});

	$scope.$watch('paginacioncom.currentPage + paginacioncom.numPerPage', function() {
		var begincom = (($scope.paginacioncom.currentPage - 1) * $scope.paginacioncom.numPerPage)
		, endcom = begincom + $scope.paginacioncom.numPerPage;

		$scope.paginacioncom.filteredTodos = $scope.paginacioncom.todos.slice(begincom, endcom);

	});

	$scope.$watch('paginacionproh.currentPage + paginacionproh.numPerPage', function() {
		var beginproh = (($scope.paginacionproh.currentPage - 1) * $scope.paginacionproh.numPerPage)
		, endproh = beginproh + $scope.paginacionproh.numPerPage;

		$scope.paginacionproh.filteredTodos = $scope.paginacionproh.todos.slice(beginproh, endproh);

	});

	$scope.$watch('paginacionhipo.currentPage + paginacionhipo.numPerPage', function() {
		var beginhipo = (($scope.paginacionhipo.currentPage - 1) * $scope.paginacionhipo.numPerPage)
		, endhipo = beginhipo + $scope.paginacionhipo.numPerPage;

		$scope.paginacionhipo.filteredTodos = $scope.paginacionhipo.todos.slice(beginhipo, endhipo);

	});

	$scope.colapsar = function(){

		if($scope.busquedaIndice.indicebusqueda){
			$scope.busquedaIndice.indicebusqueda = false;
		}else{
			$scope.busquedaIndice.indicebusqueda = true;
		}

	};

	$scope.colapsarnuevabusqueda = function(){

		$scope.busquedaIndice.indicebusqueda = false;
		$scope.limpiarTitulo();
		$timeout(function(){
			$scope.doFocus('nombre');
		}, 200);
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

	//	$scope.teclado = function(keyEvent) {
	//		if (keyEvent.which === 13)
	//			alert('Im a lert');
	//	}

	//buscar rut en sesion y buscar datos si existe
	$timeout(function(){
		var promise = indiceService.getRutSesion();
		promise.then(function(data) {      
			if(data.status===null){
			}else if(data.status){
				$scope.datosSesion.rut=RutHelper.format(data.rut);
				$scope.datosSesion.requirente=data.requirente;
			}
		}, function(reason) {
			$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
		});

		$scope.doFocus('nombre');

		$scope.obtenerAtencionPorDia();
	}, 200);
	
	$scope.$on('eventoIndice', function(event, data) {
		$scope.contarAtencion();
    })

});