'use strict';

app.directive('anexosDatatable', function() {
    return function(scope, element, attrs) {

        // apply DataTable options, use defaults if none specified by user
        var options = {};
        if (attrs.anexosDatatable.length > 0) {
            options = scope.$eval(attrs.anexosDatatable);
        } else {
            options = {
                /*"bStateSave": true,
                "iCookieDuration": 2419200, //1 month
                "bJQueryUI": true,
                "bPaginate": true,
                "bLengthChange": true,
                "bFilter": false,
                "bInfo": false,
                "bDestroy": true*/
            	"visible": false,
		    	"searchable": true
            };
        }

        // Tell the dataTables plugin what columns to use
        // We can either derive them from the dom, or use setup from the controller 
        /*
        var explicitColumns = [];
        element.find('th').each(function(index, elem) {
            explicitColumns.push($(elem).text());
        });
        if (explicitColumns.length > 0) {
            options["aoColumns"] = explicitColumns;
        } else if (attrs.aoColumns) {
            options["aoColumns"] = scope.$eval(attrs.aoColumns);
        }

        // aoColumnDefs is dataTables way of providing fine control over column config
        if (attrs.aoColumnDefs) {
            options["aoColumnDefs"] = scope.$eval(attrs.aoColumnDefs);
        }
        
        if (attrs.fnRowCallback) {
            options["fnRowCallback"] = scope.$eval(attrs.fnRowCallback);
        }
        * */

        // apply the plugin
        //var dataTable = element.dataTable(options);
        
        /*aux = element.searchable({
	        striped: true,
	        oddRow: { 'background-color': '#f5f5f5' },
	        evenRow: { 'background-color': '#fff' },
	        searchType: 'fuzzy'
	    });*/

        $('.dataTables_wrapper .toolbar').prepend('<div style="color:red; font-weight:bold">Toolbar insert</div>');
        
        // watch for any changes to our data, rebuild the DataTable
        scope.$watch(attrs.aaData, function(value) {
            var val = value || null;
            if (val) {
                dataTable.fnClearTable();
                dataTable.fnAddData(scope.$eval(attrs.aaData));
            }
        });
    };
});