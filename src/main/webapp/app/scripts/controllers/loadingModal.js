/**
 * Created by jaguileram on 25/06/2014.
 */

'use strict';

app.controller('LoadingModalCtrl', function ($rootScope, $scope, $modalInstance,titulo, detalle) {

  $scope.titulo = titulo;
  $scope.detalle = detalle;

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
