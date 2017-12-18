'use strict';

app.directive('mySubmenu', function () {
    return {
      restrict: 'A',
      link: function (scope, element, attrs) {

        if(scope.$last){

          /*SubMenu hover */
          var tool = $('<div id="sub-menu-nav" style="position:fixed;z-index:9999;"></div>');

          var showMenu = function(_this, e){
            if(($('#cl-wrapper').hasClass('sb-collapsed') || ($(window).width() > 755 && $(window).width() < 963)) && $('ul',_this).length > 0){
              $(_this).removeClass('ocult');
              var menu = $('ul',_this);
              if(!$('.dropdown-header',_this).length){

                var head = '<li class="dropdown-header">' +  $(_this).children().html()  + '</li>' ;
                menu.prepend(head);
              }

              tool.appendTo('body');
              var top = ($(_this).offset().top + 8) - $(window).scrollTop();
              var left = $(_this).width();

              tool.css({
                'top': top,
                'left': left + 8
              });
              tool.html('<ul class="sub-menu">' + menu.html() + '</ul>');
              tool.fadeIn('fast');

              menu.css('top', top);

            }else if(($('#cl-wrapper').hasClass('sb-collapsed') || ($(window).width() > 755 && $(window).width() < 963))){
              $(_this).removeClass('ocult');

              var link = $(_this).clone();

              $(link).find('span').not('.ng-hide').remove();
              $(link).find('i').remove();
              $(link).find('span').removeClass('ng-hide');
              $(link).find('a').addClass('parent-alone');

              var menu = '<li>'+link.html()+'</li>';

/*              var menu = $('ul',_this);
              if(!$('.dropdown-header',_this).length){
                var head = '<li class="dropdown-header">' +  $(_this).find('a span.ng-hide').text()  + '</li>' ;
                menu.prepend(head);
              }*/

              tool.appendTo('body');
              var top = ($(_this).offset().top + 8) - $(window).scrollTop();
              var left = $(_this).width();

              tool.css({
                'top': top,
                'left': left + 8
              });
              tool.html('<ul class="sub-menu">' + menu + '</ul>');
              tool.fadeIn('fast');

              //menu.css('top', top);


            }else{
              tool.fadeOut('fast');
            }
          };

          $('.cl-vnavigation li').hover(function(e){
            showMenu(this, e);
          },function(e){

            tool.removeClass('over');
            setTimeout(function(){
              if(!tool.hasClass('over') && !$('.cl-vnavigation li:hover').length > 0){
                tool.hide();
              }
            },500);
          });

          tool.hover(function(e){
            $(this).addClass('over');
          },function(){
            $(this).removeClass('over');
            setTimeout(function(){
              if(!tool.hasClass('over') && !$('.cl-vnavigation li:hover').length > 0){
                tool.fadeOut('fast');
              }
            },500);
          });

          $(document).click(function(){
            tool.hide();
          });

          tool.click(function(e){
            e.stopPropagation();
          });

          $('.cl-vnavigation li').click(function(e){
            if((($('#cl-wrapper').hasClass('sb-collapsed') || ($(window).width() > 755 && $(window).width() < 963)) && $('ul',this).length > 0) && !($(window).width() < 755)){
              showMenu(this, e);
              e.stopPropagation();
            }
          });
        }
      }
    };
  });
