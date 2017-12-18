'use strict';

app.directive('myNotification', function () {
  return{
    restrict: 'E',
    scope: {
      tipo: '=',
      titulo: '=',
      texto: '='
    },
    link:function(scope, elem, attr) {

      var icono = '';

      if(scope.tipo==='warning'){
        icono = '<i class="fa fa-warning"></i> ';
      }else if(scope.tipo==='success'){
        icono = '<i class="fa fa-check"></i> ';
      }else if(scope.tipo==='clean'){
        //icono = '<i class="fa fa-check"></i> ';
      }

      $.gritter.add({
        title: icono+' '+scope.titulo,
        text: scope.texto,
        class_name: scope.tipo
      });
    }
};
});
