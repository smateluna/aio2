'use strict';

app.controller('CitadoModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, data, estadoService) {

	$scope.data = data;
	$scope.inscripcion = angular.copy($scope.data.res.caratulaDTO.citadoDTO);
	$scope.listaRegistros = [
	    {id:1, descripcion:'Propiedad'},
	    {id:2, descripcion:'Hipotecas'},
	    {id:3, descripcion:'Prohibiciones'},
	    {id:4, descripcion:'Comercio'},
	    {id:5, descripcion:'Aguas'}
	];
	                                       
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  $scope.guardarDatosCitado = function(){
	$scope.openLoadingModal('Guardando...', '');
	
    var promise = estadoService.cambiarInscripcion($scope.data.req.numeroCaratula, $scope.inscripcion);
    promise.then(function(data) {
    $scope.closeModal();
      if(data.status==null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.citadoDTO = $scope.inscripcion;
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
        $scope.openMensajeModal('success','Datos citado modificados exitosamente', '',  false, null);

      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    });
    
  };
  
  //Util
  
  $scope.raiseErr = function(msg){
    $scope.openMensajeModal('error',msg, '',  false, null);
  };
  
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
  
  $timeout(function(){
	  if($scope.inscripcion.registroDTO==null)
	    $scope.doFocus('registro');
	  else
		$scope.doFocus('foja');  
  }, 500);  

});