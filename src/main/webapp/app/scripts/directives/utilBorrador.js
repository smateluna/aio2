/**
 * Created by jaguileram on 13/05/2014.
 */

'use strict';

app.directive('utilBorrador', function () {
  return{
    scope: {
      modelo: '@' // Dynamically created ng-model in the directive element
    },
    link: function (scope, element, attrs) {

      var w = $(window);

      scope.$watch('modelo', function () {
        borradorResize();
      });

      w.on('resize', function () {
        borradorResize();
      });

      function borradorResize(){
        if(attrs.modelo=='true'){
          $(element).css('height', (w.height() - 325) + 'px');
        }else{
          $(element).css('height', '0px');
        }
      }
    }
  }
});
