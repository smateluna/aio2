'use strict';

app.directive('gpOnline', function($timeout) {
    return {
      restrict: 'E',
      replace: false,
      transclude: true,
      controller: 'GponlineCtrl',
      templateUrl: 'views/gponline.html',
      link: function(scope, element, attrs) {
    	  scope.resetResultadoBorrador();
          scope.busquedaGponline.borrador = attrs.borrador;
          scope.busquedaGponline.folio = attrs.folio;
          scope.req.simpleMode = attrs.simpleMode;
		  $timeout(function(){			
			scope.buscarPorBorrador();  
		  },1000);
                                        
      }
    };
  });