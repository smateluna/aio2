'use strict';

app.directive('loadingState',function () {
  return {
    link:function (scope, element, attrs) {

      scope.$watch(

        function () {
          return scope.$eval(attrs.loadingState);
        },
        function (value) {

          if(value) {


            if(!element.hasClass('disabled')){
              element.addClass('disabled').attr('disabled', 'disabled');

            }

            element.data('resetText', element.html());
            element.html(element.data('loading-text'));
          } else {
            if (element.hasClass('disabled')) {
              element.removeClass('disabled').removeAttr('disabled');
            }

            element.html(element.data('resetText'));
          }
        }
      );
    }
  };
});
