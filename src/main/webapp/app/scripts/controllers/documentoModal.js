/**
 * Created by jaguileram on 25/06/2014.
 */

'use strict';

app.controller('DocumentoModalCtrl', function ($rootScope, $scope, $modalInstance, url) {

  $scope.url = url;

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});