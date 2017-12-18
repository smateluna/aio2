/**
 * Created by jaguileram on 13/05/2014.
 */

'use strict';

app.directive('utilTerminada', function () {
  return{
    scope: {
      modelo: '@' // Dynamically created ng-model in the directive element
    },
    link: function (scope, element, attrs) {

      var w = $(window);

      scope.$watch('modelo', function () {
        terminadaResize();
      });

      w.on('resize', function () {
        terminadaResize();
      });

      function terminadaResize(){
        if(attrs.modelo=='true'){
          $(element).css('height', (w.height() - 325) + 'px');
        }else{
          $(element).css('height', '0px');
        }
      }
    }
  }
});
