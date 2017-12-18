/**
 * Created by jaguileram on 25/06/2014.
 */

'use strict';

app.controller('MensajeModalCtrl', function ($rootScope, $scope, $modalInstance, tipo, titulo, detalle) {

  $scope.tipo = tipo;
  $scope.titulo = titulo;
  $scope.detalle = detalle;

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
