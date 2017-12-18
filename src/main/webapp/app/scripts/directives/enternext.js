'use strict';

app.directive('enterNext', function () {
    return {
      restrict: 'A',
      link: function($scope, elem, attrs) {

        elem.bind('keydown', function(e) {
          var code = e.keyCode || e.which;
          if (code === 13) {
            e.preventDefault();

            var indexNext = $('input:input, select').index(document.activeElement) + 1;

            if($('input:input, select')[indexNext].focus!==undefined)
            	$('input:input, select')[indexNext].focus();
            if($('input:input, select')[indexNext].select!==undefined)
            	$('input:input, select')[indexNext].select();
          }
        });
      }
    };
  });
