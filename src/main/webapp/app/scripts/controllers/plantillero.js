'use strict';

app.controller('PlantilleroCtrl', function ($log, $rootScope, $scope, $modal, $timeout, $modalStack, $sce, firmaService,taOptions,certificacionService) {

	$scope.fechahoy = new Date().toJSON().split('T')[0];

	$scope.entity = {
			fields : null
//			[
//			{type: "solonumero", name: "valor", label: "Valor" , max:10, required: true, data:""},
//			{type: "text", name: "Texto", label: "Texto" , max:100, required: true, data:""},
//			{type: "radio", name: "radio_id", label: "Radio" , options:[{id: 1, name: "name1"},{id: 2, name: "name2"},{id: 3, name: "name3"},{id: 4, name: "name4"}], required: true, data:""},
//			{type: "email", name: "emailUser", label: "Email" , required: true, data:""},
//			{type: "password", name: "pass", label: "Password" , min: 6, max:20, required: true, data:""},
//			{type: "select", name: "select_id", label: "Select" , options:[{name: "name1"},{name: "name2"},{name: "name3"},{name: "name4"}], required: true, data:""},
//			{type: "checkbox", name: "check_id", label: "Checkbox" , options:[{id: 1, name: "name1"},{id: 2, name: "name2"},{id: 3, name: "name3"},{id: 4, name: "name4"}], required: true, data:""}
//			{type: "date", name: "Fecha", label: "Fecha" , required: true, data:""}
//			]
	};
	
	$scope.field = {
			model:"200"
	};

	$scope.plantillero = {
			tipocertificado : null,
			cuerpocertificado : null,
			copiatemplate : null,
			tiposcertificados : null,
			caratula : null,
			valor : null,
			mostrarcampotexto : false,
			mostrarboton : 0
	};	

	$scope.states = {
			isLoading: false,
			isError: false,
			isOk: false,
			title: null,
			msg: null
	};
	
	taOptions.toolbar = [['html']];

	$scope.obtenerTiposCertificadosPorPerfil = function(){
		$scope.openLoadingModal('Cargando Listado...', '');

		var promise = firmaService.obtenerTiposCertificadosPorPerfil($rootScope.idPerfil);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){
			}else if(data.status){

				$scope.plantillero.tiposcertificados=data.listatiposCertificados;

				$scope.doFocus('caratula');

			}else{
				$scope.raiseErr('data.msg', data.msg);
			}
		}, function(reason) {
			$scope.raiseErr('Problema contactando al servidor.', '');
			$scope.closeModal();
		});
	};

	$scope.obtenertemplate = function(){
		if($scope.plantillero.tipocertificado){
			$scope.plantillero.cuerpocertificado = $scope.plantillero.tipocertificado.plantillas.plantillaTemplate;
			$scope.plantillero.copiatemplate = $scope.plantillero.tipocertificado.plantillas.plantillaTemplate;
			$scope.entity.fields=JSON.parse($scope.plantillero.tipocertificado.plantillas.json);
			$scope.plantillero.valor = $scope.plantillero.tipocertificado.plantillas.valor;
		}else
			$scope.plantillero.cuerpocertificado = '';

		$scope.reemplazaData();
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

	$scope.limpiarPlantillero = function(){

		$scope.plantillero.tipocertificado = null;
		$scope.plantillero.cuerpocertificado = null;
		$scope.plantillero.caratula = null;
		$scope.plantillero.copiatemplate = null;
		
		$scope.states.isLoading = false;
		$scope.states.isError = false;
		$scope.states.isOk = false;
		$scope.states.title = null;
		$scope.states.msg = null;
		
		$scope.myform.$setPristine(true);

		$scope.doFocus('caratula');

	};

	$scope.doFocus = function(name){
		$scope.$broadcast(name+'IsFocused');
	};

	$timeout(function(){
		$scope.obtenerTiposCertificadosPorPerfil();
	}, 200);

	$scope.reemplazaData = function(){
		if($scope.plantillero.cuerpocertificado){
			var texto = $scope.plantillero.copiatemplate;
			if($scope.plantillero.caratula)
				texto = texto.replace("_CARATULA_", $scope.plantillero.caratula);
			if($scope.plantillero.valor)
				texto = texto.replace("_VALOR_", $scope.plantillero.valor);

			angular.forEach($scope.entity.fields, function(value, key) {
				if(value.model){
					value.data = $scope.$eval(value.model)
				}
					
				if(value.type!='button'){
					if(value.data){
						if(value.type=='select')
							texto = texto.replace("_*_", value.data.name);
						else if(value.type=='date')
							texto = texto.replace("_*_",$scope.obtenerFechaEnPalabras(value.data));
						else
							texto = texto.replace("_*_", value.data);
					}else{
						texto = texto.replace("_*_", ".......");
					}
				}
			});

			$scope.plantillero.cuerpocertificado = texto;
//			$scope.plantillero.cuerpocertificado=$sce.trustAsHtml(texto);
		}
	};

	$scope.vistaPrevia = function(){

		$scope.openLoadingModal('Generando Vista Previa...', '');

		var promise = certificacionService.vistaprevia($scope.plantillero.caratula,$scope.plantillero.tipocertificado.plantillas.plantillaCertificado,$scope.plantillero.cuerpocertificado,$scope.plantillero.tipocertificado.plantillas.fePDocumentoTipos.identificador,$scope.plantillero.valor);
		promise.then(function(data) {
			$scope.closeModal();
			if(data.status===null){

			}else if(data.status){

				$scope.raiseOk('', 'Certificacion realizada');

				$scope.states.isError= false;

				$rootScope.go('/verVistaPreviaPlantilla/'+$scope.plantillero.caratula+'/'+$scope.plantillero.tipocertificado.plantillas.fePDocumentoTipos.identificador+'/plantillero/');

				$scope.limpiarPlantillero();
			}else{
				$scope.states.isOk= false;
				$scope.raiseErr('No se pudo certificar caratula', data.msg);
			}
		}, function(reason) {
			$scope.states.isOk= false;
			$scope.raiseErr('Problema detectado', 'No se ha podido establecer comunicación con el servidor.');
			$scope.closeModal();
		});

	};

	$scope.raiseErr = function(title, msg){
		$scope.states.isError = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};

	$scope.raiseOk = function(title, msg){
		$scope.states.isOk = true;
		$scope.states.title = title;
		$scope.states.msg = msg;
	};

	$scope.multiple = function(valor, multiple){
		var resto = valor % multiple;
		if(resto==0)
			return true;
		else
			return false;
	};

	$scope.add = function(jsonAgregar){

		var newJson = { 
				fiels : [] 
		};

		var copiaJson  = JSON.stringify(jsonAgregar);

		var texto='';
		angular.forEach($scope.entity.fields, function(value, key) {
			if(value.type == "button"){

				var jsonanterior = newJson.fiels[key-1];
				jsonanterior.saltolinea=true;

				var idgenerado;
				angular.forEach(JSON.parse(copiaJson), function(value2, key2) {
					if(key2==0){
						idgenerado=Math.floor((Math.random() * 1000) + 1);
					}
					value2.id = 'A'+idgenerado;
					newJson.fiels.push(value2);
				});

				texto = $scope.plantillero.copiatemplate;
				if(!value.textotemplate.endsWith('<br />'))
					texto = texto.replace(value.textotemplate, value.textotemplate+" ,"+value.textotemplate);
				else
					texto = texto.replace(value.textotemplate, value.textotemplate+value.textotemplate);

				$scope.plantillero.copiatemplate = texto;
				$scope.plantillero.cuerpocertificado = texto;

				newJson.fiels.push(value);

			}else{
				value.id = key;

				newJson.fiels.push(value);
			}
		});

		$scope.entity.fields=newJson.fiels;
		$scope.plantillero.mostrarboton = $scope.plantillero.mostrarboton+1;
		
		$scope.reemplazaData();
	};

	$scope.remove = function(jsonQuitar){
		var sigueIteracion = true;
		var texto='';
		angular.forEach($scope.entity.fields, function(value, key) {
			if(sigueIteracion){ 
				var elementos = jsonQuitar.length; 
				
				if(value.type == "button"){
					$scope.entity.fields.splice(key-elementos,elementos);
					sigueIteracion=false;
					
					var jsonanterior = $scope.entity.fields[key-elementos-1];
					jsonanterior.saltolinea=false;
					
					texto = $scope.plantillero.copiatemplate;
					if(!value.textotemplate.endsWith('<br />'))
						texto = texto.replace(" ,"+value.textotemplate, "");
					else{
						var lastIndex = texto.lastIndexOf('<br />'+value.textotemplate);
						var beginString = texto.substring(0, lastIndex);
					    var endString = texto.substring(lastIndex + (value.textotemplate).length);
					    texto = beginString + endString;
					}
					
					$scope.plantillero.copiatemplate = texto;
				}
								
			}
		});
		
		$scope.plantillero.mostrarboton = $scope.plantillero.mostrarboton-1;
		$scope.reemplazaData();
	};
	
	$scope.obtenerFechaEnPalabras = function(fecha){
		var parts = fecha.split("-");
		var meses = new Array ("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
		var f=new Date(parts[0], parts[1] - 1, parts[2]);
		return f.getDate() + " de " + meses[f.getMonth()] + " de " + f.getFullYear();
	};

})

.directive("dynamicName",function($compile){
	return {
		restrict:"A",
		terminal:true,
		priority:1000,
		link:function(scope,element,attrs){
			element.attr('name', scope.$eval(attrs.dynamicName));
			element.removeAttr("dynamic-name");
			$compile(element)(scope);
		}
	}
})
