'use strict';

app.controller('DatosFormularioModalCtrl', function ($log, $rootScope, $scope, $modal, $modalInstance, $modalStack, $window, $timeout, data, estadoService) {
	
	$scope.data = data;
	$scope.myv = {};
	
	//Lista de estados para elegir
	$scope.listaEstados = [
	    {id:'p', descripcion:'En Proceso'},
	    {id:'d', descripcion:'Despachada'},
	    {id:'w', descripcion:'Auditoría'},
	    {id:'d', descripcion:'Aprobada'} //Estado Despachado + seccion Aprobada
	];
	
	//Estado original
	switch( $scope.data.res.caratulaDTO.datosFormularioDTO.estado ){
		case 'p' : $scope.myv.estadoCaratula = {id:'p', descripcion:'En Proceso'}; break;
		case 'd' : $scope.myv.estadoCaratula = {id:'d', descripcion:'Despachada'}; break;
		case 'w' : $scope.myv.estadoCaratula = {id:'w', descripcion:'Auditoria'}; break;
	}
	
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  $scope.guardarEstadoFormulario = function(){
	$scope.openLoadingModal('Cambiando estado...', '');
    var promise = estadoService.cambiarEstado($scope.data.req.numeroCaratula, $scope.myv.estadoCaratula.id, $scope.myv.estadoCaratula.descripcion);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status===null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.datosFormularioDTO.estado = $scope.myv.estadoCaratula.id;
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
		
		//Si fue un cambio a Aprobada, refrescar cambio de estado 
		if($scope.myv.estadoCaratula.descripcion == "Aprobada"){
			$scope.refrescarEstados();
		}
		
        $scope.openMensajeModal('success','Estado carátula modificado exitosamente', '',  false, null);

      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    });
    
  };
  
  $scope.refrescarEstados = function(){
	    var promise = estadoService.getEstado($scope.data.res.caratulaDTO.datosFormularioDTO.numeroCaratula);
	    promise.then(function(data) {
	      if(data.status===null){
	      }else if(data.status){
	    	  $scope.data.res.caratulaDTO.movimientoDTOs = data.res.caratulaDTO.movimientoDTOs;
	    	  $scope.data.res.caratulaDTO.estadoActualDTO = data.res.caratulaDTO.estadoActualDTO;
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
	    $scope.doFocus('estado');  
}, 500); 

});