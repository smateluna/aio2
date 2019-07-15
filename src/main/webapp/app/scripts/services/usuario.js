'use strict';

app.factory('Usuario', function ($q, $http) {



  // Public API here
  return {

    tienePermiso: function(key, permisos){
      console.log(key);

      if( (key.indexOf('/verInscripcion')===0) ||
        key.indexOf('/verCartel')===0){
        return true;
	  }else if(key.indexOf('/verEscritura')===0){
    	return true;
      }else if(key.indexOf('/verEscrituraEstudio')===0){
    	return true;
      }else if(key.indexOf('/verGP')===0){
    	return true;
      }else if(key.indexOf('/verInscripcionReg')===0){
    	return true;
      }else if(key.indexOf('/consultadiablito')===0){
    	return true;
      }else if(key.indexOf('/verVistaPreviaPlantilla')===0){
      	return true;
      }else if(key.indexOf('/verVistaPreviaGP')===0){
        	return true;
    	
      }else {
    	  if(permisos!=null){
	        for(var i = 0; i<permisos.length; i++){
	          var perm = permisos[i];
	
	          if(perm.path === key || key.indexOf(perm.path)===0){
	            return true;
	          }else{
	            var subs = perm.subOpcionesDTO;
	
	            for(var j = 0; j<subs.length; j++){
	              var sub = subs[j];
	
	              if(sub.path === key || key.indexOf(sub.path)===0){
	                return true;
	              }
	            }
	          }
	        }
    	  }
      }
      return false;
    },
    
    getPermisosSinHome: function (permisos) {
      var array = [];
      for(var i = 0; i<permisos.length; i++){
        var perm = permisos[i];

        if(perm.id !== 'home'){
          array.push(perm);
        }
      }

      return array;
    },
    
    getSubPermisos: function (permiso, permisos) {
      var array = [];
      for(var i = 0; i<permisos.length; i++){
        var perm = permisos[i];

        if(perm.id === permiso){
          array = perm.subPermisos;
        }
      }

      return array;
    },
    
    getAllSubPermisos: function (permisos) {
        var res = {};
        for(var i = 0; i<permisos.length; i++)        	
        	res[permisos[i].id] = permisos[i].subPermisos;

        return res;
    },
    
    setPermisos: function (perm) {
      permisos = perm;
    },
    
    getUsuario: function (caratula) {
        var paramsObj = {metodo: 'getUsuario'};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/usuario',
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
      
    getPermisosUsuario: function (perfil) {
        var paramsObj = {metodo: 'getPermisosUsuario', perfil: perfil};

        var deferred = $q.defer();

        $http({
        	method: 'GET',
        	url: '../do/service/usuario',
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
      
      getPerfilesUsuario: function () {
          var paramsObj = {metodo: 'getPerfilesUsuario'};

          var deferred = $q.defer();

          $http({
          	method: 'GET',
          	url: '../do/service/usuario',
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
       
       getSessionAttribute: function (attr) {
           var paramsObj = {metodo: 'getSessionAttribute', attribute: attr};

           var deferred = $q.defer();

           $http({
           	method: 'GET',
           	url: '../do/service/usuario',
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
       
       setSessionAttribute: function (attr, val) {
           var paramsObj = {metodo: 'setSessionAttribute', attribute: attr, value: val};

           var deferred = $q.defer();

           $http({
           	method: 'GET',
           	url: '../do/service/usuario',
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
       
       setSessionActiva: function (path, perfil) {
           var paramsObj = {metodo: 'setSessionActiva', path: path, perfil: perfil};

           var deferred = $q.defer();

           $http({
           	method: 'GET',
           	url: '../do/service/usuario',
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
       
       getUsuariosLogueados: function (path) {
           var paramsObj = {metodo: 'getUsuariosLogueados', path: path};

           var deferred = $q.defer();

           $http({
           	method: 'GET',
           	url: '../do/service/usuario',
           	params: paramsObj
           	}).
             success(function(data, status, headers, config){
               deferred.resolve(data);
             }).
             error(function(data, status, headers, config){
               deferred.reject(status);
             });

           return deferred.promise;
       }
  };
});
