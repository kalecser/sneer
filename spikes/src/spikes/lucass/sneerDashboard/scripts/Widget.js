function loadWidgets(state){
	for (var i in state['widgets']){
		appendWidget(state.widgets[i]);
	}
}

function createColumns(state){
	for (var i in state['columnSizes']){
		appendColumn(state.columnSizes[i],i);
	}
}

function appendWidget(widget){
	$("#widgetColumnWrap"+widget['column']).append(
		'<div class="widget">'+
		widget['title']+'<br>'+
		'<iframe src="'+widget['url']+'" height="'+widget['height']+'""></iframe><br>'+
		'</div>'
	);
}

function appendBlankWidget(column){
	$("#widgetColumnWrap"+column).append(
		'<div class="widget">'+
		'New Widget<br>'+
		'Title:<br>'+
		'<input type="text"/><br>'+
		'Url:<br>'+
		'<input type="text"/><br>'+
		'Height:<br>'+
		'<input type="text"/><br>'+
		'<input type="button" value="Add"/><br>'+
		'</div>'
	);
}

function appendColumn(size,index){
	var c = $('#widgetTable tr:first td').length;
	$('#widgetTable tr:first').append('<td  style="width:'+size+'"> <a href="javascript:appendBlankWidget('+index+');" >Add widget</a> </td>');
	$('#widgetTable tr:gt(0)').append('<td class="widgetColumn" style="width:'+size+';"> <div id="widgetColumnWrap'+index+'" class="col"></div> </td>');
}
