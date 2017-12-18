'use strict';

app.controller('SidebarCtrl', function ($scope, $location) {

  $scope.getClass = function (path) {
    //return ($location.path().substr(0, path.length) === path);
	  return ($location.path() === path);
  };

  $scope.ocultar = function () {
    return ( ($location.path().indexOf('verInscripcion')>0) ||
    ($location.path().indexOf('verCartel')>0)||
    ($location.path().indexOf('consultadiablito')>0)||
    ($location.path().indexOf('verVistaPreviaPlantilla')>0));
  };
});
