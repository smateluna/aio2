/**
 * Created by jaguileram on 26/09/2014.
 */

/**
 * Created by jaguileram on 26/09/2014.
 */

'use strict';

app.directive('dvCurrentPage', function () {
  return {
    require: 'ngModel',
    link: function (_scope, _element, _attrs, _ctrl) {

      _element.on('keyup', function () {
          setTimeout(function(){
            var current = parseInt(_ctrl.$viewValue);


            if(!isNaN(current) && current >0){
              if($('#np' + current).length == 0){
                $('#np1')[0].scrollIntoView(true);

                setTimeout(function () {
                  _ctrl.$setViewValue('1');
                  _ctrl.$render();

                  _scope.$apply();
                }, 300);


              }else{
                $('#np' + current)[0].scrollIntoView(true);

                setTimeout(function () {
                  _ctrl.$setViewValue(current+'');
                  _ctrl.$render();

                  _scope.$apply();
                }, 300);
              }

            }
          }, 500);



      });
    }
  }
});