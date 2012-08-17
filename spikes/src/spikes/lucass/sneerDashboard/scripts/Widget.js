function appendWidget(widget, column){
	$("#widgetColumn"+column).append(
		'<div class="widget">'+
		widget['title']+'<br>'+
		'<iframe src="'+widget['url']+'" height="'+widget['height']+'""></iframe><br>'+
		'</div>'
		);
}
