/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerExportarPDFCtrl', function ($scope, $routeParams, $sce, $modalStack,nc) {
   
  $scope.caratula = nc;
	
  $scope.buscar = function(){
      var nc = $scope.caratula;
      
	  $scope.urlPDF = $sce.trustAsResourceUrl("../do/service/estado?metodo=getEstadoReporte&ingresoEgreso=true&tipo=pdf&nc="+nc);

  }

  $scope.cerrar = function(){

	  var top = $modalStack.getTop();
	  if (top) {
		  $modalStack.dismiss(top.key);
	  }

  };

  $scope.buscar();

});