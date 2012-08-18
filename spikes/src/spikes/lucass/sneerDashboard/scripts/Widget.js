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

function appendColumn(size,index){
	var c = $('#widgetTable tr:first td').length;
	$('#widgetTable tr:first').append('<td  style="width:'+size+'">'+index+'</td>');
	$('#widgetTable tr:gt(0)').append('<td class="widgetColumn"> <div id="widgetColumnWrap'+index+'" class="col"></div> </td>');
}
