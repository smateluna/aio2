'use strict';

app.directive('requirenteFull', function (requirenteService) {
  return {
    require: 'ngModel',
    restrict: 'A',
    link: function postLink(scope, elem, attrs) {
      elem.on('blur', function (evt) {

        scope[attrs.destino].nombre = '';
        scope[attrs.destino].apep = '';
        scope[attrs.destino].apem = '';
        scope[attrs.destino].direccion = '';
        scope[attrs.destino].telefono = '';
        scope[attrs.destino].correo = '';
        scope[attrs.destino].rutConsultado = false;

        scope.$apply(function () {
          var val = elem.val();

          var promise = requirenteService.getRequirenteFull(val);
          promise.then(function(data) {
        	scope[attrs.destino].rutConsultado = true;
            if(data.status && data.hayNombre){
              scope[attrs.destino].nombre = data.nombre;
              scope[attrs.destino].apep = data.apep;
              scope[attrs.destino].apem = data.apem;
              scope[attrs.destino].direccion = data.direccion;
              scope[attrs.destino].telefono = data.telefono;
              scope[attrs.destino].correo = data.correo;              
            }
          }, function(reason) {
          });
        });
      });
    }
  };
});
