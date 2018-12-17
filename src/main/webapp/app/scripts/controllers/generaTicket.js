'use strict';

app.controller('generaTicketCtrl', function ($scope, $rootScope, $timeout, $routeParams, $modalInstance, colilla, tramites, registro,foja,numero,ano,requirente,rutrequirente,titulos,colillacanvas,email) {

	$scope.fechahoy = new Date().toJSON().split('T')[0];

	var fecha=new Date();

	$scope.colilla = colilla;
	$scope.tramites = tramites;
	$scope.registro = registro;
	$scope.foja = foja;
	$scope.numero = numero;
	$scope.ano = ano;
	$scope.requirente = requirente;
	$scope.usuario = $rootScope.userLoginSinCBRS;
	$scope.rutrequirente= rutrequirente;
	$scope.titulos= titulos;
	$scope.colillacanvas=colillacanvas;
	$scope.email=email;

	$scope.generaTicket=function(colilla,tramites,registro,foja,numero,ano,requirente,rutrequirente,titulos,colillacanvas){
		if(titulos!=''){
			for(j in titulos){
				var text = colilla[j];
				var altx = '';
				var opts = '';

				var bw = new BWIPJS;

				// Convert the options to a dictionary object, so we can pass alttext with
				// spaces.
				var tmp = opts.split(' '); 
				opts = {};
				for (var i = 0; i < tmp.length; i++) {
					if (!tmp[i])
						continue;
					var eq = tmp[i].indexOf('=');
					if (eq == -1)
						opts[tmp[i]] = bw.value(true);
					else
						opts[tmp[i].substr(0, eq)] = bw.value(tmp[i].substr(eq+1));
				}

				// Add the alternate text
				if (altx)
					opts.alttext = bw.value(altx);

				// Add any hard-coded options required to fix problems in the javascript
				// emulation. 
				opts.inkspread = bw.value(0);
				if (needyoffset['code39ext'] && !opts.textxalign && !opts.textyalign &&
						!opts.alttext && opts.textyoffset === undefined)
					opts.textyoffset = bw.value(-10);

				bw.bitmap(new Bitmap);

				var scl = 1; //define tamaño de codigo barra
				bw.scale(scl,scl);

				var div = document.getElementById('output');
				if (div)
					div.innerHTML = '';

				bw.push(text);
				bw.push(opts);

				try {
					bw.call('code39ext'); //tipo codigo barra
					bw.bitmap().show('canvas'+titulos[j].numero+titulos[j].anio, 'N');
				} catch(e) {
					var s = '';
					if (e.fileName)
						s += e.fileName + ' ';
					if (e.lineNumber)
						s += '[line ' + e.lineNumber + '] ';
					alert(s + (s ? ': ' : '') + e.message);
				}
			}
		} else {

			var text = colilla;
			var altx = '';
			var opts = '';

			var bw = new BWIPJS;

			// Convert the options to a dictionary object, so we can pass alttext with
			// spaces.
			var tmp = opts.split(' '); 
			opts = {};
			for (var i = 0; i < tmp.length; i++) {
				if (!tmp[i])
					continue;
				var eq = tmp[i].indexOf('=');
				if (eq == -1)
					opts[tmp[i]] = bw.value(true);
				else
					opts[tmp[i].substr(0, eq)] = bw.value(tmp[i].substr(eq+1));
			}

			// Add the alternate text
			if (altx)
				opts.alttext = bw.value(altx);

			// Add any hard-coded options required to fix problems in the javascript
			// emulation. 
			opts.inkspread = bw.value(0);
			if (needyoffset['code39ext'] && !opts.textxalign && !opts.textyalign &&
					!opts.alttext && opts.textyoffset === undefined)
				opts.textyoffset = bw.value(-10);

			bw.bitmap(new Bitmap);

			var scl = 1; //define tamaño de codigo barra
			bw.scale(scl,scl);

			var div = document.getElementById('output');
			if (div)
				div.innerHTML = '';

			bw.push(text);
			bw.push(opts);

			try {
				bw.call('code39ext'); //tipo codigo barra
				bw.bitmap().show('canvas', 'N');
			} catch(e) {
				var s = '';
				if (e.fileName)
					s += e.fileName + ' ';
				if (e.lineNumber)
					s += '[line ' + e.lineNumber + '] ';
				alert(s + (s ? ': ' : '') + e.message);
			}

		}


	};

	$scope.printDiv = function(divName) {
		if(titulos!=''){
			var popupWin = window.open('', '_blank', 'width=100,height=100');
			popupWin.document.open();
			
			var texto = '<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()"><div style=" max-width: 290px; padding: 19px 15px 10px 15px;  margin: 0 auto 20px; border: 1px solid #ccc; -webkit-border-radius: 2px; -moz-border-radius: 2px;  border-radius: 2px;-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); ">';

			texto = texto + '<h4 style="text-align:center;font-family:arial;">Solicitud Caratula</h4>';

			for(j in titulos){
				var canvas = document.getElementById("canvas"+titulos[j].numero+titulos[j].anio); 
				var img = canvas.toDataURL("image/png");
				//	  var printContents = document.getElementById(divName).innerHTML;
				//	  var originalContents = document.body.innerHTML;        

				texto = texto + '<div style="text-align:center">';
				texto = texto + 	'<img width=230 height=100 src="'+img+'"/>';
				texto = texto + '</div>';
				texto = texto + '<ul>';
				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Numero Colilla: '+$scope.colilla[j]+'</li>';

				texto = texto + '<br><li style="font-family:arial;font-weight: normal;font-size: 14px;">Registro: '+$scope.registro+'</li>';
				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Foja: '+titulos[j].foja+' Numero: '+titulos[j].numero+' Año: '+titulos[j].anio+'</li>';
				var tramite=tramites[j];
				for(var i = 0; i<tramite.length;i++){
					texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">'+tramite[i].nombre+'</li>';

					if(tramite[i].obs!='')
						texto = texto + '<li style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">'+tramite[i].obs+'</li>';

					texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;"> Valor $'+tramite[i].valor+'</li>';
				}
				texto = texto + '</ul>';
				texto = texto + '<hr style="margin-bottom:7px;">';

			}
			var mes=fecha.getMonth()+1;

			texto = texto + '<label style="font-family:arial;font-weight: normal;font-size: 14px;">Fecha Solicitud: '+fecha.getDate() + "-" + mes + "-" + fecha.getFullYear() + " " +fecha.getHours() + ":" + fecha.getMinutes() +'</label><br>';

			texto = texto + '<label style="font-family:arial;font-weight: normal;font-size: 14px;">Rut Req.: '+$scope.rutrequirente.substring(0,$scope.rutrequirente.length-1)+'-'+$scope.rutrequirente.substring($scope.rutrequirente.length-1,$scope.rutrequirente.length)+'</label><br>';
			texto = texto + '<label style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">Requirente: '+$scope.requirente+'</label><br>';
			texto = texto + '<label style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">Email: '+$scope.email+'</label>';

			texto = texto + '<div style="text-align:center;font-family:arial;"><h6>'+$scope.usuario+'</h6></div>';
			texto = texto + '<div style="text-align:center;font-family:arial;"><h6>Atenci\u00F3n de P\u00FAblico<br>Lunes a Viernes de 8:30 a 15:00 Hrs.<br>Morande 440, Santiago Centro<br>www.conservador.cl</h6></div>';

			texto = texto + '</div></body></html>';
		}	else {
			var canvas = document.getElementById("canvas"); 
			var img = canvas.toDataURL("image/png");
			//	  var printContents = document.getElementById(divName).innerHTML;
			//	  var originalContents = document.body.innerHTML;        
			var popupWin = window.open('', '_blank', 'width=100,height=100');
			popupWin.document.open();
			var texto = '<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()"><div style=" max-width: 290px; padding: 19px 15px 10px 15px;  margin: 0 auto 20px; border: 1px solid #ccc; -webkit-border-radius: 2px; -moz-border-radius: 2px;  border-radius: 2px;-moz-box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05); ">';

			texto = texto + '<h4 style="text-align:center;font-family:arial;">Solicitud Caratula</h4>';
			texto = texto + '<div style="text-align:center">';
			texto = texto + 	'<img width=230 height=100 src="'+img+'"/>';
			texto = texto + '</div>';
			texto = texto + '<ul>';
			texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Numero Colilla: '+$scope.colilla+'</li>';

			var mes=fecha.getMonth()+1;

			texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Fecha Solicitud: '+fecha.getDate() + "-" + mes + "-" + fecha.getFullYear() + " " +fecha.getHours() + ":" + fecha.getMinutes() +'</li>';

			texto = texto + '<label style="font-family:arial;font-weight: normal;font-size: 14px;">Rut Req.: '+$scope.rutrequirente.substring(0,$scope.rutrequirente.length-1)+'-'+$scope.rutrequirente.substring($scope.rutrequirente.length-1,$scope.rutrequirente.length)+'</label><br>';
			texto = texto + '<label style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">Requirente: '+$scope.requirente+'</label><br>';
			texto = texto + '<label style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">Email: '+$scope.email+'</label>';
			texto = texto + '<br><li style="font-family:arial;font-weight: normal;font-size: 14px;">Registro: '+$scope.registro+'</li>';
			texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">Foja: '+$scope.foja+' Numero: '+$scope.numero+' Año: '+$scope.ano+'</li>';

			for(var i = 0; i<tramites.length;i++){
				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;">'+tramites[i].nombre+'</li>';

				if(tramites[i].obs!='')
					texto = texto + '<li style="word-wrap: break-word;font-family:arial;font-weight: normal;font-size: 14px;">'+tramites[i].obs+'</li>';

				texto = texto + '<li style="font-family:arial;font-weight: normal;font-size: 14px;"> Valor $'+tramites[i].valor+'</li>';
			}

			texto = texto + '</ul>';
			
			texto = texto + '<hr style="margin-bottom:7px;">';
			texto = texto + '<div style="text-align:center;font-family:arial;"><h6>'+$scope.usuario+'</h6></div>';
			texto = texto + '<div style="text-align:center;font-family:arial;"><h6>Atenci\u00F3n de P\u00FAblico<br>Lunes a Viernes de 8:30 a 15:00 Hrs.<br>Morande 440, Santiago Centro<br>www.conservador.cl</h6></div>';
			texto = texto + '</div></body></html>';
		}
		//	  var texto = '<html><head><link rel="stylesheet" type="text/css" href="style.css" /></head><body onload="window.print()"><center><h4>Solicitud Caratula</h4></center><center><img src="'+img+'"/></center><center><h5>Numero Colilla: '+$scope.colilla+'</h5><h5 class="text-center">Registro: '+$scope.registro+'</h5><h6 class="text-center">Foja: '+$scope.foja+' Numero: '+$scope.numero+' Año: '+$scope.ano+'</h6></center>';
		//	  
		//	  texto = texto + '<table class="text-center">';
		//	  for(var i = 0; i<tramites.length;i++){
		//   		 texto = texto + '<tr><td><h6> * '+tramites[i].nombre+'</h6></td>';
		//      }
		//	  texto = texto + '</table>';
		//	  texto = texto+'<center><h6>www.conservador.cl</h6></center><br>&nbsp</html>';
		popupWin.document.write(texto);
		popupWin.document.close();
		$timeout(function(){
			popupWin.close(); 
		},500);

	} 

	$timeout(function(){
		$scope.generaTicket(colilla,tramites,registro,foja,numero,ano,requirente,rutrequirente,titulos);
	},1000);

	$timeout(function(){
		$scope.printDiv('paraimprimir');
		$scope.cancel();
	},1000);

	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};



});