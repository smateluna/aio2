/**
 * Created by jaguileram on 10/03/14.
 */

'use strict';

app.directive('stickySectionHeader', function () {
  return {
    link: function(_scope, _element, _attrs, _ctrl) {

      _element.stickySectionHeaders({
        stickyClass     : 'sticky',
        headlineSelector: 'strong'
      });
    }
  };
});