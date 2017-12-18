'use strict';

app.directive('nombreRequirente', function (requirenteService) {
  return {
    require: 'ngModel',
    restrict: 'A',
    link: function postLink(scope, elem, attrs) {
      elem.on('blur', function (evt) {

        scope[attrs.destino].nombre = '';

        scope.$apply(function () {
          var val = elem.val();

          var promise = requirenteService.getNombre(val);
          promise.then(function(data) {
            if(data.status && data.hayNombre){
              scope[attrs.destino].nombre = data.nombre;

              if(attrs.destino==='busquedaTitulo'){
                scope.$broadcast('fojaIsFocused');
              }
            }
          }, function(reason) {
          });
        });
      });
    }
  };
});
