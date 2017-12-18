'use strict';

/*angular.module('aioApp')
  .controller('AnexosCtrl', function ($scope) {
    $scope.awesomeThings = [
      'HTML5 Boilerplate',
      'AngularJS',
      'Karma'
    ];
  });
*/
app.controller('AnexosCtrl', function ($scope, $timeout, localStorageService, $log, anexoService, $modal, $modalStack, $routeParams, anexoModel) {

	//Buscar anexos al inicio
    var promise = anexoService.getAnexos();
    promise.then(function(data) {
      //$scope.closeModal();
      if(data.estado===null){
      }else if(data.estado){
        $scope.data = data;

      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
    });
	    
	    
	$scope.editar = function(anexo){
		$scope.anexoEditar = anexo;
  	};
  
  	$scope.submitted = false;
  
  	$scope.guardarAnexo = function(){
		if ($scope.formEditar.$valid) {
		    var promise = anexoService.guardarAnexo($scope.anexoEditar);
		    promise.then(function(data) {
		      //$scope.closeModal();
		      if(data.status===null){
		      }else {
		    	  $('#edit-anexo').modal('hide');
		    	  if(!data.estado)
			        $scope.raiseErr(data.msg);
		      }
		    }, function(reason) {
		      $scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		    });
			
	    } else {
	      $scope.formEditar.submitted = true;
	    }
  };
  
  $scope.raiseErr = function(msg){
	    $scope.openMensajeModal('error',msg, '',  false, null);
	  };

	  //utils

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
	  
	  $scope.openSolicitar = function () {
	    $scope.$broadcast('sfojaIsFocused');
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

	  //fin utils  
  
});
