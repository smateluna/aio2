/**
 * Created by jaguileram on 25/07/2014.
 */

'use strict';

app.controller('RechazoModalCtrl', function ($scope, $modalInstance, $log, $timeout, $window, dataRechazo, estado, estadoService, $modal, $modalStack) {

  $scope.dataRechazo = dataRechazo;

  $scope.savingRechazo = false;
  $scope.rechazado = false;
  
	//Obtener lista tipo causales
    var promise = estadoService.obtenerCausasRechazo();
    promise.then(function(data) {
      if(data.status==null){
      }else if(data.status){
    	  $scope.causales = data.rechazos;
      }else{
        $scope.raiseErr(data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    }); 

  /*$scope.causales = [{
    codigo: 1,
    descripcion: 'test',
    template: 'ttttt'
  },{
    codigo: 2,
    descripcion: 'test2',
    template: 'ttttt2'
  }];*/


  $scope.rechazo = {
    causal: null,
    detalle: ''
  };

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
    var promise = estadoService.rechazarCaratula($scope.dataRechazo.numeroCaratula, $scope.rechazo.causal.codigo, $scope.rechazo.detalle);
    promise.then(function(data) {
      if(data.status==null){
      }else if(data.status){     	
    	$scope.openMensajeModal('success','Carátula rechazada exitosamente', '',  false, null);
    	$scope.closeModal();
    	estado.buscar();
      }else{
        $scope.raiseErr(data.msg);
        $scope.closeModal();
    	estado.buscar();
      }
    }, function(reason) {
      $scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
    }); 

  };

  $scope.cambioCausal = function(){
    //console.log($scope.rechazo.causal);

    var detalle = $scope.rechazo.detalle===undefined?'':$scope.rechazo.detalle;

    $scope.rechazo.detalle = detalle + $scope.rechazo.causal.template+'\n-------------------------\n';

  };

  $scope.closeRechazo = function () {
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


  $scope.doFocus(true, 'causal');
  
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
  
  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };
  
});