'use strict';

app.controller('TicketInformacionCtrl', function ($scope, $rootScope, $timeout, $routeParams, $modalInstance, foja,numero,ano,nombre,direccion,comuna,registro,titulos) {

	$scope.nombre = nombre;
	$scope.direccion = direccion;
	$scope.comuna = comuna;
	$scope.foja = foja;
	$scope.numero = numero;
	$scope.ano = ano;
	$scope.usuario = $rootScope.userLoginSinCBRS;
	$scope.titulos = titulos;

	if(registro=='prop')
		$scope.registro = 'Propiedad';
	else if(registro=='hip')
		$scope.registro = 'Hipoteca';
	else if(registro=='proh')
		$scope.registro = 'Prohibiciones';
	else if(registro=='com')
		$scope.registro = 'Comercio';


	$scope.printDiv = function(divName) {

		var popupWin = window.open('', '_blank', 'width=100,height=100');
		popupWin.document.open();
		var texto = '<html><head><style><!-- @page { size:8cm 10cm; margin: 220; margin-top: 220mm;} --></style><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()"><div style=" max-width: 290px; padding: 19px 15px 10px 15px;  margin: 0 auto 20px; border: 1px solid #ccc; -webkit-border-radius: 2px; -moz-border-radius: 2px;  border-radius: 2px;-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); ">';
		texto = texto + '<h4 style="text-align:center;font-family:arial;">Para revisar por el interesado</h4>';

		if($scope.titulos==''){
			texto = texto + '<ul>';
			texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Foja: '+$scope.foja+' Numero: '+$scope.numero+' Año: '+$scope.ano+'</li>';
			if($scope.registro=='Comercio')
				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Nombre Sociedad: '+$scope.nombre+'</li>';
			//      else
			//    	texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Nombre: '+$scope.nombre+'</li>';

			if( $scope.registro=='Propiedad'){                
				if(typeof $scope.direccion!= 'undefined')  
					texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Direccion: '+$scope.direccion+'</li>';
				if(typeof $scope.comuna!= 'undefined')
					texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Comuna: '+$scope.comuna+'</li>';
			}

			texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Registro: '+$scope.registro+'</li>';
			texto = texto + '</ul>';
		}else{
			for(var i in $scope.titulos){
				var fila=$scope.titulos[i];
				
				texto = texto + '<ul>';
				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Foja: '+fila.foja+' Numero: '+fila.num+' Año: '+fila.ano+'</li>';
				if($scope.registro=='Comercio')
					texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Nombre Sociedad: '+fila.nombreSociedad+'</li>';
				//      else
				//    	texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Nombre: '+$scope.nombre+'</li>';

				if( $scope.registro=='Propiedad'){                
					if(typeof fila.dir!= 'undefined' && fila.dir!="")  
						texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Direccion: '+fila.dir+'</li>';
					if(typeof fila.comuna!= 'undefined' && fila.comuna!="")
						texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Comuna: '+fila.comuna+'</li>';
				}

				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Registro: '+$scope.registro+'</li>';
				texto = texto + '</ul>';
			}
		}


		
		texto = texto + '<hr style="margin-bottom:7px;">';
		texto = texto + '<div style="text-align:center;font-family:arial;"><h6>'+$scope.usuario+'</h6></div>';
		texto = texto + '<div style="text-align:center;font-family:arial;"><h6>www.conservador.cl</h6></div>';
		texto = texto + '</div></body></html>';

		popupWin.document.write(texto);
		popupWin.document.close();
		$timeout(function(){
			popupWin.close(); 
		},2000);

	} 

	$timeout(function(){
		$scope.printDiv('paraimprimir');
		$scope.cancel();
	},500);

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};



});