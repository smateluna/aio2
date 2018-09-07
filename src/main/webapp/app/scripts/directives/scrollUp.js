app.directive('scrollUp', function () {
  return {
    restrict: 'E',
    link: function(_scope, _element, _attrs, _ctrl) {

      $.scrollUp({
        animation: 'fade',
        //activeOverlay: '#00FFFF',
        scrollImg: { active: true, type: 'background', src: 'img/top.png' }
      });

    }
  };
});