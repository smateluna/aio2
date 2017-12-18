'use strict';

app.controller('AuditoriaCtrl', function ($rootScope, $scope, $timeout, $location, $filter, $modal, $modalStack, $routeParams, gponlineModel, gponlineService) {
  
  $scope.fechahoy = new Date().toJSON().split('T')[0];	
  
  //modelos
  $scope.tab = gponlineModel.getTab();
  $scope.mostrar = gponlineModel.getMostrar();
  $scope.busquedaGponline = gponlineModel.getBusquedaGponline();
  $scope.busquedaCaratula = gponlineModel.getBusquedaCaratula();
  $scope.busquedaInscripcion = gponlineModel.getBusquedaInscripcion();
  $scope.busquedaPlanos = gponlineModel.getBusquedaPlanos();
  $scope.states = gponlineModel.getStates();
  //fin modelos

  $scope.req = {
       simpleMode: false
  };
  
  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
    gponlineModel.setTab($scope.tab);

    if($scope.tab.parentActive===1){
    	$timeout(function(){
    		$scope.doFocus('borrador');
    	}, 200);
    }else if($scope.tab.parentActive===2){
    	$timeout(function(){
    		$scope.doFocus('caratula');
    	}, 200);
    }else if($scope.tab.parentActive===3){
    	$timeout(function(){
    		$scope.doFocus('foja');
    	}, 200);
    }
  };
  //fin controles tabs

  $scope.buscarFolio = function(){

	  var promise = gponlineService.buscarFolios($scope.busquedaGponline.borrador);
	  promise.then(function(data) {
		  $scope.closeModal();
		  if(data.status===null){
			  
		  }else if(data.status){
			  if(data.resultado.length==1){
				  $scope.busquedaGponline.folio=data.resultado[0].folio;
				  $scope.buscarPorBorrador();
			  }else{
				  //levantar modal con lista folios	
				  $scope.busquedaGponline.folios=data.resultado;
				  $scope.openfolioModal();
			  }
		  }else{
			  $scope.raiseErr('borrador','Problema detectado', data.msg);
		  }
	  }, function(reason) {
		  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	  });

  };
  
  $scope.buscarPorBorrador = function(){

	  var borrador = $scope.busquedaGponline.borrador,
	  folio = $scope.busquedaGponline.folio,
	  fechaHoy = $scope.busquedaGponline.fechaHoy,
	  ano = $scope.busquedaGponline.anos;

	  $scope.resetResultadoBorrador();

	  $scope.states.borrador.isLoading = true;

	  $scope.openLoadingModal( 'Realizando Busqueda...', '');

	  if(folio==='' || folio==null){
		  $scope.buscarFolio();
	  }else{
		  var promise = gponlineService.buscarBorrador(borrador, folio, fechaHoy, ano);
		  promise.then(function(data) {
			  $scope.closeModal();
			  if(data.status===null){

			  }else if(data.status){
  				  $scope.busquedaGponline.duenyos = data.duenyos;
				  $scope.busquedaGponline.datosPropiedad = data.datosPropiedad;
				  $scope.busquedaGponline.hipoteca = data.hipotecasVigentes;
				  $scope.busquedaGponline.prohibicion = data.prohibicionesVigentes;
				  $scope.busquedaGponline.roles = data.listaRoles;
				  $scope.busquedaGponline.fechaUltimoGP = data.fechaUltimoGP;
				  if(data.gp.length>0)
				  	$scope.busquedaGponline.codigoAlpha = data.gp[0].codigoAlpha;
				  
				  $scope.busquedaGponline.gp = data.gp;
				  $scope.busquedaGponline.data = data;
				  $scope.busquedaGponline.eventos = data.eventos;
				  $scope.busquedaGponline.numeroEventos = data.numeroEventos; 
				  $scope.busquedaGponline.tieneNoVigenteProp=data.tieneNoVigenteProp;
				  $scope.busquedaGponline.tieneNoVigenteProh=data.tieneNoVigenteProh;
				  $scope.busquedaGponline.tieneNoVigenteHipo=data.tieneNoVigenteHipo;
			  }else{
				  $scope.raiseErr('borrador','Problema detectado', data.msg);
			  }
		  }, function(reason) {
			  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		  });
	  }

  };
  
  $scope.buscarCaratula = function(){
	  var caratula = $scope.busquedaCaratula.caratula;

	  $scope.resetResultadoBorrador();

	  $scope.states.borrador.isLoading = true;

	  $scope.openLoadingModal( 'Realizando Busqueda...', '');

	  var promise = gponlineService.buscarBorradorPorCaratula(caratula);
	  promise.then(function(data) {
		  $scope.closeModal();
		  if(data.status===null){

		  }else if(data.status){
			  if(data.listaBorradores.length==1){
				  $scope.busquedaGponline.borrador = data.listaBorradores[0].borrador;
				  $scope.busquedaGponline.folio = data.listaBorradores[0].folio;
				  $scope.buscarPorBorrador();
			  } else {
				//levantar modal con lista folios	
				$scope.busquedaGponline.folios=data.listaBorradores;
				$scope.openfolioModal();
			  }
		  }else{
			  $scope.raiseErr('borrador','Problema detectado', data.msg);
		  }
	  }, function(reason) {
		  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	  });

  };
  
  $scope.buscarBorradorPorInscripcion = function(){
	  var foja = $scope.busquedaInscripcion.fojas,
	  numero = $scope.busquedaInscripcion.numero,
	  ano = $scope.busquedaInscripcion.ano;

	  $scope.resetResultadoBorrador();

	  $scope.states.borrador.isLoading = true;

	  $scope.openLoadingModal( 'Realizando Busqueda...', '');

	  var promise = gponlineService.buscarBorradorPorInscripcion(foja,numero,ano);
	  promise.then(function(data) {
		  $scope.closeModal();
		  if(data.status===null){

		  }else if(data.status){
			  if(data.listaBorradores.length==1){
				  $scope.busquedaGponline.borrador = data.listaBorradores[0].borrador;
				  $scope.busquedaGponline.folio = data.listaBorradores[0].folio;
				  $scope.buscarPorBorrador();
			  } else {
				//levantar modal con lista folios	
				$scope.busquedaGponline.folios=data.listaBorradores;
				$scope.openfolioModal();
			  }
		  }else{
			  $scope.raiseErr('borrador','Problema detectado', data.msg);
		  }
	  }, function(reason) {
		  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	  });

  };

  $scope.buscarPlanos = function(){
	  $scope.busquedaGponline.urls='';
	  
	  var res = "\[";
	  for (var i = 0; i < $scope.busquedaGponline.duenyos.length; i++) {
		  res += "\{\"fojasProp\":\""+$scope.busquedaGponline.duenyos[i].fojas+"\", \"numeroProp\":\""+$scope.busquedaGponline.duenyos[i].numero+"\", \"anyoProp\":\""+$scope.busquedaGponline.duenyos[i].anyo+"\", \"bisProp\":\""+$scope.busquedaGponline.duenyos[i].bis+"\"\}";
	  }	
	  res+="\]";
	  $scope.busquedaGponline.res=res;

	  var promise = gponlineService.buscarPlano($scope.busquedaGponline.res);
	  promise.then(function(data) {

		  if(data.status===null){

		  }else if(data.status){
			  if(data.tienePlanos){
				  $scope.busquedaGponline.urls=data.urls;
			  }
			  $scope.busquedaPlanos.tienePlanos=data.tienePlanos;
			  $scope.busquedaPlanos.data = data;
		  }else{
			  $scope.raiseErr('borrador','Problema detectado', data.msg);
		  }
	  }, function(reason) {
		  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	  });
  };
  
  $scope.buscarSegunFolio = function(titulo){
	    $scope.busquedaGponline.borrador=titulo.borrador;
  		$scope.busquedaGponline.folio=titulo.folio;
		$scope.buscarPorBorrador();
		$scope.cancel();
  }
    
  $scope.verTitulo = function(titulo){

	  var bis = titulo.bis==1?true:false,
	                         //realizar alternativa para saber tipo de documento;                       
	                         estado = '0';

	  $rootScope.go('/verInscripcion/prop/'+titulo.fojas+'/'+titulo.numero+'/'+titulo.anyo+'/'+bis+'/gponline/'+estado);

  };
  
  $scope.resetResultadoBorrador = function(){
	  $scope.busquedaGponline.data = null;
	  $scope.busquedaGponline.datosPropiedad = null;

	  if($scope.busquedaGponline.duenyos!==undefined){
		  $scope.busquedaGponline.duenyos.length = 0;
	  }
	  if($scope.busquedaGponline.hipoteca!==undefined){
		  $scope.busquedaGponline.hipoteca.length = 0;
	  }
	  if($scope.busquedaGponline.prohibicion!==undefined){
		  $scope.busquedaGponline.prohibicion.length = 0;
	  }
	  if($scope.busquedaGponline.roles!==undefined){
		  $scope.busquedaGponline.roles.length = 0;
	  }
	  $scope.busquedaGponline.urls='';
	  $scope.busquedaPlanos.data = null;
	  $scope.busquedaPlanos.tienePlanos = null;
	  
	  $scope.mostrar.mostrarInactivosProp=false;
	  $scope.mostrar.mostrarInactivosProh=false;
	  $scope.mostrar.mostrarInactivosHipo=false;
	  
	  $scope.cleanErr('borrador');
  };
  
  $scope.limpiarBorrador = function(){
    $scope.resetResultadoBorrador();

    $scope.busquedaGponline.borrador = null;
    $scope.busquedaGponline.folio = '';
    $scope.busquedaGponline.fecha = null;
    $scope.busquedaGponline.anos = null;

    $scope.myform.$setPristine(true);

    $scope.doFocus('borrador');
  };
  
  $scope.limpiarCaratula = function(){
    $scope.resetResultadoBorrador();

    $scope.busquedaCaratula.caratula = null;

    $scope.myform.$setPristine(true);

    $scope.doFocus('caratula');
  };
  
  $scope.limpiarInscripcion = function(){
    $scope.resetResultadoBorrador();

    $scope.busquedaInscripcion.fojas = null;
    $scope.busquedaInscripcion.numero = null;
    $scope.busquedaInscripcion.ano = null;

    $scope.myform.$setPristine(true);

    $scope.doFocus('foja');
  };

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
  
  $scope.openfolioModal = function () {
    $modal.open({
      templateUrl: 'foliosModal.html',
      backdrop: 'static',
      keyboard: false,
      size: 'sm',
      windowClass: 'modal',
      controller: 'GponlineCtrl',
      resolve: {
        busquedaGponline: function () {
          return $scope.busquedaGponline;
        }
      }
    });
  };
  
  $scope.openGpCertfEmitModal = function () {
    $modal.open({
      templateUrl: 'gpCertificadoEmitidoModal.html',
      backdrop: 'static',
      keyboard: false,
      size: 'lg',
      windowClass: 'modal',
      controller: 'GponlineCtrl',
      resolve: {
        busquedaGponline: function () {
          return $scope.busquedaGponline;
        }
      }
    });
  };
  
  $scope.openEventosModal = function () {
    $modal.open({
      templateUrl: 'gpEventosModal.html',
      backdrop: 'static',
      keyboard: false,
      size: 'lg',
      windowClass: 'modal',
      controller: 'GponlineCtrl',
      resolve: {
        busquedaGponline: function () {
          return $scope.busquedaGponline;
        }
      }
    });
  };
  
  $scope.openCaratulasModal = function () {
    $modal.open({
      templateUrl: 'gpCaratulasModal.html',
      backdrop: 'static',
      keyboard: false,
      size: 'lg',
      windowClass: 'modal',
      controller: 'GponlineCtrl',
      resolve: {
        busquedaGponline: function () {
          return $scope.busquedaGponline;
        }
      }
    });
  };
  
  $scope.verEstadoCaratula = function(numeroCaratula){
	  $modal.open({
		  templateUrl: 'estadoIndiceModal.html',
		  backdrop: 'static',
		  windowClass: 'modal modal-dialog-xl', 
		  controller: 'EstadoIndiceModalCtrl',
		  size: 'lg',
		  resolve: {  
		  numeroCaratula : function(){
		  return parseInt(numeroCaratula);
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

  //validaciones anos
  $scope.anos = function(value){
    //TODO parametrizar año
    return !(value>80);
  };
  //fin validaciones anos

  $timeout(function(){
    if($scope.tab.parentActive===1){
        $scope.doFocus('borrador');
    }else if($scope.tab.parentActive===2){
      $scope.doFocus('caratula');
    }else if($scope.tab.parentActive===3){
      $scope.doFocus('foja');
    }
  }, 200);
  
  //validaciones titulo
  $scope.archivoNacional = function(value){
	  return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
	  return !(moment(new Date()).year()<value);
  };
  
  $scope.cancel = function(){
	  var top = $modalStack.getTop();
	  if (top) {
		  $modalStack.dismiss(top.key);
	  }
  };
  
  $scope.vertodoProp = function(){
	  	if($scope.mostrar.mostrarInactivosProp)
	  		$scope.mostrar.mostrarInactivosProp=false;
	  	else
	  		$scope.mostrar.mostrarInactivosProp=true;
  }
  
  $scope.vertodoProh = function(){
	  	if($scope.mostrar.mostrarInactivosProh)
	  		$scope.mostrar.mostrarInactivosProh=false;
	  	else
	  		$scope.mostrar.mostrarInactivosProh=true;
  }
  
  $scope.vertodoHipo = function(){
	  	if($scope.mostrar.mostrarInactivosHipo)
	  		$scope.mostrar.mostrarInactivosHipo=false;
	  	else
	  		$scope.mostrar.mostrarInactivosHipo=true;
  }
  
  $scope.verGP = function(){
	  	var codigoAlpha=$scope.busquedaGponline.codigoAlpha;
		$rootScope.go('/verGP/'+codigoAlpha+'/gponline');
  };
  
  $scope.verGPModal = function (gp) {
    $modal.open({
      templateUrl: 'views/verGP.html',
      backdrop: 'static',
      keyboard: false,
      size: 'lg',
      windowClass: 'modal modal-dialog-xl',
      controller: 'VerGPCtrl',
      resolve: {
        codigoAlphap: function () {
          return gp.codigoAlpha;
        },
        origenp: function () {
          return 'gponline';
        }
      }
    });
  };
  
  $scope.buscarCaratulas = function () {

//	  $scope.openLoadingModal( 'Buscando Caratulas...', '');

	  var res = "\[";
	  angular.forEach($scope.busquedaGponline.duenyos, function(value, key) {
		  console.log(value.fojas);
		  res += "\{\"fojasProp\":\""+value.fojas+"\", \"numeroProp\":\""+value.numero+"\", \"anyoProp\":\""+value.anyo+"\", \"bisProp\":\""+value.bis+"\"\}";
	  });
	  res+="\]";
	  
	  var promise = gponlineService.buscarCaratulas($scope.busquedaGponline.borrador,$scope.busquedaGponline.folio,res);
	  promise.then(function(data) {
//		  $scope.closeModal();
		  if(data.status===null){
		  }else if(data.status){
			  $scope.busquedaGponline.listaCaratulaPorPropiedad = data.caratulasPorPropiedad; 
			  $scope.openCaratulasModal();	  	
		  }else{
			  $scope.openCaratulasModal();
//			  $scope.raiseErr('borrador','Problema detectado', data.msg);
		  }
	  }, function(reason) {
		  $scope.raiseErr('borrador','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	  });
  };
  
  $scope.verTipoImagenHipo = function(titulo){
	 
	  var bis = titulo.bis==1?true:false;
	  $rootScope.go('/verInscripcionReg/hip/'+titulo.fojas+'/'+titulo.numero+'/'+titulo.anyo+'/'+bis+'/gponline');
  };

  $scope.verTipoImagenProh = function(titulo){

	  var bis = titulo.bis==1?true:false;
	  $rootScope.go('/verInscripcionReg/proh/'+titulo.fojas+'/'+titulo.numero+'/'+titulo.anyo+'/'+bis+'/gponline');
  };
  
  $scope.stringIsNumber = function(s) {
	  var x = +s;
	  return x.toString() === s;
  };
  
  //fin utils
  $timeout(function(){
	  //init
	  if($routeParams.borrador!==undefined){
		  if($scope.stringIsNumber($routeParams.borrador)){

			  $scope.busquedaGponline.borrador = $routeParams.borrador;
			  $scope.busquedaGponline.folio = $routeParams.folio;

			  $timeout(function(){
				  $scope.buscarPorBorrador();
			  }, 500);
		  }
	  }

	  $scope.doFocus('borrador');
  }, 500);
  
});