/**
 * Created by jaguileram on 25/07/2014.
 */

'use strict';

app.controller('RechazoCertificacionModalCtrl', function ($scope, $route, $location, $modalInstance, $log, $timeout, $window, $rootScope, dataRechazo, caratulaService,origen) {

  $scope.dataRechazo = dataRechazo;
  $scope.origen = origen;

  $scope.savingRechazo = false;
  $scope.rechazado = false;
  
  $scope.states = {
      isLoading: false,
      isError: false,
      isOk: false,
      title: null,
      msg: null
  };
	
  $scope.rechazo = {
    causal: null,
    detalle: ''
  };

  var promise = caratulaService.obtenerCausalesRechazo();
	promise.then(function(data) {
	    if(data.status===null){
	    }else if(data.status){
	      $scope.causales = data.listacausales;
	    }else{
	      $scope.raiseErr('Problema detectado', data.msg);
	    }
	  }, function(reason) {
	    $scope.raiseErr('Problema contactando al servidor.', 'Problema contactando al servidor.');
  });
  
  $scope.doFocus = function(conT, campo){

    if(conT){
      $timeout(function(){
        $scope.$broadcast(campo+'IsFocused');
      },100);
    }else{
      $scope.$broadcast(campo+'IsFocused');
    }
  };

  $scope.saveRechazo = function () {
	  	$scope.rechazado = true;
		var codigo = $scope.rechazo.causal.codigo,
		descripcion = $scope.rechazo.detalle;
		var promise = caratulaService.rechazarCaratula($scope.dataRechazo.numeroCaratula,codigo,descripcion);
		promise.then(function(data) {
		    if(data.status===null){
		    }else if(data.status){		
		      $scope.raiseOk('', data.msg);
		      $scope.salirSave();
		    }else{
		      $scope.raiseErr('Problema detectado', data.msg); 
		    }
		  }, function(reason) {
		    $scope.raiseErr('Problema contactando al servidor', 'Problema contactando al servidor.');
	   });
  };

  $scope.cambioCausal = function(){
    //console.log($scope.rechazo.causal);

    var detalle = $scope.rechazo.detalle===undefined?'':$scope.rechazo.detalle;

    $scope.rechazo.detalle = detalle + $scope.rechazo.causal.template+'\n-------------------------\n';

  };

  $scope.closeRechazo = function () {
    $modalInstance.dismiss('cancel');
    if($location.path()!='/'+$scope.origen)
    	$rootScope.go('/'+$scope.origen);
    else
    	$route.reload()
  };
  
  $scope.close = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.agregaRechazo = function(){
		
  };


  $scope.salirSave = function(){
    $timeout(function(){
      $scope.savingRechazo = false;
      $scope.closeRechazo();
    },2000);
  };
  
  $scope.raiseErr = function(title, msg){
    $scope.states.isError = true;
    $scope.states.title = title;
    $scope.states.msg = msg;
  };
  
  $scope.raiseOk = function(title, msg){
    $scope.states.isOk = true;
    $scope.states.title = title;
    $scope.states.msg = msg;
  };

  $scope.doFocus(true, 'causal');
});