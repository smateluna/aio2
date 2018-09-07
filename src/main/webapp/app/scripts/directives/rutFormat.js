/**
 * Created by jaguileram on 10/03/14.
 */

'use strict';

app.directive('rutFormat', function () {
  return {
    require: '?ngModel',
    link: function(_scope, _element, _attrs, _ctrl) {

      _element.Rut({
        format_on: 'keyup'
      });
    }
  };
});