/**
 * Created by jaguileram on 07/07/2014.
 */

'use strict';

app.directive('resizeInscripcion', function ($window) {
  return function (scope, element, attrs) {
    var w = angular.element($window);
    scope.getWindowDimensions = function () {
      return { 'h': w.height() };
    };
    scope.$watch(scope.getWindowDimensions, function (newValue, oldValue) {
      scope.windowHeight = newValue.h;

      scope.style = function () {
        return {
          'height': (newValue.h - 46) + 'px'
        };
      };

      scope.styleImagen = function () {
        return {
          'height': (newValue.h - 72) + 'px'
        };
      };

      scope.styleImagenDOC = function () {
        return {
          'height': (newValue.h - 85) + 'px'
        };
      };

    }, true);

    w.bind('resize', function () {
      scope.$apply();
    });
  };
});