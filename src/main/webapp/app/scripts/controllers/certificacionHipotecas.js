'use strict';

app.controller('CertificacionHipotecasCtrl', function ($scope,$timeout,$rootScope,$location,$anchorScroll,solicitudesModel,certificacionModel, certificacionHipotecasService, caratulaService, $filter,$modal,$modalStack) {
	
	$scope.busquedaCertificacion = certificacionModel.getBusquedaCertificacion();
	$scope.states = certificacionModel.getStates();
	$scope.listaResumida = certificacionModel.getListaResumida();
	$scope.tab = solicitudesModel.getTab();

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

	var promise = certificacionHipotecasService.obtenerCaratulasParaCertificar();
	promise.then(function(data) {
	    if(data.status===null){
	    }else if(data.status){
	    	
	      $scope.busquedaCertificacion.data = data;
	      $scope.busquedaCertificacion.resultados = data.resultado;

	    }else{
	      $scope.setErr('data.msg', data.msg);
	      
	    }
	  }, function(reason) {
	    $scope.setErr('Problema contactando al servidor.', '');
	});

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
			    $rootScope.go('/verInscripcionCertificarHipo/hipo/'+inscripcion.numeroCaratula+'/'+inscripcion.foja+'/'+inscripcion.numero+'/'+inscripcion.ano+'/'+inscripcion.bis+'/'+inscripcion.fechaDocLong+'/'+inscripcion.rehaceImagen+'/'+inscripcion.idtipoFormulario+'/certificacionHipotecas/');
		    }else{
		      $scope.setErr('data.msg', data.msg);
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
    
    }
	
	$scope.verInscripcionCertificar = function(inscripcion){
	    var bis = inscripcion.bis==1?0:1;
	    $rootScope.go('/verInscripcionCertificarHipo/hipo/'+inscripcion.caratula+'/'+inscripcion.foja+'/'+inscripcion.numero+'/'+inscripcion.ano+'/'+inscripcion.bis+'/'+inscripcion.fechaDocLong+'/'+inscripcion.rehaceImagen+'/'+inscripcion.idtipoFormulario+'/certificacionHipotecas/');
    }; 
    
    $scope.refrescar = function(){
    	$scope.openLoadingModal('Actualizando...', '');
		var promise = certificacionHipotecasService.obtenerCaratulasParaCertificar();
		promise.then(function(data) {
			$scope.closeModal();
		    if(data.status===null){
		    }else if(data.status){
		      $scope.busquedaCertificacion.data = data;
		      $scope.busquedaCertificacion.resultados = data.resultado;
		    }else{
		      $scope.setErr('data.msg', data.msg);
		      
		    }
		  }, function(reason) {
		    $scope.setErr('Problema contactando al servidor.', '');
		});
    };
	
//	$scope.buscarIndice = function(){
//		
//		 var foja = $scope.busquedaIndice.foja,
//	     numero = $scope.busquedaIndice.numero,
//	     ano = $scope.busquedaIndice.ano,
//	     bis = $scope.busquedaIndice.bis,
//		 rut = $scope.busquedaIndice.rut,
//		 apellidos = $scope.busquedaIndice.nombre,
//		 direccion = $scope.busquedaIndice.direccion,
//		 regPropiedades = $scope.busquedaIndice.propiedades,
//		 regHipoteca = $scope.busquedaIndice.hipoteca,
//		 regProhibiciones = $scope.busquedaIndice.prohibiciones,
//		 regComercio = $scope.busquedaIndice.comercio,
//		 buscarPorInscricionHipo = $scope.busquedaIndice.buscarPorInscricionHipo,
//		 buscarPorInscricionProh = $scope.busquedaIndice.buscarPorInscricionProh,
//		 exacta = $scope.busquedaIndice.exacta;
//		 
//		 if(!buscarPorInscricionHipo && !buscarPorInscricionProh)
//		 	$scope.resetResultadoIndice();
//		
//		 if($scope.busquedaIndice.comuna!=null)
//		 	var comuna = $scope.busquedaIndice.comuna.codigo;
//		 if($scope.busquedaIndice.anoInscripcion!=null)
//		 	var anoInscripcion = $scope.busquedaIndice.anoInscripcion.codigo;
//		 if($scope.busquedaIndice.acto!=null)
//		 	var acto = $scope.busquedaIndice.acto.nombre;
//		 if($scope.busquedaIndice.tipo!=null)
//		 	var tipo = $scope.busquedaIndice.tipo.nombre;
//		 
//		 
//	     $scope.openLoadingModal('Buscando...', '');
//	
//	     var promise = indiceService.getIndicePropiedades(rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,acto,tipo,exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh);
//	     promise.then(function(data) {
//	      $scope.closeModal();
//	      if(data.status===null){
//	
//	      }else if(data.status){
////	        $scope.busquedaIndice.fecha = new Date();
//	
//	        $scope.busquedaIndice.data = data;
//	        
// 			if(data.aaDataComercio.length>0){
//	        	if(!buscarPorInscricionHipo && !buscarPorInscricionProh){
//		        	$scope.busquedaIndice.resultadoscomercio = data.aaDataComercio;
//		        	$scope.loadMoreComercio();
//		        	$scope.activateParent(4);
//	        	}
//	        }
//	        if(data.aaDataHipotecas.length>0){
//	        	if(!buscarPorInscricionProh){
//		        	$scope.busquedaIndice.resultadoshipotecas = data.aaDataHipotecas;
//		        	$scope.loadMoreHipoteca();
//		        	$scope.activateParent(3);
//	        	}
//	        }	
//	        if(data.aaDataProhibiciones.length>0){
//	        	if(!buscarPorInscricionHipo){
//	        		$scope.busquedaIndice.resultadosprohibiciones = data.aaDataProhibiciones;
//	        		$scope.loadMoreProhibiciones();
//	        		$scope.activateParent(2);
//	        	}
//	        }	
//	        if(data.aaData.length>0){
//	        	if(!buscarPorInscricionHipo && !buscarPorInscricionProh){
//		        	$scope.busquedaIndice.resultados = data.aaData;
//		        	$scope.loadMorePropiedad();
//		        	$scope.activateParent(1);
//	        	}
//	        }	
//	
//	      }else{
//	        $scope.raiseErr('buscar','Problema detectado', data.msg);
//	      }
//	    }, function(reason) {
//	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
//	    });
//	};
	
	$scope.loadMorePropiedad = function() {

	    var carga = $scope.busquedaIndice.resultados.slice($scope.listaPropiedad.inicio, $scope.listaPropiedad.fin);
	
	    angular.forEach(carga, function(sol){
	      $scope.listaPropiedad.data.push(sol);
	    });
	
	    $scope.listaPropiedad.inicio = $scope.listaPropiedad.inicio + $scope.listaPropiedad.offset;
	    $scope.listaPropiedad.fin = $scope.listaPropiedad.fin + $scope.listaPropiedad.offset;
  	};
  	
  	$scope.loadMoreProhibiciones = function() {

	    var carga = $scope.busquedaIndice.resultadosprohibiciones.slice($scope.listaProhibiciones.inicio, $scope.listaProhibiciones.fin);
	
	    angular.forEach(carga, function(sol){
	      $scope.listaProhibiciones.data.push(sol);
	    });
	
	    $scope.listaProhibiciones.inicio = $scope.listaProhibiciones.inicio + $scope.listaProhibiciones.offset;
	    $scope.listaProhibiciones.fin = $scope.listaProhibiciones.fin + $scope.listaProhibiciones.offset;
  	};
  	
  	$scope.loadMoreHipoteca = function() {

	    var carga = $scope.busquedaIndice.resultadoshipotecas.slice($scope.listaHipoteca.inicio, $scope.listaHipoteca.fin);
	
	    angular.forEach(carga, function(sol){
	      $scope.listaHipoteca.data.push(sol);
	    });
	
	    $scope.listaHipoteca.inicio = $scope.listaHipoteca.inicio + $scope.listaHipoteca.offset;
	    $scope.listaHipoteca.fin = $scope.listaHipoteca.fin + $scope.listaHipoteca.offset;
  	};
  	
  	$scope.loadMoreComercio = function() {

	    var carga = $scope.busquedaIndice.resultadoscomercio.slice($scope.listaComercio.inicio, $scope.listaComercio.fin);
	
	    angular.forEach(carga, function(sol){
	      $scope.listaComercio.data.push(sol);
	    });
	
	    $scope.listaComercio.inicio = $scope.listaComercio.inicio + $scope.listaComercio.offset;
	    $scope.listaComercio.fin = $scope.listaComercio.fin + $scope.listaComercio.offset;
  	};
	
	$scope.resolveModal = {
    	refresca: false
  	};

	$scope.resetResultadoCertificacion = function(){
	    $scope.busquedaIndice.data = null;
	    $scope.states.buscar.isError = false;
	    
	    $scope.busquedaIndice.buscarPorInscricionHipo=false;
		$scope.busquedaIndice.buscarPorInscricionProh=false;
	    
	
	    if($scope.busquedaIndice.resultados!==undefined){
	      $scope.busquedaIndice.resultados.length = 0;
	    }
	    
	    if($scope.busquedaIndice.resultadosprohibiciones!==undefined){
	      $scope.busquedaIndice.resultadosprohibiciones.length = 0;
	    }
	    
	    if($scope.busquedaIndice.resultadoshipotecas!==undefined){
	      $scope.busquedaIndice.resultadoshipotecas.length = 0;
	    }
	    
	    if($scope.busquedaIndice.resultadoscomercio!==undefined){
	      $scope.busquedaIndice.resultadoscomercio.length = 0;
	    }
	    
	    if($scope.listaPropiedad.data!==undefined){
	      $scope.listaPropiedad.data.length = 0;
	    }
	    $scope.listaPropiedad.inicio = 0;
	    $scope.listaPropiedad.fin = $scope.listaPropiedad.offset;
	    
	    if($scope.listaProhibiciones.data!==undefined){
	      $scope.listaProhibiciones.data.length = 0;
	    }
	    $scope.listaProhibiciones.inicio = 0;
	    $scope.listaProhibiciones.fin = $scope.listaProhibiciones.offset;
	    
	    if($scope.listaHipoteca.data!==undefined){
	      $scope.listaHipoteca.data.length = 0;
	    }
	    $scope.listaHipoteca.inicio = 0;
	    $scope.listaHipoteca.fin = $scope.listaHipoteca.offset;
	    
	    if($scope.listaComercio.data!==undefined){
	      $scope.listaComercio.data.length = 0;
	    }
	    $scope.listaComercio.inicio = 0;
	    $scope.listaComercio.fin = $scope.listaComercio.offset;
	};
	
		$scope.limpiarTitulo = function(){
		$scope.resetResultadoIndice();

	    $scope.busquedaIndice.foja = null;
	    $scope.busquedaIndice.numero = null;
	    $scope.busquedaIndice.ano = null;
	    $scope.busquedaIndice.bis = false;
	    $scope.busquedaIndice.rut = null;
	    $scope.busquedaIndice.nombre = null;
	    $scope.busquedaIndice.direccion = null;
	    $scope.busquedaIndice.comuna = null;
	    $scope.busquedaIndice.anoInscripcion = null;
	    $scope.busquedaIndice.acto = null;
	    $scope.busquedaIndice.tipo = null;
	
	    $scope.myform.$setPristine(true);
	
	    $scope.doFocus('rutt');
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
});