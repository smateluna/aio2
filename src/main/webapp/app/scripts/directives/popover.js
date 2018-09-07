'use strict';

app.directive('popover', function($compile, $timeout){
  return {
    restrict: 'A',
    link:function(scope, el, attrs){
      var content = attrs.content; //get the template from the attribute
      var elm = angular.element('<div />'); //create a temporary element
      elm.append(attrs.content); //append the content
      $compile(elm)(scope); //compile 
      $timeout(function() { //Once That is rendered
        el.removeAttr('popover').attr('data-content',elm.html()); //Update the attribute
        el.popover(); //set up popover
       });
    }
  }
});