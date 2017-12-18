'use strict';

app.controller('masInfoLiquidacionModalCtrl', function ($rootScope, $scope,$route, $timeout, $modalStack, $modalInstance, $modal, tareasService, caratula, estadoService, $window) {		
	
	$scope.nuevoDocumento = {
			valorDocumento: null,
			tipoDocumentoDTO: {
				codigo: -1,
				descripcion: ''
			}
	};
	
	  $timeout(function(){
		  $scope.openLoadingModal('Obteniendo liquidación de carátula ' + caratula.numeroCaratula, '');	  
	  }, 500);
	
	var promise = tareasService.getCaratulaLiquidacion(caratula.numeroCaratula);
	promise.then(function(data) {
		$scope.closeModal();
		if(data.estado===null){
	  	}else if(data.status){
	  		$scope.liquidacionCaratula = data.liquidacionCaratula;
	  		$scope.liquidacionCaratula.caratulaDTO.valorReal = $scope.getTotalPapeles();
	  		$scope.liquidacionCaratula.caratulaDTO.diferencia = $scope.liquidacionCaratula.caratulaDTO.valorReal - $scope.liquidacionCaratula.caratulaDTO.valorPagado;
	  	}else{
	    	$scope.raiseErr(data.msg);
	  	}
	}, function(reason) {
		$scope.closeModal();
		$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
	});	
	
	$scope.downloadFirma = function(documentoEntrega) {console.log(documentoEntrega);
		var documento ={
			"nombreArchivo": documentoEntrega.nombreArchivoVersion,
			"fechaDocumento": documentoEntrega.fechaPdf,
			"rutFirmador": documentoEntrega.usuario
		};
		
		//existe documento
		var promise = estadoService
		.existeFirma(documento);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=downloadFirma&documento='+ JSON.stringify(documento);
				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});		

	};	

	$scope.downloadBoleta = function(numero) {
		var documento ={
			"nombreArchivo": numero+".pdf",
			"idTipoDocumento": 12, //idTipoDocumento BOLETA
			"idReg": 0
		};
		
		//existe documento
		var promise = estadoService
		.existeDocumento(documento);
		promise
		.then(
			function(data) {
				if (data.hayDocumento) {
					$window.location.href = $window.location.protocol+'//'+$window.location.host+'/aio/do/service/estado?metodo=downloadDocumento&documento='+ JSON.stringify(documento);
				} else {
					$scope
					.raiseErr(data.errormsg);
				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});		

	};
	
	$scope.aprobarCaratula = function(){
		var promise = tareasService.aprobarCaratula($scope.liquidacionCaratula.caratulaDTO.numeroCaratula, JSON.stringify($scope.liquidacionCaratula.papeles), $scope.getTotalPapeles());
		promise.then(function(data) {
			
			if(data.estado===null){
		  	}else if(data.status){
		  		$scope.openMensajeModal('success','Carátula liquidada exitosamente', '',  false, null);
		  	    $timeout(function(){
		  	    	$scope.closeModal();
		  	    	$route.reload();	  
			    }, 2000);		  		
		  	}else{
		    	$scope.raiseErr(data.msg);
		  	}
		}, function(reason) {
			$scope.closeModal();
			$scope.raiseErr('No se ha podido establecer comunicacion con el servidor.');
		});	
	};
	
	$scope.getTotalPapeles = function(){
	    var total = 0;
	    for(var i = 0; i < $scope.liquidacionCaratula.papeles.length; i++){
	    	var papel =$scope.liquidacionCaratula.papeles[i];
	    	if(papel!=null && papel.valorDocumento!=null){	    		
	        	total += parseInt(papel.valorDocumento);
	    	}
	    }
	    return total;		
		
	};
	
	$scope.agregarDocumento = function(){
		$scope.liquidacionCaratula.papeles.push(angular.copy($scope.nuevoDocumento));
		$scope.liquidacionCaratula.caratulaDTO.valorReal = $scope.getTotalPapeles();
		$scope.liquidacionCaratula.caratulaDTO.diferencia = $scope.liquidacionCaratula.caratulaDTO.valorReal - $scope.liquidacionCaratula.caratulaDTO.valorPagado;
		$scope.resetNuevoDocumento();
		$scope.doFocus('descripcion');

	};
	
	$scope.eliminarDocumento = function(papel){
		$scope.liquidacionCaratula.papeles.splice($scope.liquidacionCaratula.papeles.indexOf(papel), 1);
		$scope.liquidacionCaratula.caratulaDTO.valorReal = $scope.getTotalPapeles();
		$scope.liquidacionCaratula.caratulaDTO.diferencia = $scope.liquidacionCaratula.caratulaDTO.valorReal - $scope.liquidacionCaratula.caratulaDTO.valorPagado;
	};	
	
	$scope.resetNuevoDocumento = function(){
		$scope.nuevoDocumento = {
				valorDocumento: null,
				tipoDocumentoDTO: {
					codigo: -1,
					descripcion: ''
				}
		};
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
	  
	$scope.verTransaccion = function (caratulaDTO) {
	    
	    var myModal = $modal.open({
	      templateUrl: 'transaccionModal.html',
	      backdrop: true,
	      windowClass: 'modal',
	      controller: 'transaccionModalCtrl',
	      resolve: { 
	    	  caratulaDTO: function(){
	        	return caratulaDTO;
	        }
	      }
	  });
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

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	
});