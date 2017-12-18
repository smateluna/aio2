'use strict';

app.controller('DistribucionCtrl', function ($scope, $filter, $routeParams, $modalStack, $modal, $timeout, distribucionService) {

  $scope.secciones = [];
  $scope.responsables = [];
  $scope.responsableSel = {};

  $scope.caratulas = [];
  $scope.caratula = {};
  $scope.formulario = {
    numero: '',
    seccion: {}
  };

  $scope.buscarCaratula = function(caratulas){
	  
	if(caratulas!==undefined){
		for(var i in caratulas){
		      $scope.openLoadingModal( 'Buscando...', '');

		      distribucionService.getCaratula(caratulas[i]).then(function(data){
		        $scope.closeModal();

		        if(data.status===undefined){
		          $scope.openMensajeModal('warn', 'Inicie sesión nuevamente.', '');
		        }else if(data.status){
		          $scope.formulario.numero = '';
		          $scope.doFocus('numero');

		          $scope.caratula = data.res.caratulaDTO;
		          $scope.caratulas.push(data.res.caratulaDTO);
		        }else{
		          $scope.doFocus('numero');
		          $scope.openMensajeModal('info', 'No se ha encontrado carátula.', '');
		        }

		      }, function(err){
		        $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		      });
		}
	}else if(!$scope.existe($scope.formulario.numero)){
      $scope.openLoadingModal( 'Buscando...', '');

      distribucionService.getCaratula($scope.formulario.numero).then(function(data){
        $scope.closeModal();

        if(data.status===undefined){
          $scope.openMensajeModal('warn', 'Inicie sesión nuevamente.', '');
        }else if(data.status){
          $scope.formulario.numero = '';
          $scope.doFocus('numero');

          $scope.caratula = data.res.caratulaDTO;
          $scope.caratulas.push(data.res.caratulaDTO);
        }else{
          $scope.doFocus('numero');
          $scope.openMensajeModal('info', 'No se ha encontrado carátula.', '');
        }

      }, function(err){
        $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
      });
    }else{
      $scope.openMensajeModal('info', 'Carátula ya ingresada.', '');
    }
  };

  $scope.existe = function(ncaratula){

    for(var i = 0; i<$scope.caratulas.length; i++){
      if($scope.caratulas[i].datosFormularioDTO.numeroCaratula == ncaratula){
        return true;
      }
    }
    return false;
  };


  $scope.selectCaratula = function(caratula){
    $scope.caratula = caratula;
  };

  $scope.isSelected = function(ncaratula){

    if($scope.caratula.datosFormularioDTO){
      return $scope.caratula.datosFormularioDTO.numeroCaratula == ncaratula;
    }

    return false;
  };

  $scope.eliminar = function(index, caratula){

    if(confirm('Desea eliminar la carátula '+caratula.datosFormularioDTO.numeroCaratula+' ?')){
      var ncaratula = caratula.datosFormularioDTO.numeroCaratula;

      $scope.caratulas.splice(index, 1);

      if($scope.caratula.datosFormularioDTO && $scope.caratula.datosFormularioDTO.numeroCaratula == ncaratula){
        $scope.caratula = {};
      }
    }

  };

  $scope.distribuir = function(){
    $scope.responsableSel = {};

    $scope.openLoadingModal( 'Distribuyendo...', '');

    distribucionService.getResponsables($scope.formulario.seccion.codigo).then(function(data){
      $scope.closeModal();

      if(data.status===undefined){
        $scope.openMensajeModal('warn', 'Inicie sesión nuevamente.', '');
      }else if(data.status){

        $scope.responsables = data.funcionarios;

        if($scope.responsables.length===0){
          $scope.openMensajeModal('info', 'No se ha encontrado responsable para distribuir', '');
        }else if($scope.responsables.length===1){

          $scope.doDistribucion($scope.responsables[0]);

        }else{
          $scope.openDistribucionModal($scope.formulario.seccion, $scope.responsables);
        }
      }else{
        $scope.doFocus('numero');
        $scope.openMensajeModal('info', 'No se han encontrado responsables.', '');
      }

    }, function(err){
      $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };

  //utils
  $scope.openDistribucionModal = function (seccion, responsables) {
    var modalInstance = $modal.open({
      templateUrl: 'distribucionModal.html',
      backdrop: true,
      keyboard: false,
      size: 'sm',
      windowClass: 'modal',
      controller: 'DistribucionModalCtrl',
      resolve: {
        seccion: function () {
          return seccion;
        },
        responsables: function () {
          return responsables;
        }
      }
    });

    modalInstance.result.then(function (responsable) {
      $scope.doDistribucion(responsable);
    }, function () {
      console.log('Modal dismissed at: ' + new Date());
    });


  };


  $scope.doDistribucion = function(responsable){
    var arr = [];

    for(var i = 0; i<$scope.caratulas.length; i++){
      arr.push({numero: $scope.caratulas[i].datosFormularioDTO.numeroCaratula});
    }

    $scope.openLoadingModal( 'Distribuyendo...', '');

    distribucionService.distribuir(arr, $scope.formulario.seccion.codigo, responsable).then(function(data){
      $scope.closeModal();

      if(data.status===undefined){
        $scope.openMensajeModal('warn', 'Inicie sesión nuevamente.', '');
      }else if(data.status){
        $scope.doFocus('numero');
        $scope.caratulas = [];
        $scope.caratula = {};

      }else{
        $scope.openMensajeModal('error', 'Problema detectado', 'Error en servidor.');
      }

    }, function(err){
      $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
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

  $scope.openMensajeModal = function (tipo, titulo, detalle) {
    $scope.closeModal();

    $modal.open({
      templateUrl: 'mensajeModal.html',
      backdrop: true,
      keyboard: true,
      size: 'sm',
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
  };

  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };

  $scope.doFocus = function(name){
    $scope.$broadcast(name+'IsFocused');
  };

  // init en focus
  $timeout(function(){
    $scope.doFocus('numero');
  }, 200);


  distribucionService.getSecciones().then(function(data){

    if(data.status){

      $scope.secciones = data.secciones;

    }else{
      $scope.openMensajeModal('warn', 'No se han encontrado secciones.', '');
    }

  }, function(err){
    $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
  });
  
  $scope.stringIsNumber = function(s) {
	  var x = +s;
	  return x.toString() === s;
	};  
  
  //init
	if($routeParams.caratulas!==undefined){
		var caratulas = JSON.parse($routeParams.caratulas);
	    $timeout(function(){
	        $scope.buscarCaratula(caratulas);
	    }, 500);
	};
  if($routeParams.caratula!==undefined){
	//Si caratula viene en el request, buscar
    if($scope.stringIsNumber($routeParams.caratula) && $routeParams.caratula.length<=10){
    	$scope.formulario.numero = $routeParams.caratula;
      $timeout(function(){
        $scope.buscarCaratula();
      }, 500);
    }
  } else{
  	//Si no, buscar caratula en sesion y buscar datos si existe
//    var promise = distribucionService.getCaratulaSesion();
//    promise.then(function(data) {      
//      if(data.status===null){
//      }else if(data.status){
//    	  $scope.formulario.numero = data.numeroCaratula;
//	
//	      $timeout(function(){
//	        $scope.buscarCaratula();
//	      }, 500);
//      }
//    }, function(reason) {
//      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
//    });
  };  


});
