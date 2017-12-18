'use strict';

app.directive('bnLazySrc', function () {
  return {
    link: function(_scope, _element, _attrs, _ctrl) {


      if(_scope.$last){

        setTimeout(function(){
          new Blazy(
            {
              container: '.DV-doc',
              success : function(element) {

                //console.log(element);

                var page = $(element).data('page');


                $('#np'+page).fadeOut('fast', function(){
                  $('#np'+page).html('Pág. '+page);
                  $('#np'+page).fadeIn();
                  $.waypoints('refresh');

                  //setTimeout(function(){
                  //  $('#np' + page)[0].scrollIntoView(true);
                  //}, 300);

                });



              },error : function(element) {

              //console.log(element);

              var page = $(element).data('page');

              $('#np'+page).fadeOut('fast', function(){
                $('#np'+page).html('<i class="fa fa-warning"></i> Problema cargando pág. '+page);
                $('#np'+page).fadeIn();


                $(element).attr('src', '../app/images/error-placeholder.png');


                $.waypoints('refresh');

                //setTimeout(function(){
                //  $('#np' + page)[0].scrollIntoView(true);
                //}, 300);

              });



            }
            });

        }, 1000);
      }

    }
  };
});