'use strict';

app.controller('MantenedorCertificaCtrl', function ($scope,$timeout,$rootScope,$location,$anchorScroll,mantenedorCertificaModel, mantenedorService,$filter,$modal,$modalStack,$routeParams) {
	
	$scope.busquedaUsuario = mantenedorCertificaModel.getBusquedaUsuario();
	$scope.states = mantenedorCertificaModel.getStates();
	
	$scope.obtenerUsuarios = function(){
		//if($scope.busquedaUsuario.resultados==undefined || $scope.busquedaUsuario.resultados.length==0){
			var promise = mantenedorService.obtenerUsuarios();
			promise.then(function(data) {
			    if(data.status===null){
			    }else if(data.status){
			      $scope.busquedaUsuario.resultados = data.usuarios;
			    }else{
			      $scope.setErr('data.msg', 'data.msg');
			    }
			  }, function(reason) {
			    $scope.setErr('Problema contactando al servidor.', '');
			});
		//}
	}
	$scope.obtenerUsuarios();
	 
	$scope.cambiaEstadoUsuario = function(usuario,estado){
		 
		 $scope.openLoadingModal('Actualizando...', '');
	
	     var promise = mantenedorService.cambiaEstadoUsuario(usuario.usuario,estado, usuario.perfil);
	     promise.then(function(data) {
	      $scope.closeModal();
	      if(data.status===null){
	
	      }else if(data.status){
			usuario.estado=estado;
	      }else{
	        $scope.raiseErr('buscar','Problema detectado', data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	    });
		
	}

	$scope.redistribuirCaratulas = function(usuario){
		 
		 $scope.openLoadingModal('Redistribuyendo Caratulas...', '');
	
	     var promise = mantenedorService.redistribuirCaratulas(usuario.usuario, usuario.perfil);
	     promise.then(function(data) {
	      $scope.closeModal();
	      if(data.status===null){
	      }else if(data.status){
	    	  $scope.obtenerUsuarios();
	      }else{
	        $scope.raiseErr('buscar','Problema detectado', data.msg);
	      }
	    }, function(reason) {
	      $scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
	    });
		
	}
	
	$scope.miPerfil = function(sol){
		var show = false;
		angular.forEach($scope.busquedaUsuario.resultados, function(value, key) {			
			if($rootScope.perfil.toUpperCase()==="DESARROLLO AIO" || 
					value.usuario.toUpperCase().trim()==$rootScope.userLoginSinCBRS.toUpperCase().trim() && 
					sol.perfil.toUpperCase().trim()==value.perfil.toUpperCase().trim())
				show = true		
		});		
		return show;
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
  	
  	$scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  	};
});