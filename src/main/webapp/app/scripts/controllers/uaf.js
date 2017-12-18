'use strict';
var aux;
app.controller('UAFCtrl', function ($scope, $timeout, $rootScope, $location, $anchorScroll, $filter, $modal, $modalStack, $routeParams, UAFModel, uafService, parametrosService) {
	
	$scope.busquedaUAF = UAFModel.getBusquedaUAF();	
	$scope.tab = UAFModel.getTab();
	
  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
    UAFModel.setTab($scope.tab);

	$timeout(function(){
		$scope.doFocus('inscripciones');
	}, 200);

  };
  //fin controles tabs	
	
	$scope.buscarPersonas = function(){
		$scope.openLoadingModal('Buscando personas...', '');
        
        var promise = uafService.buscarPersonas($scope.busquedaUAF.inscripciones, $scope.busquedaUAF.ano);
        promise.then(function(data) {
        $scope.closeModal();
          if(data.status){        	  
        	  $scope.busquedaUAF.data = data;	      	
	      	  $scope.busquedaUAF.listaPersonasAgrupadas = data.listaPersonasAgrupadas;
	      	  $scope.busquedaUAF.resultadosPersonas = Object.keys(data.listaPersonasAgrupadas).length;	      	  
          } else{
	      	$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	      }
        }, function(reason) {
        });  
        
	};
	
	$scope.buscarBienes = function(){
		$scope.openLoadingModal('Buscando bienes...', '');
        
        var promise = uafService.buscarBienes($scope.busquedaUAF.inscripciones, $scope.busquedaUAF.ano, JSON.stringify($scope.busquedaUAF.naturaleza));
        promise.then(function(data) {
        $scope.closeModal();
          if(data.status){
        	  $scope.busquedaUAF.data = data;	      	
	      	  $scope.busquedaUAF.listaBienesAgrupados = data.listaBienesAgrupados;
	      	  $scope.busquedaUAF.resultadosBienes = Object.keys(data.listaBienesAgrupados).length;	      	  
          } else{
	      	$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	      }
        }, function(reason) {
        });  
        
	};	
	
	$scope.personaEnLista = function(lista, persona){
		
		angular.forEach(lista, function(personaLista){
			if(personaLista.rut == persona.rut)
				return true;
		});
		
		return false;
	};	
	
	$scope.verEstadoCaratula = function(numeroCaratula){
	  var numCaratula = numeroCaratula.indexOf("-")>0?numeroCaratula.substring(0, numeroCaratula.indexOf("-")) : numeroCaratula;
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
	  
	$scope.irGpOnlineAIO = function(borradorFolio){
		var borrador = borradorFolio.substring(0, borradorFolio.indexOf("-"));
		var folio = borradorFolio.substring(borradorFolio.indexOf("-")+1);
		
		$modal.open({
			templateUrl: 'gpOnlineModal.html',
			backdrop: 'static',
			windowClass: 'modal modal-dialog-xl', 
			controller: 'gpOnlineModalCtrl',
			size: 'lg',
			resolve: {  
			borrador : function(){
			return parseInt(borrador);
			},
			folio : function(){
			return parseInt(folio);
			}
		}
		});
	};	  

  if($scope.busquedaUAF.anos==undefined || $scope.busquedaUAF.anos.length==0){
	 var ano = (new Date).getFullYear();
	 for(var i = ano; i >= ano-80; i--){
		$scope.busquedaUAF.anos.push(i);
	 }
  }
  
  if($scope.busquedaUAF.naturalezas==undefined || $scope.busquedaUAF.naturalezas.length==0){
      var promise = parametrosService.getNaturalezas();
      promise.then(function(data) {
        if(data.estado){        	  
        	angular.forEach(data.listaNaturalezas, function(naturaleza) {
        		if(naturaleza.registro==="PR"){
	        		var nat = {"id":naturaleza.codNaturaleza, "label": naturaleza.descNaturaleza}
	        		$scope.busquedaUAF.naturalezas.push(nat);
        		}
        	});
        } else{
	      	//$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
        	console.log(data);
	      }
      }, function(reason) {
      }); 
  }   
  
  	$scope.limpiar = function(){
		$scope.resetResultadoUAF();

	    $scope.busquedaUAF.inscripciones = null;
	    $scope.busquedaUAF.ano = null;
	   
	    $scope.doFocus('inscripciones');
  	};

  	$scope.doFocus = function(name){
	    $scope.$broadcast(name+'IsFocused');
	 };
	 
  	$scope.resetResultadoUAF = function(){
	    $scope.busquedaUAF.data = null;
	
	    if($scope.busquedaUAF.resultados!==undefined){
	      $scope.busquedaUAF.resultados.length = 0;
	    }
	};
	
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
  
});