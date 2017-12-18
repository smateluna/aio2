'use strict';

app.directive('autocomplete', function($timeout) {
    return function(scope, iElement, iAttrs) {
        iElement.autocomplete({
        	source: function(request, response){
        		response(scope.$eval(iAttrs.uiItems));
        	},
            select: function( event, ui ) {
                $timeout(function() {
                  iElement.trigger('input');
                  //$( this ).val( ui.item.value );
                }, 0);
            }
        }).data("ui-autocomplete")._renderItem = function (ul, item) {
            return $("<li></li>")
            .data("item.autocomplete", item)
            .append("<a>" + item.label + "</a>")
            .appendTo(ul);
    };
  };
});
