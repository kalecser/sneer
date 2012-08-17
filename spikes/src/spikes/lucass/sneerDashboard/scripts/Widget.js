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
	$("#widgetColumn"+widget['column']).append(
		'<div class="widget">'+
		widget['title']+'<br>'+
		'<iframe src="'+widget['url']+'" height="'+widget['height']+'""></iframe><br>'+
		'</div>'
		);
}

function appendColumn(size,index){
	$("#widgets").append('<div id="widgetColumn'+index+'" class="col" style="width:'+size+'"></div>');
}
