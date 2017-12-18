'use strict';

app.factory('reingresoEscrituraModel', function () {

	//titulo
	var busquedaReingreso = {
		caratula: null,
		observacion1: null,
		observacion2: null,
		escrituras: [],
		notario: null,
		codescritura: null,
		notario2: null,
		codescritura2: null,
		data: null,
		predicate: null,
		reverse: null,
		version: null
	};

	var states = {
		buscar : {
		isLoading: false,
		isError: false,
		title: '',
		msg: ''
	}
	};

	var statesEscritura = {
		ok: null,
		msg: null
	};
	var statesListado = {
			ok: null,
			msg: null
		};	
	var statesDescarga = {
		ok: null,
		msg: null
	};
	var statesAnexo = {
		ok: null,
		msg: null
	};
	var statesDescargaFusion = {
		ok: null,
		msg: null
	};

	return {
		getBusquedaReingreso: function () {
		return busquedaReingreso;
	},
	setBusquedaReingreso: function (newBusquedaReingreso){
		busquedaReingreso = newBusquedaReingreso;
	},
	getStates: function () {
		return states;
	},
	setStates: function (newStates){
		states = newStates;
	},
	getStatesEscritura: function () {
		return statesEscritura;
	},
	setStatesEscritura: function (newStatesEscritura){
		statesEscritura = newStatesEscritura;
	},
	getStatesDescarga: function () {
		return statesDescarga;
	},
	setStatesDescarga: function (newStatesDescarga){
		statesDescarga = newStatesDescarga;
	},
	getStatesListado: function () {
		return statesListado;
	},
	setStatesListado: function (newStatesListado){
		statesListado = newStatesListado;
	},	
	getStatesAnexo: function () {
		return statesAnexo;
	},
	setStatesAnexo: function (newStatesAnexo){
		statesAnexo = newStatesAnexo;
	},
	getStatesDescargaFusion: function () {
		return statesDescargaFusion;
	},
	setStatesDescargaFusion: function (newStatesDescargaFusion){
		statesDescargaFusion = newStatesDescargaFusion;
	}
	};
});
