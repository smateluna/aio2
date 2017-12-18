/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerEscrituraCtrl', function ($scope, $routeParams, $rootScope, $sce,escrituraService) {
   
  $scope.parametros = {
    caratula: $routeParams.caratula,
    origen: $routeParams.origen
  };
	
  $scope.buscar = function(){
      var caratula = $scope.parametros.caratula;
            
	  $scope.urlPDF = $sce.trustAsResourceUrl("../do/service/escritura?metodo=verDocumento&caratula="+caratula+"&type=uri");
	  
  };

  $scope.cerrar = function(){
    $rootScope.go('/'+$scope.parametros.origen);
  };
  
  $scope.buscar();


});