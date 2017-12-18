'use strict';

app.controller('DigitalHipotecasCtrl', function ($rootScope, $scope, $timeout, $location, digitalHipotecasModel, $filter, $modal,
                                        $modalStack, inscripcionDigitalHipotecasService, solicitudHipotecasService) {
  //modelos
  $scope.tab = digitalHipotecasModel.getTab();
  $scope.busquedaTitulo = digitalHipotecasModel.getBusquedaTitulo();
  $scope.busquedaMis = digitalHipotecasModel.getBusquedaMis();
  $scope.states = digitalHipotecasModel.getStates();

  $scope.listaMis = {
    data: [],
    inicio: 0,
    fin: 15,
    offset: 15
  };

  $scope.resolveModal = {
    refresca: false,
    origen: 'digital'
  };

  //modal
  $scope.solicita = {
    foja: null,
    numero: null,
    ano: null,
    bis: false,
    origen: 'digitalHipotecas',
    consultaDocumentoDTO: {},
    estado: {}
  };


  $scope.$watchCollection('resolveModal', function(){

    if($scope.resolveModal.refresca){
      $scope.buscarMis(false);
    }

  });

  $scope.promiseUpdateSolicitudMis = null;

  //fin modelos

  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
    digitalHipotecasModel.setTab($scope.tab);

    if($scope.tab.parentActive===1){
      $timeout(function(){
        $scope.doFocus('foja');
      }, 200);
    }else if($scope.tab.parentActive===2){
      if($scope.busquedaMis.resultados===undefined || $scope.busquedaMis.resultados.length===0){
        $scope.buscarMis(true);
      }
    }
  };
  //fin controles tabs

  //titulo
  $scope.verTitulo = function(titulo){
    var bis = titulo.bis==1?true:false;

    $rootScope.go('/verInscripcion/hip/'+titulo.foja+'/'+titulo.numero+'/'+titulo.ano+'/'+bis+'/'+$scope.solicita.origen);
  };

  $scope.limpiarTitulo = function(){

    $scope.busquedaTitulo.foja = null;
    $scope.busquedaTitulo.numero = null;
    $scope.busquedaTitulo.ano = null;
    $scope.busquedaTitulo.bis = false;

    $scope.myform.$setPristine(true);

    $scope.doFocus('foja');
  };

  $scope.buscarTitulo = function(){
    var foja = $scope.busquedaTitulo.foja,
      numero = $scope.busquedaTitulo.numero,
      ano = $scope.busquedaTitulo.ano,
      bis = $scope.busquedaTitulo.bis,
      bisN = $scope.busquedaTitulo.bis===true?1:0;

    $scope.states.titulo.isLoading = true;

    $scope.openLoadingModal( 'Buscando...', '');

    var promise = inscripcionDigitalHipotecasService.getInscripcion(foja, numero, ano, bis);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status===null){

      }else if(data.status){


        $scope.solicita.foja = data.inscripcionDigitalDTO.foja;
        $scope.solicita.numero = data.inscripcionDigitalDTO.numero;
        $scope.solicita.ano = data.inscripcionDigitalDTO.ano;
        $scope.solicita.bis = data.inscripcionDigitalDTO.bis;
        $scope.solicita.consultaDocumentoDTO = data.consultaDocumentoDTO;
        $scope.solicita.estado = data.estado;

        if(data.estado.tieneRechazo){
          //tiene rechazo
          $scope.openSolicitar();

        }else if(!data.estado.fna){
        	
        	$scope.openSolicitar();   
        
        }else if(!data.consultaDocumentoDTO.hayDocumento){
          //no tiene imagen y es solicitable 1

          //TODO: no tiene imagen y es solicitable

          $scope.openSolicitar();


        }else if(data.consultaDocumentoDTO.hayDocumento &&
          (data.consultaDocumentoDTO.tipoDocumento===9 || data.consultaDocumentoDTO.tipoDocumento===10)){
          //tiene imagen y es solicitable 2

          //TODO: ver referencial o solicitar?

          if(data.seDigitalizoEnElDia){
        	digitalHipotecasModel.setDataState(data);
          	$scope.verTitulo({foja: foja, numero: numero, ano: ano, bis: bisN});
          }else{	
          	$scope.openSolicitar();
          }

        }else if(data.consultaDocumentoDTO.hayDocumento && data.consultaDocumentoDTO.tipoDocumento===8){
          //esta digitalizada 3

        	digitalHipotecasModel.setDataState(data);
          $scope.verTitulo({foja: foja, numero: numero, ano: ano, bis: bisN});
        }

      }else{
        $scope.raiseErr('titulo','Problema detectado', data.msg);
      }
    }, function(reason) {

      $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');


      //$scope.raiseErr('titulo','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };



  $scope.openSolicitar = function () {
    $scope.$broadcast('sfojaIsFocused');

    $modal.open({
      templateUrl: 'digitalModal.html',
      backdrop: true,
      windowClass: 'modal',
      controller: 'DigitalModalCtrl',
      resolve: {
        solicita: function () {
          return $scope.solicita;
        },
        servicio: function () {
          return solicitudHipotecasService;
        },
        origen: function(){
        	return $scope.solicita.origen;
        }
      }
    });
  };
  //fin titulo




  //inicio mis
  $scope.openSolicitarMis = function () {

    $scope.resolveModal.refresca = false;

    var myModal = $modal.open({
      templateUrl: 'solicitarModal.html',
      backdrop: true,
      windowClass: 'modal',
      controller: 'SolicitarModalCtrl',
      resolve: {
        resolveModal : function(){
          return $scope.resolveModal;
        },
        servicio: function () {
	      return solicitudHipotecasService;
	    }
      }
    });
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


  $scope.openMensajeModal = function (tipo, titulo, detalle) {
    $scope.closeModal();

    $modal.open({
      templateUrl: 'mensajeModal.html',
      backdrop: true,
      keyboard: true,
      size: 'sm',
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
  };

  $scope.columnSortMis = function(field){
    $scope.busquedaMis.sorter.reverse = $scope.busquedaMis.sorter.predicate == field && !$scope.busquedaMis.sorter.reverse;
    $scope.busquedaMis.sorter.predicate = field;
  };

  $scope.resetResultadoMis = function(){

    if($scope.busquedaMis.resultados!==undefined){
      $scope.busquedaMis.resultados.length = 0;
    }

    if($scope.listaMis.data!==undefined){
      $scope.listaMis.data.length = 0;
    }

    $scope.busquedaMis.data = null;

    $scope.busquedaMis.fecha = null;
    $scope.listaMis.inicio = 0;
    $scope.listaMis.fin = $scope.listaMis.offset;

    $scope.cleanErr('mis');
  };

  $scope.buscarMis = function(loading){
    $scope.resetResultadoMis();

    if(loading){
      $scope.openLoadingModal('Obteniendo solicitudes...', '');
    }

    var promise = solicitudHipotecasService.getByUserInSession();
    promise.then(function(data) {
      if(loading){
        $scope.closeModal();
      }
      if(data.status===null){

      }else if(data.status){
        $scope.busquedaMis.fecha = new Date();

        $scope.busquedaMis.data = data;
        $scope.busquedaMis.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);

      }else{
        $scope.raiseErr('mis', 'Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('mis', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };

  $scope.loadMoreMis = function() {
    var carga = $scope.busquedaMis.resultados.slice($scope.listaMis.inicio, $scope.listaMis.fin);

    //console.log('busquedaMis.resultados.length>'+$scope.busquedaMis.resultados.length+' $scope.listaMis.data.length'+$scope.listaMis.data.length);
    //console.log('desde:'+$scope.listaMis.inicio+' fin:'+ $scope.listaMis.fin);
    //console.log(carga);

    angular.forEach(carga, function(sol){
      $scope.listaMis.data.push(sol);
    });

    $scope.listaMis.inicio = $scope.listaMis.inicio + $scope.listaMis.offset;
    $scope.listaMis.fin = $scope.listaMis.fin + $scope.listaMis.offset;
  };


  $scope.hayDataEnTablaMis = function(){

    var dataParaMostrar = $filter('filter')($scope.busquedaMis.resultados, $scope.isSolicitudRevisada, true);

    return (dataParaMostrar!==undefined && dataParaMostrar.length>0)
  };


  $scope.accionSolicitud = function(promesa, solicitud){

    if(solicitud.estadoDTO.idEstado==5){
      var bis = solicitud.bis==1?true:false;
      $scope.verTitulo({foja: solicitud.foja, numero: solicitud.numero, ano: solicitud.ano, bis: bis});
    }else{

      var accion = '';
      if(solicitud.estadoDTO.idEstado==2){
        accion = 'remove-ok';

      }else if(solicitud.estadoDTO.idEstado==3){
        accion = 'remove-rechazo';
      }

      $scope[promesa] = solicitudHipotecasService.actualizar(solicitud.idSolicitud, accion);
      $scope[promesa].then(function(data) {

        if(data.status==null){

        }else if(data.status){
          solicitud.fechaEstado = data.fechaEstado;
          solicitud.estadoDTO = {idEstado: data.idEstado, descripcion: data.descripcionEstado};

          if(accion=='remove-ok'){
            var bis = solicitud.bis==1?true:false;

            $scope.verTitulo({foja: solicitud.foja, numero: solicitud.numero, ano: solicitud.ano, bis: bis});
          }

        }else{

        }
      }, function(reason) {

      });
    }
  };
  //fin mis








  //utiles
  $scope.doFocus = function(name){
    $scope.$broadcast(name+'IsFocused');
  };

  $scope.raiseErr = function(key, title, msg){
    $scope.closeModal();
    $scope.states[key].isError = true;
    $scope.states[key].title = title;
    $scope.states[key].msg = msg;
  };

  $scope.cleanErr = function(key){
    $scope.states[key].isError = false;
    $scope.states[key].title = '';
    $scope.states[key].msg = '';
  };

  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };

  $scope.isSolicitudRevisada = function(sol){
    return sol.estadoDTO.idEstado!==6 && sol.estadoDTO.idEstado!==5;
  };



  //validaciones titulo
  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
    return !(moment(new Date()).year()<value);
  };
  //fin validaciones titulo

  $timeout(function(){
    if($scope.tab.parentActive===1){
      $scope.doFocus('foja');
    }else if($scope.tab.parentActive===2){

    }
  }, 200);
});