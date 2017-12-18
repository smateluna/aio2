/**
 * Created by jaguileram on 26/09/2014.
 */

'use strict';

app.directive('dvBotonPage', function () {
  return {
    require: 'ngModel',
    link: function(_scope, _element, _attrs, _ctrl) {

      _element.on('click', function(){
        var direccion = $(_element).data('direccion'),
          total = $(_element).data('total'),
          actual = parseInt(_ctrl.$viewValue),
          nueva = 0;


        if(!isNaN(actual) && actual >0){
          if(direccion == 'atras'){
            if(actual>1 && actual<=total){
              nueva = actual-1;
            }else{
              nueva = 1;
            }
          }else if(direccion == 'siguiente'){
            if(actual>=1 && actual<total){
              nueva = actual + 1;
            }else{
              nueva = total;
            }
          }else if(direccion == 'final'){
            nueva = total;
          }else if(direccion == 'inicio'){
            nueva = 1;
          }

          if($('#np' + nueva).length > 0){
            $('#hp'+nueva)[0].scrollIntoView( true );

            setTimeout(function(){
              _ctrl.$setViewValue(nueva);
              _ctrl.$render();

              _scope.$apply();
            }, 100);
          }
        }





      });
    }
  };
});