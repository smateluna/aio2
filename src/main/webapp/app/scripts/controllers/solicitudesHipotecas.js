'use strict';

app.controller('SolicitudesHipotecasCtrl', function ($rootScope, $scope, $timeout, $location, solicitudesHipotecasModel, $filter, $modal, $modalStack, inscripcionDigitalService, solicitudService) {
  //modelos
  $scope.tab = solicitudesHipotecasModel.getTab();
  $scope.busquedaTitulo = solicitudesHipotecasModel.getBusquedaTitulo();

  $scope.$watchCollection('busquedaTitulo', function(){

    if($scope.busquedaTitulo.rut === undefined || $scope.busquedaTitulo.rut===''){
      $scope.busquedaTitulo.nombre = '';
    }
  });

  $scope.busquedaRut = solicitudesHipotecasModel.getBusquedaRut();

  $scope.$watchCollection('busquedaRut', function(){

    if($scope.busquedaRut.rut === undefined || $scope.busquedaRut.rut===''){
      $scope.busquedaRut.nombre = '';
    }

    if($.Rut.validar($scope.busquedaRut.rut)){
    	solicitudesHipotecasModel.setBusquedaRut($scope.busquedaRut);
    }
  });

  $scope.busquedaMis = solicitudesHipotecasModel.getBusquedaMis();

  //$scope.$watchCollection('busquedaMis', function(){
  //solicitudesHipotecasModel.setBusquedaMis($scope.busquedaMis);
  //});

  $scope.states = solicitudesHipotecasModel.getStates();

  //$scope.$watchCollection('states', function(){
  //solicitudesHipotecasModel.setStates($scope.states);
  //});

  $scope.listaMis = {
    data: [],
    inicio: 0,
    fin: 15,
    offset: 15
  };

  $scope.listaRut = {
    data: [],
    inicio: 0,
    fin: 15,
    offset: 15
  };

  $scope.resolveModal = {
    refresca: false,
    origen: 'solicitudes'
  };

  $scope.$watchCollection('resolveModal', function(){

    if($scope.resolveModal.refresca){
      $scope.buscarMis(false);
    }

  });


  $scope.promiseUpdateSolicitudMis = null;
  $scope.promiseUpdateSolicitudRut = null;

  //fin modelos

  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
    solicitudesHipotecasModel.setTab($scope.tab);

    if($scope.tab.parentActive===1){

      if($scope.busquedaTitulo.rut!==null){
        $timeout(function(){
          $scope.doFocus('foja');
        }, 200);
      }else{
        $timeout(function(){
          $scope.doFocus('rutt');
        }, 200);
      }
    }else if($scope.tab.parentActive===2){
      $timeout(function(){
        $scope.doFocus('rut');
      }, 200);
    }else if($scope.tab.parentActive===3){
      if($scope.busquedaMis.resultados===undefined || $scope.busquedaMis.resultados.length===0){
        $scope.buscarMis(true);
      }
    }
  };
  //fin controles tabs

  //titulo
  $scope.verTitulo = function(titulo){
    var bis = titulo.bis==1?true:false,
      estado = titulo.estadoDigitalId;

    $rootScope.go('/verInscripcion/prop/'+titulo.foja+'/'+titulo.numero+'/'+titulo.ano+'/'+bis+'/solicitudes/'+estado);
  };

  $scope.resetResultadoTitulo = function(){
    $scope.busquedaTitulo.data = null;

    $scope.busquedaTitulo.req.rut = null;
    $scope.busquedaTitulo.req.nombre = '';

    if($scope.busquedaTitulo.resultados!==undefined){
      $scope.busquedaTitulo.resultados.length = 0;
    }

    $scope.busquedaTitulo.titulosSeleccionados = {};
    $scope.busquedaTitulo.checkTodo = false;
  };

  $scope.limpiarTitulo = function(){
    $scope.resetResultadoTitulo();

    $scope.busquedaTitulo.rut = null;
    $scope.busquedaTitulo.nombre = '';
    $scope.busquedaTitulo.foja = null;
    $scope.busquedaTitulo.numero = null;
    $scope.busquedaTitulo.ano = null;
    $scope.busquedaTitulo.bis = false;
    $scope.busquedaTitulo.anteriores = true;

    $scope.myform.$setPristine(true);

    $scope.doFocus('rutt');
  };

  $scope.limpiarFnaTitulo = function(){
    $scope.resetResultadoTitulo();

    $scope.busquedaTitulo.foja = null;
    $scope.busquedaTitulo.numero = null;
    $scope.busquedaTitulo.ano = null;
    $scope.busquedaTitulo.bis = false;
    $scope.busquedaTitulo.anteriores = true;

    $scope.myform.$setPristine(true);

    $scope.doFocus('foja');
  };

  $scope.buscarTitulo = function(){
    var foja = $scope.busquedaTitulo.foja,
      numero = $scope.busquedaTitulo.numero,
      ano = $scope.busquedaTitulo.ano,
      bis = $scope.busquedaTitulo.bis,
      anteriores = $scope.busquedaTitulo.anteriores;

    $scope.resetResultadoTitulo();

    $scope.states.titulo.isLoading = true;

    $scope.openLoadingModal( 'Buscando título...', '');

    var promise = inscripcionDigitalService.getBusquedaTitulo(foja, numero, ano, bis, anteriores);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status===null){

      }else if(data.status){

        if(data.tieneRechazo){
          $scope.openMensajeModal('error', 'Hay rechazo', 'Una solicitud con los mismos antecedentes ya ha sido rechazada anteriormente.');
        }else {

          //angular knows
          if (data.solicita) {

          } else {

          }

          if (!data.fna && data.req.ano >= 2014) {
            $scope.openMensajeModal('error', 'No está en índice', 'La información citada no ha sido encontrada en nuestros registros.');
          } else if (!data.fna && data.req.ano < 2014) {
            //TODO show warn and can continue

            $scope.openMensajeModal('warn', 'No está en índice', 'La información citada no ha sido encontrada en ' +
              'nuestros registros. <br>Si está seguro de los datos citados puede continuar.');

            $scope.busquedaTitulo.req.rut = $scope.busquedaTitulo.rut;
            $scope.busquedaTitulo.req.nombre = $scope.busquedaTitulo.nombre;

            $scope.busquedaTitulo.data = data;
            $scope.busquedaTitulo.resultados = data.aaData.slice(0, 20);
          } else {
            $scope.busquedaTitulo.req.rut = $scope.busquedaTitulo.rut;
            $scope.busquedaTitulo.req.nombre = $scope.busquedaTitulo.nombre;

            $scope.busquedaTitulo.data = data;
            $scope.busquedaTitulo.resultados = data.aaData.slice(0, 20);
          }
        }
      }else{
        $scope.raiseErr('titulo','Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('titulo','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };

  $scope.cerrarResultadoTitulo = function(){
    $scope.resetResultadoTitulo();

    $scope.doFocus('foja');
  };

  $scope.seleccionaSingle = function(id){

    if(!$scope.busquedaTitulo.titulosSeleccionados[id]){
      $scope.busquedaTitulo.checkTodo = false;
    }
  };

  $scope.seleccionarTodo = function() {

    for (var i = 0; i < $scope.busquedaTitulo.resultados.length; i++) {
      if (($scope.busquedaTitulo.resultados[i].tipoDocumento === 7 || $scope.busquedaTitulo.resultados[i].tipoDocumento === 2
        || $scope.busquedaTitulo.resultados[i].tipoDocumento === 0)) {
        if ($scope.busquedaTitulo.resultados[i].puedeSolicitar) {
          $scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[i].firma] = $scope.busquedaTitulo.checkTodo;
        }
      }
    }
  };

  $scope.solicitarTodoTitulo = function(){
    var titulosParaSolicitar = [];

    for(var i = 0; i<$scope.busquedaTitulo.resultados.length; i++){

      if($scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[i].firma]){
        titulosParaSolicitar.push(
          {foja: $scope.busquedaTitulo.resultados[i].foja,
            numero: $scope.busquedaTitulo.resultados[i].numero,
            ano: $scope.busquedaTitulo.resultados[i].ano,
            bis: $scope.busquedaTitulo.resultados[i].bis,
            firma: $scope.busquedaTitulo.resultados[i].firma
          }
        );
      }
    }

    if(titulosParaSolicitar.length>0){
      var dataJson = angular.toJson(titulosParaSolicitar);

      $scope.promiseSaveSolicitud = solicitudService.save(dataJson, $scope.busquedaTitulo.req.rut, $scope.busquedaTitulo.req.nombre);
      $scope.promiseSaveSolicitud.then(function(data) {
        if(data.status===null){

        }else if(data.status){

          for(var j = 0; j<$scope.busquedaTitulo.resultados.length; j++){

            for(var x = 0; x<titulosParaSolicitar.length; x++){

              if(titulosParaSolicitar[x].firma === $scope.busquedaTitulo.resultados[j].firma){
                $scope.busquedaTitulo.resultados[j].puedeSolicitar = false;
                $scope.busquedaTitulo.titulosSeleccionados[$scope.busquedaTitulo.resultados[j].firma] = false;
                break;
              }
            }
          }
        }else{

        }
      }, function(reason) {});
    }
  };
  //fin titulo

  //rut
  $scope.columnSortRut = function(field){
    $scope.busquedaRut.sorter.reverse = $scope.busquedaRut.sorter.predicate == field && !$scope.busquedaRut.sorter.reverse;
    $scope.busquedaRut.sorter.predicate = field;
  };

  $scope.loadMoreRut = function() {

    var carga = $scope.busquedaRut.resultados.slice($scope.listaRut.inicio, $scope.listaRut.fin);

    angular.forEach(carga, function(sol){
      $scope.listaRut.data.push(sol);
    });

    $scope.listaRut.inicio = $scope.listaRut.inicio + $scope.listaRut.offset;
    $scope.listaRut.fin = $scope.listaRut.fin + $scope.listaRut.offset;
  };

  $scope.resetResultadoRut = function(){

    if($scope.busquedaRut.resultados!==undefined){
      $scope.busquedaRut.resultados.length = 0;
    }

    if($scope.listaRut.data!==undefined){
      $scope.listaRut.data.length = 0;
    }

    $scope.busquedaRut.data = null;

    $scope.busquedaRut.fecha = null;
    $scope.listaRut.inicio = 0;
    $scope.listaRut.fin = $scope.listaRut.offset;

    $scope.cleanErr('rut');
  };

  $scope.limpiarRut = function(){
    $scope.busquedaRut.rut = null;
    $scope.busquedaRut.nombre = '';

    $scope.resetResultadoRut();

    $scope.myformRut.$setPristine(true);

    $scope.doFocus('rut');
  };

  $scope.cerrarResultadoRut = function(){
    $scope.resetResultadoRut();
    $scope.doFocus('rut');
  };

  $scope.buscarRut = function(rut){
    //var rut = $scope.busquedaRut.rut;

    $scope.resetResultadoRut();
    $scope.openLoadingModal('Buscando...', '');

    var promise = solicitudService.getByRut(rut);
    promise.then(function(data) {
      $scope.closeModal();
      if(data.status===null){

      }else if(data.status){
        $scope.busquedaRut.fecha = new Date();
        $scope.busquedaRut.data = data;
        $scope.busquedaRut.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);

        //var dataParaMostrar = $filter('filter')(data.aaData, $scope.isSolicitudRevisada, true);
//
//        if(dataParaMostrar!==undefined && dataParaMostrar.length>0){
//          $scope.busquedaRut.resultados = $filter('orderBy')(data.aaData, 'idSolicitud', true);
//        }else{
//          $scope.busquedaRut.resultados = [];
//        }
      }else{
        $scope.raiseErr('rut','Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.raiseErr('rut','Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };

  $scope.hayDataEnTablaRut = function(){

    var dataParaMostrar = $filter('filter')($scope.busquedaRut.resultados, $scope.isSolicitudRevisada, true);

    return (dataParaMostrar!==undefined && dataParaMostrar.length>0)
  };

  //fin rut

  //inicio mis
  $scope.openSolicitarEnMis = function () {
    $scope.$broadcast('sfojaIsFocused');

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

    myModal.result.then(function () {

      console.log($scope.resolveModal.refresca);

      if($scope.resolveModal.refresca){
        //$scope.buscarMis();
      }
    }, function () {
      console.log($scope.resolveModal.refresca);


      if($scope.resolveModal.refresca){
        //$scope.buscarMis();
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

    var promise = solicitudService.getByUserInSession();
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

      $rootScope.go('/verInscripcion/prop/'+solicitud.foja+'/'+solicitud.numero+'/'+solicitud.ano+'/'+bis+'/solicitudes/3');
    }else{

      var accion = '';
      if(solicitud.estadoDTO.idEstado==2){
        accion = 'remove-ok';

      }else if(solicitud.estadoDTO.idEstado==3){
        accion = 'remove-rechazo';
      }

      $scope[promesa] = solicitudService.actualizar(solicitud.idSolicitud, accion);
      $scope[promesa].then(function(data) {

        if(data.status==null){

        }else if(data.status){
          solicitud.fechaEstado = data.fechaEstado;
          solicitud.estadoDTO = {idEstado: data.idEstado, descripcion: data.descripcionEstado};

          if(accion=='remove-ok'){
            var bis = solicitud.bis==1?true:false;

            $rootScope.go('/verInscripcion/prop/'+solicitud.foja+'/'+solicitud.numero+'/'+solicitud.ano+'/'+bis+'/solicitudes/3');
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


  $scope.isSolicitudRevisada = function(sol){
    return sol.estadoDTO.idEstado!==6;
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

      if($scope.busquedaTitulo.rut!==null){
        $scope.doFocus('foja');

        if(!$.Rut.validar($scope.busquedaTitulo.rut)){
          $scope.busquedaTitulo.rut = '';
          $scope.doFocus('rutt');
        }
      }else{
        $scope.doFocus('rutt');
      }
    }else if($scope.tab.parentActive===2){
      $scope.doFocus('rut');

      if(!$.Rut.validar($scope.busquedaRut.rut)){
        $scope.busquedaRut.rut = '';
      }
    }
  }, 200);
});