/**
 * Created by jaguileram on 29/09/2014.
 */

app.directive('scrollBubble', function () {
  return {
    link: function (_scope, _element, _attrs, _ctrl) {


      $(window).ready(function() {
        var scrollTimer = null,
          touch = 'ontouchstart' in window,
          totalScroll = 0,
          previousScroll = 0,
          bubble = $('#scrollbubble'),
          post = '.DV-doc';

        /* event listeners */

        // detect starts and stops to evaluate if we need to show the bubble or not
        $(window).bind('scrollstart', function(e) {
          previousScroll = null;
        });

        $(window).bind('scrollstop', function(e) {
          totalScroll = 0;
        });

        if(touch)
          bubble.css('webkit-transition', '-webkit-transform 0.2s ease-out');

        // main scroll function
        $('.DV-doc').on('scroll', function() {
          $.waypoints('refresh');
          scrollBubble();
        }).on('resize', function(){
          $.waypoints('refresh');
          $('.DV-doc').trigger('scroll');
        });

        function scrollBubble() {
          var distance = distanceBubbleTop();

          bubble.css('top', distance);

            bubble.fadeIn(100);

          // Fade out the annotation after 1 second of no scrolling.
          if (scrollTimer !== null) {
            clearTimeout(scrollTimer);
          }

          scrollTimer = setTimeout(function() {
            bubble.fadeOut(100);
          }, 1000);
        }

        // helper function to calculate where to draw the bubble
        function distanceBubbleTop() {
          var viewportHeight = $(post).height(),
            scrollbarHeight = viewportHeight / $(post)[0].scrollHeight * viewportHeight,
            progress = $(post).scrollTop() / ($(post)[0].scrollHeight  - viewportHeight),
            distance = progress * ( viewportHeight - scrollbarHeight) + (scrollbarHeight/2)  + (bubble.height()/2);// + bubble.height();

          distance = distance + 10;

          /*console.log('viewportHeight: '+viewportHeight);
          console.log('scrollbarHeight: '+scrollbarHeight);
          console.log('progress: '+progress);
          console.log('$(post)[0].scrollHeight: '+$(post)[0].scrollHeight);
          console.log('distance: '+distance);*/

          if(distance>viewportHeight){
            distance = viewportHeight;
          }

          return distance;
        }
      });
    }
  }
});