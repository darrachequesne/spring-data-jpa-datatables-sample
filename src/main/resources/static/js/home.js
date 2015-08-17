$(document).ready(function() {
	var table = $('table#sample').DataTable({
		'ajax' : '/data/users',
		'serverSide' : true,
		columns : [ {
			data : 'id'
		}, {
			data : 'mail'
		}, {
			data : 'role'
		}, {
			data : 'status'
		}, {
			data : 'home.town',
			render : function(data, type, row) {
				if (row.home) {
					return row.home.town;
				}
				return '';
			}
		}, {
			// add another column not persisted on the server-side
			data : 'anothercolumn',
			// order is not available
			orderable : false,
			// yet filter should be available through the method
			// findAll(DataTablesInput input, Specification<T>
			// additionalSpecification)
			searchable : false,
			render : function(data, type, row) {
				if (row.role && row.status) {
					return row.role + ' and ' + row.status;
				}
				return '';
			}
		} ]
	});

	$('select#role_selector').change(function() {
		var filter = '';
		$('select#role_selector option:selected').each(function() {
			filter += $(this).text() + "+";
		});
		filter = filter.substring(0, filter.length - 1);
		table.columns(2).search(filter).draw();
	});

	$('select#status_selector').change(function() {
		var filter = '';
		$('select#status_selector option:selected').each(function() {
			filter += $(this).text() + "+";
		});
		filter = filter.substring(0, filter.length - 1);
		table.columns(3).search(filter).draw();
	});
});