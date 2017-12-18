'use strict';

app.directive('estadoIndice', function() {
    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      controller: 'EstadoCtrl',
      templateUrl: 'views/estado.html',
      link: function(scope, element, attrs) {
          scope.req.numeroCaratula = attrs.numeroCaratula;
          scope.req.simpleMode = attrs.simpleMode;
          scope.buscar();                                
      }
    };
  });