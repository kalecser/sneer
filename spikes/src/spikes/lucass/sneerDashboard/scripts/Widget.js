function loadWidgets(state){
	for (var i in state['widgets']){
		appendWidget(state.widgets[i]);
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
