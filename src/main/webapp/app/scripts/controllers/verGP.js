/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerGPCtrl', function ($scope, $routeParams, $rootScope, $sce, $modalStack, escrituraService,codigoAlphap,origenp) {
   
  $scope.parametros = {
    codigoAlpha: $routeParams.codigoAlpha,
    origen: $routeParams.origen
  };
	
  $scope.buscar = function(){
      var codigoAlpha = $scope.parametros.codigoAlpha;
      if(codigoAlphap!='')
    	  codigoAlpha=codigoAlphap;
      
	  $scope.urlPDF = $sce.trustAsResourceUrl("../do/service/gponline?metodo=verGP&codigoAlpha="+codigoAlpha);
  };

  $scope.cerrar = function(){
	  if(origenp!=''){
		  var top = $modalStack.getTop();
		  if (top) {
			  $modalStack.dismiss(top.key);
		  }
	  } else { 
		  $rootScope.go('/'+$scope.parametros.origen);
	  }
  };

  $scope.buscar();


});