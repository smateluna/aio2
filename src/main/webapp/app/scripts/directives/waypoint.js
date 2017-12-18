'use strict';

app.directive('waypoint', function () {
  return {
    require: 'ngModel',
    link: function(_scope, _element, _attrs, _ctrl) {

      if(_scope.$last){

        setTimeout(function(){
          $('.DV-waypoint').waypoint(function(direction) {
            if (direction === 'down') {

              var page = $(this).data('page');

              $('#scrollBubbleText').html(page);

              _ctrl.$setViewValue(page);
              _ctrl.$render();
              _scope.$apply();
            }
          }, {
            continuous: false,
            offset: '50%',
            context: '.DV-doc'
          }).waypoint(function(direction) {
            if (direction === 'up') {

              var page = $(this).data('page');

              $('#scrollBubbleText').html(page);

              _ctrl.$setViewValue(page);
              _ctrl.$render();
              _scope.$apply();
            }
          }, {
            continuous: false,
            offset: '-50%',
            context: '.DV-doc'
          });

        }, 1000);

      }
    }
  };
});