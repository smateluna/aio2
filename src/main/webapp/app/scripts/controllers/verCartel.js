/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.controller('VerCartelCtrl', function ($scope, $routeParams, $rootScope, $sce, $timeout, $modalStack, $modal, $filter, CartelesService, cartelesModel) {

  $scope.isLoading = true;
  $scope.actualizandoAlgo = false;

  $scope.estadoDocumento = cartelesModel.getEstadoDocumento();

  $scope.hoy = moment().format('DD/MM/YYYY');
  $scope.paginas = [];

  $scope.errorObj = {
    hay: false,
    level: 'warning', //warning o error
    title: '',
    msg: ''
  };

  $scope.imagen = {
    page: 1
  };

  $scope.tab = {
    parentActive: 1
  };

  $scope.parametros = {
    numero: $routeParams.numero,
    mes: $routeParams.mes,
    ano: $routeParams.ano,
    registro: $routeParams.registro,
    bis: $routeParams.bis,
    origen: $routeParams.origen,
    tipo: $routeParams.tipo,
    rehacer: $routeParams.rehacer
  };

  $scope.formulario = {
    caratula: '',
    numero: '',
    mes: '',
    ano: '',
    bis: false,
    paginaDesde: '',
    paginaHasta: ''
  };

  $scope.certificados = [];

  //controles tabs
  $scope.isActiveParent = function(id){
    return $scope.tab.parentActive === id;
  };

  $scope.activateParent = function(id){
    $scope.tab.parentActive = id;
  };
  //fin controles tabs

  $scope.buscarCartel = function(){
    var numero = $scope.parametros.numero,
      mes = $scope.parametros.mes,
      ano = $scope.parametros.ano,
      registro = $scope.parametros.registro,
      bis  = $scope.parametros.bis,
      tipo  = $scope.parametros.tipo;

    //cartelesModel.resetEstadoDocumento();

    //$scope.openLoadingModal( 'Buscando...', '');

    var promise =  CartelesService.buscarCartel(numero, mes, ano, registro, bis, tipo);
    promise.then(function(data) {
      //$scope.closeModal();
      $scope.isLoading = false;
      if(data.status===null){

      }else if(data.status){

        if(data.cartelDTO.hayArchivo){
          //cartelesModel.setEstadoDocumento(data.cartelDTO);        
            var promise = CartelesService.listadoCertificadosByUserInSession();
            promise.then(function(data) {
              if(data.status===null){
              }else if(data.status){
//                $scope.certificados.push($filter('orderBy')(data.aaData, 'fechaCreacion', true));
            	for (var i = 0; i < data.aaData.length; i++) 
            		if(data.aaData[i].mes==mes && data.aaData[i].ano==ano && data.aaData[i].numero==numero && data.aaData[i].idReg==registro)
            			$scope.certificados.push(data.aaData[i]);        		
              }else{
                $scope.openMensajeModal('error', 'Problema detectado', data.msg);
              }
            }, function(reason) {
              $scope.raiseErr('mis', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
            });

          $scope.estadoDocumento = data.cartelDTO;

          if(data.cartelDTO.tipoArchivoDTO.tipoArchivo===0){ //tomo

                $scope.iniciaVisualizador();

          }else if(data.cartelDTO.tipoArchivoDTO.tipoArchivo===1){ //cartel
            $scope.iniciaVisualizador();
          }
        }else{
          $scope.openMensajeModal('info', 'No documento para cartel', '');
        }
      }else{
        $scope.openMensajeModal('error', 'Problema detectado', data.msg);
      }
    }, function(reason) {
      $scope.isLoading = false;
      //$scope.openMensajeModal('error', 'Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
    });
  };

  $scope.cerrar = function(){
    $rootScope.go('/'+$scope.parametros.origen);
  };

  $scope.hayArchivo = function(){
    return $scope.estadoDocumento.hayArchivo;
  };

  $scope.hayPaginas = function(){
    return ($scope.estadoDocumento.cantidadPaginas > 0);
  };

  $scope.certificarCartel = function(){

    //$scope.actualizandoAlgo = true;

    $scope.openLoadingModal( 'Creando certificado...', '');


    var caratula = $scope.formulario.caratula,
      numero,
      mes = $scope.estadoDocumento.mes,
      ano = $scope.estadoDocumento.ano,
      registro = $rootScope.aioParametros.sistema=='AIO'?1:$scope.estadoDocumento.registro,
      bis, pdesde, phasta,
      tipo = $scope.estadoDocumento.tipoArchivoDTO.tipoArchivo;

    if(tipo===0){ //tomo
      numero = $scope.formulario.numero;
      bis = $scope.formulario.bis;
      pdesde = $scope.formulario.paginaDesde;
      phasta = $scope.formulario.paginaHasta;

    }else if(tipo===1){ //cartel
      numero = $scope.estadoDocumento.numero;
      bis = $scope.estadoDocumento.bis;
      pdesde = 0;
      phasta = 0;
    }

    CartelesService.certificar(caratula, numero, mes, ano, registro, bis, pdesde, phasta, tipo).then(function(data){

      if(data.status){
        $scope.openMensajeModal('info', 'Certificado creado exitosamente.', '');

        $timeout(function(){

          $scope.closeModal();

        }, 2000);

        $scope.certificados.push({idCertificado: data.idCertificado, caratula: caratula, numero: numero, mes: mes, ano: ano, idReg: registro, bis: bis, pdesde: pdesde, phasta: phasta, tipo: tipo});
        $scope.limpiarFormulario();
      }else{
        $scope.openMensajeModal('error', 'Problema detectado creando certificado.', '');
      }

      //$scope.actualizandoAlgo = false;
    }, function(err){
      //$scope.actualizandoAlgo = false;
      $scope.closeModal();
    });

  };


  $scope.irRehacer = function(cartel){
    //cartelesModel.resetEstadoDocumento();

      $rootScope.go('/verCartel/'+cartel.numero+'/'+cartel.mes+'/'+cartel.ano+'/'+cartel.registro+'/'+cartel.bis+'/0/carteles/true');

  };

  $scope.rehacer = function(){

    $scope.openLoadingModal( 'Regenerando cartel...', '');

    var numero = $scope.formulario.numero,
      mes = $scope.estadoDocumento.mes,
      ano = $scope.estadoDocumento.ano,
      registro = $scope.estadoDocumento.registro,
      bis = $scope.formulario.bis,
      pdesde = $scope.formulario.paginaDesde,
      phasta = $scope.formulario.paginaHasta;

    CartelesService.rehacer(numero, mes, ano, registro, bis, pdesde, phasta).then(function(data){

      if(data.status){
        $scope.limpiarFormulario();

        $scope.openMensajeModal('info', 'Cartel generado exitosamente', 'Redireccionando...');

        $timeout(function(){
          $scope.closeModal();
          $rootScope.go('/verCartel/'+numero+'/'+mes+'/'+ano+'/'+registro+'/'+bis+'/1/carteles/');

        }, 2000);

      }else{
        $scope.openMensajeModal('error', 'Problema detectado regenerando cartel.', '');}
      $scope.actualizandoAlgo = false;
    }, function(err){
      $scope.openMensajeModal('error', 'Problema detectado regenerando cartel.', '');
    });

  };

  $scope.eliminar = function(index, idCertificado, registro){

    if(confirm('Desea eliminar el certificado?')){
      $scope.actualizandoAlgo = true;

      CartelesService.eliminar(idCertificado, registro).then(function(data){

        $scope.certificados.splice(index, 1);

        $scope.actualizandoAlgo = false;
      }, function(err){
        $scope.actualizandoAlgo = false;
      });
    }
  };


  $scope.firmar = function(index, idCertificado, registro){

    //$scope.actualizandoAlgo = true;

    $scope.openLoadingModal( 'Firmando...', '');

    CartelesService.firmar(idCertificado, registro).then(function(data){
      //$scope.actualizandoAlgo = false;

      if(data.status){
        $scope.openMensajeModal('success', 'Documento firmado exitosamente.', '');

        $timeout(function(){

          $scope.closeModal();

        }, 2000);

        $scope.certificados.splice(index, 1);
      }else{
        $scope.openMensajeModal('error', 'Problema detectado firmando documento.', '');

      }

    }, function(err){
      //$scope.actualizandoAlgo = false;
      $scope.closeModal();
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



  $scope.iniciaVisualizador = function(){
    $scope.isLoading = false;

    if($scope.estadoDocumento.hayArchivo){

      var numero = $scope.estadoDocumento.numero,
        mes = $scope.estadoDocumento.mes,
        ano = $scope.estadoDocumento.ano,
        registro = $scope.estadoDocumento.registro,
        bis = $scope.estadoDocumento.bis,
        tipoArchivoDTO = $scope.estadoDocumento.tipoArchivoDTO;

      for(var i = 0; i<$scope.estadoDocumento.cantidadPaginas; i++){
        var d = new Date(),
          dateNumber = d.getTime(),
          num = i + 1;

        var url = '../do/service/carteles?v='+dateNumber+'&metodo=getJPG&pagina='+num+'&numero='+numero+'&mes='+mes+'&ano='+ano+'&registro='+registro+'&bis='+bis+'&tipoArchivo='+tipoArchivoDTO.tipoArchivo;

        var urlProc = $sce.trustAsResourceUrl(url);

        $scope.paginas.push({pagina: i+1, numero: numero, mes: mes, ano: ano, bis: bis, tipoArchivo: tipoArchivoDTO.tipoArchivo, url: urlProc});
      }
    }
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

  $scope.verCertificado = function(idCertificado, registro){
    $scope.openDocumentoModal('../do/service/carteles?metodo=getPDF&idCertificado='+idCertificado+'&registro='+registro);
  };

  $scope.closeModal = function(){
    var top = $modalStack.getTop();
    if (top) {
      $modalStack.dismiss(top.key);
    }
  };

  $scope.limpiarFormulario = function(){
    $scope.formulario.caratula = '';
    $scope.formulario.numero = '';
    $scope.formulario.mes = '';
    $scope.formulario.ano = '';
    $scope.formulario.bis = false;
    $scope.formulario.paginaDesde = '';
    $scope.formulario.paginaHasta = '';

    $scope.doFocus('caratula');
  };

  //validaciones titulo
  $scope.archivoNacional = function(value){
    return !(value<$rootScope.aioParametros.anoArchivoNacional);
  };

  $scope.anoActual = function(value){
    return !(moment(new Date()).year()<value);
  };
  //fin validaciones titulo

  $scope.doFocus = function(name){
    $scope.$broadcast(name+'IsFocused');
  };


    $scope.buscarCartel();

});