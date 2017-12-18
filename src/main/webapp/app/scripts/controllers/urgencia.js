'use strict';

app.controller('UrgenciaCtrl', function ($scope,$timeout,urgenciaModel,solicitudService,$filter,$modal,$modalStack,$rootScope) {
	
	$scope.busquedaTitulo = urgenciaModel.getBusquedaTitulo();
	$scope.states = urgenciaModel.getStates();

    $scope.buscar = function(){
    	 $scope.resetResultadoTitulo();
    		
    	 var foja = $scope.busquedaTitulo.foja,
	     numero = $scope.busquedaTitulo.numero,
	     ano = $scope.busquedaTitulo.ano,
	     bis = $scope.busquedaTitulo.bis

	    $scope.openLoadingModal('Buscando...', '');
	
	    var promise = solicitudService.getByFojaNumeroAno(foja,numero,ano,bis);
	    promise.then(function(data) {
	      $scope.closeModal();
	      if(data.status===null){
	
	      }else if(data.status){
	        $scope.busquedaTitulo.fecha = new Date();
	
	        $scope.busquedaTitulo.data = data;
	        $scope.busquedaTitulo.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', false);
	
	      }else{
	        $scope.raiseErr('buscar','Problema detectado', data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	    });
	};  
	
	$scope.darUrgencia = function(){
    	 var foja = $scope.busquedaTitulo.foja,
	     numero = $scope.busquedaTitulo.numero,
	     ano = $scope.busquedaTitulo.ano,
	     bis = $scope.busquedaTitulo.bis

	    $scope.openLoadingModal('Urgencia, en proceso...', '');
	
	    var promise = solicitudService.urgenciaSolicitud(foja,numero,ano,bis);
	    promise.then(function(data) {
	      $scope.closeModal();
	      if(data.status===null){
	
	      }else if(data.status){
	        $scope.openMensajeModal('success', 'Urgencia Solicitud', 'Se ha incorporado urgencia a la solicitud.',true,1);
			
	        $scope.resetResultadoTitulo();
	      }else{
	        $scope.raiseErr('buscar','Problema detectado', data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	    });
	};
	
	$scope.limpiarTitulo = function(){
		$scope.resetResultadoTitulo();

	    $scope.busquedaTitulo.foja = null;
	    $scope.busquedaTitulo.numero = null;
	    $scope.busquedaTitulo.ano = null;
	    $scope.busquedaTitulo.bis = false;
	
	    $scope.myform.$setPristine(true);
	
	    $scope.doFocus('foja');
  	};
  	
  	$scope.resetResultadoTitulo = function(){
	    $scope.busquedaTitulo.data = null;
	
	    if($scope.busquedaTitulo.resultados!==undefined){
	      $scope.busquedaTitulo.resultados.length = 0;
	    }
	};

  	
	//validaciones titulo
	$scope.archivoNacional = function(value){
	  return !(value<$rootScope.aioParametros.anoArchivoNacional);
	};
	
	$scope.anoActual = function(value){
	  return !(moment(new Date()).year()<value);
	};
	//fin validaciones titulo
	

	//utiles	
	 $scope.doFocus = function(name){
	    $scope.$broadcast(name+'IsFocused');
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
  
   $scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  };
});
