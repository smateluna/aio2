'use strict';

app.directive('myLoad', function() {
  return function(scope, element, attrs, controller) {
      element.bind('load', function(evt) {
        angular.element( '#'+attrs.myLoad ).addClass('ng-hide');
        element.removeClass('ng-hide');
        scope.$apply(attrs.pgLoad);
      });
  };
});
