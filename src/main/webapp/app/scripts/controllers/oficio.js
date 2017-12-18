'use strict';

app.controller('OficioCtrl', function ($scope,$timeout,$window,$rootScope,$location,$anchorScroll,oficioModel,oficioService,$filter,$modal,$modalStack,$routeParams) {

	$scope.busquedaOficio = oficioModel.getBusquedaOficio();
	$scope.states = oficioModel.getStates();
	$scope.listaOficio = oficioModel.getListaOficio();
	
	$scope.rowHighilited = function (idSelected) {
		$scope.busquedaOficio.selectedRow = idSelected;
	};

	$scope.paginacionMaster = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}

	$scope.datosSesion = {
		rut: null,
		requirente: null
	}

	$scope.contador = {
		usuario: null,
		total: null
	}
	
	$scope.solrSuggester = function(){
        angular.element.ajax({
            url: $scope.solrSuggesterUrl,
            data: {
                "q": $scope.busquedaOficio.institucion,
                "wt": "json",
                "suggest": "true"
            },
            traditional: true,
            cache: true,
            html: true,
            async: true,
            dataType: 'jsonp',
            success: function (data) {
                $scope.$apply(function () {
                	$scope.suggest = [];

                	if($scope.busquedaOficio.institucion, data.suggest[Object.keys(data.suggest)[0]][$scope.busquedaOficio.institucion].numFound>0){
                		for(var i=0; i<data.suggest[Object.keys(data.suggest)[0]][$scope.busquedaOficio.institucion].numFound; i++){
                			var newSuggest = {
							"label": data.suggest[Object.keys(data.suggest)[0]][$scope.busquedaOficio.institucion].suggestions[i].term.trim().replace(/\s+/g, " "),
							"value": data.suggest[Object.keys(data.suggest)[0]][$scope.busquedaOficio.institucion].suggestions[i].term.trim().replace(/\s+/g, " ").replace(/<b>/g, "").replace(/<\/b>/g, ""),
                			};   
                			
                			//filtrar por ciudad
                			var ciudadSuggest = data.suggest[Object.keys(data.suggest)[0]][$scope.busquedaOficio.institucion].suggestions[i].payload.trim().replace(/\s+/g, " ");                        		
                			if($scope.busquedaOficio.ciudad!==undefined && $scope.busquedaOficio.ciudad!==null && $scope.busquedaOficio.ciudad!=ciudadSuggest)
                				continue;    
                			
                			var repetido = false;
                			for(var j=0; j<$scope.suggest.length; j++){
                				if(JSON.stringify($scope.suggest[j]) == JSON.stringify(newSuggest)){
                					repetido = true;
                					break;
                				}
                			}  
                			
                			if(!repetido)
                				$scope.suggest.push( newSuggest );
                		}
                	}            
                });
            },
            jsonp: 'json.wrf'
        });
	}
	
	$scope.buscarOficio = function(){
		var caratula = $scope.busquedaOficio.caratula,
		institucion = $scope.busquedaOficio.institucion,
		requirente = $scope.busquedaOficio.requirente,
		estado = $scope.busquedaOficio.estado,
		fechaCreacionDesde = $scope.busquedaOficio.fechaCreacionDesde,
		fechaCreacionHasta = $scope.busquedaOficio.fechaCreacionHasta,
		fechaEntrega = $scope.busquedaOficio.fechaEntrega,
		materia = $scope.busquedaOficio.materia,
		rol = $scope.busquedaOficio.rol,
		oficio = $scope.busquedaOficio.oficio,
		identificador = $scope.busquedaOficio.identificador,
		ciudad = $scope.busquedaOficio.ciudad;
		$scope.resetResultados();
		$scope.openLoadingModal('Buscando...', '');

		var promise = oficioService.getOficios(caratula, institucion, requirente, estado, fechaCreacionDesde, fechaCreacionHasta, fechaEntrega, materia, rol, oficio, identificador, ciudad);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){
				$scope.busquedaOficio.data = data;

				if(data.aaData.length>0){
					$scope.busquedaOficio.resultados = data.aaData;
					$scope.loadMore();
				}	

				//Paginacion
				$scope.makeTodos();

			}else{
				$scope.raiseErr('buscar','Problema detectado', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('buscar','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
		});
	};

	$scope.loadMore = function() {

		var carga = $scope.busquedaOficio.resultados.slice($scope.listaOficio.inicio, $scope.listaOficio.fin);

		angular.forEach(carga, function(sol){
			$scope.listaOficio.data.push(sol);
		});

		$scope.listaOficio.inicio = $scope.listaOficio.inicio + $scope.listaOficio.offset;
		$scope.listaOficio.fin = $scope.listaOficio.fin + $scope.listaOficio.offset;
	};

	$scope.resetResultados = function(){
		$scope.busquedaOficio.data = null;
		$scope.states.buscar.isError = false;

		if($scope.busquedaOficio.resultados!==undefined){
			$scope.busquedaOficio.resultados.length = 0;
		}

		if($scope.listaOficio.data!==undefined){
			$scope.listaOficio.data.length = 0;
		}
		$scope.listaOficio.inicio = 0;
		$scope.listaOficio.fin = $scope.listaOficio.offset;

	};

	$scope.limpiarTitulo = function(){
		$scope.resetResultados();

		$scope.busquedaOficio.caratula = null;
		$scope.busquedaOficio.institucion = null;
		$scope.busquedaOficio.requirente = null;
		$scope.busquedaOficio.estado = "";
		$scope.busquedaOficio.fechaCreacionDesde = null;
		$scope.busquedaOficio.fechaCreacionHasta = null;
		$scope.busquedaOficio.fechaEntrega = null;
		$scope.busquedaOficio.materia = null;
		$scope.busquedaOficio.rol = null;
		$scope.busquedaOficio.oficio = null;
		$scope.busquedaOficio.identificador = null;
		$scope.busquedaOficio.ciudad = null;

		$scope.myform.$setPristine(true);

		$scope.doFocus('caratula');
		$scope.indicebusqueda=false;
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

	$scope.setErr = function(title, mensaje){
		$scope.setStatusMsg(title, mensaje);
	};


	$scope.paginacion = angular.copy($scope.paginacionMaster);
	$scope.paginacion.todos=$scope.busquedaOficio.resultados;

	$scope.makeTodos = function() {

		$scope.paginacion = angular.copy($scope.paginacionMaster);

		$scope.paginacion.todos=$scope.busquedaOficio.resultados;

		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;		

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);
		$scope.busquedaOficio.indicebusqueda=true;
	};

	$scope.$watch('paginacion.currentPage + paginacion.numPerPage', function() {
		var begin = (($scope.paginacion.currentPage - 1) * $scope.paginacion.numPerPage)
		, end = begin + $scope.paginacion.numPerPage;

		$scope.paginacion.filteredTodos = $scope.paginacion.todos.slice(begin, end);

	});

	$scope.colapsar = function(){

		if($scope.busquedaOficio.indicebusqueda){
			$scope.busquedaOficio.indicebusqueda = false;
		}else{
			$scope.busquedaOficio.indicebusqueda = true;
		}

	};

	$scope.colapsarnuevabusqueda = function(){

		$scope.busquedaOficio.indicebusqueda = false;
		$scope.limpiarTitulo();
		$timeout(function(){
			$scope.doFocus('caratula');
		}, 200);
	};
	
	$timeout(function() {
		//Buscar url solr
		var promise = oficioService.init();
		promise
		.then(
			function(data) {
				if (data.status === null) {
				} else if (data.status) {
					$scope.solrSuggesterUrl = data.solrSuggesterUrl;
					$scope.ciudades = data.ciudades;

				}
			},
			function(reason) {
				$scope
				.raiseErr('No se ha podido establecer comunicación con el servidor.');
			});
	}, 500);	

});