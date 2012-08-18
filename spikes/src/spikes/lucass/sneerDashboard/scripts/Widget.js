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

widgetColumnWrap = 'widgetColumnWrap';

function getWidgetCode(widget){
	return '<div class="widget">'+
			getTitleDiv(widget['title'])+
			'<div class="widgetBody">'+
				'<iframe src="'+widget['url']+'" height="'+widget['height']+'""></iframe><br>'+
			'</div>'+
		'</div>';
}

function appendWidget(widget){
	$('#'+widgetColumnWrap+widget['column']).prepend(getWidgetCode(widget));
}

function getTitleDiv(title){
	return '<div class="widgetTitle">'+title+'</div>';
}

addWidgetLink = 'addWidgetLink';

function addNewWidgetForButton(button){
	var dummyWidget = {
		"height":"200px",
		"title":"Dontpad/sneer_dashboard",
		"url":"http://dontpad.com/sneer_dashboard"
	};
	$(button).parent().parent().replaceWith(getWidgetCode(dummyWidget));
}

function appendBlankWidget(columnIndex){
	$('#'+addWidgetLink+columnIndex).replaceWith(
		'<div class="widget">'+
		getTitleDiv('New Widget')+
			'<div class="widgetBody">'+
				'Title:<br>'+
				'<input type="text" style="width:100%;"/><br>'+
				'Url:<br>'+
				'<input type="text" style="width:100%;"/><br>'+
				'Height:<br>'+
				'<input type="text" style="width:100%;"/><br>'+
				'<input type="button" value="Add" onclick="addNewWidgetForButton(this)"/><br>'+
			'</div>'+
		'</div>'
	);
	addWidgetAddLink(columnIndex)
}

function addWidgetAddLink(columnIndex){
	$('#'+widgetColumnWrap+columnIndex).append('<div id="'+addWidgetLink+columnIndex+'" class="'+addWidgetLink+'" style="width:100%;"> <a href="javascript:appendBlankWidget('+columnIndex+');" >+</a> </div>');
}

function appendColumn(size,columnIndex){
	$('#widgetTable tr:first').append('<td class="widgetColumn" style="width:'+size+';"> <div id="'+widgetColumnWrap+columnIndex+'" class="col"></div> </td>');
	addWidgetAddLink(columnIndex);
}
