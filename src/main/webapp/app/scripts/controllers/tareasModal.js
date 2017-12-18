'use strict';

app.controller('TareasModalCtrl', function ($log, $scope, $modal, $modalStack, $timeout, $filter, data, operacion, tareaDTO, estadoService) {

	$scope.data = data;
	$scope.operacion = operacion;
	$scope.tarea = {};
	$scope.tarea.tipoTarea = tareaDTO; 
	
	//Obtener lista tipo tareas
    var promise = estadoService.obtenerListaTareas();
    promise.then(function(data) {
      if(data.status==null){
      }else if(data.status){
    	  $scope.listaTipoTareas = data.listaTareas;
		//Sacar de la lista los tipos de tareas a que la caratula ya tiene agregadas	
		var i;
		for(i=0; i<$scope.data.res.caratulaDTO.tareaDTOs.length; i++){
			$scope.listaTipoTareas = $filter('filter')($scope.listaTipoTareas, {id: '!'+$scope.data.res.caratulaDTO.tareaDTOs[i].id});
		}
      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    }); 
                                   
	$scope.cancel = function () {
	    var top = $modalStack.getTop();
	    if (top) {
	      $modalStack.dismiss(top.key);
	    }
  	};
  	
  $scope.agregarTarea = function(){	 
	$scope.openLoadingModal('Agregando tarea...', '');
	  
    var promise = estadoService.agregarTarea($scope.data.req.numeroCaratula, $scope.tarea.tipoTarea.id, $scope.tarea.tipoTarea.descripcion);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status==null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.tareaDTOs.push($scope.tarea.tipoTarea);
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
        $scope.openMensajeModal('success','Tarea agregada exitosamente', '',  false, null);

      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    });    
  };
  
  $scope.eliminarTarea = function(){	
	$scope.openLoadingModal('Eliminando tarea...', '');
    var promise = estadoService.eliminarTarea($scope.data.req.numeroCaratula, $scope.tarea.tipoTarea.id, $scope.tarea.tipoTarea.descripcion);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status==null){

      }else if(data.status){
    	$scope.data.res.caratulaDTO.tareaDTOs = $filter('filter')($scope.data.res.caratulaDTO.tareaDTOs, {id: '!'+$scope.tarea.tipoTarea.id});
    	$scope.data.res.caratulaDTO.bitacoraDTOs.unshift(data.bitacoraDTO);
		$scope.closeModal();
        $scope.openMensajeModal('success','Tarea eliminada exitosamente', '',  false, null);

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
	    $scope.doFocus('tarea');  
  }, 500);   

});