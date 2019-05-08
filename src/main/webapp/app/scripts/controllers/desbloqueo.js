'use strict';

app.controller('DesbloqueoCtrl', function ($scope, $rootScope, $timeout, localStorageService, $log, $modal, $modalStack, $routeParams, desbloqueoService) {
    
  	$scope.req = {
    	foja: null,
    	numero: null,
    	ano: null,
    	bis: null
  	};    
  	
  	$scope.buscar = function(){
  		$scope.openLoadingModal('Buscando...', '');
  		
	    var promise = desbloqueoService.buscar($scope.req);
	    promise.then(function(data) {
	    	$scope.closeModal();
	      	if(data.estado===null){
	      	}else if(data.estado){
	        	$scope.data = data;

	      	}else{
	        	$scope.raiseErr(data.msg);
	        	$scope.limpiar();
	      	}
	    }, function(reason) {
	      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	    });
  	};
  	
  	$scope.desbloquear = function(inscripcion){
  		$scope.openLoadingModal('Desbloqueando...', '');

	    var promise = desbloqueoService.desbloquear(inscripcion.idSolicitud, JSON.stringify($scope.data.idSolicitudes)) ;
	    promise.then(function(data) {
	    	$scope.closeModal();
	      	if(data.estado===null){
	      	}else if(data.estado){
//	      		$scope.data.listaSolicitudes = data.listaSolicitudes;
	      		$scope.buscar();
	      	}else{
	        	$scope.raiseErr(data.msg);
	      	}
	    }, function(reason) {
	      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	    });
  	};  	
  	
    $scope.limpiar = function(){
        $scope.reset();
        $scope.req.foja = null;
        $scope.req.numero = null;
        $scope.req.ano = null;
        $scope.req.bis = null;
        $scope.doFocus('foja')
    };
      
    $scope.reset = function(){
        $scope.data = {};
    };
  	
    //UTILES  
    $scope.doFocus = function(name){
      $scope.$broadcast(name+'IsFocused');
    };
    
    $scope.raiseErr = function(msg){
      $scope.openMensajeModal('error',msg, '',  false, null);
    };
    
    $scope.raiseSuccess = function(msg){
      $scope.openMensajeModal('success',msg, '',  false, null);
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


    $scope.openRechazoModal = function () {
      var dataRechazo = {
        numeroCaratula : $scope.req.numeroCaratula
      };

      $modal.open({
        templateUrl: 'rechazoModal.html',
        backdrop: 'static',
        keyboard: false,
        //size: 'sm',
        windowClass: 'modal',
        controller: 'RechazoModalCtrl',
        resolve: {
          dataRechazo: function(){
            return dataRechazo;
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
    
    $scope.stringIsNumber = function(s) {
      var x = +s;
      return x.toString() === s;
    };
    
     //init
    if($routeParams.foja!==undefined && $routeParams.numero!==undefined && $routeParams.ano!==undefined){
  	//Si parametros vienen en el request, buscar
      if($scope.stringIsNumber($routeParams.foja) && $scope.stringIsNumber($routeParams.numero) && $scope.stringIsNumber($routeParams.ano) ){

        $scope.req.foja = $routeParams.foja;
        $scope.req.numero = $routeParams.numero;
        $scope.req.ano = $routeParams.ano;

        $timeout(function(){console.log("buscar por parametros");
          $scope.buscar();
        }, 500);
      }
    } /*else{
    	//Si no, buscar parametros en sesion y buscar datos si existe
      var promise = desbloqueoService.getInscripcionSesion();
      promise.then(function(data) {      
        if(data.status===null){
        }else if(data.status){
            $scope.req.foja = data.foja;
            $scope.req.numero = data.numero;
            $scope.req.ano = data.ano;
            $scope.req.bis = data.bis;
  	
  	      $timeout(function(){console.log("buscar por sesion");
  	        $scope.buscar();
  	      }, 500);
        }
      }, function(reason) {
        $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
      });
    };  */	
  	
    $timeout(function(){
    	$scope.doFocus('foja');  
	}, 500);      
  	
  });

