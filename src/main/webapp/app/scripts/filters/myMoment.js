/**
 * Created by jaguileram on 08/10/2014.
 */

'use strict';

app .filter('myMoment', function () {
  return function (dateString, patron, formato) {
    return moment(dateString, patron).format(formato);
  };
});