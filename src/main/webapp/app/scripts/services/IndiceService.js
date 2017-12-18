'use strict';

app.factory('indiceService', function ($http, $q, $modal, $rootScope) {
	var indiceService = this;
	return {
		getIndicePropiedades: function (rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,acto,tipo,exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador) {
			var paramsObj = {metodo: 'getIndicePropiedades', rut:rut,apellidos:apellidos,direccion:direccion,foja:foja, numero:numero, ano:ano, bis:bis, comuna:comuna, anoInscripcion:anoInscripcion, exacta:exacta, acto:acto, tipo:tipo,regPropiedades:regPropiedades,regHipoteca:regHipoteca,regProhibiciones:regProhibiciones,regComercio:regComercio,buscarPorInscricionHipo:buscarPorInscricionHipo,buscarPorInscricionProh:buscarPorInscricionProh,conservador:conservador};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},obtenerActos: function () {
			var paramsObj = {metodo: 'obtenerActos'};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},obtenerTipos: function () {
			var paramsObj = {metodo: 'obtenerTipos'};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},obtenerCBRSDisponibles: function () {
			var paramsObj = {metodo: 'obtenerCBRSDisponibles'};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},guardaensesion: function (foja, num, ano, bis, registro, idregistro) {
			var paramsObj = {metodo: 'guardaensesion', foja:foja, num:num, ano:ano, bis:bis, registro:registro, idregistro:idregistro};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},exportarExcel: function (rut,apellidos,direccion,foja, numero, ano, bis, comuna, anoInscripcion,acto,tipo,exacta,regPropiedades,regHipoteca,regProhibiciones,regComercio,buscarPorInscricionHipo,buscarPorInscricionProh,conservador) {
			var paramsObj = {metodo: 'exportarExcel', rut:rut,apellidos:apellidos,direccion:direccion,foja:foja, numero:numero, ano:ano, bis:bis, comuna:comuna, anoInscripcion:anoInscripcion, exacta:exacta, acto:acto, tipo:tipo,regPropiedades:regPropiedades,regHipoteca:regHipoteca,regProhibiciones:regProhibiciones,regComercio:regComercio,buscarPorInscricionHipo:buscarPorInscricionHipo,buscarPorInscricionProh:buscarPorInscricionProh,conservador:conservador};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},
		getRutSesion: function () {
			var paramsObj = {metodo: 'getRutSesion'};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},agregarNuevaAtencion: function (rut) {
			var paramsObj = {metodo: 'agregarNuevaAtencion', rut:rut};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},obtenerAtencionPorDia: function () {
			var paramsObj = {metodo: 'obtenerAtencionPorDia'};

			var deferred = $q.defer();

			$http({
				method: 'GET',
				url: '../do/service/indice',
				params: paramsObj
			}).
			success(function(data, status, headers, config){
				deferred.resolve(data);
			}).
			error(function(data, status, headers, config){
				deferred.reject(status);
			});

			return deferred.promise;
		},contarAtencionService: function (titulo) {
			if('Índice'==titulo){
				$rootScope.$broadcast('eventoIndice', {
	                data: 'something'
	            });
			}else{
				var myModal = $modal.open({
					templateUrl: 'ingresaRutModal.html',
					backdrop: true,
					windowClass: 'modal',
					controller: 'ingresaRutModalCtrl'
				});
			}
		}
	};
});
