'use strict';

app.controller('CartelesCtrl', function ($rootScope, $scope, $timeout, $location, cartelesModel, $filter, $modal,
                                        $modalStack, $window, CartelesService) {
  //modelos
  $scope.tab = cartelesModel.getTab();
  $scope.busquedaCartel = cartelesModel.getBusquedaCartel();
  $scope.busquedaMis = cartelesModel.getBusquedaMis();
  $scope.states = cartelesModel.getStates();

  $scope.listaMis = {
    data: [],
    inicio: 0,
    fin: 15,
    offset: 15
  };
  //fin modelos

  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
    cartelesModel.setTab($scope.tab);

    if($scope.tab.parentActive===1){
      $timeout(function(){
        $scope.doFocus('numero');
      }, 200);
    }else if($scope.tab.parentActive===2){
      if($scope.busquedaMis.resultados===undefined || $scope.busquedaMis.resultados.length===0){
        $scope.buscarMis(true);
      }
    }
  };
  //fin controles tabs
  //cartel

  $scope.verCartel = function(cartel){
    $rootScope.go('/verCartel/'+cartel.numero+'/'+cartel.mes+'/'+cartel.ano+'/'+cartel.registro+'/'+cartel.bis+'/'+cartel.tipoArchivoDTO.tipoArchivo+'/carteles');
  };

  $scope.limpiarFormulario = function(){
    $scope.busquedaCartel.numero = null;
    $scope.busquedaCartel.mes = '';
    $scope.busquedaCartel.ano = null;
    $scope.busquedaCartel.registro = 1;
    $scope.busquedaCartel.bis = false;

    $scope.myform.$setPristine(true);

    $scope.doFocus('numero');
  };

  $scope.buscarCartel = function(){
    var numero = $scope.busquedaCartel.numero,
      mes = $rootScope.aioParametros.sistema=='AIO AGUAS'?1:$scope.busquedaCartel.mes,
      ano = $scope.busquedaCartel.ano,
      registro = $rootScope.aioParametros.sistema=='AIO'?1:$scope.busquedaCartel.registro,
      bis  = $scope.busquedaCartel.bis;

    $scope.openLoadingModal( 'Buscando...', '');

    var promise =  CartelesService.buscarCartel(numero, mes, ano, registro, bis, null);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status===null){

      }else if(data.status){

        if(data.cartelDTO.hayArchivo){
          //cartelesModel.setEstadoDocumento(data.cartelDTO);

          if(data.cartelDTO.tipoArchivoDTO.tipoArchivo===0){ //tomo

            $timeout(function(){
              if(confirm('No se ha encontrado cartel, pero si el tomo de ese MES - AÑO, desea verlo?')){
                $scope.verCartel(data.cartelDTO);
              }
            }, 500);
          }else if(data.cartelDTO.tipoArchivoDTO.tipoArchivo===1){ //cartel
            $scope.verCartel(data.cartelDTO);
          }
        }else{
          $scope.openMensajeModal('info', 'No existe documento para cartel buscado.', '');
        }
      }else{
        $scope.openMensajeModal('error', 'Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
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




  $scope.openDocumentoModal = function (url) {
    $scope.closeModal();

    $modal.open({
      templateUrl: 'documentoModal.html',
      backdrop: true,
      keyboard: true,
      size: 'lg',
      windowClass: 'modal',
      controller: 'DocumentoModalCtrl',
      resolve: {
        url: function () {
          return url;
        }
      }
    });
  };

  $scope.verCertificado = function(idCertificado, registro){
    $scope.openDocumentoModal('../do/service/carteles?metodo=getPDF&idCertificado='+idCertificado+'&registro='+registro);
  };

  $scope.eliminar = function(index, idCertificado, registro){

    if(confirm('Desea eliminar el certificado?')){
      $scope.openLoadingModal( 'Eliminando...', '');


      CartelesService.eliminar(idCertificado, registro).then(function(data){
        $scope.closeModal();

        $scope.busquedaMis.resultados.splice(index, 1);

        $scope.actualizandoAlgo = false;
      }, function(err){
        $scope.actualizandoAlgo = false;
      });
    }
  };


  $scope.firmar = function(index, idCertificado, registro){

    //if(confirm('Desea eliminar el cetificado?')){
      $scope.openLoadingModal( 'Firmando...', '');


      CartelesService.firmar(idCertificado, registro).then(function(data){      
    	  if(data.status){
	          $scope.openMensajeModal('success', 'Documento firmado exitosamente.', '');
	
	          $timeout(function(){
	
	            $scope.closeModal();
	
	          }, 2000);
	
	          $scope.certificados.splice(index, 1);
        }else{
        	  $scope.openMensajeModal('error', 'Problema detectado firmando documento.', '');

        }}, function(err){
        $scope.actualizandoAlgo = false;
      });
    //}
  };


  $scope.buscarMis = function(loading){
    $scope.resetResultadoMis();

    if(loading){
      $scope.openLoadingModal('Obteniendo certificados...', '');
    }

    var promise = CartelesService.listadoCertificadosByUserInSession();
    promise.then(function(data) {
      if(loading){
        $scope.closeModal();
      }
      if(data.status===null){

      }else if(data.status){
        $scope.busquedaMis.fecha = new Date();

        $scope.busquedaMis.data = data;
        $scope.busquedaMis.resultados = $filter('orderBy')(data.aaData, 'fechaCreacion', true);

      }else{
        $scope.raiseErr('mis', 'Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('mis', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
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

  //validaciones titulo
  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
	  console.log(''+value);
    return !(moment(new Date()).year()<value);
  };
  //fin validaciones titulo

  $timeout(function(){
    if($scope.tab.parentActive===1){
      $scope.doFocus('numero');
    }else if($scope.tab.parentActive===2){

    }
  }, 200);
});