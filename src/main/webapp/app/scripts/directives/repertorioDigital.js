'use strict';

app.directive('repertorioDigital', function() {
    return {
      restrict: 'E',
      replace: true,
      transclude: true,
      controller: 'RepertorioCtrl',
      templateUrl: 'views/repertorio.html',
      link: function(scope, element, attrs) {
      	  scope.busquedaRepertorio.repertorio = attrs.numeroRepertorio;
          scope.busquedaRepertorio.simpleMode = attrs.simpleMode;
          scope.buscar();                                
      }
    };
  });