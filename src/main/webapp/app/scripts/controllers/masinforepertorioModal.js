'use strict';

app.controller('masinforepertorioModalCtrl', function ($rootScope, $scope, $modalStack, $modalInstance, $modal, repertorioService, repertorio) {

	$scope.resultadoDetalle = {
		resultados: [],
		data: null
	};
	
	var promise = repertorioService.consultaDetalleRepertorio(repertorio.caratula,repertorio.repertorio,repertorio.anorepertorio);
	promise.then(function(data) {
		if(data.status===null){
		}else if(data.status){
			$scope.resultadoDetalle.resultados = data.resultado;;
			$scope.resultadoDetalle.data = data;
		}else{
			$scope.setErr('data.msg', data.msg);
		}
	}, function(reason) {
		$scope.raiseErr('No se ha podido establecer comunicación con el servidor.');
	});

	$scope.closeModal = function(){
		var top = $modalStack.getTop();
		if (top) {
			$modalStack.dismiss(top.key);
		}
	};

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
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
    }
});