'use strict';

app.factory('indiceModel', function () {
  
  //titulo
  var busquedaIndice = {
    foja: null,
    numero: null,
    ano: null,
    bis: false,
    esDigital: false,
    tipodocumento: null,
    propiedades: true,
    hipoteca: false,
    prohibiciones: false,
    buscarPorInscricionHipo: false,
    buscarPorInscricionProh: false,
    exacta: false,
    rut: null,
    nombre: null,
    direccion: null,
    comuna: null,
    conservador: null,
    conservadorelegido: null,
    caratula: null,
    anoInscripcion: null,
    acto: null,
    tipo: null,
    button_clicked:false,
    resultados: [],
    resultadosprohibiciones: [],
    resultadoshipotecas: [],
    resultadoscomercio: [],
    listaactos: [],
    listatipos: [],
    listaconservadores: [],
    comunas: [],
    anosInscripciones: [],
    data: null,
    selectedRow: null,
    indicebusqueda: false
  };
  
  var paginacion = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}   
  
  var paginacioncom = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	} 
  
  var paginacionproh = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}   
  
  var paginacionhipo = {
		currentPage: 1,
		numPerPage: 10,
		maxSize: 10,
		filteredTodos: [],
		todos: []
	}   

  var busquedaMis = {
    resultados: [],
    resultadosprohibiciones: [],
    resultadoshipotecas: [],
    resultadoscomercio: [],
    data: null,
    fecha: null
  };

  var states = {
    buscar : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    },
    urgencia : {
      isLoading: false,
      isError: false,
      title: '',
      msg: ''
    }
  };
  
  var listaPropiedad = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
		   
  var listaProhibiciones = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
	
  var listaHipoteca = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
  
  var listaComercio = {
	    data: [],
	    inicio: 0,
	    fin: 20,
	    offset: 20
  };
  
  return {
    getBusquedaIndice: function () {
      return busquedaIndice;
    },
    setBusquedaIndice: function (newBusquedaIndice){
      busquedaIndice = newBusquedaIndice;
    },
    getStates: function () {
      return states;
    },
    setStates: function (newStates){
      states = newStates;
    },
    getListaPropiedad: function () {
      return listaPropiedad;
    },
    setListaPropiedad: function (newlistaPropiedad){
      listaPropiedad = newlistaPropiedad;
    },
    getListaProhibiciones: function () {
      return listaProhibiciones;
    },
    setListaProhibiciones: function (newlistaProhibiciones){
      listaProhibiciones = newlistaProhibiciones;
    },
    getListaHipoteca: function () {
      return listaHipoteca;
    },
    setListaHipoteca: function (newlistaHipoteca){
      listaHipoteca = newlistaHipoteca;
    },
    getListaComercio: function () {
      return listaComercio;
    },
    setListaComercio: function (newlistaComercio){
      listaComercio = newlistaComercio;
    },
    getListaActos: function () {
      return listaactos;
    },
    setListaActos: function (newlistaActos){
      listaactos = newlistaActos;
    },
    getListaTipos: function () {
      return listatipos;
    },
    setListaTipos: function (newlistaTipos){
      listatipos = newlistaTipos;
    },
    getListaConservadores: function () {
      return listaconservadores;
    },
    setListaConservadores: function (newlistaConservadores){
      listaconservadores = newlistaConservadores;
    },
    getListaComuna: function () {
      return comunas;
    },
    setListaComuna: function (newListaComuna){
      comunas = newlistaComuna;
    },
    getListaAnoInscripcion: function () {
      return anosInscripciones;
    },
    setListaAnoInscripcion: function (newListaAnoInscripcion){
      anosInscripciones = newlistaAnoInscripcion;
    },
    getPaginacion: function (){
    	return paginacion;
    },
    getPaginacioncom: function (){
    	return paginacioncom;
    },
    getPaginacionproh: function (){
    	return paginacionproh;
    },
    getPaginacionhipo: function (){
    	return paginacionhipo;
    },
    resetPaginacion: function (){
    	  paginacion = {
				currentPage: 1,
				numPerPage: 10,
				maxSize: 10,
				filteredTodos: [],
				todos: []
			}   
		  
		  paginacioncom = {
				currentPage: 1,
				numPerPage: 10,
				maxSize: 10,
				filteredTodos: [],
				todos: []
			} 
		  
		  paginacionproh = {
				currentPage: 1,
				numPerPage: 10,
				maxSize: 10,
				filteredTodos: [],
				todos: []
			}   
		  
		  paginacionhipo = {
				currentPage: 1,
				numPerPage: 10,
				maxSize: 10,
				filteredTodos: [],
				todos: []
			} 
    }
  };
});
