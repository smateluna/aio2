'use strict';

app.controller('RepertorioCtrl', function ($scope, $timeout, $rootScope, $location, $anchorScroll, $filter, $modal, $modalStack, $routeParams, RepertorioModel,repertorioService) {
	
	$scope.busquedaRepertorio = RepertorioModel.getBusquedaRepertorio();
	$scope.states = RepertorioModel.getStates();

	$scope.solicitudStatus = {
	    ok: false,
	    error : false,
	    warning: false,
	    msgTitle: null,
	    msg: null,
	    tipo: null
  	};
  
	$scope.resolveModal = {
    	refresca: false
  	};
	
	$scope.buscar = function(){
		$scope.buscando=true;
        
		if($scope.busquedaRepertorio.anoRepertorio!=null)
		 	var anio = $scope.busquedaRepertorio.anoRepertorio.codigo;
		
		//Buscar Repertorio
        var promise = repertorioService.getRepertorio($scope.busquedaRepertorio.caratula,$scope.busquedaRepertorio.repertorio,anio);
        promise.then(function(data) {
        	$scope.buscando=false;
          if(data.status){
        	  $scope.busquedaRepertorio.data = data;
	      	  $scope.busquedaRepertorio.resultados = data.resultado;
          } else{
	      	$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	      }
        }, function(reason) {
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
	
  $scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  };
    
  $scope.resetStatus = function(){
    $scope.solicitudStatus.ok = false;
    $scope.solicitudStatus.error = false;
    $scope.solicitudStatus.warning = false;
    $scope.solicitudStatus.msgTitle = null;
    $scope.solicitudStatus.msg = null;
  };
  
  $scope.setErr = function(title, mensaje){
    $scope.solicitudStatus.error = true;
    $scope.setStatusMsg(title, mensaje);
  };
  
  $scope.setStatusMsg = function(title, mensaje){
    $scope.solicitudStatus.msgTitle = title;
    $scope.solicitudStatus.msg = mensaje;
  };

  if($scope.busquedaRepertorio.anosRepertorios==undefined || $scope.busquedaRepertorio.anosRepertorios.length==0){
	 var ano = (new Date).getFullYear();
	 for(var i = ano; i >= ano-80; i--){
		$scope.busquedaRepertorio.anosRepertorios.push({codigo: i, descripcion: i});
	 }
  }
  
  	$scope.limpiar = function(){
		$scope.resetResultadoRepertorio();

	    $scope.busquedaRepertorio.caratula = null;
	    $scope.busquedaRepertorio.repertorio = null;
	    $scope.busquedaRepertorio.anoRepertorio = null;
	
	    $scope.myform.$setPristine(true);
	   
	    $scope.doFocus('caratula');
  	};

  	$scope.doFocus = function(name){
	    $scope.$broadcast(name+'IsFocused');
	 };
	 
  	$scope.resetResultadoRepertorio = function(){
	    $scope.busquedaRepertorio.data = null;
	    $scope.states.buscar.isError = false;
	
	    if($scope.busquedaRepertorio.resultados!==undefined){
	      $scope.busquedaRepertorio.resultados.length = 0;
	    }
	};
	
	$scope.masInfo = function (repertorio) {
	
	    $scope.resolveModal.refresca = false;
	    
	    var myModal = $modal.open({
	      templateUrl: 'masInfoRepertorioModal.html',
	      backdrop: true,
	      windowClass: 'modal',
	      controller: 'masinforepertorioModalCtrl',
	      resolve: {   
	        resolveModal : function(){
	          return $scope.resolveModal;
	        },
	        repertorio: function(){
	        	return repertorio;
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
  
});