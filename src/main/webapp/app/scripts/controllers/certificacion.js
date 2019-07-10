'use strict';

app.controller('CertificacionCtrl', function ($scope,$timeout,$rootScope,$location,$anchorScroll,solicitudesModel,certificacionModel, certificacionService, caratulaService, $filter,$modal,$modalStack,$interval,filterFilter) {
	
	$scope.busquedaCertificacion = certificacionModel.getBusquedaCertificacion();
	$scope.states = certificacionModel.getStates();
	$scope.listaResumida = certificacionModel.getListaResumida();
	$scope.tab = solicitudesModel.getTab();
	$scope.paginacionCertificacion = certificacionModel.getPaginacionMaster();

	$scope.solicitudStatus = {
	    ok: false,
	    error : false,
	    warning: false,
	    msgTitle: null,
	    msg: null,
	    tipo: null
  	};
	
	$scope.solicita = {
	    foja: null,
	    numero: null,
	    ano: null,
	    bis: false,
	    tipo: null,
	    esDigital: false,
	    tieneRechazo: false
  	};
	
	$timeout(function(){
		$scope.refrescar();
	}, 500);   

	$scope.redistribuir = function(numerocaratula){
	    $rootScope.go('/distribucion/'+numerocaratula+'/');
    };
    
    $scope.redistribuirCaratula = function(inscripcion){
    	$scope.openLoadingModal('Redistribuyendo...', '');
		var promise = caratulaService.movercaratula(inscripcion.numeroCaratula, inscripcion.codSeccion);
		promise.then(function(data) {
			$scope.closeModal();
		    if(data.status===null){
		    }else if(data.status){
	    	var bis = inscripcion.bis==1?0:1;
			    $rootScope.go('/verInscripcionCertificar/prop/'+inscripcion.numeroCaratula+'/'+inscripcion.foja+'/'+inscripcion.numero+'/'+inscripcion.ano+'/'+inscripcion.bis+'/'+inscripcion.fechaDocLong+'/'+inscripcion.rehaceImagen+'/'+inscripcion.idtipoFormulario+'/certificacion/');
		    }else{
		      $scope.setErr('data.msg', data.msg);
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
    
    }
	
	$scope.verInscripcionCertificar = function(inscripcion){
	    var bis = inscripcion.bis==1?0:1;
	    $rootScope.go('/verInscripcionCertificar/prop/'+inscripcion.caratula+'/'+inscripcion.foja+'/'+inscripcion.numero+'/'+inscripcion.ano+'/'+inscripcion.bis+'/'+inscripcion.fechaDocLong+'/'+inscripcion.rehaceImagen+'/'+inscripcion.idtipoFormulario+'/certificacion/');
    }; 
    
    $scope.refrescar = function(){
    	$scope.openLoadingModal('Actualizando...', '');
		var promise = certificacionService.obtenerCaratulasParaCertificar();
		promise.then(function(data) {
			$scope.closeModal();
		    if(data.status===null){
		    }else if(data.status){
		      $scope.busquedaCertificacion.resultados = data.resultado;
		      $scope.makeTodos(data.resultado);
		    }else{
		      $scope.setErr('data.msg', data.msg);
		      
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
    };
	
	$scope.resolveModal = {
    	refresca: false
  	};
  	
  	$scope.limpiar = function(){
  	  var busquedaCertificacion = {
  		    caratula: null,
  			foja: null,
  		    numero: null,
  		    ano: null,
  		    bis: false,
  			fechaCreacion: null,
  		    resultados: [],
  		    data: null,
  		    predicate: null,
  		    reverse: null 
  		  };

  		  var busquedaMis = {
  		    resultados: [],
  		    data: null,
  		    fecha: null
  		  };

  		  var states = {
  		    buscar : {
  		      isLoading: false,
  		      isError: false,
  		      title: '',
  		      msg: ''
  		    },
  		    urgencia : {
  		      isLoading: false,
  		      isError: false,
  		      title: '',
  		      msg: ''
  		    }
  		  };
  		  
  		  var listaresumida = {
  			    data: [],
  			    inicio: 0,
  			    fin: 20,
  			    offset: 20
  		  };  		
  	}
  	
  	//utiles	
	 $scope.doFocus = function(name){
	    $scope.$broadcast(name+'IsFocused');
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
	
   
  
  $scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  };
    
  $scope.resetStatus = function(){
    $scope.solicitudStatus.ok = false;
    $scope.solicitudStatus.error = false;
    $scope.solicitudStatus.warning = false;
    $scope.solicitudStatus.msgTitle = null;
    $scope.solicitudStatus.msg = null;
  };
  
  $scope.setErr = function(title, mensaje){
    $scope.solicitudStatus.error = true;
    $scope.setStatusMsg(title, mensaje);
  };
  
  $scope.setStatusMsg = function(title, mensaje){
    $scope.solicitudStatus.msgTitle = title;
    $scope.solicitudStatus.msg = mensaje;
  };
  
  //validaciones titulo
  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
    return !(moment(new Date()).year()<value);
  };
  //fin validaciones titulo
  
  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };
  
  $scope.caratulafuera = {
	    caratula:null,
	    status:false,
	    muestra:false
  };
  
  $scope.buscarCaratulaFuera = function(){
		 var caratula = $scope.busquedaCertificacion.caratula;
		 
	     $scope.openLoadingModal('Buscando...', '');
	
	     var promise = caratulaService.obtenerCaratula(caratula);
	     promise.then(function(data) {
	      $scope.closeModal();
	      if(data.status===null){
	
	      }else if(data.status){
	    	 $scope.states.buscar.isError=false;
	    	 $scope.caratulafuera.caratula = data.caratula;
			 $scope.caratulafuera.status = data.status;
			 $scope.caratulafuera.muestra = data.muestra;
			 
	      }else{
	        $scope.raiseErr('buscar','Problema detectado', data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	    });
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
	
	$scope.makeTodos = function(data) {
		$scope.paginacionCertificacion.todos=data;
		var begin = (($scope.paginacionCertificacion.currentPage - 1) * $scope.paginacionCertificacion.numPerPage),
		 end = begin + $scope.paginacionCertificacion.numPerPage;
		$scope.paginacionCertificacion.filteredTodos = $scope.paginacionCertificacion.todos.slice(begin, end);		
		$scope.paginacionCertificacion.maxSize = Math.round($scope.paginacionCertificacion.todos.length / $scope.paginacionCertificacion.numPerPage);

	};	
	
	// $watch search to update pagination
	$scope.$watch('filterExpr', function (newVal, oldVal) {
		if(newVal!=undefined){
			$scope.filtered = filterFilter($scope.busquedaCertificacion.resultados, newVal);
			$scope.paginacionCertificacion.todos = $scope.filtered;
//			$scope.paginacionCertificacion.currentPage = 1;
			var begin = (($scope.paginacionCertificacion.currentPage - 1) * $scope.paginacionCertificacion.numPerPage),
			 end = begin + $scope.paginacionCertificacion.numPerPage;
			$scope.paginacionCertificacion.filteredTodos = $scope.paginacionCertificacion.todos.slice(begin, end);			
			$scope.paginacionCertificacion.maxSize = Math.round($scope.paginacionCertificacion.todos.length / $scope.paginacionCertificacion.numPerPage);
		}
		
	}, true);	
	
	$scope.$watch('busquedaCertificacion.tiemporefresco', function() {
	  $scope.start();
    });	
	
	var promise;    
    $scope.start = function() {
      // stops any running interval to avoid two intervals running at the same time
      $scope.stop(); 
      
      // store the interval promise
      if($scope.busquedaCertificacion.tiemporefresco!=0){
  			promise = $interval(function(){
  				$scope.refrescar();
  			}.bind(this), $scope.busquedaCertificacion.tiemporefresco*60000);
      }
      
    };
  
    // stops the interval
    $scope.stop = function() {
      $interval.cancel(promise);
    };
    
    $scope.start();
   
    $scope.$on('$destroy', function() {
    	$scope.stop(); 
    });	
});