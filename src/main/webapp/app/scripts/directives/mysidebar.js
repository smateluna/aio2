'use strict';

app.directive('mySidebar', function (localStorageService) {
  return function(scope, elem, attr) {
      var inicio = function() {

        $('.cl-vnavigation li ul').each(function(){
          $(this).parent().addClass('parent');
        });

        $('.cl-vnavigation li ul li.active').each(function(){
          $(this).parent().show().parent().addClass('open');
        });

        $('.cl-vnavigation').delegate('.parent > a','click',function(e){
          $('.cl-vnavigation .parent.open > ul').not($(this).parent().find('ul')).slideUp(300, 'swing',function(){
            $(this).parent().removeClass('open');
          });

          var ul = $(this).parent().find('ul');
          ul.slideToggle(300, 'swing', function () {
            var p = $(this).parent();
            if(p.hasClass('open')){
              p.removeClass('open');
            }else{
              p.addClass('open');
            }

            $('#cl-wrapper .nscroller').nanoScroller({ preventPageScrolling: true });
          });
          e.preventDefault();
        });

        $('.cl-toggle').click(function(e){
          var ul = $('.cl-vnavigation');
          ul.slideToggle(300, 'swing', function () {
          });
          e.preventDefault();
        });

        /*Collapse sidebar*/
        $('#sidebar-collapse').click(function(){
          toggleSideBar();
        });

        if($('#cl-wrapper').hasClass('fixed-menu')){
          var scroll =  $('#cl-wrapper .menu-space');
          scroll.addClass('nano nscroller');

          var update_height = function(){
            var button = $('#cl-wrapper .collapse-button');
            var collapseH = button.outerHeight();
            var navH = $('#head-nav').height();
            var height = $(window).height() - ((button.is(':visible'))?collapseH:0) - navH;
            scroll.css('height',height);
            $('#cl-wrapper .nscroller').nanoScroller({ preventPageScrolling: true });
          };

          $(window).resize(function() {
            update_height();
          });

          update_height();
          $('#cl-wrapper .nscroller').nanoScroller({ preventPageScrolling: true });

        }else{
          $(window).resize(function(){
            //updateHeight();
          });
          //updateHeight();
        }

        var domh = $('#pcont').height();
        $(document).bind('DOMSubtreeModified', function(){
          var h = $('#pcont').height();
          if(domh != h) {
          }
        });

        $('.nscroller').nanoScroller();

        var sbCollapsed = localStorageService.get('sbCollapsed');


        //console.log(sbCollapsed);

        if (sbCollapsed === 'true') {
          toggleSideBar();
        }

      };

      var toggleSideBar = function() {
        var b = $('#sidebar-collapse')[0];
        var w = $('#cl-wrapper');
        var s = $('.cl-sidebar');

        if (w.hasClass('sb-collapsed')) {
          localStorageService.set('sbCollapsed', 'false');

          jQuery('body').addClass('shelf-open');

          $('.fa', b).addClass('fa-angle-left').removeClass('fa-angle-right');
          w.removeClass('sb-collapsed');
        } else {
          localStorageService.set('sbCollapsed', 'true');
          jQuery('body').removeClass('shelf-open');

          $('.fa', b).removeClass('fa-angle-left').addClass('fa-angle-right');
          w.addClass('sb-collapsed');
        }
      };

      inicio();
  };
});
