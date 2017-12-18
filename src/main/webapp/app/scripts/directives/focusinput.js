'use strict';

app.directive('focusInput', function () {
  return function(scope, elem, attr) {
    scope.$on(attr.focusInput, function(e, name) {
      var node = elem[0].nodeName.toLowerCase();
      
      elem[0].focus();
      if(node == 'input' || node == 'textarea' || node == 'select')
      	elem[0].select();
    });
  };
});
