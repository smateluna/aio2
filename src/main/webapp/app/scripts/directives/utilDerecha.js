/**
 * Created by jaguileram on 13/05/2014.
 */

'use strict';

app.directive('utilDerecha', function () {
  return{
    scope: {
      modelod: '@' // Dynamically created ng-model in the directive element
    },
    link: function (scope, element, attrs) {
      var w = $(window),
        target = attrs.target,
        versionado = attrs.versionado,
        barElement = $('#'+target+'-bar'),
        barElementQueda = $('#'+target+'-bar-queda'),
        barElementProgress =  $('#'+target+'-bar-progress'),
        htmlQueda = 'Hay más <i class="fa fa-arrow-down"></i>',
        valueResize = 0;

      if(target=='nota'){
        valueResize = 210;
      }else if(target=='formulario'){
        valueResize = versionado === 'true'? 210 : 210;
      }

      scope.$watch('modelod', function () {
        scope.panelResizeder();
      });

      w.on('resize', function () {
        scope.panelResizeder();
      });

      element.on('scroll', function(){
        scope.panelMore();
      });

      scope.panelResizeder = function(){
        if(attrs.modelod=='true'){
          $(element).css('height', (w.height() - valueResize) + 'px');
        }else{
          $(element).css('height', '0px');
        }

        setTimeout( function() {
          scope.panelMore();
        }, 500);
      };

      scope.panelMore = function(){
        var scrollHeight = $(element)[0].scrollHeight,
          viewportHeight = $(element).height(),
          progress = $(element).scrollTop() / (scrollHeight - viewportHeight),
          fprogress = progress * 100,
          showProgress = Math.floor(fprogress);

        if(viewportHeight>0){
          if(showProgress==0){
            barElementProgress.css('width', fprogress+'%');
            barElementProgress.html(showProgress+'%');
            barElement.hide();

            barElementQueda.html(htmlQueda);
            barElementQueda.stop(true, true).fadeIn();
          }else{
            barElementQueda.hide();
            barElement.show();

            if(fprogress==100){
              barElementProgress.css('width', fprogress+'%');
              barElementProgress.html('Todo leído');
            }else{
              barElementProgress.css('width', fprogress+'%');
              barElementProgress.html(showProgress+'% leído');
            }
          }
        }else{
          barElement.hide();
          barElementQueda.html('&nbsp;');
          barElementQueda.stop(true, true).fadeIn();
        }
      };

      scope.panelMore();
    }
  }
});