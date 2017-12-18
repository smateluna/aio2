/**
 * Created by rgonzaleza on 25/07/2014.
 */

'use strict';

app.controller('RehacerImagenModalCtrl', function ($scope, $modalInstance, $modalStack, $modal, $log, $timeout, $window, $rootScope, dataImagen, certificacionService) {

  $scope.dataImagen = dataImagen;
  
  $scope.states = {
      isLoading: false,
      isError: false,
      isOk: false,
      title: null,
      msg: null
  };
	
  $scope.rehacerImagen = {
    motivo: ''
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

  $scope.saveRehacerImagen = function () {
	    $scope.openLoadingModal('Enviado a Rehacer...', '');
		var motivo = $scope.rehacerImagen.motivo;
		var promise = certificacionService.rehacerImagen($scope.dataImagen.numeroCaratula, $scope.dataImagen.foja,$scope.dataImagen.numero,$scope.dataImagen.ano,$scope.dataImagen.bis,motivo);
		promise.then(function(data) {
			$scope.closeModal();
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

  $scope.closeRehacerImagen = function () {
    $modalInstance.dismiss('cancel');
    $rootScope.go('/certificacion');
  };

  $scope.close = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.salirSave = function(){
    $timeout(function(){
     $scope.closeRehacerImagen();
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

  $scope.doFocus(true, 'motivo');
});