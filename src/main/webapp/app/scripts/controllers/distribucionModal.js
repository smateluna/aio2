/**
 * Created by jaguileram on 25/06/2014.
 */

'use strict';

app.controller('DistribucionModalCtrl', function ($rootScope, $scope, $modalInstance, seccion, responsables) {

  $scope.seccion = seccion;
  $scope.responsables = responsables;

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };

  $scope.seleccionarResponsable = function (responsable) {
    $modalInstance.close(responsable);
  };

});
