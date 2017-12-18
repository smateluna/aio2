'use strict';

app.controller('ModificaAnotacionModalCtrl', function ($rootScope, $scope, $sce, $modalInstance, $timeout, $window, $modalStack,
                                                taOptions,inscripcionDigitalService, AnotacionService,nota,inscripcionDigitalDTO,anotaciones,notas,origen, paginacionNotas) {

  $scope.servicioAnotacion = origen=="digital"?AnotacionService:origen=="digitalHipotecas"?AnotacionHipotecasService:AnotacionProhibicionesService;
  $scope.servicioInscripcion = origen=="digital"?inscripcionDigitalService:origen=="digitalHipotecas"?inscripcionDigitalHipotecasService:inscripcionDigitalProhibicionesService;
	
  taOptions.toolbar = [
      ['h4', 'h5', 'h6', 'p',],
      ['redo', 'undo'],
      ['justifyLeft', 'justifyCenter', 'justifyRight']
      //,['html']
  ];
  
  $scope.paginacionMaster = {
	  currentPage: 1,
	  numPerPage: 10,
	  maxSize: 2,
	  totalpaginas: 0,
	  filteredTodos: [],
	  todos: [],
	  filterExprNotas: ''
  }
	
  $scope.savingAnotacion = false;

  $scope.anotacionStatus = {
    ok: false,
    error : false,
    msg: null
  };

  $scope.anotacion = {
    caratula: null,
    texto: null,
    version: null
  };
  
  $scope.loaders = {
	  borradores : {
	  isLoading : false,
	  error : false
  },
  enProceso : {
	  isLoading : false,
	  error : false
  },
  terminadas : {
	  isLoading : false,
	  error : false
  },
  anotaciones : {
	  isLoading : false,
	  error : false
  },
  notas : {
	  isLoading : false,
	  error : false
  }
  };
  
  $scope.anotacion.texto=nota.texto;
  $scope.anotacion.caratula=nota.caratula;
  var res = nota.caratulaMatriz.split('-');
  if(res.length>1)
  	$scope.anotacion.version=res[1];
  
  $scope.doFocus = function(conT, campo){

    if(conT){
      $timeout(function(){
        $scope.$broadcast(campo+'IsFocused');
      },100);
    }else{
      $scope.$broadcast(campo+'IsFocused');
    }
  };

  $scope.closeAnotacion = function () {

    var top = $modalStack.getTop();
    if (top) {
      $modalInstance.dismiss('cancel');
    }
  };

  
  $scope.modificarAnotacion = function(){
    $scope.savingAnotacion = true;

    $scope.anotacionStatus = {
      ok: false,
      error : false,
      msg: null
    };
    
    var promise = AnotacionService.modificaAnotacion($scope.anotacion.texto,$scope.anotacion.caratula,$scope.anotacion.version,nota.idAnotacion);
    promise.then(function(data) {
      if(data.status===null){
        $window.location.href = $window.location.protocol+'//'+$window.location.host+$window.location.pathname;
      }else if(data.status){
        $scope.anotacionStatus.ok = true;
        
        if(origen=="digital"){
        	$scope.buscaNotas();
        }else{
        	
	        var promiseIns = $scope.servicioInscripcion.getInscripcion(inscripcionDigitalDTO.foja, inscripcionDigitalDTO.numero, inscripcionDigitalDTO.ano, inscripcionDigitalDTO.bis);
	        promiseIns.then(function(data) {
	
	          anotaciones.length = 0;
	          notas.length = 0;
	          angular.forEach(data.inscripcionDigitalDTO.anotacionsForIdInscripcionDestino, function(obj){
	            if(obj.tipoAnotacionDTO.idTipoAnotacion===3){
	              anotaciones.push(obj);
	            }
	            else if( (obj.tipoAnotacionDTO.idTipoAnotacion===1 || obj.tipoAnotacionDTO.idTipoAnotacion===2) && obj.estadoAnotacionDTO.idEstado!==7){
	              notas.push(obj);
	            }
	          });
	
	        }, function(reason) {
	        });
	        
	         $scope.makeTodos();
        }
		$scope.salirSave();
        
      }else{
        $scope.savingAnotacion = false;
        $scope.anotacionStatus.error = true;
        $scope.anotacionStatus.msg = data.msg;
      }
    }, function(reason) {
      $scope.savingAnotacion = false;
      $scope.anotacionStatus.error = true;
      $scope.anotacionStatus.msg = 'Problemas contactando al servidor.';
    });
  };


  $scope.salirSave = function(){
    $timeout(function(){
      $scope.savingAnotacion = false;
      $scope.closeAnotacion();
    },2000);
  };

  $scope.doFocus(true, 'texto');
  
  $scope.buscaNotas = function() {

	  //			alert('buscando notas');

	  $scope.loaders['anotaciones'].isLoading = true;
	  $scope.loaders['notas'].isLoading = true;

	  var foja = inscripcionDigitalDTO.foja, numero = inscripcionDigitalDTO.numero, ano = inscripcionDigitalDTO.ano, bis = inscripcionDigitalDTO.bis;

	  var promise = $scope.servicioInscripcion.getNotas(
		  foja, numero, ano, bis);
	  promise
	  .then(
		  function(data) {
			  if (data.status == null) {
			  } else if (data.status) {
				  anotaciones.length = 0;
          		  notas.length = 0;

          		  angular
          		  .forEach(
          			  data.anotaciones,
          			  function(obj) {
          				  if ((obj.tipoAnotacionDTO.idTipoAnotacion === 1 || obj.tipoAnotacionDTO.idTipoAnotacion === 2)
          						  && obj.estadoAnotacionDTO.idEstado !== 7) {
          					  notas.push(obj);

          				  } else if (obj.tipoAnotacionDTO.idTipoAnotacion === 3) {
          					  anotaciones
          					  .push(obj);
          				  }
          			  });



				  $scope.makeTodos();

			  } else {
				  $scope.raiseErr('error','Error Obteniendo Notas.',data.msg);
				  $scope.loaders['anotaciones'].error = true;
				  $scope.loaders['notas'].error = true;
			  }

			  $scope.loaders['anotaciones'].isLoading = false;
			  $scope.loaders['notas'].isLoading = false;

		  },
		  function(reason) {
			  $scope.loaders['anotaciones'].isLoading = false;
			  $scope.loaders['notas'].isLoading = false;

			  $scope.loaders['anotaciones'].error = true;
			  $scope.loaders['notas'].error = true;

			  $scope.raiseErr('error','Error detectado.',	'No se ha podido establecer comunicación.');

		  });

  };
  
  $scope.makeTodos = function() {

	  paginacionNotas = angular.copy($scope.paginacionMaster);

	  paginacionNotas.todos=notas;

	  var begin = ((paginacionNotas.currentPage - 1) * paginacionNotas.numPerPage)
	  , end = begin + paginacionNotas.numPerPage;

	  paginacionNotas.filteredTodos = paginacionNotas.todos.slice(begin, end);

	  paginacionNotas.totalpaginas = Math.round(paginacionNotas.todos.length / paginacionNotas.numPerPage);
	  
  };
  
});
