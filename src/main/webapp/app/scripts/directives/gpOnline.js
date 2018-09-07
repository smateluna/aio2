'use strict';

app.directive('gpOnline', function() {
    return {
      restrict: 'E',
      replace: false,
      transclude: true,
      controller: 'GponlineCtrl',
      templateUrl: 'views/gponline.html',
      link: function(scope, element, attrs) {
          scope.busquedaGponline.borrador = attrs.borrador;
          scope.busquedaGponline.folio = attrs.folio;
          scope.req.simpleMode = attrs.simpleMode;
          scope.buscarPorBorrador();                                
      }
    };
  });