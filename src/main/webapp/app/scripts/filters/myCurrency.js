/**
 * Created by jaguileram on 15/07/2014.
 */

app.filter('myCurrency', function() {
  var
    symbol = '$',
    thousand = '.',
    decimal = ',',
    precision = 0,
    format = '$%v';

  return function(number) {
    return number!==undefined?accounting.formatMoney(number, symbol, precision, thousand, decimal, format):'';
  };
});