/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerEscrituraEstudioCtrl', function ($scope, $routeParams, $rootScope, $sce,escrituraService) {
   
  $scope.parametros = {
    caratula: $routeParams.caratula,
    version: $routeParams.version,
    origen: $routeParams.origen
  };
	
  $scope.buscar = function(){
      var caratula = $scope.parametros.caratula;
      var version = $scope.parametros.version;
      var idTipoDocumento = $scope.parametros.idTipoDocumento;
            
	  $scope.urlPDF = $sce.trustAsResourceUrl("../do/service/escritura?metodo=verDocumentoEstudio&caratula="+caratula+"&version="+version+"&idTipoDocumento="+idTipoDocumento+"&type=uri");
	  
  };

  $scope.cerrar = function(){
    $rootScope.go('/'+$scope.parametros.origen);
  };
  
  $scope.buscar();


});